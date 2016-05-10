package es.uji.coop;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CanvasMundo extends JFrame {

	private static final long serialVersionUID = 1L;

	private PanelRadar contentPane;


	private String agenteInterfaz;
//	public static int MAXMUNDOX, MAXMUNDOY;
//	private int tc;
//	private int nFilas, nColumnas;
//	private String[] filas;
//	private int[][] mapaEntero;
	
	public CanvasMundo (String agenteInterfaz, String pathFicheroMapa, 
			            int MAXMUNDOX, int MAXMUNDOY) {
		super();
//		leerFichero(pathFicheroMapa);
		this.agenteInterfaz = agenteInterfaz;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Agente Interfaz: "+ this.agenteInterfaz);
		setBounds(10, 10, MAXMUNDOX, MAXMUNDOY);
		contentPane = new PanelRadar();
		setContentPane(contentPane);
		// Muestra el frame
		setVisible(true);

	}
	
//	private void leerFichero(String pathFicheroMapa) {
//		try{
//		      FileReader f = new FileReader(pathFicheroMapa);
//		      BufferedReader b = new BufferedReader(f);
//		      tc = Integer.parseInt(b.readLine());
//		      nFilas = Integer.parseInt(b.readLine());
//		      nColumnas = Integer.parseInt(b.readLine());
//		      MAXMUNDOX = nColumnas * tc;
//		      MAXMUNDOY = nFilas * tc;
//		      filas = new String[nFilas];
//		      b.close();
//		      f = new FileReader(pathFicheroMapa);
//		      b = new BufferedReader(f);
//		      int i=0;
//		      while(i<nFilas && (filas[i] = b.readLine())!=null) {
//		         i++;
//		      }
//		      b.close();
//			  mapaEntero = new int[nFilas][nColumnas];
//			} catch (Exception e){
//				System.out.println("problemas al leer el mapa del fuchero");
//			}	
//
//			for (int i = 0; i < nFilas; i++) {
//				for (int j = 0; j < nColumnas; j++) {
//					mapaEntero[i][j] = (int) (filas[i].charAt(j)) - 48;
//					System.out.print(mapaEntero[i][j]);
//				}
//				System.out.println();			
//			}		
//	}

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
		private Image fondo;
		private ImageIcon imagenMar = 
				new ImageIcon(getClass().getResource("mar.png"));
		Boolean ayuda;
		
		
		public PanelRadar() {
			posicionesSensores = new ArrayList<Sensor>();
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
		
		public void paint(Graphics gi) {

			Graphics2D g = (Graphics2D) gi;

			// Dibuja el fondo
			g.drawImage(fondo, 0, 0, this);
			Font fplain = g.getFont();
			g.setFont(new Font("default", Font.BOLD, fplain.getSize()));
			for (Sensor s : posicionesSensores) {
				g.setColor(s.getColor());
				// según sea el sensor dibuja un cuadrado, círculo o circunferencia
				if (s.getTipo().equals("fijo")) 
					g.fillRect(s.getX() -5, s.getY()-5, 10, 10);
				else if (s.getTipo().equals("medio")) 
					g.fillOval(s.getX(), s.getY(), 10, 10);
				else g.drawOval(s.getX(), s.getY(), 10, 10);
				g.drawString(s.getAgente(), s.getX()-5, s.getY()+23);
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
