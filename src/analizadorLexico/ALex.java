package analizadorLexico;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gestorErrores.GestorErrores;
import gestorTablas.GestorTablas;

public class ALex {

	// Declaracion de variables globales
	private char car;
	private int nlin;

	private FileReader fF;
	private FileWriter fTok;

	private GestorErrores gestorErrores;
	private GestorTablas gestorTablas;

	// Tablas del AL
	private ArrayList<String> tablaPR;


	// Matriz de transicion del AFD                            Columnas: l d + * / = | ' _  ( ) [ ] { } , ; EOF del otrosCaracteres. 	      Filas
	private String [][] matrizTransicionAFD = {
			{"1","B","3","D","18","F","17","G","13","A","10","A","7","A","5","A","","50","19","N","20","O","21","P","22","Q","23","R","24","S","25","T","26","U","27","V","0","A","","51"},				//0
			{"1","B","1","B","2","C","2","C","2","C","2","C","2","C","2","C","1","B","2","C","2","C","2","C","2","C","2","C","2","C","2","C","2","C","2","C","2","C","2","C"}, 							//1
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 																								//2
			{"4","E","3","D","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E","4","E"}, 							//3
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 																	//4
			{"5","B","5","B","5","B","5","B","5","B","5","B","5","B","6","M","5","B","5","B","5","B","5","B","5","B","5","B","5","B","5","B","5","B","","52","5","B","5","B"}, 							//5
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 																	//6
			{"","53","","53","","53","","53","","53","8","J","9","L","","53","","53","","53","","53","","53","","53","","53","","53","","53","","53","","53","","53","","53"}, 							//7
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 		 															//8
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 		 															//9
			{"11","I","11","I","11","I","11","I","11","I","12","K","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I","11","I"},	 	//10
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 																	//11
			{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""}, 	 																//12
			{"16","H","16","H","16","H","14","A","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H","16","H"}, 		//13
			{"14","A","14","A","14","A","15","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","","54","14","A","14","A"}, 		//14
			{"14","A","14","A","14","A","15","A","0","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","14","A","","55","14","A","14","A"}}; 		//15
			//   l        d        +        *       /        =        |        '        _        (        )        [        ]        {        }        ,        ;     EOF       del		  OC

	public ALex (GestorTablas gestorTablas, GestorErrores gestorErrores, String fichFuente) throws IOException {	//	Constructor
		this.gestorErrores = gestorErrores;
		this.gestorTablas = gestorTablas;
		this.nlin = 1;
		
		fF = new FileReader(new File(fichFuente));
		fTok = new FileWriter(new File("Pruebas/Tokens.txt"));

		// Metemos en la tabla de palabras reservadas
		tablaPR = new ArrayList<>();
		tablaPR.add("bool"); tablaPR.add("do"); tablaPR.add("function"); tablaPR.add("if"); tablaPR.add("int"); tablaPR.add("print"); tablaPR.add("prompt"); 
		tablaPR.add("return"); tablaPR.add("string"); tablaPR.add("var"); tablaPR.add("while"); tablaPR.add("true"); tablaPR.add("false");

		car = leer();
	}

	public Token sigToken () throws IOException {
		
		//Declaracion de variables locales
		int pos;
		int est = 0;
		int val = 0;
		String acc;
		String trans;
		String pal = "";
		Token token = null;

		while (est==0 || est==1 || est==3 || est==5 || est==7 || est==10 || est==13 || est==14 || est==15) {	// Si no es un estado final:

			pos = posicionamiento();	// Busco la posicion de la columna en la MT
			acc = matrizTransicionAFD[est][pos + 1];	// Coge la accion que se va a ejecutar
			trans = matrizTransicionAFD[est][pos];	// Coge el estado al que hay que transitar

			if (trans.equals("")) {	//Si la transicion esta vacia se genera el error contenido en la matriz del AFD
				gestorErrores.generarError("Lexico",acc,nlin);
				car = leer();
			}
			
			est = Integer.parseInt(trans);

			switch (acc) { // Acciones semantica

			case "A":
				car = leer();
				break;

			case "B":
				pal+=car;
				car = leer();
				break;

			case "C":
				String palabra = pal;
				if (tablaPR.contains(palabra))
					token = generarToken("PR", Integer.toString(tablaPR.indexOf(palabra)));
				else {
					Pair<Integer, Integer> posLexema = gestorTablas.buscarPosLexemaTS(palabra,nlin);
					
					token = generarToken("ID", Integer.toString(posLexema.getLeft()));
//					 En caso de estar en TSL, pone el booleano del token a true
					token.setEnTSL(posLexema.getRight() == 0);
				}
				break;

			case "D":
				val = 10 * val + (car - '0');
				car = leer();
				break;

			case "E": 
				if (val > -Math.pow(2, 15) && val < Math.pow(2, 15))
					token = generarToken("Entero",Integer.toString(val));
				else
					gestorErrores.generarError("Lexico","56",nlin);
				break;

			case "F":
				token = generarToken("OpAritmetico","0");	// +
				car = leer();
				break;

			case "G":
				token = generarToken("OpAritmetico","1");	// *
				car = leer();
				break;

			case "H":
				token = generarToken("OpAritmetico","2");	// /
				break;

			case "I":
				token = generarToken("OpAsignacion", "0");	// =
				break;

			case "J":
				token = generarToken("OpAsignacion","1");	// |=
				car = leer();
				break;

			case "K":
				token = generarToken("OpRelacional","0");	// ==
				car = leer();
				break;

			case "L":
				token = generarToken("OpLogico","0");	// ||
				car = leer();
				break;

			case "M":
				token = generarToken("Cadena","\'" + pal + "\'");
				car = leer();
				break;

			case "N":
				token = generarToken("ParAb","");
				car = leer();
				break;

			case "O":
				token = generarToken("ParCerr","");
				car = leer();
				break;

			case "P":
				token = generarToken("CorAb","");
				car = leer();
				break;

			case "Q":
				token = generarToken("CorCerr","");
				car = leer();
				break;

			case "R":
				token = generarToken("LLaveAb","");
				car = leer();
				break;

			case "S":
				token = generarToken("LlaveCerr","");
				car = leer();
				break;

			case "T":
				token = generarToken("Coma","");
				car = leer();
				break;

			case "U":
				token = generarToken("PtoYComa","");
				car = leer();
				break;

			case "V":
				token = generarToken("EOF","");	
				fTok.close();
				break;
			default:
			}
		}
		return token;
	}

	private int posicionamiento () {	// Coge la posicion de la columna de la MT_AFD a partir del caracter leido

		int pos;

		if (((car >= 'A') && (car <= 'Z')) || ((car >= 'a') && (car <= 'z')) || (car == 'Ñ') || (car == 'ñ'))
			pos = 0;
		else if ((car >= '0') && (car <= '9'))
			pos = 2;
		else if (car == ' ' || car == '\t' || car == '\r' || car == '\n')
			pos = 36;
		else {
			switch (car) {
			case '+':	pos = 4;  break;
			case '*':	pos = 6;  break;
			case '/':	pos = 8;  break;
			case '=':	pos = 10; break;
			case '|':	pos = 12; break;
			case '\'':	pos = 14; break;
			case '_':	pos = 16; break;
			case '(':	pos = 18; break;
			case ')':	pos = 20; break;
			case '[':	pos = 22; break;
			case ']':	pos = 24; break;
			case '{':	pos = 26; break;
			case '}':	pos = 28; break;
			case ',':	pos = 30; break;
			case ';':	pos = 32; break;
			case '\0':	pos = 34; break;
			default:	pos = 38; 
			}
		}
		return pos;
	}

	private char leer () throws IOException {

		int a = fF.read(); 

		if (a == -1) 	// Lee un null (EOF)
			car = '\0';

		else {
			car = (char) a;
			if (car == '\r')	// Lee un salto de linea
				nlin++;
		}

		return car;
	}

	private Token generarToken (String cod, String valor) throws IOException {
		Token token = new Token(cod, valor, nlin);
		String doc  = "< " + cod +", " + valor + " >";	

		// Escribe en el fichero Tokens.txt
		fTok.write(doc + "\r\n");	

		return token;
	}
}