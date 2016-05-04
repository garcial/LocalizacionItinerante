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
import jade.lang.acl.UnreadableException;

public class AgSensor extends Agent {

	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<AID, Point> agentes;
	private final MessageTemplate mtPetVecinos = 
			MessageTemplate.and(
			    MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			    MessageTemplate.MatchConversationId("vecinos"));  
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
	
	public synchronized void IncorporaAgente(AID agente, Point posicion) {
		agentes.put(agente, posicion);
	}
	
	public class BDevolverDetectados extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtPetVecinos);
			if (msg!= null) {
				Point p1 = null;;
				try {
					p1 = (Point) msg.getContentObject();
				} catch (UnreadableException e1) { e1.printStackTrace(); }
				// Calcula vecinos entre todos los agentes activos
				List<AID> vecinos = new ArrayList<AID>(); 
				String agenteSolicitante = msg.getSender().getLocalName();
				for(AID agente: agentes.keySet()){
					Point p2 = agentes.get(agente);
					double dist = Point.CalcularDistancia(p1, p2);
					if (dist <= p1.getRadio() && dist <= p2.getRadio()
							&& !agenteSolicitante.equals(agente.getLocalName())) {
						vecinos.add(agente);
					}
				}
				// Elimina al agente solicitante del resultado
//				if (!vecinos.remove(msg.getSender())) 
//					System.out.println("ERROR eliminando al agente solicitante " + 
//										msg.getSender().getLocalName() +
//				                       " de la lista de vecinos a devolver");;
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

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtINF);
			if (msg!= null) {
				if (msg.getConversationId().equals("posicionSensor")) {
					Point p = null;
					try {
						p = (Point) msg.getContentObject();
					} catch (UnreadableException e) { e.printStackTrace(); }
					AID aidInformador = msg.getSender();
					if (agentes.containsKey(aidInformador)) {
						agentes.put(aidInformador, p);
					} else {
						System.out.println("ERROR:Agente: " + 
					                    aidInformador.getName() + 
					                    " desconocido para actualizar posicion.");
					}
				} else if (msg.getConversationId().equals("nuevoSensor")) {
					Point p = null;
					try {
						p = (Point) msg.getContentObject();
					} catch (UnreadableException e) { e.printStackTrace(); }
					AID aidInformador = msg.getSender();
					if (!agentes.containsKey(aidInformador)) {
						agentes.put(aidInformador, p);
					} else {
						System.out.println("ERROR:Agente: " + 
					                  aidInformador.getName() + 
					                  " ya activo, no se puede dar de alta.");						
					}
				} else if (msg.getConversationId().equals("bajaSensor")) {
					AID aidInformador = msg.getSender();
					if (agentes.remove(aidInformador) == null) 
						System.out.println("ERROR:Agente: " + 
					                   aidInformador.getName() + 
					                   " ya existe, no se puede dar de baja.");;						
					}			
			} else block();
		}
	}
}
