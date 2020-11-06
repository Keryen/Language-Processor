package analizadorSemantico;

import analizadorLexico.Pair;
import codigoIntermedio.AtributosInt;

public class AtributosSem {
	
	private int valorEntero;
	private String valorCadena; // aniadidos los dos a partir del generador de codigo intermedio, funciona

	private String tipo;
	private int tamanio;
	private String ret;
	
	private String lexema;
	private int posLexema;
	private int nlin;
	
	//aniadido
	private AtributosInt temporal;
	private Pair<String, Integer> par;

	public AtributosSem() {
		this.temporal = new AtributosInt();
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public int getTamanio() {
		return tamanio;
	}
	
	public String getRet() {
		return ret;
	}
	
	public String getLexema() {
		return lexema;
	}
	
	public int getPosLexema() {
		return posLexema;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setTamanio(int tamanio) {
		this.tamanio = tamanio;
	}

	public void setRet(String ret) {
		this.ret=ret;
	}
	
	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
	
	public void setPosLexema(int posLexema) {
		this.posLexema = posLexema;
	}

	public int getNlin() {
		return nlin;
	}

	public void setNlin(int nlin) {
		this.nlin = nlin;
	}

	public AtributosInt getTemporal() {
		return temporal;
	}

	public void setTemporal(AtributosInt temporal) {
		this.temporal = temporal;
	}

	public int getValorEntero() {
		return valorEntero;
	}

	public void setValorEntero(int valorEntero) {
		this.valorEntero = valorEntero;
	}

	public String getValorCadena() {
		return valorCadena;
	}

	public void setValorCadena(String valorCadena) {
		this.valorCadena = valorCadena;
	}

	public void setPar(Pair<String, Integer> nombre) {
		this.par = nombre; 
	}
	
	public Pair<String,Integer> getPar() {
		return this.par; 
	}
}
