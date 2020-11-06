package codigoIntermedio;

import codigoObjeto.*;
import gestorTablas.GestorTablas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class GestorCodigoIntermedio {

	GeneradorCodigoObjeto generadorCodigoObjeto;
	GestorTablas gestorTablas; 
	LinkedList<String> listaCadenas;
	File codigoIntermedio;
	FileWriter writer;
	boolean hayQueCerrar;

	public GestorCodigoIntermedio (GestorTablas gestorTablas ,LinkedList<String> listaCadenas) {
		generadorCodigoObjeto = new GeneradorCodigoObjeto();
		this.gestorTablas = gestorTablas; 
		this.listaCadenas = listaCadenas;
		this.hayQueCerrar = false;
		this.codigoIntermedio = new File("Pruebas/CodigoIntermedio.txt");
		if(codigoIntermedio.exists()) codigoIntermedio.delete();
		try {
			this.writer = new FileWriter(codigoIntermedio);
		} catch (IOException e) {
			System.err.println("Ha habido un error escribiendo el cuarteto");
			System.exit(1);
		}
	}

	public void emite(String operacion, Operando [] elems) throws IOException {
		Cuarteto cuarteto = null;
		switch (operacion) {
		case "if":  // lugar, operacion, valorComparar, etiqueta
			cuarteto = new Cuarteto(elems[1].getOperando(), elems[0], elems[2], elems[3]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
			break;

		case "asignacion": // elem[0] = elem[1]
			cuarteto = new Cuarteto(":=", elems[1], null, elems[0]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
			break;

		case "operacion": // op, valor ,valor , almacen
			cuarteto = new Cuarteto(elems[0].getOperando(), elems[2], elems[3], elems[1]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
			break;

		case "printINT":
			cuarteto = new Cuarteto(operacion, null, null, elems[0]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
			break;
			
		case "printSTR":
			cuarteto = new Cuarteto(operacion, null, null, elems[0]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
			break; 
			
		case "call":
			cuarteto = new Cuarteto(elems[0].getOperando(), elems[1], null, elems[2]);
			generadorCodigoObjeto.addCuarteto(cuarteto);			
			break;
			
		case "EOF":
			cuarteto = new Cuarteto("returnMain", null, null, null); // puesto para que imprimir no de mal
			generadorCodigoObjeto.addCuarteto(cuarteto);
			HashMap<String,Integer> tamanios = gestorTablas.calcularDesplazamiento();
			generadorCodigoObjeto.generarCodigoObjeto(tamanios,listaCadenas);
			hayQueCerrar = true;
			break;
			
		default:	// goto null null et, : null null et , param null null et , return null null et 
			cuarteto = new Cuarteto(operacion, null, null, elems[1]);
			generadorCodigoObjeto.addCuarteto(cuarteto);
		}
		cuarteto.imprimir(this.writer);
		if(hayQueCerrar)
			writer.close();
	}
}
