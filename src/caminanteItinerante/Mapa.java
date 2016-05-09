package caminanteItinerante;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Mapa {


	String[] filas;
	public int[][] mapaEntero;
	int tc;
	Random random = new Random();
	
	public Mapa(String archivo, int tc, Random random) {
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
		this.random = random;
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
				return new Point(j*tc+tc/2-1, i*tc+tc/2-1);
		}
	}
	
	public void Avanza(Point p, Movil m) {

		Point nuevo = new Point(p.getX(), p.getY());

		while (true) {			
			// Avanza un paso segun sea tu direccion actual
			switch (m.dir) {
			case NORTE:
				p.setY(p.getY() + 2);
				p.setX(p.getX() + random.nextInt(3)-1);
				break;
			case NORESTE:
				p.setX(p.getX() + random.nextInt(3) + 1);
				p.setY(p.getY() - random.nextInt(3) + 1);
				break;
			case ESTE:
				p.setX(p.getX() + 2);
				p.setY(p.getY() + random.nextInt(3)-1);
				break;
			case SURESTE:
				p.setX(p.getX() + random.nextInt(3) + 1);
				p.setY(p.getY() + random.nextInt(3) + 1);
				break;
			case SUR:
				p.setY(p.getY() - 2);
				p.setX(p.getX() + random.nextInt(3)-1);
				break;
			case SUROESTE:
				p.setX(p.getX() - random.nextInt(3) + 1);
				p.setY(p.getY() + random.nextInt(3) + 1);
				break;
			case OESTE:
				p.setX(p.getX() - 2);
				p.setY(p.getY() + random.nextInt(3)-1);
				break;
			case NOROESTE:
				p.setX(p.getX() - random.nextInt(3) + 1);
				p.setY(p.getY() - random.nextInt(3) + 1);
				break;
			}
			// Comprobar los puntos (xO,yO), (xO+tc/2, yO), 
			//                      (xO, yO+tc/2) y (xO+tc/2, yO+tc/2)
			int nXMSE = (p.getX() + tc/2) / tc;
			int nYMSE = (p.getY() + tc/2) / tc;
			int nXMO = p.getX() / tc;
			int nYMO = p.getY() / tc;

			// No salirse del escenario
			if (nXMSE >= 0 && nXMSE < filas[0].length() &&
					nXMO >= 0 && nXMO < filas[0].length() &&
					nYMSE >= 0 && nYMSE < filas.length &&
					nYMO >=0 && nYMO < filas.length) {
				// Si toco algún muro hay que calcular otro paso con otra
				//   orientación
				if (mapaEntero[nYMSE][nXMSE] == 1 ||
						mapaEntero[nYMO][nXMO] == 1	||
						mapaEntero[nYMO][nXMSE] == 1 ||
						mapaEntero[nYMSE][nXMO] == 1) {
					p.setX(nuevo.getX()); 
					p.setY(nuevo.getY());
					m.dir = Direccion.nextDireccion(m.dir);

				} else return;	 // No invado ningún muro, el paso es correcto.	
			} else {
				// Si me he salido del escenario hay que calcular otro
				//   paso con otra orientación
				p.setX(nuevo.getX()); 
				p.setY(nuevo.getY());
				m.dir = Direccion.nextDireccion(m.dir);
			}
		}		
	}
	
}
