package es.uji.coop.mapa;

public class Point {

	public Point(int nuevaX, int nuevaY, double radio) {
		x = nuevaX;
		y = nuevaY;
		this.radio = radio;
	}
	public synchronized int getX() {
		return x;
	}
	public synchronized void setX(int x) {
		this.x = x;
	}
	public synchronized int getY() {
		return y;
	}
	public synchronized void setY(int y) {
		this.y = y;
	}
	public synchronized double getRadio() {
		return radio;
	}
	public synchronized void setRadio(int radio) {
		this.radio = radio;
	}
	private int x;
	private int y;
	private double radio;

	
}
