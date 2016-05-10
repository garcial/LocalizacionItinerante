package es.uji.coop.mapa;

import java.util.Random;

import es.uji.coop.mapa.Point;

public class CaminarAleatorio {
	
	int[][] mapaEntero;
	Random random;
	int tc;
	
	public CaminarAleatorio(int[][] mapaEntero, Random random, int tc){
		this.mapaEntero = mapaEntero;
		this.random = random;
		this.tc = tc;
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
}
