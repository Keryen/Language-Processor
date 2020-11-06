package codigoIntermedio;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import analizadorLexico.Pair;
import analizadorSemantico.ASem;
import analizadorSemantico.AtributosSem;
import codigoObjeto.Operando;
import codigoObjeto.tipoOperando;
import gestorTablas.GestorTablas;

public class AccionesIntermedio {

	// asi puede acceder a la pila auxilliar del semantico si lo necesita
	private ASem asem;
	private GestorTemporales tablatemporales;
	private GestorCodigoIntermedio gestorIntermedio;
	private GestorTablas gestorTablas;
	private Etiquetador etiquetador;
	private boolean returned; 
	private LinkedList<String> listaCadenas;
	private boolean venimosDeLlamada;
	private boolean venimosDePrint; // por si acaso

	public AccionesIntermedio(ASem asem, GestorTablas gestorTablas, LinkedList<String> listaCadenas) {
		this.asem = asem;
		this.tablatemporales = new GestorTemporales(gestorTablas);
		this.gestorIntermedio = new GestorCodigoIntermedio(gestorTablas, listaCadenas);
		this.gestorTablas = gestorTablas;
		this.etiquetador = new Etiquetador();
		this.listaCadenas = listaCadenas; 
		returned = false;
	}

	// puede acceder a ambas pilas en caso de que lo necesite sin trastocar ninguna
	// de ellas, sin mover ninguno de sus elementos.
	public void ejecutarIntermedio(String puntero, Deque<Pair<String, AtributosSem>> pila) throws IOException {
		Iterator<Pair<String, AtributosSem>> it;
		Stack<Pair<String, AtributosSem>> pilaAux = asem.getPilaAux();
		int accion = Integer.parseInt(puntero) - 100; // para que sea mas legible la accion.
		AtributosInt e, b, x, y, l, q, q1, d, r, d1, v, g, i, w, j, j1, z, u, i1;
		Pair<Integer, Integer> despl = new Pair<>();
		Operando [] op = new Operando[10];

		switch (accion) {
		case 1: // var T ID ;
			b = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			despl = gestorTablas.getDesplazamiento();
			b.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				b.setEnGlobal(false);
			else
				b.setEnGlobal(true);
			break;

		case 2: // if ( E )
			b = pilaAux.get(pilaAux.size() - 5).getRight().getTemporal();
			e = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			b.setSalida(etiquetador.generarEtiqueta());
			if(e.isEnGlobal())
				op[0] = new Operando(tipoOperando.VG, Integer.toString(e.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(e.getLugar()));

			op[1] = new Operando(tipoOperando.OP, "=");
			op[2] = new Operando(tipoOperando.CE, "0");
			op[3] = new Operando(tipoOperando.VG, b.getSalida());

			gestorIntermedio.emite("if", op);
			break;

		case 3: // S
			b = pilaAux.get(pilaAux.size() - 6).getRight().getTemporal();

			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, b.getSalida());

			gestorIntermedio.emite("etiqueta", op);
			break;

		case 4: // do {
			b = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			b.setInicio(etiquetador.generarEtiqueta());
			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, b.getInicio());

			gestorIntermedio.emite("etiqueta", op);
			break;

		case 5: // C } while ( E
			b = pilaAux.get(pilaAux.size() - 8).getRight().getTemporal();
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			//revisar
			if(e.isEnGlobal())
				op[0] = new Operando(tipoOperando.VG, Integer.toString(e.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(e.getLugar()));

			op[1] = new Operando(tipoOperando.OP, "=");
			op[2] = new Operando(tipoOperando.CE, "1");
			op[3] = new Operando(tipoOperando.VG, b.getInicio());

			gestorIntermedio.emite("if", op);
			
			break;

		case 6: // ID 6 Y
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			y = it.next().getRight().getTemporal(); // fin del acceso
			int pos = pilaAux.get(pilaAux.size() - 1).getRight().getPosLexema();
			boolean enGlobal = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal().isEnGlobal();
			despl = gestorTablas.buscarLugar(pos);
			y.setLugar(despl.getLeft(),despl.getRight()==0);
			y.setEnGlobal(pilaAux.get(pilaAux.size() - 1).getRight().getTemporal().isEnGlobal());
			if (gestorTablas.buscarTipoTS(pos, enGlobal).equals("funcion")) {
				y.setVerdad(gestorTablas.buscarEtiqueta(pos));
				despl = tablatemporales.newTemporal(gestorTablas.buscarTipoRetornoTS(pos));
				y.setLugar(despl.getLeft(),despl.getRight()==0);
				w.setFalso(gestorTablas.buscarTipoRetornoTS(pos));
			}
			break;

		case 7: // return X ;
			AtributosSem x2 = pilaAux.get(pilaAux.size() - 2).getRight();
			x = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			op[0] = new Operando(tipoOperando.OP, "return");
			if(x2.getTipo().equals("")) 
				op[1] = new Operando(tipoOperando.VG, "null");
			else if (x.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(x.getLugar()));
			else
				op[1] = new Operando(tipoOperando.VL, Integer.toString(x.getLugar()));
			returned = true;

			gestorIntermedio.emite("return", op);
			break;

		case 8: // print ( E ) ;
			e = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			AtributosSem aux2 = pilaAux.get(pilaAux.size() - 3).getRight();

			if (e.isEnGlobal()) 
				op[0] = new Operando(tipoOperando.VG, Integer.toString(e.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(e.getLugar()));
			if(aux2.getTipo().equals("cadena")) {
				System.out.println("print String");
				gestorIntermedio.emite("printSTR", op);
			}else {
				gestorIntermedio.emite("printINT", op);	
			}			
			break;

		case 9: // prompt ( ID ) ;

			// falta, es con busca lugar ocn una pos esta en otros metodos ya implementado

			break;

		case 10: // E
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			x = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			x.setLugar(e.getLugar(),e.isEnGlobal());
			break;

		case 11: // = E
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			y = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			if(y.isEnGlobal()) 
				op[0] = new Operando(tipoOperando.VG, Integer.toString(y.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(y.getLugar()));
			
			if(e.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(e.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(e.getLugar()));

			gestorIntermedio.emite("asignacion" , op);
			break;

		case 12: // |= E
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			y = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			op[0] = new Operando(tipoOperando.OP, "OR");
			if(y.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(y.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(y.getLugar()));

			if(y.isEnGlobal()) 
				op[2] = new Operando(tipoOperando.VG, Integer.toString(y.getLugar()));
			else 
				op[2] = new Operando(tipoOperando.VL, Integer.toString(y.getLugar()));

			if(e.isEnGlobal())
				op[3] = new Operando(tipoOperando.VG, Integer.toString(e.getLugar()));
			else 
				op[3] = new Operando(tipoOperando.VL, Integer.toString(e.getLugar()));


			gestorIntermedio.emite("operacion" , op); // y y e
			break;

		case 13: // ( L 13 )
			l = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			b = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			op[0] = new Operando(tipoOperando.OP, "param");
			LinkedList<Integer> lugares = (LinkedList<Integer>) l.getLugares();
			for (Integer lugar : lugares) {
				op[1] = new Operando(tipoOperando.OP, lugar.toString());
				gestorIntermedio.emite("param",op);
			}
			op[0] = new Operando(tipoOperando.OP, "call");
			op[1] = new Operando(tipoOperando.VG, b.getVerdad());
			System.out.println(b.getVerdad());
			String func = b.getVerdad().substring(3, b.getVerdad().length());
			int posicion = gestorTablas.buscarPosLexemaTS(func, 501).getLeft();
			op[2] = new Operando(tipoOperando.OP, "null");
			if(!gestorTablas.buscarTipoRetornoTS(posicion).equals("")){
				op[2] = new Operando(tipoOperando.VG, Integer.toString(b.getLugar()));
				
			}

			gestorIntermedio.emite("call", op);
			break;

		case 14: // function H ID ( A ) 114 { C 171 } 
			v = pilaAux.get(pilaAux.size() - 7).getRight().getTemporal();

			int pos3 = pilaAux.get(pilaAux.size() - 4).getRight().getPosLexema();
			v.setVerdad(gestorTablas.buscarEtiqueta(pos3));
			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, v.getVerdad());

			gestorIntermedio.emite("etiquetaFuncion", op);
			break;

		case 71: // function H ID ( A ) 114 { C 171 } 
			if(!returned) {
				returned = false;
				op[0] = new Operando(tipoOperando.OP, "return");
				op[1] = new Operando(tipoOperando.VG, "null");
				gestorIntermedio.emite("return", op);
			}
			v = pilaAux.get(pilaAux.size() - 9).getRight().getTemporal();
			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, v.getVerdad() + "_final");

			gestorIntermedio.emite("etiqueta", op);
			break;

		case 15: // E 15 Q 16

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			q = it.next().getRight().getTemporal(); // fin del acceso
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			q.aniadirLugares(e.getLugar());
			q.setCont(1);
			break;

		case 16: // E 15 Q 16
			q = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			l = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			l.setLugares(q.getLugares());
			l.setCont(q.getCont());
			break;

		case 17: // , E 17 Q1 18

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			q1 = it.next().getRight().getTemporal(); // fin del acceso
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			q = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			q1.setLugares(q.getLugares());
			q1.aniadirLugares(e.getLugar());
			q1.setCont(q1.getCont() + 1);
			break;

		case 18: // , E 17 Q1 18
			q1 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			q = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			q.setLugares(q1.getLugares());
			q.setCont(q1.getCont());
			break;

		case 19: // R 19 D 50 
			r = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			d = it.next().getRight().getTemporal(); // fin del acceso
			d.setLugar(r.getLugar(),r.isEnGlobal());
			break;

		case 50: // R 19 D 50
			e = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			d = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			e.setLugar(d.getLugar(),d.isEnGlobal());
			break;

		case 20: // || R 20 D1 21
			r = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			d = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			d1 = it.next().getRight().getTemporal(); // fin del acceso
			despl = tablatemporales.newTemporal("logico");
			d1.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				d1.setEnGlobal(false);
			else
				d1.setEnGlobal(true);
			op[0] = new Operando(tipoOperando.OP, "OR");
			if(d1.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(d1.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(d1.getLugar()));

			if(d.isEnGlobal()) 
				op[2] = new Operando(tipoOperando.VG, Integer.toString(d.getLugar()));
			else 
				op[2] = new Operando(tipoOperando.VL, Integer.toString(d.getLugar()));

			if(r.isEnGlobal()) 
				op[3] = new Operando(tipoOperando.VG, Integer.toString(r.getLugar()));
			else 
				op[3] = new Operando(tipoOperando.VL, Integer.toString(r.getLugar()));

			gestorIntermedio.emite("operacion" , op); // d1 d r
			break;

		case 21:// || R 20 D1 21
			d1 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			d = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			d.setLugar(d1.getLugar(),d.isEnGlobal());
			break;

		case 22: // U 22 G 23
			u = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			g = it.next().getRight().getTemporal(); // fin del acceso
			g.setLugar(u.getLugar(),u.isEnGlobal());
			break;

		case 23: // U 22 G 23
			g = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			r = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			r.setLugar(g.getLugar(),g.isEnGlobal());
			break;

		case 24:
			// == U 24 G1 157
			g = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			u = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			g.setVerdad(etiquetador.generarEtiqueta());
			g.setFalso(etiquetador.generarEtiqueta());
			g.setSalida(etiquetador.generarEtiqueta());
			int aux = g.getLugar();
			boolean gEstabaEnLocal = g.isEnGlobal();
			despl = tablatemporales.newTemporal("logico");
			g.setLugar(despl.getLeft(),despl.getRight() == 0);

			if(gEstabaEnLocal)
				op[0] = new Operando(tipoOperando.VG, Integer.toString(aux));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(aux));
			op[1] = new Operando(tipoOperando.OP, "=");
			if(u.isEnGlobal())
				op[2] = new Operando(tipoOperando.VG, Integer.toString(u.getLugar()));
			else
				op[2] = new Operando(tipoOperando.VL, Integer.toString(u.getLugar()));
			op[3] = new Operando(tipoOperando.VG, g.getVerdad());
			gestorIntermedio.emite("if", op);

			op[0] = new Operando(tipoOperando.OP, "goto");
			op[1] = new Operando(tipoOperando.VG, g.getFalso());
			gestorIntermedio.emite("goto", op);

			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, g.getVerdad());
			gestorIntermedio.emite("etiqueta", op);

			if(g.isEnGlobal())
				op[0] = new Operando(tipoOperando.VG, Integer.toString(g.getLugar()));
			else
				op[0] = new Operando(tipoOperando.VL, Integer.toString(g.getLugar()));
			op[1] = new Operando(tipoOperando.CE, "1");
			gestorIntermedio.emite("asignacion" , op);

			op[0] = new Operando(tipoOperando.OP, "goto");
			op[1] = new Operando(tipoOperando.VG, g.getSalida());
			gestorIntermedio.emite("goto", op);

			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, g.getFalso());
			gestorIntermedio.emite("etiqueta", op);

			if(g.isEnGlobal())
				op[0] = new Operando(tipoOperando.VG, Integer.toString(g.getLugar()));
			else
				op[0] = new Operando(tipoOperando.VL, Integer.toString(g.getLugar()));
			op[1] = new Operando(tipoOperando.CE, "0");
			gestorIntermedio.emite("asignacion" , op);

			op[0] = new Operando(tipoOperando.OP, "etiqueta");
			op[1] = new Operando(tipoOperando.VG, g.getSalida());
			gestorIntermedio.emite("etiqueta", op);
			
			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			d1 = it.next().getRight().getTemporal(); // fin del acceso
			d1.setLugar(u.getLugar(),u.isEnGlobal());
			break;

		case 57: // == U 24 G1 157
			g = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			if (pilaAux.get(pilaAux.size() - 7).getLeft().equals("G")) {
				b = pilaAux.get(pilaAux.size() - 7).getRight().getTemporal();
				op[0] = new Operando(tipoOperando.OP, "OR");
				if(b.isEnGlobal()) 
					op[1] = new Operando(tipoOperando.VG, Integer.toString(b.getLugar()));
				else 
					op[1] = new Operando(tipoOperando.VL, Integer.toString(b.getLugar()));

				if(g.isEnGlobal()) 
					op[2] = new Operando(tipoOperando.VG, Integer.toString(g.getLugar()));
				else 
					op[2] = new Operando(tipoOperando.VL, Integer.toString(g.getLugar()));

				if(b.isEnGlobal()) 
					op[3] = new Operando(tipoOperando.VG, Integer.toString(b.getLugar()));
				else 
					op[3] = new Operando(tipoOperando.VL, Integer.toString(b.getLugar()));

				gestorIntermedio.emite("operacion" , op);
			}
			break;

		case 25: // V 25 I 26
			v = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			i = it.next().getRight().getTemporal(); // fin del acceso
			i.setLugar(v.getLugar(),v.isEnGlobal());
			break;

		case 26: // V 25 I 26
			i = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			u = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			u.setLugar(i.getLugar(),i.isEnGlobal());
			
			if(i.isModified()) {
				v = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
                u.setLugar(i.getLugar(),v.isEnGlobal());
			}
				
			break;

		case 27: // + V 27 I1 28
			i = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			v = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			i.setModified(true);
			
			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			i1 = it.next().getRight().getTemporal(); // fin del acceso
			despl = tablatemporales.newTemporal("entero");
			i1.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				i1.setEnGlobal(false);
			else
				i1.setEnGlobal(true);
			op[0] = new Operando(tipoOperando.OP, "+");
			if(i1.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(i1.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(i1.getLugar()));

			if(i.isEnGlobal()) 
				op[2] = new Operando(tipoOperando.VG, Integer.toString(i.getLugar()));
			else 
				op[2] = new Operando(tipoOperando.VL, Integer.toString(i.getLugar()));

			if(v.isEnGlobal()) 
				op[3] = new Operando(tipoOperando.VG, Integer.toString(v.getLugar()));
			else 
				op[3] = new Operando(tipoOperando.VL, Integer.toString(v.getLugar()));

			gestorIntermedio.emite("operacion" , op);
			break;

		case 28:// + V 27 I1 28
			i = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();
			i1 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			i.setLugar(i1.getLugar(),i1.isEnGlobal());
			break;

		case 29: // W 29 J 30
			w = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();

			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			j = it.next().getRight().getTemporal(); // fin del acceso
			j.setLugar(w.getLugar(),w.isEnGlobal());
			break;

		case 30:// W 29 J 30
			j = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			v = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();
			if(venimosDeLlamada) {
				venimosDeLlamada = false; 
				w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
				v.setLugar(j.getLugar(),w.isEnGlobal());
			}else if(!j.isModified()){
				w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
				v.setLugar(j.getLugar(),w.isEnGlobal());
			}
			else if(venimosDePrint){
				venimosDeLlamada = false; 
				w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
				v.setLugar(j.getLugar(),w.isEnGlobal());
			} else {
				v.setLugar(j.getLugar(),j.isEnGlobal());
			}
			break;

		case 31: // * W 31 J1 32
			w = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			j = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			j.setModified(true);
			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			j1 = it.next().getRight().getTemporal(); // fin del acceso
			despl = tablatemporales.newTemporal("entero");
			j1.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				j1.setEnGlobal(false);
			else
				j1.setEnGlobal(true);

			op[0] = new Operando(tipoOperando.OP, "*");
			if(j1.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(j1.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(j1.getLugar()));

			if(j.isEnGlobal()) 
				op[2] = new Operando(tipoOperando.VG, Integer.toString(j.getLugar()));
			else 
				op[2] = new Operando(tipoOperando.VL, Integer.toString(j.getLugar()));

			if(w.isEnGlobal()) 
				op[3] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			else 
				op[3] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));

			gestorIntermedio.emite("operacion" , op);
			break;

		case 32: // * W 31 J1 32
			j1 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			j = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			j.setLugar(j1.getLugar(),j1.isEnGlobal());
			break;

		case 33: // / W 33 J 34
			w = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			j = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			j.setModified(true);
			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			j1 = it.next().getRight().getTemporal(); // fin del acceso
			despl = tablatemporales.newTemporal("entero");
			j1.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				j1.setEnGlobal(false);
			else
				j1.setEnGlobal(true);

			op[0] = new Operando(tipoOperando.OP, "/");
			if(j1.isEnGlobal()) 
				op[1] = new Operando(tipoOperando.VG, Integer.toString(j1.getLugar()));
			else 
				op[1] = new Operando(tipoOperando.VL, Integer.toString(j1.getLugar()));

			if(j.isEnGlobal()) 
				op[2] = new Operando(tipoOperando.VG, Integer.toString(j.getLugar()));
			else 
				op[2] = new Operando(tipoOperando.VL, Integer.toString(j.getLugar()));

			if(w.isEnGlobal()) 
				op[3] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			else 
				op[3] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));

			gestorIntermedio.emite("operacion" , op); // j1 j w
			break;

		case 34:// / W 33 J 34
			j1 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			j = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			j.setLugar(j1.getLugar(),j1.isEnGlobal());
			break;

		case 35: // ( E )
			e = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
			w = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();

			w.setLugar(e.getLugar(),e.isEnGlobal());
			break;

		case 36: // Entero
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			despl = tablatemporales.newTemporal("entero");
			w.setLugar(despl.getLeft(),despl.getRight() == 0);
			if (despl.getRight() == 1) 
				w.setEnGlobal(false);
			else 
				w.setEnGlobal(true);

			if(w.isEnGlobal()) 
				op[0] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));

			op[1] = new Operando(tipoOperando.CE, Integer.toString(pilaAux.get(pilaAux.size() - 1).getRight().getValorEntero()));
			
			gestorIntermedio.emite("asignacion" , op);
			break;

		case 37: // Cadena
			e = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			despl = tablatemporales.newTemporal("cadena");
			
			w.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				w.setEnGlobal(false);
			else
				w.setEnGlobal(true);
			if(w.isEnGlobal()) 
				op[0] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			else 
				op[0] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));

			op[1] = new Operando(tipoOperando.CS,"Cadena_" + listaCadenas.indexOf(listaCadenas.getLast()));

			gestorIntermedio.emite("asignacion" , op);
			break;

		case 38: // true
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			despl = tablatemporales.newTemporal("logico");
			w.setLugar(despl.getLeft(),despl.getRight() == 0);
			if (despl.getRight() == 1)
				w.setEnGlobal(false);
			else
				w.setEnGlobal(true);
			if(w.isEnGlobal()) {
				op[0] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			}else {
				op[0] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));
			}
			op[1] = new Operando(tipoOperando.CE, "1");

			gestorIntermedio.emite("asignacion" , op);
			break;

		case 39: // false
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();

			despl = tablatemporales.newTemporal("logico");
			w.setLugar(despl.getLeft(),despl.getRight()==0);
			if (despl.getRight() == 1)
				w.setEnGlobal(false);
			else
				w.setEnGlobal(true);
			if(w.isEnGlobal()) {
				op[0] = new Operando(tipoOperando.VG, Integer.toString(w.getLugar()));
			}else {
				op[0] = new Operando(tipoOperando.VL, Integer.toString(w.getLugar()));
			}
			op[1] = new Operando(tipoOperando.CE, "0");

			gestorIntermedio.emite("asignacion" , op);
			break;

		case 40: // ( L )
			l = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
			z = pilaAux.get(pilaAux.size() - 4).getRight().getTemporal();
			break;

		case 55: // W -> ID 155 Z 156
			w = pilaAux.get(pilaAux.size() - 2).getRight().getTemporal();
			// acceso al elemento de la pila que necesito
			it = pila.iterator();
			it.next();
			z = it.next().getRight().getTemporal(); // fin del acceso
			int pos2 = pilaAux.get(pilaAux.size() - 1).getRight().getPosLexema();
			boolean enGlobal2 = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal().isEnGlobal();
			despl = gestorTablas.buscarLugar(pos2);
			w.setLugar(despl.getLeft(),despl.getRight()==0);
			w.setEnGlobal(pilaAux.get(pilaAux.size() - 1).getRight().getTemporal().isEnGlobal());
			if (gestorTablas.buscarTipoTS(pos2, enGlobal2).equals("funcion")) {
				z.setVerdad(gestorTablas.buscarEtiqueta(pos2));
				despl = tablatemporales.newTemporal(gestorTablas.buscarTipoRetornoTS(pos2));
				z.setLugar(despl.getLeft(), despl.getRight() == 0);
				w.setFalso(gestorTablas.buscarTipoRetornoTS(pos2));
			}
			
			String print = pilaAux.get(pilaAux.size() - 8).getLeft(); //auxiliar para saber si venimos de print
			if(print.equals("print")) {
				venimosDePrint = true; 
			}
			
			break;

		case 56: // ID 155 Z 156

			z = pilaAux.get(pilaAux.size() - 1).getRight().getTemporal();
			w = pilaAux.get(pilaAux.size() - 3).getRight().getTemporal();

			
			if (w.getFalso() != null) {
				
				w.setLugar(z.getLugar(),z.isEnGlobal());
				venimosDeLlamada = true; 
			}
			break; 

		default:
			gestorIntermedio.emite("EOF",null);
			break;
		}
	}

	public void printearPilaAux(Stack<Pair<String, AtributosSem>> pilaAux) {
		for (Pair<String, AtributosSem> pair : pilaAux) {
			System.out.println(pair.getLeft());
		}
	}

	public void printearPila(Deque<Pair<String, AtributosSem>> pila) {
		for (Pair<String, AtributosSem> pair : pila) {
			System.out.println(pair.getLeft());
		}
	}

	/*     imagen  = filtro (imagen1, imagen2, numero) ; 
	 * variable global (VG + despl) variable local parametro temporal.... ( VI ,
	 * despl ) cte (Cte , )
	 */
}
