package gestorErrores;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GestorErrores {

	private FileWriter fErr;

	public GestorErrores() throws IOException {

		File fich = new File("Pruebas/Errores.txt"); // Fichero fuente
		fErr = new FileWriter(fich);
	}

	public void generarError(String analizador, String msg, int nlin) throws IOException {
		if(analizador.equals("Lexico")) {
			switch(Integer.parseInt(msg)) {
			case 50:
				msg = "Se ha recibido el carácter ‘_‘ desde el estado inicial.";
				break;
			case 51:
				msg = "Se ha recibido un carácter que no está contemplado para el lenguaje desde el estado inicial.";
				break;
			case 52:
				msg = "Se ha recibido el final de fichero y no ha terminado la cadena, se esperaba una ‘ \\’ ’. ";
				break;
			case 53:
				msg = "Se ha recibido un carácter distinto al esperado, se esperaba ‘|‘ o ‘=‘.";
				break;
			case 54:
				msg = "Se ha recibido el final de fichero y no ha terminado el comentario, cuando se esperaba ‘*’.";
				break;
			case 55:
				msg = "Se ha recibido el final de fichero y no ha terminado el comentario, cuando se esperaba ‘/’.";
				break;
			case 56:
				msg = "El valor del entero está fuera del rango especificado (2 bytes).";
				break;
			case 57:
				msg = "Se ha declarado dos veces la misma variable";
				break;
			}
		}

		fErr.write("Se ha producido un error " + analizador + " en la linea " + nlin +  ": " + msg);
		System.out.println("Se ha producido un error " + analizador + " en la linea " + nlin +  ": " + msg);
		fErr.close();
		System.exit(1);
	}
}
