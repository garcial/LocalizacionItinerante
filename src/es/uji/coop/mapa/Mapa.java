package es.uji.coop.mapa;

import java.util.Random;

public class Mapa {
	
	public static final int MAXMUNDOX = 1620;
	public static final int MAXMUNDOY = 930;
	public Random rnd = new Random();;
	private int sentidoX = rnd.nextBoolean()?1:-1,
	            sentidoY = rnd.nextBoolean()?1:-1;
	private int pasoX = 2, pasoY = 2;
	private int cambioX = 5, cambioY = 10;
	private int contX = 0, contY = 0;

	public void Avanza(Point actual){
		if (contX < cambioX) contX++; 
		else {
			contX = 0; 
			cambioX = rnd.nextInt(9)+1;
			pasoX = rnd.nextInt(3)+1;
		}
		if (contY < cambioY) contY++; 
		else {
			contY = 0; 
			cambioY = rnd.nextInt(8)+1;
			pasoY = rnd.nextInt(3)+1;
		}
		int nuevaX = actual.getX() + sentidoX*pasoX; 
		int nuevaY = actual.getY() + sentidoY*pasoY; 
		if ((nuevaY > MAXMUNDOY-30) || (nuevaY < 50)) sentidoY = -sentidoY;
		if ((nuevaX > MAXMUNDOX-20) || (nuevaX < 20)) sentidoX = -sentidoX;
		actual.setX(nuevaX);
		actual.setY(nuevaY);
	}
}
