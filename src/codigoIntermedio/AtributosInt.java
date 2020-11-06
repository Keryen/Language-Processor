package codigoIntermedio;

import java.util.LinkedList;
import java.util.List;

//import Temporales.GestorTemporales;

public class AtributosInt {

	private AtributosInt aux; // auxiliar usado en caso de que necesitemos un valor lugar para mas tarde

	private int lugar; 		// lugar en la tabla de simbolos o en la tabla de temporales.
	private LinkedList<Integer> lugares; 	// para el caso de concatenacion
	private int cont;		// contador para la concatenacion

	// etiquetas que usamos en el intermedio, se puede optimizar pero lo dejo asi
	// para no liar el intermedio
	private String inicio;
	private String salida;
	private String verdad;
	private String falso;

	// booleano usado para saber si esta en tabla de simbolos o en temporal
	private boolean enGlobal;
	private boolean isModified;
	// se podria hacer el constructor con tipo cadena entero y demas... pero no se
	// si hace falta ya que podemos hacer lo que nos salga del conio
	public AtributosInt() {
		enGlobal = false;
		isModified = false; 
		lugares = new LinkedList<Integer>();
	}

	public void aniadirLugares(int elem) {
		lugares.addLast(elem);
	}

	public int getLugar() {
		if(aux == null) 
			return this.lugar;
		else 
			return aux.getLugar();
	}

	public void setLugar(int lugar, boolean enGlobal) {
		this.enGlobal = enGlobal; 
		this.lugar = lugar;
	}

	public void setAux(AtributosInt aux) {
		this.aux = aux;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public List<Integer> getLugares() {
		return lugares;
	}

	public void setLugares(List<Integer> lugares) {
		this.lugares = (LinkedList<Integer>) lugares;
	}

	public boolean isEnGlobal() {
		return enGlobal;
	}

	public void setEnGlobal(boolean enGlobal) {
		this.enGlobal = enGlobal;
	}
	
	public boolean isModified() {
		return this.isModified;
	}
	
	public void setModified(boolean modified) {
		this.isModified = modified;
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
	}

	public String getVerdad() {
		return verdad;
	}

	public void setVerdad(String verdad) {
		this.verdad = verdad;
	}

	public String getFalso() {
		return falso;
	}

	public void setFalso(String falso) {
		this.falso = falso;
	}

}
