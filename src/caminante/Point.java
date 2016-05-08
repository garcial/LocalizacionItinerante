package caminante;

public class Point {
	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	int x;
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
	int y;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+getX()+","+ getY()+")";
	}
	
}
