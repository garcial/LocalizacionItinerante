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
				return new Point(j*tc+tc/2, i*tc+tc/2);
		}
	}
	
	// Flata asegurar que no nos salimos del rando de la matriz ni del mundo
	public void Avanza(Point p, Movil m) {
		int posXM = p.getX()/tc;  // Casilla actual del mapa
		int posYM = p.getY()/tc;
		Point nuevo = new Point(p.getX(), p.getY());

		while (true) {			
			// Avanza un paso segun sea tu direccion actual
			switch (m.dir) {
			case NORTE:
				p.setY(p.getY() + 2);
				break;
			case NORESTE:
				p.setX(p.getX() + random.nextInt(2) + 1);
				p.setY(p.getY() - random.nextInt(2) + 1);
				break;
			case ESTE:
				p.setX(p.getX() + 2);
				break;
			case SURESTE:
				p.setX(p.getX() + random.nextInt(2) + 1);
				p.setY(p.getY() + random.nextInt(2) + 1);
				break;
			case SUR:
				p.setY(p.getY() - 2);
				break;
			case SUROESTE:
				p.setX(p.getX() - random.nextInt(2) + 1);
				p.setY(p.getY() + random.nextInt(2) + 1);
				break;
			case OESTE:
				p.setX(p.getX() - 2);
				break;
			case NOROESTE:
				p.setX(p.getX() - random.nextInt(2) + 1);
				p.setY(p.getY() - random.nextInt(2) + 1);
				break;
			}
			int nposXM = (p.getX() +
					       ((m.dir == Direccion.NORESTE ||
					         m.dir == Direccion.ESTE ||
					         m.dir == Direccion.SURESTE)? tc/2:0))/tc;
			int nposYM = (p.getY() +
					       ((m.dir == Direccion.SUR ||
	                 	     m.dir == Direccion.SURESTE ||
	                 		 m.dir == Direccion.SUROESTE)? tc/2:0))/tc;
			// Si estas dentro de los limites del escenario
			if (nposXM >= 0 && nposXM < filas[0].length() &&
				nposYM >= 0 && nposYM < filas.length) {
				// Si has cambiado de casilla mira si es valida
				if (nposXM != posXM || nposYM != posYM) {
				   if (mapaEntero[nposYM][nposXM] == 0) {
					   System.out.println("pos("+posXM+","+posYM+"(; npos("+nposXM+","+nposYM+")");
					   System.out.println("Posicion debe ser 0: " +mapaEntero[nposYM][nposXM]);
						return;
				   }
				   else { // Si hay un muro, vuelve al valor inicial y
					      //    cambia de dirección
						p.setX(nuevo.getX()); 
						p.setY(nuevo.getY());
						m.dir = Direccion.nextDireccion(m.dir);
				   }
				} else {
					if (mapaEntero[nposYM][nposXM] == 0) {
					   System.out.println("No me cambio de cuadrícula");
					   System.out.println("pos("+posXM+","+posYM+"(; npos("+nposXM+","+nposYM+")");
					   System.out.println("Posicion debe ser 0: " +mapaEntero[nposYM][nposXM]);
					   return; // En otro caso la nueva posicion es OK.
					} else {
						p.setX(nuevo.getX()); 
						p.setY(nuevo.getY());
						m.dir = Direccion.nextDireccion(m.dir);
					}
				}
			} else { // Nos hemos salido del escenario, busca otra nueva dirección
				     //   desde la posición inicial
				p.setX(nuevo.getX()); 
				p.setY(nuevo.getY());
				m.dir = Direccion.nextDireccion(m.dir);
			}		
		}		
	}
	
}
