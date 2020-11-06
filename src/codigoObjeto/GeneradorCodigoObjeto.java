package codigoObjeto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import codigoIntermedio.Cuarteto;

public class GeneradorCodigoObjeto {

	FileWriter fileWriterCO;
	LinkedList<Cuarteto> listaCuartetos;	// lista de cuartetos creados por el codigo intermedio, a modo de FIFO
	LinkedList<Operando> listaParams;			// lista de parametros para volcarlos todos en el call
	HashMap<String, Integer> tamTablas;
	boolean enFuncion;
	int contEtiquetasDir, tamVL, despParams;

	public GeneradorCodigoObjeto() {
		try {
			fileWriterCO = new FileWriter(new File("Pruebas/Codigo Objeto/CodigoObjeto.ens"));
			listaCuartetos = new LinkedList<>();
			listaParams = new LinkedList<>();
			despParams = 1;
			contEtiquetasDir = 1;
		} catch (IOException e) {
			System.err.println("Error al crear GCO:");
			e.printStackTrace();
		}
	}

	public void addCuarteto(Cuarteto cuarteto) {
		listaCuartetos.addLast(cuarteto);
	}

	public void generarCodigoObjeto(Map<String, Integer> tamTablas, LinkedList<String> listaCadenas) throws IOException {
		this.tamTablas = (HashMap<String, Integer>) tamTablas;

		// Crea el inicio de la pila
		fileWriterCO.write("\t\t MOVE #inicio_pila, .IY\n"	
				+  "\t\t ADD .IY, #" + tamTablas.get("main") + "\n"
				+  "\t\t MOVE .A, .IX\n\n"
				+  "\t\t ; Comienzo del codigo:\n");		// Coloca IX al principio de la pila de RAs (despues de las variables globales)


		for(Cuarteto cuarteto: listaCuartetos) {
			switch (cuarteto.getOperacion()) {
			case ":=":
				generarAsignacion(cuarteto);
				break;

			case "=":
				generarIf(cuarteto);
				break;

			case "OR":
				generarOperacion("OR", cuarteto);
				break;

			case "+":
				generarOperacion("ADD", cuarteto);
				break;

			case "*":
				generarOperacion("MUL", cuarteto);
				break;

			case "/":
				generarOperacion("DIV", cuarteto);
				break;

			case "printINT":
				generarPrint(cuarteto, false);
				break;
				
			case "printSTR":
				generarPrint(cuarteto, true);
				break;
				
			case "goto":
				generarGoTo(cuarteto);
				break;

			case "etiqueta":
				generarEtiqueta(cuarteto, false);
				break;

			case "etiquetaFuncion":
				generarEtiqueta(cuarteto, true);
				break;

			case "param":
				generarParam(cuarteto);
				break;

			case "call":
				generarCall(cuarteto);
				break;

			case "return":
				generarReturn(cuarteto);
				break;

			case "returnMain":
				generarReturnMain();
				break;
			}
		}

		// Lista de cadenas
		fileWriterCO.write("\n; Cadenas:\n");
		for(int i = 0; i < listaCadenas.size(); i++)
			fileWriterCO.write("Cadena_" + i + ": DATA \"" + listaCadenas.get(i) + "\"\n" );
		
		// Inicio de la pila
		fileWriterCO.write("\ninicio_pila: NOP\n");
		fileWriterCO.close();
	}

	private void generarAsignacion(Cuarteto cuarteto) throws IOException {
		// Crea instruccion MOVE
		fileWriterCO.write("\t\t ; Asignacion:\n"
				+ "\t\t MOVE");
		imprimirDireccion(cuarteto.getOperador1());
		fileWriterCO.write(",");
		imprimirDireccion(cuarteto.getResultado());
		fileWriterCO.write("\n");	
	}

	private void generarIf(Cuarteto cuarteto) throws IOException {
		// Crea instruccion CMP
		fileWriterCO.write("\t\t ; If:\n"
				+ "\t\t CMP");
		imprimirDireccion(cuarteto.getOperador1());
		fileWriterCO.write(",");
		imprimirDireccion(cuarteto.getOperador2());
		fileWriterCO.write("\n\t\t BZ /" + cuarteto.getResultado().getOperando() + "\n");	
	}

	private void generarOperacion(String op, Cuarteto cuarteto) throws IOException {
		// Crea instruccion OR
		fileWriterCO.write("\t\t ; Operacion " + op + ":\n"
				+ "\t\t " + op);
		imprimirDireccion(cuarteto.getOperador1());
		fileWriterCO.write(",");
		imprimirDireccion(cuarteto.getOperador2());
		fileWriterCO.write("\n");

		// Crea instruccion MOVE
		fileWriterCO.write("\t\t MOVE .A, ");
		imprimirDireccion(cuarteto.getResultado());
		fileWriterCO.write("\n");	
	}

	private void generarPrint(Cuarteto cuarteto, boolean esCadena) throws IOException {
		fileWriterCO.write("\t\t ; Print:\n");
		
		if(esCadena) {
			fileWriterCO.write("\t\t MOVE");
			imprimirDireccion(cuarteto.getResultado());
			fileWriterCO.write(", .R9\n");
			fileWriterCO.write("\t\t WRSTR [.R9]");
		} else {
			fileWriterCO.write("\t\t WRINT");
			imprimirDireccion(cuarteto.getResultado());
		}
		fileWriterCO.write("\n");	
			
	}
	private void generarGoTo(Cuarteto cuarteto) throws IOException {
		fileWriterCO.write("\t\t ; GoTo:\n"
				+ "\t\t BR /" + cuarteto.getResultado().getOperando() + "\n");
	}

	private void generarEtiqueta(Cuarteto cuarteto, boolean esFuncion) throws IOException {
		if(esFuncion) {
			tamVL = tamTablas.get(cuarteto.getResultado().getOperando());
			fileWriterCO.write("\t\t BR /" + cuarteto.getResultado().getOperando() + "_final\n");
			enFuncion = true;
		}
		
		if(cuarteto.getResultado().getOperando().contains("_final"))
			enFuncion = false;
		fileWriterCO.write("\n" + cuarteto.getResultado().getOperando() + ":\n");
	}

	private void generarParam(Cuarteto cuarteto) throws IOException {
		fileWriterCO.write("\t\t ; Param:\n"
				+ "\t\t MOVE #" + cuarteto.getResultado().getOperando() + "[.IY], #" + despParams + "[.IX]\n");		// Coloca el parametro desde la tabla global
		despParams++;		// Se suma al tam del RA actual el tam del parametro
	}


	private void generarCall(Cuarteto cuarteto) throws IOException {
		int tamRA = 1 + tamTablas.get(cuarteto.getOperador1().getOperando()) + 1;		// RA: EM + Params, VL y VT + VD
		int tamVLAnteriorRA = 0;
		if(enFuncion)
			tamVLAnteriorRA = tamTablas.get(cuarteto.getOperador1().getOperando()) + 1;

		fileWriterCO.write("\t\t ; Call:\n"
				+  "\t\t ADD .IX, #" + tamVLAnteriorRA		// Colocamos el puntero sobre el siguiente RA
				+  "\t\t MOVE .A, .IX"
				+  "\t\t MOVE #dir_ret_" + contEtiquetasDir + ", [.IX]\n"		// Mete la direccion de retorno
				+  "\t\t ADD .IX, #1\n"		// Suma uno para ponerme en la posicion inicial de los parametros
				+  "\t\t MOVE .A, .IX\n"		// Coloca IX al principio de los parametros		
				+  "\t\t BR /" + cuarteto.getOperador1().getOperando() + "\n\n");		// Salta a la funcion
		
		fileWriterCO.write("dir_ret_" + contEtiquetasDir++ + ":\n"		// Pone la etiqueta
				+  "\t\t SUB .IX, #1\n"		// Resta uno para ponerme en la posicion inicial de la pila
				+  "\t\t MOVE .A, .IX\n");		// Recupera el IX	

		if(!cuarteto.getResultado().getOperando().equals("null")) {
			fileWriterCO.write("\t\t SUB #" + tamRA + ", #1\n"	// Resta al tamanio del RA para poner el acumulador en la posicion del VD
					+  "\t\t ADD .A, .IX\n"		// Suma a la posicion de IX el tam del RA
					+  "\t\t MOVE [.A], #" + cuarteto.getResultado().getOperando() + "[.IY]\n");	// Mete en la posicion del VD
		}

		despParams = 1;		// Reinicio el desplazamiento de los parametros a 1 (despues del EM)

	}

	private void generarReturn(Cuarteto cuarteto) throws IOException {
		fileWriterCO.write("\t\t ; Return:\n");
		if(!cuarteto.getResultado().getOperando().equals("null")) {
			fileWriterCO.write("\t\t ADD #" + tamVL + ", .IX\n"			// Suma a la posicion de IX el tam de los parametros y la VLs
					+  "\t\t MOVE #" + cuarteto.getResultado().getOperando() + "[.IX], [.A]\n");		// Mete la temporal que contiene el dato a devolver en el VD
		}

		fileWriterCO.write("\t\t SUB .IX, #1\n"
				+ "\t\t BR [.A]\n");		// Salta a la direccion de retorno

	}

	private void generarReturnMain() throws IOException {
		fileWriterCO.write("\t\t HALT\n");		// Finaliza el programa
	}

	private void imprimirDireccion(Operando op) throws IOException {

		switch(op.getCodigo()){
		case VG:
			fileWriterCO.write(" #" + op.getOperando() + "[.IY]");		// Coge la variable global
			break;
		case VL:
			fileWriterCO.write(" #" + op.getOperando() + "[.IX]");		// Coge la variable local
			break;
		default:
			fileWriterCO.write(" #" + op.getOperando());		// Coge la direccion de la etiqueta o el valor de la cte
			break;
		}
	}
}
