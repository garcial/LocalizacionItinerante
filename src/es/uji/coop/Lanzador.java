package es.uji.coop;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


/**
 * 
 * @author Luis Amable
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
public class Lanzador extends JFrame implements ChangeListener, ActionListener{

	private static String hostname = "127.0.0.1"; 
	private static List<AgentController> agentList;// agents's ref
	private static Runtime rt;	
	private static ContainerController mainContainer;
	
	public static void main(String[] args) {
		// Crea el Gui para introducir los parametros de la ejecucion
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Lanzador();
            }
        });
	}
	
	private static final long serialVersionUID = 1L;
	
	//private JPanel contentPane;
	JSlider sliderFijos;
	JSlider sliderMedios;
	JSlider sliderSimples;
	JLabel cantidadFijos;
	JLabel cantidadMedios;
	JLabel cantidadSimples;
	JButton botonLanzar;
	JFileChooser ficheroMapa;
	boolean hayFicheroMapa;
	String pathFicheroMapa;
	JFileChooser ficheroSalida;
	boolean hayFicheroSalida;
	String pathFicheroSalida;
    JRadioButton fichero;
    JRadioButton aleatorio;
    
    public Lanzador(){

    	hayFicheroMapa = hayFicheroSalida = false;
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setTitle("Localizacion compartida");
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	setContentPane(panel);

    	// Primer bloque del GUI
    	JPanel panel1 = new JPanel();
    	panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
    	JLabel mapa = new JLabel("Mapa");
    	panel1.add(mapa);
    	JPanel panel1_b = new JPanel();
    	panel1_b.setLayout(new BoxLayout(panel1_b, BoxLayout.Y_AXIS));
    	fichero = new JRadioButton("Fichero", false);
    	aleatorio = new JRadioButton("Aleatorio", true);
    	fichero.addActionListener(this);
    	aleatorio.addActionListener(this);
    	ButtonGroup grupo = new ButtonGroup();
    	grupo.add(fichero);
    	grupo.add(aleatorio);
    	panel1_b.add(fichero);
    	panel1_b.add(aleatorio);

    	panel1.add(panel1_b);
    	panel.add(panel1);

    	// Segundo bloque del GUI (Fijos, Medios, Simples)

    	cantidadFijos = new JLabel("Número de Sensores Fijos: 0");
    	cantidadFijos.setAlignmentX(Component.CENTER_ALIGNMENT);
    	sliderFijos = new JSlider(JSlider.HORIZONTAL, 0, 20, 0);
    	sliderFijos.addChangeListener(this);
    	sliderFijos.setMajorTickSpacing(10);
    	sliderFijos.setMinorTickSpacing(1);
    	sliderFijos.setPaintTicks(true);
    	sliderFijos.setPaintLabels(true);
    	sliderFijos.setAlignmentX(Component.CENTER_ALIGNMENT);
    	panel.add(cantidadFijos);
    	panel.add(sliderFijos);

    	cantidadMedios = new JLabel("Numero de Sensores Medios: 0");
    	cantidadMedios.setAlignmentX(Component.CENTER_ALIGNMENT);
    	sliderMedios = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
    	sliderMedios.addChangeListener(this);
    	sliderMedios.setMajorTickSpacing(10);
    	sliderMedios.setMinorTickSpacing(1);
    	sliderMedios.setPaintTicks(true);
    	sliderMedios.setPaintLabels(true);
    	sliderMedios.setAlignmentX(Component.CENTER_ALIGNMENT);
    	panel.add(cantidadMedios);
    	panel.add(sliderMedios);

    	cantidadSimples = new JLabel("Numero de Sensores Simples: 0");
    	cantidadSimples.setAlignmentX(Component.CENTER_ALIGNMENT);
    	sliderSimples = new JSlider(JSlider.HORIZONTAL, 0, 200, 0);
    	sliderSimples.addChangeListener(this);
    	sliderSimples.setMajorTickSpacing(50);
    	sliderSimples.setMinorTickSpacing(1);
    	sliderSimples.setPaintTicks(true);
    	sliderSimples.setPaintLabels(true);
    	sliderSimples.setAlignmentX(Component.CENTER_ALIGNMENT);
    	panel.add(cantidadSimples);
    	panel.add(sliderSimples);

    	JCheckBox checkRMA = new JCheckBox("Activar RMA");
    	checkRMA.setAlignmentX(Component.CENTER_ALIGNMENT);
    	panel.add(checkRMA);

    	JCheckBox salidaFichero = new JCheckBox("Salida a fichero");
    	salidaFichero.setAlignmentX(Component.CENTER_ALIGNMENT);
        salidaFichero.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox yo = (JCheckBox) e.getSource();
				if (yo.isSelected()) {
					hayFicheroSalida = true;
					ficheroSalida = new JFileChooser();
					int valor = ficheroSalida.showOpenDialog(yo);
					if(valor == JFileChooser.APPROVE_OPTION)
						pathFicheroSalida = ficheroSalida.getSelectedFile().getAbsolutePath();
					else { 
						yo.setSelected(false);
					    hayFicheroSalida = false;
					}					
				}
				
			}
		});
    	panel.add(salidaFichero);

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
    	panel.add(botonLanzar);
    	setSize(300, 350);
    	setResizable(false);
    	// this.pack(); Esto elimina el efecto del tamaño impuesto por setSize(,)
    	// Muestra el frame
    	this.setVisible(true);    	
    }

	@Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        int cuantos = (int)source.getValue();
        if (source.equals(sliderFijos)) 
        	cantidadFijos.setText("Numero de Sensores Fijos: " + cuantos);
        else if (source.equals(sliderMedios)) 
        	cantidadMedios.setText("Numero de Sensores Medios: " + cuantos);
        else cantidadSimples.setText("Numero de Sensores Simples: " + cuantos);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		JRadioButton radio = (JRadioButton)e.getSource();
		if (radio.isSelected()) {
			if (radio.equals(fichero)) {
				hayFicheroMapa = true;
				ficheroMapa = new JFileChooser();
				int valor = ficheroMapa.showOpenDialog(this);
				if(valor == JFileChooser.APPROVE_OPTION)
					pathFicheroMapa = ficheroMapa.getSelectedFile().getAbsolutePath();
				else { 
					aleatorio.setSelected(true);
				    hayFicheroMapa = false;
				}
			} else if (radio.equals(aleatorio)) hayFicheroMapa = false;
		}	
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

}
