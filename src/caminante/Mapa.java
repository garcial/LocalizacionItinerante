package caminante;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Mapa {


	String[] filas;
	public int[][] mapaEntero;
	Random random = new Random();;
	private int sentidoX = random.nextBoolean()?1:-1,
                sentidoY = random.nextBoolean()?1:-1;
	int tc;
	
	public Mapa(String archivo, int tc) {
		try{
	      FileReader f = new FileReader(archivo);
	      BufferedReader b = new BufferedReader(f);
	      long lineas = b.lines().count();
	      filas = new String[(int)lineas];
	      b.close();
	      f = new FileReader(archivo);
	      b = new BufferedReader(f);
	      int i=0;
	      while(i<lineas && (filas[i] = b.readLine())!=null) {
	         i++;
	      }
	      b.close();
		  mapaEntero = new int[(int)lineas][filas[0].length()];
		} catch (Exception e){
			System.out.println("problemas al leer el mapa del fuchero");
		}	
		this.tc = tc;
		for (int i = 0; i < filas.length; i++) {
			for (int j = 0; j < filas[i].length(); j++) {
				mapaEntero[i][j] = (int) (filas[i].charAt(j)) - 48;
				System.out.print(mapaEntero[i][j]);
			}
			System.out.println();			
		}
	}
	
	public Point PosicionInicial() {
		int i, j;
		while (true) {
			i = random.nextInt(filas.length);
			j = random.nextInt(filas[0].length());
			if (mapaEntero[i][j] == 0) 
				return new Point(j*tc+tc/2, i*tc+tc/2);
		}
	}
	
	// Flata asegurar que no nos salimos del rando de la matriz ni del mundo
	public void Avanza(Point p) {
		int posMX = p.getX()/tc;
		int posYM = p.getY()/tc;
		Point nuevo = new Point(p.getX(), p.getY());
		p.setX(p.getX() + 2*sentidoX);
		p.setY(p.getY() + 2*sentidoY);
		int nposXM = p.getX()/tc;
		int nposYM = p.getY()/tc;
		// Si estas dentro de los limites del escenario
		if (nposXM >= 0 && nposXM < filas[0].length() &&
			nposYM >= 0 && nposYM < filas.length) {
			// Si has cambiado de casilla mira si es válida
			if (nposXM != posMX || nposYM != posYM) {
				if (mapaEntero[nposYM][nposXM] == 0)
					return;
			    else {
					p.setX(nuevo.getX()); 
					p.setY(nuevo.getY());
					sentidoX = random.nextBoolean()?1:-1;
			        sentidoY = random.nextBoolean()?1:-1;
			        return;
				}
			} else return; // En otro caso la nueva posicion es OK.
		} else {
			p.setX(nuevo.getX()); 
			p.setY(nuevo.getY());
			sentidoX = random.nextBoolean()?1:-1;
	        sentidoY = random.nextBoolean()?1:-1;
	        return;
		}
		
	}
	public void Avanza2(Point p) {
		p.setX(25);
		p.setY(25);;
	}
	

}
