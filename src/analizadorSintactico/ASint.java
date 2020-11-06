package analizadorSintactico;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import analizadorLexico.ALex;
import analizadorLexico.Pair;
import analizadorLexico.Token;
import analizadorSemantico.ASem;
import analizadorSemantico.AtributosSem;
import codigoIntermedio.AccionesIntermedio;
import gestorErrores.GestorErrores;
import gestorTablas.GestorTablas;

public class ASint {

	// Declaracion de variables globales
	private ALex aLex;
	private ASem aSem;
	private String[] simNT = { "P", "B", "T", "S", "X", "Y", "C", "F", "H", "L", "Q", "A", "K", "E", "D", "R", "G", "U", "I", "V", "J", "W", "Z" };
	private String[] simT = {"var", "if" ,"do", "ID","return", "print", "prompt", "function", "int", "bool","while", "string", "+", "*", "/","=", "|=", "==", "||", "Entero", "Cadena", "true","false", "(", ")","{","}",",", ";", "EOF"};
	private Deque<Pair<String, AtributosSem>> pila;
	private GestorErrores gestorErrores;
	private GestorTablas gestorTablas;
	private LinkedList<String> listaCadenas;
	
	//nuevo
	private AccionesIntermedio intermedio;

	private FileWriter fP;

	// Tabla de reglas
	private String[][] tablaLL1 = {
			//var if do ID return print prompt function int bool string + * / = |= == || Entero Cadena true false ( ) { } , ; EOF
			{"1","1","1","1","1","1","1","2","","","","","","","","","","","","","","","","","","","","","3"},			// P
			{"4","5","6","7","7","7","7","","","","","","","","","","","","","","","","","","","","","",""},			// B
			{"","","","","","","","","8","9","10","","","","","","","","","","","","","","","","","",""}, 				// T
			{"","","","11","12","13","14","","","","","","","","","","","","","","","","","","","","","",""},			// S
			{"","","","15","","","","","","","","","","","","","","","15","15","15","15","15","","","","","16",""},		// X
			{"","","","","","","","","","","","","","","17","18","","","","","","","19","","","","","",""},				// Y
			{"20","20","20","20","20","20","20","","","","","","","","","","","","","","","","","","","21","","",""},  	// C
			{"","","","","","","","22","","","","","","","","","","","","","","","","","","","","",""},					// F
			{"","","","24","","","","","23","23","23","","","","","","","","","","","","","","","","","",""},			// H
			{"","","","25","","","","","","","","","","","","","","","25","25","25","25","25","26","","","","",""},		// L
			{"","","","","","","","","","","","","","","","","","","","","","","","28","","","27","",""},				// Q
			{"","","","","","","","","29","29","29","","","","","","","","","","","","","30","","","","",""},			// A
			{"","","","","","","","","","","","","","","","","","","","","","","","32","","","31","",""},				// K
			{"","","","33","","","","","","","","","","","","","","","33","33","33","33","33","","","","","",""},		// E
			{"","","","","","","","","","","","","","","","","","34","","","","","","35","","","35","35",""},			// D
			{"","","","36","","","","","","","","","","","","","","","36","36","36","36","36","","","","","",""},		// R
			{"","","","","","","","","","","","","","","","","37","38","","","","","","38","","","38","38",""},			// G
			{"","","","39","","","","","","","","","","","","","","","39","39","39","39","39","","","","","",""},		// U
			{"","","","","","","","","","","","40","","","","","41","41","","","","","","41","","","41","41",""},		// I
			{"","","","42","","","","","","","","","","","","","","","42","42","42","42","42","","","","","",""},		// V
			{"","","","","","","","","","","","45","43","44","","","45","45","","","","","","45","","","45","45",""},	// J
			{"","","","49","","","","","","","","","","","","","","","47","48","50","51","46","","","","","",""},		// W
			{"","","","","","","","","","","","53","53","53","","","53","53","","","","","52","53","","","53","53",""}};// Z

	public ASint(String fichFuente) throws IOException {
		fP = new FileWriter(new File("Pruebas/Parse.txt"));
		
		gestorErrores = new GestorErrores();
		gestorTablas = new GestorTablas(gestorErrores);

		pila = new ArrayDeque<>();

		aLex = new ALex(gestorTablas, gestorErrores, fichFuente);
		aSem = new ASem(gestorTablas, gestorErrores);
		
		listaCadenas = new LinkedList<>(); 
		intermedio = new AccionesIntermedio(aSem,gestorTablas,listaCadenas);
		aSem.setIntermedio(intermedio);
		
		gestorTablas.setSemantico(aSem);
		
	}

	public void analizadorSintactico() throws IOException {
		// Declaracion de variables locales
		String puntero;
		String numRegla;
		String regla;

		StringBuilder parse = new StringBuilder();	
		parse.append("Descendente ");

		// Inicializacion de la pila y primera llamada
		pila.push(new Pair<String, AtributosSem>("EOF", new AtributosSem()));
		pila.push(new Pair<String, AtributosSem>("P", new AtributosSem()));

		Token token = aLex.sigToken();	// Primer token
		String representacionToken = transformarToken(token);

		do {
			// Recogemos token y apuntamos al primer elemento de la entrada.
			puntero = pila.peek().getLeft();
			if (comprobacionTerminal(puntero)) { // Si es un simbolo terminal o dolar
				if (puntero.equals(representacionToken)) { // Y es igual que el token, sacamos de la pila y volvemos a recibir token como ambos punteros estan apuntando a id, le seteamos su posicion
					Pair<String,AtributosSem> pusheado = pila.pop();
					pusheado.getRight().setNlin(token.getNlin());
					
					if (puntero.equals("ID")) {
						pusheado.getRight().getTemporal().setEnGlobal(token.getEnTSL());
						pusheado.getRight().setPosLexema(Integer.parseInt(token.getValor()));
						pusheado.getRight().setLexema(token.getCadena());
					}
					if(puntero.equals("Entero")) {
						pusheado.getRight().setValorEntero(Integer.parseInt(token.getValor()));
					}
					if(puntero.equals("Cadena")) {
						pusheado.getRight().setValorCadena(token.getValor());
						listaCadenas.addLast(token.getValor().substring(1, token.getValor().length()-1));
					}
					
					aSem.getPilaAux().push(pusheado);
					
					if (representacionToken != "EOF") {
						token = aLex.sigToken();
						representacionToken = transformarToken(token);
					}
				} else {
					gestorErrores.generarError("Sintactico", "El token " + representacionToken + " no esta situado correctamente", token.getNlin());
				}

			} else if (comprobacionNoTerminal(puntero)) { // Si es no terminal, aniadimos elemento
				numRegla = busquedaMatriz(puntero, representacionToken);
				if(!(numRegla).equals("")) {
					parse.append(numRegla + " ");
					regla = buscarRegla(numRegla);
					aSem.getPilaAux().push(pila.pop());
					introducirElementos(regla);
				} else {
					gestorErrores.generarError("Sintactico", "El token " + representacionToken + " no esta situado correctamente",token.getNlin());
				}

			} else if (comprobacionSemantica(puntero)) {
				aSem.ejecutarAccion(puntero, pila);
				pila.pop();

			} else if (comprobacionIntermedio(puntero)) {
				intermedio.ejecutarIntermedio(puntero,pila);
				pila.pop();
				
			} else {
				gestorErrores.generarError("Sintactico", "El token " + representacionToken + " no esta situado correctamente", token.getNlin());
			}
		} while (!puntero.equals("EOF"));
		
		intermedio.ejecutarIntermedio(Integer.toString(160) , pila);
		
		gestorTablas.impresion();
		fP.write(parse.toString());
		fP.close();
	}

	private String transformarToken(Token resultado) {

		String tipo = resultado.getCadena();
		String valor = resultado.getValor();
		String simbolo = tipo;

		switch (tipo) {

		case "PR":
			switch (valor) {
			case "0":		simbolo = "bool";		break;
			case "1":		simbolo = "do";			break;
			case "2":		simbolo = "function";	break;
			case "3":		simbolo = "if";			break;
			case "4":		simbolo = "int";		break;
			case "5":		simbolo = "print";		break;
			case "6":		simbolo = "prompt";		break;
			case "7":		simbolo = "return";		break;
			case "8":		simbolo = "string";		break;
			case "9":		simbolo = "var";		break;
			case "10":		simbolo = "while";		break;
			case "11":		simbolo = "true";		break;
			case "12":		simbolo = "false";		break;
			default:
			}
			break;

		case "OpAritmetico":
			switch (valor) {
			case "0":		simbolo = "+";	break;
			case "1":		simbolo = "*";	break;
			case "2":		simbolo = "/";	break;
			default:
			}
			break;

		case "OpAsignacion":
			switch (valor) {
			case "0":		simbolo = "=";	break;
			case "1":		simbolo = "|=";	break;
			default:
			}
			break;

		case "OpRelacional":simbolo = "==";	break;
		case "OpLogico":	simbolo = "||";	break;
		case "ParAb":		simbolo = "(";	break;
		case "ParCerr":		simbolo = ")";	break;
		case "LLaveAb":		simbolo = "{";	break;
		case "LlaveCerr":	simbolo = "}";	break;
		case "Coma":		simbolo = ",";	break;
		case "PtoYComa": 	simbolo = ";";	break;
		default:
		}

		return simbolo;
	}

	private boolean comprobacionNoTerminal(String simbolo) {
		boolean exito = false;
		int i = 0;
		while (!exito && i < simNT.length) {
			if (simNT[i].equals(simbolo))
				exito = true;
			i++;
		}
		return exito;
	}

	private boolean comprobacionTerminal(String simbolo) {
		boolean exito = false;
		int i = 0;
		while (!exito && i < simT.length) {
			if (simT[i].equals(simbolo))
				exito = true;
			i++;
		}
		return exito;
	}


	private int posicionarFila(String cadena) {
		int pos = -1;

		switch (cadena) {
		case "P":	pos = 0;	break;
		case "B":	pos = 1;	break;
		case "T":	pos = 2;	break;
		case "S":	pos = 3;	break;
		case "X":	pos = 4;	break;
		case "Y":	pos = 5;	break;
		case "C":	pos = 6;	break;
		case "F":	pos = 7;	break;
		case "H":	pos = 8;	break;
		case "L":	pos = 9;	break;
		case "Q":	pos = 10;	break;
		case "A":	pos = 11;	break;
		case "K":	pos = 12;	break;
		case "E":	pos = 13;	break;
		case "D":	pos = 14;	break;
		case "R":	pos = 15;	break;
		case "G":	pos = 16;	break;
		case "U":	pos = 17;	break;
		case "I":	pos = 18;	break;
		case "V":	pos = 19;	break;
		case "J":	pos = 20;	break;
		case "W":	pos = 21;	break;
		case "Z":	pos = 22;	break;
		default:
		}
		return pos;
	}

	private int posicionarColumna(String cadena) {
		int pos = -1;

		switch (cadena) {
		case "var":		pos = 0;	break;
		case "if":		pos = 1;	break;
		case "do":		pos = 2;	break;
		case "ID":		pos = 3;	break;
		case "return":	pos = 4;	break;
		case "print":	pos = 5;	break;
		case "prompt":	pos = 6;	break;
		case "function":pos = 7;	break;
		case "int":		pos = 8;	break;
		case "bool":	pos = 9;	break;
		case "string":	pos = 10;	break;
		case "+":		pos = 11;	break;
		case "*":		pos = 12;	break;
		case "/":		pos = 13;	break;
		case "=":		pos = 14;	break;
		case "|=":		pos = 15;	break;
		case "==":		pos = 16;	break;
		case "||":		pos = 17;	break;
		case "Entero":	pos = 18;	break;
		case "Cadena":	pos = 19;	break;
		case "true":	pos = 20;	break;
		case "false":	pos = 21;	break;
		case "(":		pos = 22;	break;
		case ")":		pos = 23;	break;
		case "{":		pos = 24;	break;
		case "}":		pos = 25;	break;
		case ",":		pos = 26;	break;
		case ";":		pos = 27;	break;
		case "EOF":		pos = 28;	break;
		default:
		}
		return pos;
	}

	private String busquedaMatriz(String puntero, String token) {
		int fila = posicionarFila(puntero);
		int columna = posicionarColumna(token);
		return tablaLL1[fila][columna];
	}
	
	//los numeros del 100 hacia arriba marcan las acciones del codigo intermedio
	private String buscarRegla(String num) {
		String regla = "";

		switch (num) {
		case "1":	regla = "B P"; 								break; // P
		case "2":	regla = "F P"; 								break; // P
		case "4":	regla = "var 0 T ID 1 ; 70";			break; // B
		case "5":	regla = "if ( E ) 102 S 2"; 			break; // B
		case "6":	regla = "do { 104 C } while ( E 105 ) ; 3";	break; // B
		case "7":	regla = "S 4"; 								break; // B
		case "8":	regla = "int 5"; 							break; // T
		case "9":	regla = "bool 6";							break; // T
		case "10":	regla = "string 7"; 						break; // T
		case "11":	regla = "ID 106 Y ; 8"; 					break; // B
		case "12":	regla = "return X ; 9";					break; // B
		case "13":	regla = "print ( E ) ; 10";				break; // B
		case "14":	regla = "prompt ( ID ) ; 11";			break; // B
		case "15":	regla = "E 12";							break; // X
		case "16":	regla = "13";								break; // X
		case "17":	regla = "= E 14";						break; // Y
		case "18":	regla = "|= E 14";						break; // Y
		case "19":	regla = "( L 113 ) 15";						break; // Y
		case "20":	regla = "B C 16";							break; // C
		case "21":  regla = "17";								break; // C
		case "22":	regla = "function H ID 18 ( A ) 19 114 { C 20 } 70";	break; // F
		case "23":	regla = "T 21";								break; // H
		case "24":  regla = "22";								break; // H
		case "25":	regla = "E 115 Q 23";					break; // L
		case "26": 	regla = "24";								break; // L
		case "27":	regla = ", E 117 Q 25"; 				break; // Q
		case "28": 	regla = "26";								break; // Q
		case "29":	regla = "T ID 27 K 28"; 					break; // A
		case "30": 	regla = "29";								break; // A
		case "31":	regla = ", T ID 27 K 30"; 					break; // K
		case "32": 	regla = "31";								break; // K
		case "33":	regla = "R 119 D 32";						break; // E
		case "34":	regla = "|| R 120 D 33";				break; // D
		case "35":	regla = "34";								break; // D
		case "36":	regla = "U 122 G 35";					break; // R
		case "37":	regla = "== U 124 G 36";					break; // G
		case "38": 	regla = "37";								break; // G
		case "39":	regla = "V 125 I 38";					break; // U
		case "40":	regla = "+ V 127 I 39";					break; // I
		case "41": 	regla = "40";								break; // I
		case "42":	regla = "W 129 J 41";					break; // V
		case "43":	regla = "* W 131 J 42";					break; // J
		case "44":	regla = "/ W 133 J 42";					break; // J
		case "45": 	regla = "43";								break; // J
		case "46":	regla = "( E ) 44";						break; // W
		case "47":	regla = "Entero 45";					break; // W
		case "48":	regla = "Cadena 46";					break; // W
		case "49":	regla = "ID 155 Z 47";							break; // W
		case "50":	regla = "true 48";						break; // W
		case "51":	regla = "false 48";						break; // W
		case "52":	regla = "( L 113 ) 49";						break; // Z
		case "53":	regla = "50";								break; // Z
		default:	
		}
		return regla;
	}

	private boolean comprobacionSemantica(String puntero) {
		int comprobante = Integer.parseInt(puntero);
		if (comprobante >= 0 && comprobante < 71) {
			return true;
		}
		return false;
	}
	
	private boolean comprobacionIntermedio(String puntero) {
		int comprobante = Integer.parseInt(puntero);
		if (comprobante >= 100 && comprobante < 160) {
			return true;
		}
		return false;
	}

	private void introducirElementos(String regla) {
		String[] conjunto = regla.split(" ");
		for (int i = conjunto.length - 1; i >= 0; i--) {
			AtributosSem at = new AtributosSem();
			String aux = conjunto[i];
			Pair<String, AtributosSem> pair = new Pair<>(aux, at);
			if (!aux.equals("")) {
				pila.push(pair);
			}
		}
	}
}