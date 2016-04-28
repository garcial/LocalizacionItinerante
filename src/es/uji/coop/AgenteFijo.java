package es.uji.coop;

import java.util.UUID;

import es.uji.coop.mapa.Point;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgenteFijo extends Agent {

	private static final long serialVersionUID = 1L;

	private Point posicion;
	private int radio;
	public synchronized int getRadio() {
		return radio;
	}
	private String id = UUID.randomUUID().toString();
	
	private DFAgentDescription agInterfaz;
	@SuppressWarnings("unused")
	private DFAgentDescription agSensor;

	protected void setup() {
		// Registra el servicio de "sensor Fijo"
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("sensorFijo");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this,  dfd);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		
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
				msg.setContent(id);
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
				radio = Integer.parseInt(cont.substring(cont.indexOf("radio=")+6));
				posicion = new Point(x, y, radio);
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
				// Ahora ya toca comenzar a escucha las peticiones de ayuda.
				addBehaviour(new BAtenderPeticiones());
				paso++;
			}
			}

		@Override
		public boolean done() {
			if (paso == 7) return true;
			return false;
		}

	}  // Fin de clase interna BConexion Interfaz
	
	private class BAtenderPeticiones extends CyclicBehaviour {
		
		private static final long serialVersionUID = -124136298166210934L;
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
				//msgResp.setConversationId(msg.getConversationId());
				msgResp.setContent("x="+x_+"y="+y_+"dist="+dist);
				myAgent.send(msgResp);
			} else block();
		}

	}
}
