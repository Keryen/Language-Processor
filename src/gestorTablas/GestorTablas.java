package gestorTablas;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import analizadorLexico.Pair;
import analizadorSemantico.ASem;
import gestorErrores.GestorErrores;

public class GestorTablas {

	private TablaSimbolos global; 
	private TablaSimbolos local; 
	private TablaSimbolos actual;
	private LinkedList<TablaSimbolos> listaTSL;
	
	private FileWriter fwTablas;
	
	private ASem aSem;
	private GestorErrores gestorErrores;
	
	public GestorTablas(GestorErrores gestorErrores) throws IOException {

		global = new TablaSimbolos("Global");
		actual = global;
		listaTSL = new LinkedList<>();
		
		fwTablas = new FileWriter(new File("Pruebas/TS.txt"));
		
		this.gestorErrores = gestorErrores;
	}

	public void setSemantico(ASem aSem) {
		this.aSem = aSem;
	}
	
	public void initLocal(String nombre , int pos) {
		actual = local = new TablaSimbolos("de la funcion " + global.getLexema(pos));
		local.setNombre(global.getLexema(pos));
	}

	public void borrarLocal() {
		listaTSL.addLast(local);
		actual = global;
		local = null;
	}

	// Lexico

	// Devuelve la posicion de la tabla y la tabla donde esta
	public Pair<Integer, Integer> buscarPosLexemaTS(String lexema, int nlin) throws IOException {
		int pos;
		// Utilizamos el valor 0 para decir que esta en la tabla global y el valor 1 para decir que esta en la local
		
		if (aSem.enZonaDeclarativa()) {
			//Probamos a buscar en local
			if (local != null) {
				pos = actual.buscarLexema(lexema);
				if (pos != -1)
					gestorErrores.generarError("Lexico", "57", nlin);
			} 
			//Buscamos en la global
			pos = global.buscarLexema(lexema);
			if (actual == global && (pos != -1)) {
				gestorErrores.generarError("Lexico", "57", nlin);
			}
			else  {
				pos = actual.aniadirLexema(lexema);
				if (actual == global) {
					return new Pair<>(pos, 0);
				}
				else {
					return new Pair<>(pos, 1);
				}
			}
		}
		else {
			//Probamos a buscar en local
			if (local != null) {
				pos = actual.buscarLexema(lexema);
				if (pos != -1) {
					return new Pair<>(pos, 1);
				}
			}
			//Buscamos en la global
			pos = global.buscarLexema(lexema);
			if (pos != -1) {
				return new Pair<>(pos, 0);
			}
			//No se ha encontrado
			else {
				if(lexema.contains("temp")) {
					if(actual != null && aSem.getEnFuncion()) {
						pos = actual.aniadirLexema(lexema);
						return new Pair<>(pos, 1);
					}else {
						pos = global.aniadirLexema(lexema);
						return new Pair<>(pos, 0);
					}
				}
				else {
				pos = global.aniadirLexema(lexema);
				global.aniadirTipo("entero");
				return new Pair<>(pos, 0);
				}
			}
		}
		return null;
	}

	//Semantico

	public void aniadirTipoTS(String tipo, boolean meterATSG) {
		if(meterATSG)
			global.aniadirTipo(tipo);
		else
			actual.aniadirTipo(tipo);
	}

	public void aniadirDesplazamientoTS() {
		actual.aniadirDesplazamiento();
	}

	public void aniadirNumeroParametrosTS(int numParams) {
		global.aniadirNumeroParametros(numParams);
	}

	public void aniadirTipoParametrosTS(String tipoParametros) {
		global.aniadirTipoParametros(tipoParametros);
	}

	public void aniadirTipoRetornoTS(String tipoRetorno) {
		global.aniadirTipoRetorno(tipoRetorno);
	}

	public void aniadirEtiquetaTS(String etiqueta) {
		global.aniadirEtiqueta(etiqueta);
	}

	public void sumarDesplazamientoTS(int tamanio) {
		actual.sumarDesplazamiento(tamanio);
	}

	public String buscarTipoTS(int pos, boolean enGlobal) {
		if(!enGlobal) {
			return local.buscarTipo(pos);
		} else {
			return global.buscarTipo(pos);
		}
	}

	public String buscarTipoParametrosTS(int pos) {
		return global.buscarTipoParametros(pos);
	}
	
	public String buscarTipoRetornoTS(int pos) {
		return global.buscarTipoRetorno(pos);
	}

	public Pair<Integer,Integer> buscarLugar(int pos) { // aniadido por codigo intermedio
		Pair<Integer,Integer> par = new Pair<>();
		if(local != null) {
			try {
				par.setRight(1);
				par.setLeft(actual.getDesplazamiento(pos));
			}catch (IndexOutOfBoundsException e) {
				par.setRight(0);
				par.setLeft(global.getDesplazamiento(pos));
			}
		} else {
			par.setRight(0);
			par.setLeft(global.getDesplazamiento(pos));
		}
	return par;
	}
	// buscarEtTS
	// busacrgLugarTS
	
	// intermedio
	
	// en la izquierda este el desplazamiento y a la derecha esta la tabla a la que pertenece, 0 global, 1 local
	public Pair<Integer,Integer> getDesplazamiento() throws IOException {
		Pair<Integer,Integer> res = new Pair<>();
		if(actual == global) {
			res.setLeft(actual.buscarDesplazamiento());
			res.setRight(0);
		}else {
			res.setLeft(actual.buscarDesplazamiento());
			res.setRight(1);
		}
		return res;
	}
	
	public void impresion() throws IOException {
		// comprobar ruta de impresion
		StringBuilder res = new StringBuilder();
		int i = 1;
		res.append(global.imprimir(i++));
		res.append("---------------------------------------------------\n\r");
		if (!listaTSL.isEmpty()) {
			for (TablaSimbolos tabla: listaTSL) {
				res.append(tabla.imprimir(i++));
				res.append("---------------------------------------------------\n\r");
			}
		}
		fwTablas.write(res.toString());
		fwTablas.close();
	}

	public String buscarEtiqueta(int pos) {
			return global.buscarEtiqueta(pos);
	}

	public HashMap<String,Integer> calcularDesplazamiento() {
		HashMap<String,Integer> tamanios = new HashMap<>();
		tamanios.put("main", global.calcularDesplazamiento());
		for (TablaSimbolos tablaSimbolos : listaTSL) {
			tamanios.put("et_"+tablaSimbolos.getNombre(),tablaSimbolos.calcularDesplazamiento());
		}
		return tamanios;
	}

	public String buscarNombre(int parseInt) { // no me mires raro, funciona jajaja
		if(local != null) {
			try {
				String res = "";
				res = actual.getLexema(parseInt); 
				if(res == null) {
					return global.getLexema(parseInt);
				}
				return res;		
			}catch (IndexOutOfBoundsException e) {
				return global.getLexema(parseInt);
			}
		}else {
			return global.getLexema(parseInt);
		}
	}
}
