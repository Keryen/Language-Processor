package codigoIntermedio;

import java.io.IOException;

import analizadorLexico.Pair;
import gestorTablas.GestorTablas;

public class GestorTemporales {
	
	private GestorTablas gestorTablas;
	private int contador;
	
	
	public GestorTemporales(GestorTablas gestorTablas) {
		contador = 0;
		this.gestorTablas = gestorTablas;
	}
	
	// aniade a tabla de simbolos, y habria que ver si seria en local o en global
	// buscarPosLexemaTS es la funcion a la que hay que llamar para la pos de la tabla de simbolos
	// nos va a devolver un par, que es la pos y la tabla en la que se encuentra.
	public Pair<Integer,Integer> newTemporal(String tipo) throws IOException {
		Pair<Integer,Integer> par = gestorTablas.buscarPosLexemaTS("temp"+contador++, 500);
		gestorTablas.aniadirTipoTS(tipo, false);
		gestorTablas.aniadirDesplazamientoTS();
		par.setLeft(gestorTablas.getDesplazamiento().getLeft());
		switch(tipo) {
			case "entero":
				gestorTablas.sumarDesplazamientoTS(1);
				break;
			case "cadena":
				gestorTablas.sumarDesplazamientoTS(1); // es uno porque son punteros
				break;
			case "logico":
				gestorTablas.sumarDesplazamientoTS(1);
				break;
		}
		return par;
	}

}
