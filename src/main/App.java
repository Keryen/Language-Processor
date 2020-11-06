package main;

import java.io.IOException;

import analizadorSintactico.ASint;

public class App {
	public static void main(String[] args) throws IOException {
		new ASint("Pruebas/Pruebas TDL/variables" + "/variables.txt").analizadorSintactico();
	}
}
