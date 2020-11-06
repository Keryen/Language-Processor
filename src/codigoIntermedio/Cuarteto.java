package codigoIntermedio;

import java.io.FileWriter;
import java.io.IOException;

import codigoObjeto.Operando;

public class Cuarteto {

	private String operacion;
	private Operando operador1;
	private Operando operador2;
	private Operando resultado;

	public Cuarteto (String operacion, Operando operador1, Operando operador2, Operando resultado) {
		this.operacion = operacion;
		this.operador1 = operador1;
		this.operador2 = operador2;
		this.resultado = resultado;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public Operando getOperador1() {
		return operador1;
	}

	public void setOperador1(Operando operador1) {
		this.operador1 = operador1;
	}

	public Operando getOperador2() {
		return operador2;
	}

	public void setOperador2(Operando operador2) {
		this.operador2 = operador2;
	}

	public Operando getResultado() {
		return resultado;
	}

	public void setResultado(Operando resultado) {
		this.resultado = resultado;
	}
	
	public void imprimir(FileWriter writer) {
		if(operacion == null) {
			operacion = "null";
		}
		if(operador1 == null) {
			operador1 = new Operando(null, null);
		}
		if(operador2 == null) {
			operador2 = new Operando(null, null);
		}
		if(resultado == null) {
			resultado = new Operando(null, null);
		}
		String aux = null;
		if(operacion.equals("etiquetaFuncion") || operacion.equalsIgnoreCase("etiqueta")) {
			aux = operacion;
			operacion = ":";
		}
		
		System.out.println(operacion + ", " + operador1.getOperando() + ", "+  operador2.getOperando() + ", " + resultado.getOperando());
		
		try {
			writer.write(operacion + ", " + operador1.getOperando() + ", "+  operador2.getOperando() + ", " + resultado.getOperando() +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(aux != null) {
			operacion = aux; 
		}
	}
}
