package es.uji.coop;

import java.util.Iterator;
import java.util.UUID;

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

public class AgMedio extends Agent {

	private static final long serialVersionUID = 1L;
	private Point posicion;

	private Mapa mapa;
	private String idAg = UUID.randomUUID().toString();
	public String GetIdAg() {return idAg;}
	
	private DFAgentDescription agInterfaz;
	@SuppressWarnings("unused")
	private DFAgentDescription agSensor;

	protected void setup() {
		// Registra el servicio de "sensor Medio"
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("sensorMedio");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this,  dfd);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		// Para saber desplazarse por el mundo
		mapa = new Mapa();
		// Incorpora un comportamiento para realizar las conexiones e identificar a los 
		//    agentes de la infraestructura: AgInterfaz, AgLog, AgSensor
		addBehaviour(new BConexionInfraestructura());
	}
	
	private class BConexionInfraestructura extends Behaviour {

		private static final long serialVersionUID = 1L;
		int paso = 0;
		DFAgentDescription dfd = null;
		DFAgentDescription[] result = null;
		ServiceDescription sd;
		
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
				msg.setContent(idAg);
				msg.addReceiver(agInterfaz.getName());
				myAgent.send(msg);
				paso++;
				break;
			case 3: // Espera respuesta del interfaz y no avanza hasta recibirla
				msg = myAgent.blockingReceive();
				String cont = msg.getContent();
				// Recupera datos del contenido del mensaje
				int x = Integer.parseInt(
						cont.substring(cont.indexOf("x=")+2, cont.indexOf("y=")));
				int y = Integer.parseInt(
						cont.substring(cont.indexOf("y=")+2, cont.indexOf("radio=")));
				double radio = Double.parseDouble(cont.substring(cont.indexOf("radio=")+6));
				posicion = new Point(x, y, radio);
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
				// Ahora ya toca comenzar a escucha las peticiones de ayuda y a 
				//    desplazarme por el escenario.
				addBehaviour(new BAtenderPeticiones());
				addBehaviour(new BPasoSimple());
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
		final MessageTemplate mt = 
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST); 
		@Override
		public void action() {

			ACLMessage msg = myAgent.receive(mt);
			if (msg!= null) {
				String cont = msg.getContent();
				// Recuperar datos del contenido del mensaje
				int x_ = Integer.parseInt(
						cont.substring(cont.indexOf("x=")+2, cont.indexOf("y=")));
				int y_ = Integer.parseInt(cont.substring(cont.indexOf("y=")+2));
				// Calcula distancia
				double dist = Math.sqrt((posicion.getX()-x_)*(posicion.getX()-x_) + 
						                (posicion.getY()-y_)*(posicion.getY()-y_));
				ACLMessage msgResp = new ACLMessage(ACLMessage.INFORM);
				msgResp.addReceiver(msg.getSender());
				msgResp.setConversationId("distancia");
				msgResp.setConversationId(msg.getConversationId());
				msgResp.setContent("x="+x_+"y="+y_+"dist="+dist);
				myAgent.send(msgResp);
			} else block();
		}

	}
	
	public class BPasoSimple extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		final MessageTemplate mtRespVecinos = 
				MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
						            MessageTemplate.MatchConversationId("vecinos"));  

		@Override
		public void action() {
			// Avanza un paso
			mapa.Avanza(posicion);
			// Obtiene agentes vecinos en tu radio dada tu posicion actual
			ACLMessage msgVecinos = new ACLMessage(ACLMessage.REQUEST);
			msgVecinos.addReceiver(agSensor.getName());
			msgVecinos.setConversationId("vecinos");
			msgVecinos.setContent("x=" + posicion.getX() + "y=" + posicion.getY()
			                          + "radio=" + posicion.getRadio());
			send(msgVecinos);
			ACLMessage msgResp = blockingReceive();
			AID[] vecinos = null;
			try {
				vecinos = (AID[]) msgResp.getContentObject();
			} catch (UnreadableException e) { e.printStackTrace(); }
			if (vecinos != null) { // Si alguien me detecta pedir datos
				Point posicionCalculada = PreguntaVecinos(vecinos);
				// Comunica al agente log la posicion calculada

			}
		}

		private Point PreguntaVecinos(AID[] vecinos) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			for (AID aid : vecinos) {
				msg.addReceiver(aid);
			}
			msg.setContent("x=" + posicion.getX() + "y=" + posicion.getY()
			                          + "radio=" + posicion.getRadio());
			send(msg);
			Point[] puntos = new Point[vecinos.length];
			
			return null;
		}

	}
}
