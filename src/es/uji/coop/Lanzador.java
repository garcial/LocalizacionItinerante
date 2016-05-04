package es.uji.coop;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


/**
 * 
 * @author luisamable
 *  Esta es la clase Lanzador que permite ejecutar la plataforma 
 *  Jade directamente desde codigo
 *  Se basa en el codigo ejemplo propuesto por Cedric Herpson 
 *  y que se puede encontrar en la siguiente direccion web:  
 *  Notas (muy breves)
 *  http://herpsonc.eu/docs/README_HowToStartJadeFromSourceCode.pdf 
 *  Codigo fuente
 *  http://herpsonc.eu/docs/ y seleccionar el fichero: 
 *  HowToStartJadeFromSourceCode
 */
public class Lanzador {

	private static String hostname = "127.0.0.1"; 
	private static List<AgentController> agentList;// agents's ref
	private static Runtime rt;	
	private static ContainerController mainContainer;
	
	public static void main(String[] args) {
		// Crea el Gui para introducir los parametros de la ejecucion
		new Lanzador().new GuiInicial();
	}

	/**********************************************
	 * 
	 * Methods used to create an empty platform
	 * 
	 **********************************************/

	/**
	 * Create an empty platform composed of 1 main container and 3 containers.
	 * 
	 * @return a ref to the platform and update the containerList
	 */
	private static Runtime emptyPlatform(boolean RMA){

		rt = Runtime.instance();

		// 1) create a platform (main container+DF+AMS)
		Profile pMain = new ProfileImpl(hostname, 8888, null);
		System.out.println("Launching a main-container..." + pMain);
		//DF and AMS are included
		mainContainer = rt.createMainContainer(pMain); 
		// Si se ha seleccionado lanzar la ejecucion de RMA 
		//    se recibe un boolean indicandolo: RMA
		if (RMA) createRMA();

		System.out.println("Plaform ok");
		return rt;

	}

	/**
	 * Crea el agente RMA para monitorización/activación en 
	 * ejecucion de agentes en la plataforma
	 */

	private static void createRMA() {
		System.out.println("Launching the rma agent on the main container ...");
		AgentController rma;

		try {
			rma = mainContainer.createNewAgent(
					               "rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("Launching of rma agent failed");
		}
	}



	/**********************************************
	 * 
	 * Methods used to create the agents and to start them
	 * 
	 **********************************************/

	private static List<AgentController> createAgents(
			                               int nFijos, int nMedios, int nSimples) {
		System.out.println("Launching agents..." + nFijos + " Fijos; " + nMedios + 
				           " Medios; "+nSimples+" Sencillos.");
		String agentName;
		List<AgentController> agentList = new ArrayList<AgentController>();
		
		// Primero los agentes de la infraestructura
		
	    try {
		    AgentController	ag=mainContainer.createNewAgent("Agente Escenario",
		    		                                     AgInterfaz.class.getName(),
		    		                                     null);
		    agentList.add(ag);
		    System.out.println("Agente Interfaz Escenario launched");
		    ag=mainContainer.createNewAgent("Agente Sensor", 
		    		                        AgSensor.class.getName(),
		    		                        null);
		    agentList.add(ag);
		    System.out.println("Agente Sensor launched");
	    } catch (StaleProxyException e) { e.printStackTrace(); }
	    
		//Luego los agentes fijos
		
	    Object[] args = new Object[] {"fijo"};
		for(int i = 1; i<= nFijos; i++) {
			agentName="F"+i;		
		    try {
			    AgentController	ag=mainContainer.createNewAgent(agentName, 
			    		                                  AgActivo.class.getName(),
			    		                                  args);
			    agentList.add(ag);
			    System.out.println(agentName+" launched");
		    } catch (StaleProxyException e) { e.printStackTrace(); }
		}
	
		//Ahora los agentes medios

		args = new Object[] {"medio"};
		for(int i = 1; i<= nMedios; i++) {
			agentName="M"+i;	
		    try {
			    AgentController	ag=mainContainer.createNewAgent(agentName,
			    		                                  AgActivo.class.getName(),
			    		                                  args);
			    agentList.add(ag);
			    System.out.println(agentName+" launched");
		    } catch (StaleProxyException e) { e.printStackTrace(); }
		}
		
		//Por ultimo los agentes simples

		args = new Object[] {"simple"};
		for(int i = 1; i<= nSimples; i++) {
			agentName="S"+i;		
		    try {
			    AgentController	ag = mainContainer.createNewAgent(agentName, 
			    		                                    AgActivo.class.getName(),
			    		                                    args);
			    agentList.add(ag);
			    System.out.println(agentName+" launched");
		    } catch (StaleProxyException e) { e.printStackTrace(); }
		}
		
	    return agentList;
	}
	
	/**
	 * Start the agents
	 * @param agentList
	 */
	private static void startAgents(List<AgentController> agentList){

		System.out.println("Starting agents...");

		for(final AgentController ac: agentList){
			try {
				ac.start();
			} catch (StaleProxyException e) { e.printStackTrace(); }
		}
		System.out.println("Agents started...");
	}

	private class GuiInicial extends JFrame {
		
		private static final long serialVersionUID = 1L;
		//private JPanel contentPane;
		JSlider sliderFijos;
		JSlider sliderMedios;
		JSlider sliderSimples;
		JButton botonLanzar;
		
		public GuiInicial(){
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("Parametros de ejecucion: Localizacion compartida");
			Container container = this.getContentPane();
	        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
	        setBounds(10, 10, 400, 800);
	        setContentPane(container);

	        JLabel labelFijos = new JLabel("Numero de sensores fijos");
	        labelFijos.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(labelFijos);
			sliderFijos = new JSlider(JSlider.HORIZONTAL, 1, 20, 1);
			sliderFijos.setMajorTickSpacing(10);
			sliderFijos.setMinorTickSpacing(1);
			sliderFijos.setPaintTicks(true);
			sliderFijos.setPaintLabels(true);
	        sliderFijos.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(sliderFijos);

	        JLabel labelMedios = new JLabel("Numero de sensores medios");
	        labelMedios.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(labelMedios);
			sliderMedios = new JSlider(JSlider.HORIZONTAL, 1, 50, 3);
			sliderMedios.setMajorTickSpacing(10);
			sliderMedios.setMinorTickSpacing(1);
			sliderMedios.setPaintTicks(true);
			sliderMedios.setPaintLabels(true);
	        sliderMedios.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(sliderMedios);

	        JLabel labelSimples = new JLabel("Numero de sensores sencillos");
	        labelSimples.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(labelSimples);
			sliderSimples = new JSlider(JSlider.HORIZONTAL, 3, 200, 5);
			sliderSimples.setMajorTickSpacing(50);
			sliderSimples.setMinorTickSpacing(1);
			sliderSimples.setPaintTicks(true);
			sliderSimples.setPaintLabels(true);
			sliderSimples.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(sliderSimples);

	        JCheckBox checkRMA = new JCheckBox("Activar RMA");
	        checkRMA.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(checkRMA);
	        
			botonLanzar = new JButton("Lanzar ejecucion");
			botonLanzar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					rt = emptyPlatform(checkRMA.isSelected());
					agentList = createAgents(sliderFijos.getValue(), 
							                 sliderMedios.getValue(), 
							                 sliderSimples.getValue());
     				startAgents(agentList);
				}
			});
	        botonLanzar.setAlignmentX(Component.CENTER_ALIGNMENT);
	        container.add(botonLanzar);
	        this.pack();
			// Muestra el frame
			this.setVisible(true);
		}
		
	}
}
