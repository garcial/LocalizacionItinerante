package es.uji.coop.mapa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import es.uji.coop.mapa.Point;

public class MapaFichero {
	
	public int getMAXMUNDOX() {
		return MAXMUNDOX;
	}

	public int getMAXMUNDOY() {
		return MAXMUNDOY;
	}

	public int getnFilas() {
		return nFilas;
	}

	public int getnColumnas() {
		return nColumnas;
	}

	public int[][] getMapaEntero() {
		return mapaEntero;
	}

	public int getTc() {
		return tc;
	}

	public Random getRandom() {
		return random;
	}
	
	int MAXMUNDOX;
	int MAXMUNDOY;
	int nFilas;
	int nColumnas;	
	int[][] mapaEntero;
	int tc;
	static Random random = new Random();

	
	public MapaFichero(String pathFicheroMapa){
		LeerFichero(pathFicheroMapa);
	}

	public Point PosicionInicial() {
		int i, j;
		while (true) {
			i = random.nextInt(mapaEntero.length);
			j = random.nextInt(mapaEntero[0].length);
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
			if (nXMSE >= 0 && nXMSE < mapaEntero[0].length &&
					nXMO >= 0 && nXMO < mapaEntero[0].length &&
					nYMSE >= 0 && nYMSE < mapaEntero.length &&
					nYMO >=0 && nYMO < mapaEntero.length) {
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
		
	public void LeerFichero(String pathFicheroMapa) {
		String[] filas = null;
		try{
			FileReader f = new FileReader(pathFicheroMapa);
			BufferedReader b = new BufferedReader(f);
			tc = Integer.parseInt(b.readLine());
			nFilas = Integer.parseInt(b.readLine());
			nColumnas = Integer.parseInt(b.readLine());
			MAXMUNDOX = nColumnas * tc;
			MAXMUNDOY = nFilas * tc;
			filas = new String[nFilas];
			int i=0;
			while(i<nFilas && (filas[i] = b.readLine())!=null) {
				i++;
			}
			b.close();
			mapaEntero = new int[nFilas][nColumnas];
		} catch (Exception e){
			System.out.println("Problemas al leer el mapa del fichero "+pathFicheroMapa);
			//					takeDown();
		}	

		for (int i = 0; i < nFilas; i++) {
			for (int j = 0; j < nColumnas; j++) {
				mapaEntero[i][j] = (int) (filas[i].charAt(j)) - 48;
//				System.out.print(mapaEntero[i][j]);
			}
//			System.out.println();			
		}		
	}
	}
