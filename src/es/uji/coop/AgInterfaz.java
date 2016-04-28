package es.uji.coop;

import java.util.Random;

import javax.swing.SwingUtilities;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgInterfaz extends Agent {
	private static final long serialVersionUID = 1L;
	private CanvasMundo canvas;
	public static final int MAXMUNDOX = 810;
	public static final int MAXMUNDOY = 610;
	public Random rnd;
	
	private MessageTemplate mtNuevoSensor = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchConversationId("alta"));
	private MessageTemplate mtBajaSensor = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.INFORM),
			MessageTemplate.MatchConversationId("baja"));
	
	protected void setup() {
		// Registra el servicio de interfaz
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("interfaz");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this,  dfd);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		rnd = new Random();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				canvas = new CanvasMundo(getLocalName(), MAXMUNDOX, MAXMUNDOY);	
			}			
		});
		// Gestiona datos de nuevo sensor en el escenario.
		addBehaviour(new BIncorporaSensor());
		// Gestiona datos de posicion de sensores y de moviles.
		addBehaviour(new BBajaSensor());
	}
	
	/*
	 * Un nuevo sensor le indica al Interfaz que le indique en que coordenadas
	 *   va a estar ubicado inicialmente y cual es su radio de sensorizaci�n.
	 */
	private class BIncorporaSensor extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtNuevoSensor);
			if (msg != null) {
				// Generar datos de posicion del sensor y su radio
				final int radio = rnd.nextInt(50)+150; 
				final int x = rnd.nextInt(MAXMUNDOX-200) + 100;
				final int y = rnd.nextInt(MAXMUNDOY-200) + 100;
				final String agente = msg.getSender().getLocalName();
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						canvas.incluyeSensor(agente, x, y, radio);	
					}
				});
				ACLMessage msgres = new ACLMessage(ACLMessage.INFORM);
				msgres.addReceiver(msg.getSender());
				msgres.setContent("x="+x+"y="+y+"radio="+radio);
				myAgent.send(msgres);
			} else block();
		}
	}
	
	private class BBajaSensor extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtBajaSensor);
			if (msg != null) {
				final String agente = msg.getSender().getLocalName();
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						canvas.bajaSensor(agente);	
					}
				});
			} else block();
		}
	}
}
