package es.uji.coop.mapa;

public class Movil {
	public Point p;
	public Direccion dir;
	public Movil() {
        dir = Direccion.getRandomDireccion();
	}
}