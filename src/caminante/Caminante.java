package caminante;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Caminante extends JFrame {
	
	PanelRadar contentPane;
	Mapa mapa;
	static Point p1, p2, p3, p4;
	static int tc = 10;

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

		Mapa mapa = new Mapa("/Users/luisamable/Desktop/mapa.txt", tc);
		Caminante cam = new Caminante(mapa);
/*		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				cam = new Caminante(mapa);	
			}			
		});*/
		System.out.println("A moverse");
		p1 = mapa.PosicionInicial();
		p2 = mapa.PosicionInicial();
		p3 = mapa.PosicionInicial();
		p4 = mapa.PosicionInicial();
//		System.out.print("posicion inicial: " + p);
		while(true){
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cam.contentPane.muestra();
			mapa.Avanza(p1);
			mapa.Avanza(p2);
			mapa.Avanza(p3);
			mapa.Avanza(p4);
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


		public void paint(Graphics gi) {

			Graphics2D g = (Graphics2D) gi;
			// Dibuja el fondo
			//g.drawImage(fondo, 0, 0, this);
			//Font fplain = g.getFont();
			g.setColor(Color.LIGHT_GRAY);
			//g.setFont(new Font("default", Font.BOLD, fplain.getSize()));
			for(int i = 0; i<mapa.filas.length;i++)
				for(int j = 0; j<mapa.filas[0].length();j++)
					if (mapa.mapaEntero[i][j] == 1)
						g.fillRect(j*tc, i*tc, tc, tc);
			g.setColor(Color.BLUE);
			g.fillOval(p1.getX(), p1.getY(), 5, 5);
			g.fillOval(p2.getX(), p2.getY(), 5, 5);
			g.fillOval(p3.getX(), p3.getY(), 5, 5);
			g.fillOval(p4.getX(), p4.getY(), 5, 5);
			//g.setFont(new Font("default", Font.PLAIN, fplain.getSize()));
		}
	}
}
