package es.uji.coop;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CanvasMundo extends JFrame {

	private static final long serialVersionUID = 1L;

	private PanelRadar contentPane;
	private String agenteInterfaz;
	public int[][] mapaEntero;
	int tc;

	
	public CanvasMundo (String agenteInterfaz, int tc,
			            int MAXMUNDOX, int MAXMUNDOY, int[][] mapaEntero) {
		super();
		this.mapaEntero = mapaEntero;
		this.tc = tc;
		this.agenteInterfaz = agenteInterfaz;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Agente Interfaz: "+ this.agenteInterfaz);
		setBounds(10, 10, MAXMUNDOX, MAXMUNDOY);
		contentPane = new PanelRadar();
		setContentPane(contentPane);
		// Muestra el frame
		setVisible(true);

	}


	public void incluyeSensor(String agente, int x, int y, double radio, String tipo) {
		contentPane.incluyeSensor(agente, x, y, radio, tipo);
	}
	
	public void bajaSensor(String agente) {
		contentPane.bajaSensor(agente);		
	}
	
	public void mueveSensor(String agente, int x, int y, double radio, boolean ayuda) {
		contentPane.mueveSensor(agente, x, y, radio, ayuda);
	}

	public class PanelRadar extends JPanel{

		private static final long serialVersionUID = 1L;
		private ArrayList<Sensor> posicionesSensores;
		Boolean ayuda;
		
		
		public PanelRadar() {
			posicionesSensores = new ArrayList<Sensor>();

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
		
		public synchronized void incluyeSensor(String agente, int x, int y, double radio, 
				                  String tipo){
			posicionesSensores.add(new Sensor(agente, x, y, radio, tipo));
			repaint();
		}
		
		// Comprobada ok
		public  synchronized void mueveSensor(String agente, int x, int y, double radio,
				boolean ayuda) {
			for(Sensor s: posicionesSensores) 
				if (agente.equals(s.getAgente())) {
					s.setX(x);
					s.setY(y);
					s.setRadio(radio);
					s.ayuda = ayuda;
					break;
				}
		    repaint();
		}
		
		public void paintComponent(Graphics gi) {

			super.paintComponent(gi);
			Graphics2D g = (Graphics2D) gi;

			// Dibuja el fondo
			g.setColor(Color.LIGHT_GRAY);
			for(int i = 0; i<mapaEntero.length;i++)
				for(int j = 0; j<mapaEntero[0].length;j++)
					if (mapaEntero[i][j] == 1)
						g.fillRect(j*tc, i*tc, tc, tc);
		    
			Font fplain = g.getFont();
			g.setFont(new Font("default", Font.BOLD, fplain.getSize()));
			for (Sensor s : posicionesSensores) {
				g.setColor(s.getColor());
				// según sea el sensor dibuja un cuadrado, círculo o circunferencia
				if (s.getTipo().equals("fijo")) 
					g.fillRect(s.getX(), s.getY(), tc/2, tc/2);
				else if (s.getTipo().equals("medio")) 
					g.fillOval(s.getX(), s.getY(), tc/2, tc/2);
				else g.drawOval(s.getX(), s.getY(), tc/2, tc/2);
				g.drawString(s.getAgente(), s.getX(), s.getY()+tc/2+5);
				// Dibuja el circulo de influencia
				if (s.ayuda) g.setColor(Color.RED);
				int r = (int) Math.round(s.getRadio());							
				g.drawOval(s.getX()-r, s.getY()-r, r*2, r*2);   
			}
			g.setFont(new Font("default", Font.PLAIN, fplain.getSize()));
		}
	}
	
	
	public static class Sensor {
		int x;
		int y;
		double radio;
		Color color;
		String tipo;
		boolean ayuda;
		
		public synchronized String getTipo() {
			return tipo;
		}

		final static private Color[] colores = 
			{Color.WHITE, Color.GREEN, Color.PINK, Color.CYAN, 
			 Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.BLUE,};
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
		public double getRadio() {
			return radio;
		}
		public void setRadio(double radio){
			this.radio = radio;
		}

		public Sensor(String agente, int x, int y, double radio, String tipo) {
			super();
			this.x = x;
			this.y = y;
			this.radio = radio;
			this.agente = agente;
			this.tipo = tipo;
			this.color = colores[pos%colores.length];
			pos++;
		}
	}
}
