package caminanteItinerante;

import java.util.Random;

public enum Direccion {
    NORTE, NORESTE, ESTE, SURESTE, SUR, SUROESTE, OESTE, NOROESTE;
	
	private static final Direccion[] VALORES = values();
	private static final int SIZE = VALORES.length;
	private static final Random random = new Random();
	
	public static Direccion getRandomDireccion() {
		return VALORES[random.nextInt(SIZE)];
	}
	public static Direccion nextDireccion(Direccion d){
		return VALORES[(d.ordinal() + 1) % SIZE];
	}
}
