package codigoIntermedio;

public class Etiquetador {
	
	private int enumerador;
	
	public Etiquetador() {
		this.enumerador = 0;
	}
	
	public String generarEtiqueta() {
		return "Et"+Integer.toString(enumerador++);
	}
	
}
