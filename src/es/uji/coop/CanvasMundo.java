package es.uji.coop;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CanvasMundo extends JFrame {

	private static final long serialVersionUID = 1L;

	private PanelRadar contentPane;


	private String agenteInterfaz;
	public static int MAXMUNDOX, MAXMUNDOY;
	
	public CanvasMundo (String agenteInterfaz, int maxX, int maxY) {
		super();
		MAXMUNDOX = maxX;
		MAXMUNDOY = maxY;
		this.agenteInterfaz = agenteInterfaz;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Agente Interfaz: "+ this.agenteInterfaz);
		setBounds(10, 10, MAXMUNDOX, MAXMUNDOY);
		contentPane = new PanelRadar();
		setContentPane(contentPane);
		// Muestra el frame
		setVisible(true);

	}
	
	public void incluyeSensor(String agente, int x, int y, int radio) {
		contentPane.incluyeSensor(agente, x, y, radio);
	}
	
	public void bajaSensor(String agente) {
		contentPane.bajaSensor(agente);		
	}
	
	public void mueveSensor(String agente, int x, int y) {
		contentPane.mueveSensor(agente, x, y);
	}

	// pra cambiar el agente sensor que gestiona un agente movil
	public void modGestorMovil(String agsensor, String codigo) {
	    contentPane.modGestorMovil(agsensor, codigo, agenteInterfaz);;
	}
	
	
	public class PanelRadar extends JPanel{

		private static final long serialVersionUID = 1L;
		private ArrayList<Sensor> posicionesSensores;
		private Image fondo;
		private Image barquito;
		private ImageIcon imagenMar = 
				new ImageIcon(getClass().getResource("mar.jpg"));
		private ImageIcon imagenBarco = 
				new ImageIcon(getClass().getResource("Boat.png"));
		int a = 5;
		
		
		public PanelRadar() {
			posicionesSensores = new ArrayList<Sensor>();
			Image img = imagenBarco.getImage();
			Image resizedImg = img.getScaledInstance(40, 50, java.awt.Image.SCALE_SMOOTH);
			imagenBarco = new ImageIcon(resizedImg);
			barquito = imagenBarco.getImage();
			fondo = imagenMar.getImage();

			this.setBorder(new EmptyBorder(1, 1, 1, 1));
			this.setDoubleBuffered(true);
			this.setLayout(null);
		}
		
		public void bajaSensor(String agente) {
			for(int i = 0; i < posicionesSensores.size(); i++) 
				if (agente.equals(posicionesSensores.get(i).getAgente())) {
					posicionesSensores.remove(i);
					break;
				}
		    repaint();		
		}
		
		public void incluyeSensor(String agente, int x, int y, int radio){
			posicionesSensores.add(new Sensor(agente, x, y, radio));
			repaint();
		}
		
		public void mueveSensor(String agente, int x, int y) {
			for(Sensor s: posicionesSensores) 
				if (agente.equals(s.getAgente())) {
					s.setX(x);
					s.setY(y);
					break;
				}
		    repaint();
		}
		
		public void modGestorMovil(String agsensor, String codigo, String agenteInterfaz) {
//			for(Movil m: posicionesMoviles) 
//				if (codigo.equals(m.getCodigo())) {
//					m.setSensor(recuperaSensor(agsensor, agenteInterfaz));
//					break;
//				}
		    repaint();
		}
		
		public Sensor recuperaSensor(String agsensor, String agenteInterfaz) {
			for (Sensor s : posicionesSensores) {
				if (s.getAgente().equals(agsensor))
					return s;
			}
			// Si has salido es porque le corresponde al agente Interfaz
			return new Sensor(agenteInterfaz);
		}
		
		public void paint(Graphics gi) {

			Graphics2D g = (Graphics2D) gi;

			// Dibuja el fondo
			g.drawImage(fondo, 0, 0, this);
			// Dibuja el resto de objetos de la escena
/*			for (Movil m : posicionesMoviles) {
				g.setColor(m.getSensor().getColor());
				// Dibuja un pez (mas o menos)
				g.fillOval(m.getX()-5, m.getY()-5, 10, 10); 
				g.fillArc(m.getX()-10, m.getY()-5, 10, 10, -140, -90);
				g.drawString(m.getSensor().getAgente(), m.getX()-5, m.getY()+15);
			}*/
			Font fplain = g.getFont();
			g.setFont(new Font("default", Font.BOLD, fplain.getSize()));
			for (Sensor s : posicionesSensores) {
				g.setColor(s.getColor());
				// Tralada las coordenadas para que x,y
			    //   queden en el centro de la imagen del barco
				int trX = imagenBarco.getIconWidth()/2;
				int trY = imagenBarco.getIconHeight()/2;
				g.drawImage(barquito, s.getX() - trX, s.getY() - trY, 
						trX*2, trY*2, null);
				g.drawString(s.getAgente(), s.getX(), s.getY() + trY + 15);
				// Dibuja el circulo de influencia
				int r = s.getRadio();							
				g.drawOval(s.getX()-r, s.getY()-r, r*2, r*2);   
			}
			g.setFont(new Font("default", Font.PLAIN, fplain.getSize()));
		}
	}
	
	
	public static class Sensor {
		int x;
		int y;
		int radio;
		Color color;
		final static private Color[] colores = 
			{Color.RED, Color.WHITE, Color.GREEN, Color.PINK, Color.CYAN, 
			 Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.BLUE};
		static int pos = 0;
		String agente;	
		
		public Color getColor() {
			return color;
		}
		public void setAgente(String agsensor) {
			this.agente = agsensor;		
		}
		public String getAgente() {
			return agente;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getRadio() {
			return radio;
		}

		public Sensor(String agente, int x, int y, int radio) {
			super();
			this.x = x;
			this.y = y;
			this.radio = radio;
			this.agente = agente;
			this.color = colores[pos%colores.length];
			pos++;
		}
		
		// Este solo se invoca una vez por AgInterfaz
		public Sensor(String agente) {
			super();
			this.x = 1;
			this.y = 1;
			this.radio = (MAXMUNDOX > MAXMUNDOY)? MAXMUNDOX: MAXMUNDOY;
			this.agente = agente;
			this.color = Color.BLACK;
		}
	}

}
