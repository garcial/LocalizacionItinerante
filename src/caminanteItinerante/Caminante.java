package caminanteItinerante;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Caminante extends JFrame {
	
	PanelRadar contentPane;
	Mapa mapa;
	static Movil[] moviles;
	static int tc = 14;
	static Random random = new Random();

	public Caminante(Mapa mapa) {
		super();
		this.mapa = mapa;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Cruces");
		setBounds(10, 10, 
				  mapa.filas[0].length()*tc+10, 
				  mapa.filas.length*tc+10);
		contentPane = new PanelRadar();
		setContentPane(contentPane);
		// Muestra el frame
		setVisible(true);
	}

	public static void main(String[] args) {

		Mapa mapa = new Mapa("mapa.txt", tc, random);
		Caminante cam = new Caminante(mapa);
		moviles = new Movil[5];
/*		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				cam = new Caminante(mapa);	
			}			
		});*/
		System.out.println("A moverse");
		for (int i = 0; i < moviles.length; i++) {
			moviles[i] = new Movil(random, mapa);
		}
//		System.out.print("posicion inicial: " + p);
		while(true){
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < moviles.length; i++) {
				cam.contentPane.muestra();
				mapa.Avanza(moviles[i].p, moviles[i]);
			}
//			System.out.println("posicion siguiente: "+p);
		}

	}

	public class PanelRadar extends JPanel{

		private static final long serialVersionUID = 1L;
		
		
		public PanelRadar() {

			this.setBorder(new EmptyBorder(1, 1, 1, 1));
			this.setDoubleBuffered(true);
			this.setLayout(null);
		}
		
		
		public void muestra() {
			repaint();			
		}

		@Override
		public void paintComponent(Graphics gi) {

			super.paintComponent(gi);
			Graphics2D g = (Graphics2D) gi;
			g.setColor(Color.LIGHT_GRAY);
			for(int i = 0; i<mapa.filas.length;i++)
				for(int j = 0; j<mapa.filas[0].length();j++)
					if (mapa.mapaEntero[i][j] == 1)
						g.fillRect(j*tc, i*tc, tc, tc);
			g.setColor(Color.BLUE);
			for (int i = 0; i < moviles.length; i++) {
				g.fillOval(moviles[i].p.getX(), moviles[i].p.getY(), tc/2, tc/2);
			}
		}
	}
}
