package analizadorLexico;

public class Token {

	private String cadena;
	private String valor;
	private int nlin;
	private boolean enLocal;
	
	public Token (String cadena, String valor, int nlin) {
		this.nlin = nlin;
		this.cadena = cadena;
		this.valor = valor;
		enLocal = false;
	}
	
	public String getCadena() {
		return cadena;
	}
	
	public String getValor() {
		return valor;
	}

	public int getNlin() {
		return nlin;
	}
	
	public boolean getEnTSL() {
		return enLocal;
	}
	
	public void setEnTSL(boolean enLocal) {
		this.enLocal = enLocal;
	}

	public void setNlin(int nlin) {
		this.nlin = nlin;
	}
}
