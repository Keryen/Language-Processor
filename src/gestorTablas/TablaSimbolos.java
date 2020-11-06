package gestorTablas;
import java.util.LinkedList;

public class TablaSimbolos {
	
	private String nombre;
	private int desplazamiento;
	private LinkedList<Fila> filas;
	private String etiqueta;
	
	public TablaSimbolos(String nombre) {
		filas = new LinkedList<>();
		this.nombre = nombre; 
		desplazamiento = 0;
		filas = new LinkedList<>();
	}
	
	// Lexico 

	public int buscarLexema(String lexema) {	//Devuelve la posicion de la fila o -1
		if (!filas.isEmpty()) {
			for (Fila fila : filas) {
				if (fila.getLexema().equals(lexema))
					return filas.indexOf(fila);
			}
		}
		return -1;
	}
	
	public int aniadirLexema(String lexema) {
		filas.add(new Fila (lexema));
		return filas.size()-1;
	}
	
	// Semantico
	
	public void aniadirTipo(String tipo) {
		filas.getLast().setTipo(tipo);
	}
	
	public void aniadirDesplazamiento() {
		filas.getLast().setDesplazamiento(desplazamiento);
	}
	
	public void aniadirNumeroParametros(int numParams) {
		filas.getLast().setNParametros(numParams);
	}
	
	public void aniadirTipoParametros(String tipoParametros) {
		filas.getLast().setTipoParametros(tipoParametros);
	}
	
	public void aniadirTipoRetorno(String tipoRetorno) {
		filas.getLast().setTipoRetorno(tipoRetorno);
	}
	
	public void aniadirEtiqueta(String etiqueta) {
		filas.getLast().setEtiqueta(etiqueta);
	}
	
	public void sumarDesplazamiento(int tamanio) {
		desplazamiento += tamanio;
	}
	
	public String buscarTipo(int pos) {		
		return filas.get(pos).getTipo();
	}
	
	public String buscarTipoParametros(int pos) {
		return filas.get(pos).getTipoParametros();
	}
	
	public String buscarTipoRetorno(int pos) {
		return filas.get(pos).getTipoRetorno();
	}
	
	public void borrarLexema() {
		filas.remove(filas.size()-1);
	}

	public String imprimir(int i) {	
		StringBuilder res = new StringBuilder();
		res.append("TABLA " + nombre + " #" + i + "\n\r");
		for (Fila fila: filas) {
			res.append(fila.imprimir());
		}
		return res.toString();
	}
	
	// Codigo intermedio
	
	public String getEtiqueta(String lexema) {
		if (!filas.isEmpty()) {
			for (Fila fila : filas) {
				if (fila.getLexema().equals(lexema)){
					return filas.get(filas.indexOf(fila)).getEtiqueta();
				}
			}
		}
		System.out.println("error en get Etiqueta devuelve \"\"");
		return "";
	}
	
	public int buscarDesplazamiento() {
		return filas.getLast().getDesplazamiento();
	}

	public String getLexema(int pos) {
		if (!filas.isEmpty()) {
			return filas.get(pos).getLexema();
		}
		return null;
	}

	public int getDesplazamiento(int pos) {
		if (!filas.isEmpty()) {
			return filas.get(pos).getDesplazamiento();
		}
		return 0;
	}

	public String buscarEtiqueta(int pos) {
		if (!filas.isEmpty()) {
			return "et_"+filas.get(pos).getLexema();
		}
		return null;
	}

	public int calcularDesplazamiento(){
		int tam = 0; 
		if (!filas.isEmpty()) {
			for (Fila fila : filas) {
				if (fila.getTipo().equals("cadena"))
					tam += 128;
				else if (!fila.getTipo().equals("funcion")) {
					tam++;
				}
			}
		}
		return tam;
	}
	
	public String getNombre() {
		return this.nombre;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public void setNombre(String lexema) {
		this.nombre = lexema; 
		
	}
}
