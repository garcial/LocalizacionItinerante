package es.uji.coop.mapa;

public class PruebaAvanza {

	public static void main(String[] args) {
		Point p = new Point(10, 20, 50);
		Mapa mapa = new Mapa();
		mapa.Avanza(p);
		System.out.println("El nuevo punto es: " + p.getX() + ", "+ p.getY()+ ", "+p.getRadio());
		String cont="x=5y=7radio=10.2";
		System.out.println(Integer.parseInt(cont.substring(cont.indexOf("x=")+2,cont.indexOf("y="))));
		System.out.println(Integer.parseInt(cont.substring(cont.indexOf("y=")+ 2,cont.indexOf("radio="))));
		System.out.println(Double.parseDouble(cont.substring(cont.indexOf("radio=")+ 6)));

	}

}
