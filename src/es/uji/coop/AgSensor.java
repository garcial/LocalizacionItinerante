package es.uji.coop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import es.uji.coop.mapa.Point;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgSensor extends Agent {

	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<AID, Point> agentes;
	private final MessageTemplate mtREQ = 
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST);  
	private final MessageTemplate mtINF = 
			MessageTemplate.MatchPerformative(ACLMessage.INFORM);  
	
	protected void setup(){
		agentes = new ConcurrentHashMap<AID, Point>();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("sensor");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this,  dfd);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		addBehaviour(new BActualizar());
		addBehaviour(new BDevolverDetectados());
	}
	
	public class BDevolverDetectados extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtREQ);
			if (msg!= null) {
				String cont = msg.getContent();
				// Recuperar datos del contenido del mensaje
				int x_ = Integer.parseInt(
						cont.substring(cont.indexOf("x=")+2, cont.indexOf("y=")));
				int y_ = Integer.parseInt(cont.substring(cont.indexOf("y=")+2));
				int radio = Integer.parseInt(cont.substring(cont.indexOf("radio=")+6));
				// Calcula vecinos
				List<AID> vecinos = new ArrayList<AID>(); 
				for(AID agente: agentes.keySet()){
					Point p = agentes.get(agente);
					double dist = Math.sqrt((p.getX()-x_)*(p.getX()-x_) + 
			                (p.getY()-y_)*(p.getY()-y_));
					if (dist <= radio && dist <= p.getRadio()) {
						vecinos.add(agente);
					}
				}
				AID[] vecinosResp = vecinos.toArray(new AID[0]);
				// Contesta con los vecinos
				ACLMessage msgResp = new ACLMessage(ACLMessage.INFORM);
				msgResp.addReceiver(msg.getSender());
				msgResp.setConversationId("vecinos");
				try {
					msgResp.setContentObject(vecinosResp);
				} catch (IOException e) { e.printStackTrace(); }
				myAgent.send(msgResp);
			} else block();
		}
	}

	public class BActualizar extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtINF);
			if (msg!= null) {
				if (msg.getConversationId().equals("posicionSensor")) {
					String cont = msg.getContent();
					AID aidInformador = msg.getSender();
					int x_ = Integer.parseInt(
							cont.substring(cont.indexOf("x=")+2, cont.indexOf("y=")));
					int y_ = Integer.parseInt(cont.substring(cont.indexOf("y=")+2));
					int radio = Integer.parseInt(cont.substring(cont.indexOf("radio=")+6));
					if (agentes.containsKey(aidInformador)) {
						agentes.put(aidInformador, new Point(x_, y_, radio));
					} else {
						System.out.println("ERROR:Agente: "+aidInformador.getName()+ " desconocido para actualizar posicion.");
					}
				} else if (msg.getConversationId().equals("nuevoSensor")) {
					String cont = msg.getContent();
					AID aidInformador = msg.getSender();
					int x_ = Integer.parseInt(
							cont.substring(cont.indexOf("x=")+2, cont.indexOf("y=")));
					int y_ = Integer.parseInt(cont.substring(cont.indexOf("y=")+2));
					int radio = Integer.parseInt(cont.substring(cont.indexOf("radio=")+6));
					if (!agentes.containsKey(aidInformador)) {
						agentes.put(aidInformador, new Point(x_, y_, radio));
					} else {
						System.out.println("ERROR:Agente: "+aidInformador.getName()+ " ya activo, no se puede dar de alta.");						
					}
				} else if (msg.getConversationId().equals("bajaSensor")) {
					AID aidInformador = msg.getSender();
					if (agentes.remove(aidInformador) == null) 
						System.out.println("ERROR:Agente: "+aidInformador.getName()+ " ya existe, no se puede dar de baja.");;						
					}			
			} else block();
		}
	}
}
