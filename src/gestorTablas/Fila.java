package gestorTablas;

public class Fila {

	private String lexema;
	private String tipo;
	private int desplazamiento;
	private int nParametros;
	private String tipoParametros;
	private String tipoRetorno;
	private String etiqueta;


	public Fila(String lexema) {
		this.lexema = lexema;
		tipo = "";
		tipoParametros= "";
		tipoRetorno= "";
	}

	public String imprimir() {	
		StringBuilder res = new StringBuilder();

		if(!tipo.equals("funcion")) {
			res.append("* ");
			res.append("LEXEMA: '" + lexema + "'\n\r");
			res.append("  ATRIBUTOS : \n\r");
			res.append("  + tipo: '" + tipo + "'\n\r");
			res.append("  + despl: " + desplazamiento + "\n\r");
		} else {
			String [] params = tipoParametros.split(",");
			res.append("* ");
			res.append("LEXEMA: '" + lexema + "'\n\r");
			res.append("  ATRIBUTOS : \n\r");
			res.append("  + tipo: '" + tipo + "'\n\r");
			res.append("  + numParam: " + nParametros + "\n\r");
			for(int i = 0; i<params.length;i++) {
				res.append("  + tipoParams" + i + ": '" + params[i] + "'\n\r");
				res.append("  + modoPasarParams" + i + ": " + 1 + "\n\r");
			}
			res.append("  + tipoRetorno: '" + tipoRetorno + "'\n\r");
			res.append("  + etiqueta: " + etiqueta + "\n\r");
		}
		return res.toString();
	}

	public String getLexema() {
		return lexema;
	}

	public int getDesplazamiento() {
		return desplazamiento;
	}

	public String getTipo() {
		return tipo;
	}

	public int getNParametros() {
		return nParametros;
	}

	public String getTipoParametros() {
		return tipoParametros;
	}

	public String getTipoRetorno() {
		return tipoRetorno;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public void setDesplazamiento(int desplazamiento) {
		this.desplazamiento = desplazamiento;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setNParametros(int nParametros) {
		this.nParametros = nParametros;
	}

	public void setTipoParametros(String tipoParametros) {
		this.tipoParametros = tipoParametros;
	}

	public void setTipoRetorno(String tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
}
