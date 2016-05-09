package caminanteItinerante;

import java.util.Random;

public class Movil {
	public Point p;
	public Random random;
	public Direccion dir;
	public Movil(Random r, Mapa map){
		random = r;
		p = map.PosicionInicial();
        dir = Direccion.getRandomDireccion();
	}
}