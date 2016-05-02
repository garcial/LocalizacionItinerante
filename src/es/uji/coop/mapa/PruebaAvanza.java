package es.uji.coop.mapa;

public class PruebaAvanza {

	public static void main(String[] args) {
		Point p = new Point(10, 20, 50);
		Mapa mapa = new Mapa();
		mapa.Avanza(p);
		System.out.println("El nuevo punto es: " + p.getX() + ", "+ p.getY()+ ", "+p.getRadio());

	}

}
