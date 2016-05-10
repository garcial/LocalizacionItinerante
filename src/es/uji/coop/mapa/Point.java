package es.uji.coop.mapa;

import java.io.Serializable;

public class Point implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Point(int nuevaX, int nuevaY, double radio) {
		x = nuevaX;
		y = nuevaY;
		this.radio = radio;
	}
	
	public Point(int nuevaX, int nuevaY){
		x = nuevaX;
		y = nuevaY;
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

	@Override
	public String toString(){
		return " x= "+x+" y= "+y+" radio= "+radio;
	}
	
	public static double CalcularDistancia(Point p1, Point p2){
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + 
				(p1.getY()-p2.getY())*(p1.getY()-p2.getY()));
	}
	
}
