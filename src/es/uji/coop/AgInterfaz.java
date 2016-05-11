package es.uji.coop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.SwingUtilities;

import es.uji.coop.mapa.CaminarAleatorio;
import es.uji.coop.mapa.Point;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgInterfaz extends Agent {
	private static final long serialVersionUID = 1L;
	private CanvasMundo canvas;
	public static int MAXMUNDOX;
	public static int MAXMUNDOY;
	public Random random;
	private String pathFicheroMapa;
	private CaminarAleatorio cam;
	private int nFilas;
	private int nColumnas;
	private String[] filas;
	private int[][] mapaEntero;
	private int tc;
	
	private MessageTemplate mtNuevoSensor = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchConversationId("alta"));
	private MessageTemplate mtBajaSensor = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.INFORM),
			MessageTemplate.MatchConversationId("baja"));
	
	protected void setup() {
		// Lee el mapa recibido como parametro del agente
		Object[] args = getArguments();
		if (args.length == 0) takeDown();
		pathFicheroMapa = (String) args[0];
		leerFichero(pathFicheroMapa);
		random = new Random();
		cam = new CaminarAleatorio(mapaEntero, random, tc);
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
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				canvas = new CanvasMundo(
						getLocalName(), tc, MAXMUNDOX, MAXMUNDOY, mapaEntero);	
			}			
		});
		// Gestiona datos de nuevo sensor en el escenario.
		addBehaviour(new BIncorporaSensor());
		// Dar de baja a sensores.
		addBehaviour(new BBajaSensor());
		// Actualizar posiciones de los sensores en el escenario
		addBehaviour(new BEscuchaSensores());
	}
	
	/*
	 * Leer fichero recibido como par�metro
	 */
	
	private void leerFichero(String pathFicheroMapa) {
		try{
		      FileReader f = new FileReader(pathFicheroMapa);
		      BufferedReader b = new BufferedReader(f);
		      tc = Integer.parseInt(b.readLine());
		      nFilas = Integer.parseInt(b.readLine());
		      nColumnas = Integer.parseInt(b.readLine());
		      MAXMUNDOX = nColumnas * tc;
		      MAXMUNDOY = nFilas * tc;
		      filas = new String[nFilas];
		      int i=0;
		      while(i<nFilas && (filas[i] = b.readLine())!=null) {
		         i++;
		      }
		      b.close();
			  mapaEntero = new int[nFilas][nColumnas];
			} catch (Exception e){
				System.out.println("Problemas al leer el mapa del fichero "+pathFicheroMapa);
				takeDown();
			}	

			for (int i = 0; i < nFilas; i++) {
				for (int j = 0; j < nColumnas; j++) {
					mapaEntero[i][j] = (int) (filas[i].charAt(j)) - 48;
					System.out.print(mapaEntero[i][j]);
				}
				System.out.println();			
			}		
	}
	
	/*
	 * Un nuevo sensor le indica al Interfaz que le indique en 
	 *   que coordenadas va a estar ubicado inicialmente y cual 
	 *   es su radio de sensorizacion.
	 */
	private class BIncorporaSensor extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtNuevoSensor);
			if (msg != null) {
				// Generar datos de posicion del sensor y su radio
				String tipo = msg.getContent();
				double radio;
				if (tipo.equals("fijo")) radio = random.nextInt(25) + 175; 
				else if (tipo.equals("medio")) radio = random.nextInt(10) + 75;
				else radio = random.nextInt(5) + 25;
				// Crea un nuevo objeto movil
				Point p = cam.PosicionInicial();
				final String agente = msg.getSender().getLocalName();

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						canvas.incluyeSensor(agente, p.getX(), p.getY(), radio, tipo);
					}
				});
				ACLMessage msgres = new ACLMessage(ACLMessage.INFORM);
				msgres.setConversationId("alta");
				msgres.addReceiver(msg.getSender());
				try {
					msgres.setContentObject(new Point(p.getX(), p.getY(), radio));
				} catch (IOException e) { e.printStackTrace(); }
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
	
	/*
	 * Este comportamiento es el encargado de mostrar en el escenario la 
	 *   nueva posicion del sensor o del movil
	 */
	private class BEscuchaSensores extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		final MessageTemplate mtActualizaPos = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.MatchConversationId("posicionSensor"));

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mtActualizaPos);
			if (msg!= null) {  // Se ha recibido un mensaje de movimiento del sensor 
				               //    o del movil
				try {
					Point p = (Point) msg.getContentObject();
					final String agente = msg.getSender().getLocalName();
					//System.out.println("recibida nueva posicion de "+agente);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							canvas.mueveSensor(agente, p.getX(), p.getY(), 
									           p.getRadio(), 
									           msg.getInReplyTo().equals("Ayudado"));	
						}
							
					});
				} catch (UnreadableException e) { e.printStackTrace(); }
;

			} else block();
		}
	}
}
