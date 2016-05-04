package es.uji.coop;

import java.io.IOException;

import es.uji.coop.mapa.Mapa;
import es.uji.coop.mapa.Point;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgActivo extends Agent {
	private static final long serialVersionUID = 1L;
	private Point posicion = null;

	private Mapa mapa;
	private String tipoAgente;
	
	private DFAgentDescription agInterfaz;
	private DFAgentDescription agSensor;

	protected void setup() {
		// Registra el servicio de sensor del tipo que sea:
		//   fijo, medio, simple
		Object[] args = getArguments();
		if (args.length == 0) takeDown();
		tipoAgente = (String) args[0];
		System.out.println("Agente de tipo " + tipoAgente +
		           " con nombre: " + getLocalName());
		if (!(tipoAgente.equals("fijo") ||
		      tipoAgente.equals("medio") ||
		      tipoAgente.equals("simple"))) {
			System.out.println("Error instanciando el agente "+
		                       getLocalName() + "sensor que " +
					          "no es fijo, medio o simple");
			takeDown();
		}
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(tipoAgente);
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this,  dfd);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		// Para saber desplazarse por el mundo
		mapa = new Mapa();
		// Incorpora un comportamiento para realizar las conexiones  
		//    e identificar a los agentes de la infraestructura: 
		//    AgInterfaz, AgLog, AgSensor
		addBehaviour(new BConexionInfraestructura());
	}
	
	private class BConexionInfraestructura extends Behaviour {

		private static final long serialVersionUID = 1L;
		int paso = 0;
		DFAgentDescription dfd = null;
		DFAgentDescription[] result = null;
		ServiceDescription sd;
		final MessageTemplate mtInterfaz = MessageTemplate.and(
				MessageTemplate.MatchConversationId("alta"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM)); 
		
		@Override
		
		public void action() {
			switch(paso) { // Localiza el agente interfaz
			case 0:	dfd = new DFAgentDescription();
				sd = new ServiceDescription();
				sd.setType("interfaz");
				dfd.addServices(sd);
				paso++;
				break;
			case 1: 
			    try {  // Espera como mucho 5 segundos por si todavia 
			    	   //    no ha arrancado o hay otros agentes sensores  
			    	   //    accediendo concurrentemente al DF
				  result = DFService.searchUntilFound(
						  myAgent, getDefaultDF(), dfd, null, 5000);
				} catch (FIPAException e) { e.printStackTrace();}
			    if (result != null) paso++;
			    break;
			case 2: // Ya hay un interfaz activo, ahora hay que enviarle un msg 
				    //    para que me pase mis coordenadas
				agInterfaz = result[0];
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setContent(tipoAgente);
				msg.setConversationId("alta");
				msg.addReceiver(agInterfaz.getName());
				myAgent.send(msg);
				paso++;
				break;
			case 3: // Espera respuesta del interfaz y no avanza hasta recibirla
				msg = myAgent.blockingReceive(mtInterfaz);
				try {
					posicion = (Point) msg.getContentObject();
				} catch (UnreadableException e1) { e1.printStackTrace(); }
				paso++;
				break;
			case 4: // Busca el agente sensor
				dfd = new DFAgentDescription();
				sd = new ServiceDescription();
				sd.setType("sensor");
				dfd.addServices(sd);
				paso++;
				break;
			case 5: 
			    try {  // Espera como mucho 5 segundos por si todavia 
			    	   //    no ha arrancado o hay otros agentes sensores  
			    	   //    accediendo concurrentemente al DF
				  result = DFService.searchUntilFound(
						  myAgent, getDefaultDF(), dfd, null, 5000);
				} catch (FIPAException e) { e.printStackTrace();}
			    if (result != null) paso++;
			    break;
			case 6: // Ya hay un sensor activo
				agSensor = result[0];
				// Y están identificados los agentes de la infraestructura. 
				msg = new ACLMessage(ACLMessage.INFORM);
				// Envia datos de este nuevo sensor al AgSensor
				msg.addReceiver(agSensor.getName());
				msg.setConversationId("nuevoSensor");
				try {
					msg.setContentObject(posicion);
				} catch (IOException e) { e.printStackTrace(); }
				send(msg);
				// Ahora ya toca comenzar a escucha las peticiones de ayuda y a 
				//    desplazarme por el escenario.
				addBehaviour(new BAtenderPeticiones());
				if (!tipoAgente.equals("fijo"))	addBehaviour(new BPasoSimple());
				paso++;
				break;
			}
			}

		@Override
		public boolean done() {
			if (paso == 7) return true;
			return false;
		}

	}  // Fin de clase interna BConexion Interfaz
	
	private class BAtenderPeticiones extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		final MessageTemplate mtPet = MessageTemplate.and(
				    MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				    MessageTemplate.MatchConversationId("distancia")); 
		@Override
		public void action() {

			ACLMessage msg = myAgent.receive(mtPet);
			if (msg!= null) {
				Point p = null;
				try {
					p = (Point) msg.getContentObject();
				} catch (UnreadableException e1) { e1.printStackTrace(); }
				// Calcula distancia
				double dist = Point.CalcularDistancia(p, posicion);
				ACLMessage msgResp = new ACLMessage(ACLMessage.INFORM);
				msgResp.addReceiver(msg.getSender());
				msgResp.setConversationId("distancia");
				try {
					msgResp.setContentObject(new Point(p.getX(), p.getY(), dist));
				} catch (IOException e) { e.printStackTrace(); }
				myAgent.send(msgResp);
			} else block();
		}

	}
	
	public class BPasoSimple extends Behaviour {

		private static final long serialVersionUID = 1L;
		final MessageTemplate mtRespDist = 
				MessageTemplate.and(
				        MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				        MessageTemplate.MatchConversationId("distancia"));
		final MessageTemplate mtRespVecinos = 
				MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
						MessageTemplate.MatchConversationId("vecinos"));  
		int paso = 0;
		AID[] vecinos = null;
		Point[] puntos = null;
		int totalRespuestas = 0;

		@Override
		public void action() {
			switch (paso) {
			case 0:
				// Avanza un paso
				mapa.Avanza(posicion);
				// Comunica tu nueva posicion al agSensor y al agInterfaz
				ACLMessage msgPos = new ACLMessage(ACLMessage.INFORM);
				msgPos.addReceiver(agSensor.getName());
				msgPos.addReceiver(agInterfaz.getName());
				try {
					msgPos.setContentObject(posicion);
				} catch (IOException e1) { e1.printStackTrace(); }
				msgPos.setConversationId("posicionSensor");
				send(msgPos);
				// Obtiene agentes vecinos en tu radio dada tu posicion actual
				ACLMessage msgVecinos = new ACLMessage(ACLMessage.REQUEST);
				msgVecinos.addReceiver(agSensor.getName());
				msgVecinos.setConversationId("vecinos");
				try {
					msgVecinos.setContentObject(posicion);
				} catch (IOException e1) { e1.printStackTrace(); }
				send(msgVecinos);
				paso++;
				break;
			case 1: 
				ACLMessage msgResp = myAgent.receive(mtRespVecinos);
				if (msgResp != null) {
					try {
						vecinos = (AID[]) msgResp.getContentObject();
					} catch (UnreadableException e) { e.printStackTrace(); }
					if (vecinos != null) {
					    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					    msg.setConversationId("distancia");
					    for (AID aid : vecinos) {
						    msg.addReceiver(aid);
					    }
					    try {
							msg.setContentObject(posicion);
						} catch (IOException e) { e.printStackTrace(); }
					    send(msg);
					    puntos = new Point[vecinos.length];
					    paso++; 
					} else { paso = 3; }
				} else block();
				break;
			case 2: 
				ACLMessage msg = myAgent.receive(mtRespDist);
				if (msg != null) {
					try {
						puntos[totalRespuestas] = (Point) msg.getContentObject();
					} catch (UnreadableException e) { e.printStackTrace(); }
					totalRespuestas++;
					if (totalRespuestas == puntos.length) {
						paso++;
					}
				} else block();
				break;

			case 3:
				Point localizacionEstimada = PreguntaVecinos(puntos);
				// Envia el mensaje con la localizacion estimada al 
				//   agLog y agInterfaz
				System.out.println("Localizacion estimada agente " 
				+myAgent.getLocalName() + localizacionEstimada);
				paso++;
				break;
			}
		}

		private Point PreguntaVecinos(Point[] puntos) {
			if (puntos == null) return new Point(0,0,0);
			// Por ahora no hace nada decente con esa informacion
			return puntos[0];
		}

		@Override
		public boolean done() {
			if (paso == 4) {
				addBehaviour(new BPasoSimple());
				return true;				
			}
			return false;
		}

	}
}
