package analizadorSemantico;

import java.io.IOException;
import java.util.Deque;
//import java.util.Iterator;
import java.util.Stack;

import analizadorLexico.Pair;
import codigoIntermedio.AccionesIntermedio;
import gestorErrores.GestorErrores;
import gestorTablas.GestorTablas;

public class ASem {
	private Stack<Pair<String, AtributosSem>> pilaAux;
	private GestorTablas gestorTablas;
	private GestorErrores gestorErrores;
	private boolean zonaDeclarativa;
	private boolean enFuncion;

	private AccionesIntermedio intermedio;

	public ASem(GestorTablas gestorTablas, GestorErrores gestorErrores) {
		this.gestorTablas = gestorTablas;
		this.gestorErrores = gestorErrores;
		zonaDeclarativa = false;
		enFuncion = false;
		pilaAux = new Stack<>();
	}

	public Stack<Pair<String, AtributosSem>> getPilaAux() {
		return pilaAux;
	}

	public void ejecutarAccion(String puntero, Deque<Pair<String, AtributosSem>> pila) throws IOException {
		AtributosSem s, id, e, b, t, c, c1, x, y, l, h, q, q1, a, k, d, r, d1, v, g, g1, i, w, j, j1, z, f, k1, u, i1;

		switch (Integer.parseInt(puntero)) {
		case 0: /* B-> var T id ; */
			zonaDeclarativa = true;
			break;

		case 1: /* B-> var T id ; */
			t = pilaAux.get(pilaAux.size()-2).getRight();
			b = pilaAux.get(pilaAux.size()-4).getRight();

			gestorTablas.aniadirTipoTS(t.getTipo(), false);
			gestorTablas.aniadirDesplazamientoTS();
			gestorTablas.sumarDesplazamientoTS(t.getTamanio());
			b.setTipo("OK");
			b.setRet("");
			zonaDeclarativa = false;

			intermedio.ejecutarIntermedio(Integer.toString(101), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 2: /* B-> if ( E ) S */
			s = pilaAux.get(pilaAux.size()-1).getRight();
			e = pilaAux.get(pilaAux.size()-3).getRight();
			b = pilaAux.get(pilaAux.size()-6).getRight();

			if (!e.getTipo().equals("logico")) {
				b.setTipo("error");
				gestorErrores.generarError("Semantico", "Se esperaba tipo logico pero se ha recibido otro",pilaAux.get(pilaAux.size()-2).getRight().getNlin());
			} else {
				b.setTipo(s.getTipo());
			}
			b.setRet(s.getRet());
			b.setNlin(s.getNlin());

			intermedio.ejecutarIntermedio(Integer.toString(103), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 3: /* B-> do { C } while ( E ) ; */
			e = pilaAux.get(pilaAux.size()-3).getRight();
			c = pilaAux.get(pilaAux.size()-7).getRight();
			b = pilaAux.get(pilaAux.size()-10).getRight();

			if (e.getTipo().equals("logico") && c.getTipo().equals("OK")) {
				b.setTipo("OK");
			} else {
				b.setTipo("error"); /* S.tipo */
				gestorErrores.generarError("Semantico","La sentencia del do no es de tipo logico",pilaAux.get(pilaAux.size()-1).getRight().getNlin());
			}
			b.setRet(c.getRet());
			b.setNlin(c.getNlin());

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 4:/* B -> S */
			s = pilaAux.get(pilaAux.size()-1).getRight();
			b = pilaAux.get(pilaAux.size()-2).getRight();

			b.setTipo(s.getTipo());
			b.setRet(s.getRet());
			b.setNlin(s.getNlin());

			pilaAux.pop();
			break;

		case 5:/* T -> int */
			t = pilaAux.get(pilaAux.size()-2).getRight();

			t.setTipo("entero");
			t.setTamanio(1);

			pilaAux.pop();
			break;

		case 6:/* T -> bool */
			t = pilaAux.get(pilaAux.size()-2).getRight();

			t.setTipo("logico");
			t.setTamanio(1);

			pilaAux.pop();
			break;

		case 7:/* T -> string */
			t = pilaAux.get(pilaAux.size()-2).getRight();

			t.setTipo("cadena");
			t.setTamanio(1);   // son punteros a las variables string

			pilaAux.pop();
			break;

		case 8:/* S -> id Y ; */
			y = pilaAux.get(pilaAux.size()-2).getRight();
			id = pilaAux.get(pilaAux.size()-3).getRight();
			s = pilaAux.get(pilaAux.size()-4).getRight();

			if (gestorTablas.buscarTipoTS(id.getPosLexema(), id.getTemporal().isEnGlobal()).equals("funcion")) {
				if (gestorTablas.buscarTipoParametrosTS(id.getPosLexema()).equals(y.getTipo())) {
					s.setTipo("OK");
				} else {
					s.setTipo("error");
					gestorErrores.generarError("Semantico", "Los tipos de los parametros de la funcion llamada no coinciden con los utilizados",id.getNlin());
				}
			} else if (gestorTablas.buscarTipoTS(id.getPosLexema(), id.getTemporal().isEnGlobal()).equals(y.getTipo())) {
				s.setTipo("OK");
			} else {
				s.setTipo("error");
				System.out.println(gestorTablas.buscarTipoTS(id.getPosLexema(), id.getTemporal().isEnGlobal()) + y.getTipo());
				gestorErrores.generarError("Semantico", "El tipo del id no coincide con el dado", id.getNlin());
			}
			s.setRet("");

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 9:/* S -> return X ; */
			x = pilaAux.get(pilaAux.size()-2).getRight();
			s = pilaAux.get(pilaAux.size()-4).getRight();

			if(!enFuncion)
				gestorErrores.generarError("Semantico", "No se puede invocar un return fuera de una funcion",pilaAux.get(pilaAux.size()-3).getRight().getNlin());
			s.setTipo("OK");
			s.setRet(x.getTipo());
			s.setNlin(pilaAux.get(pilaAux.size()-3).getRight().getNlin());

			intermedio.ejecutarIntermedio(Integer.toString(107), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 10:/* S -> print ( E ) ; */
			s = pilaAux.get(pilaAux.size()-6).getRight();

			s.setTipo("OK");
			s.setRet("");

			intermedio.ejecutarIntermedio(Integer.toString(108), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 11:/* S -> prompt ( id ) ; */
			id = pilaAux.get(pilaAux.size()-3).getRight();
			s = pilaAux.get(pilaAux.size()-6).getRight();

			if (gestorTablas.buscarTipoTS(id.getPosLexema(), id.getTemporal().isEnGlobal()).equals("logico")) {
				s.setTipo("error");
			} else {
				s.setTipo("OK");
			}
			s.setRet("");

			intermedio.ejecutarIntermedio(Integer.toString(109), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 12:/* X -> E */
			e = pilaAux.get(pilaAux.size()-1).getRight();
			x = pilaAux.get(pilaAux.size()-2).getRight();

			x.setTipo(e.getTipo());

			intermedio.ejecutarIntermedio(Integer.toString(110), pila);

			pilaAux.pop();
			break;

		case 13:/* X -> lambda */
			x = pilaAux.get(pilaAux.size()-1).getRight();

			x.setTipo("");
			break;

		case 14:/* Y -> = E y Y -> |= E */
			e = pilaAux.get(pilaAux.size()-1).getRight();
			y = pilaAux.get(pilaAux.size()-3).getRight();

			y.setTipo(e.getTipo());

			if(pilaAux.get(pilaAux.size()-2).getLeft().equals("=")) 
				intermedio.ejecutarIntermedio(Integer.toString(111), pila);
			else 
				intermedio.ejecutarIntermedio(Integer.toString(112), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 15:/* Y -> ( L ) */
			l = pilaAux.get(pilaAux.size()-2).getRight();
			y = pilaAux.get(pilaAux.size()-4).getRight();

			y.setTipo(l.getTipo());

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 16:/* C -> B C1 */
			c1 = pilaAux.get(pilaAux.size()-1).getRight();
			b = pilaAux.get(pilaAux.size()-2).getRight();
			c = pilaAux.get(pilaAux.size()-3).getRight();

			if (b.getTipo().equals("OK") && (c1.getTipo().equals("OK") || c1.getTipo().isEmpty())) {
				c.setTipo("OK");
			} else if(b.getTipo().equals("OK")){
				b.setRet("error");
				gestorErrores.generarError("Semantico", "No se puede construir el contenido de un bloque por tipo error",b.getNlin());
			} 
			if (b.getRet().equals(c1.getRet())) {
				c.setRet(b.getRet());
			} else if (b.getRet().isEmpty()) {
				c.setRet(c1.getRet());
			} else if (c1.getRet().isEmpty()) {
				c.setRet(b.getRet());
			} else {
				b.setRet("error");
				gestorErrores.generarError("Semantico", "No se puede establecer un tipo de retorno",b.getNlin());
			}

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 17:/* C -> lambda */
			c = pilaAux.get(pilaAux.size()-1).getRight();

			c.setTipo("");
			c.setRet("");
			break;

		case 18:/* F -> function H id  */
			id = pilaAux.get(pilaAux.size()-1).getRight();

			if (enFuncion) 
				gestorErrores.generarError("Semantico","No se pueden anidar funciones", id.getNlin());
			gestorTablas.initLocal(id.getLexema(), id.getPosLexema());
			zonaDeclarativa = true;

			break;

		case 19:/* F -> function H id ( A ) */
			a = pilaAux.get(pilaAux.size()-2).getRight();
			id = pilaAux.get(pilaAux.size()-4).getRight();
			h = pilaAux.get(pilaAux.size()-5).getRight();

			gestorTablas.aniadirTipoTS("funcion", true);
			gestorTablas.aniadirNumeroParametrosTS(a.getTipo().split(",").length);
			gestorTablas.aniadirTipoParametrosTS(a.getTipo());
			gestorTablas.aniadirTipoRetornoTS(h.getTipo());
			gestorTablas.aniadirEtiquetaTS(gestorTablas.buscarEtiqueta(id.getPosLexema()));
			enFuncion=true;
			zonaDeclarativa = false;

			break;

		case 20:/* F -> function H id ( A ) { C  */
			c = pilaAux.get(pilaAux.size()-1).getRight();
			h = pilaAux.get(pilaAux.size()-7).getRight();
			f = pilaAux.get(pilaAux.size()-9).getRight();

			if (c.getRet().equals(h.getTipo())) {
				f.setTipo("OK");
			} else {
				f.setTipo("error");
				gestorErrores.generarError("Semantico","El retorno de la funcion no coincide con el return del cuerpo", pilaAux.get(pilaAux.size()-8).getRight().getNlin());
			}
			enFuncion=false;

			intermedio.ejecutarIntermedio(Integer.toString(171), pila);

			gestorTablas.borrarLocal();

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 21:/* H -> T */
			t = pilaAux.get(pilaAux.size()-1).getRight();
			h = pilaAux.get(pilaAux.size()-2).getRight();

			h.setTipo(t.getTipo());

			pilaAux.pop();
			break;

		case 22:/* H -> lambda */
			h = pilaAux.get(pilaAux.size()-1).getRight();

			h.setTipo("");
			break;

		case 23:/* L -> E Q */
			q = pilaAux.get(pilaAux.size()-1).getRight();
			e = pilaAux.get(pilaAux.size()-2).getRight();
			l = pilaAux.get(pilaAux.size()-3).getRight();

			l.setTipo(e.getTipo() + q.getTipo());	

			intermedio.ejecutarIntermedio(Integer.toString(116), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 24:/* L -> lambda */
			l = pilaAux.get(pilaAux.size()-1).getRight();

			l.setTipo("");
			break;

		case 25:/* Q -> , E Q1 */
			q1 = pilaAux.get(pilaAux.size()-1).getRight();
			e = pilaAux.get(pilaAux.size()-2).getRight();
			q = pilaAux.get(pilaAux.size()-4).getRight();

			q.setTipo("," + e.getTipo() + q1.getTipo());

			intermedio.ejecutarIntermedio(Integer.toString(118), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 26:/* Q -> lambda */
			q = pilaAux.get(pilaAux.size()-1).getRight();

			q.setTipo("");
			break;

		case 27:/* A -> T id   y   K -> , T id*/
			t = pilaAux.get(pilaAux.size()-2).getRight();	

			gestorTablas.aniadirTipoTS(t.getTipo(),false);
			gestorTablas.aniadirDesplazamientoTS();
			gestorTablas.sumarDesplazamientoTS(t.getTamanio());
			break;

		case 28: /* A -> T id K */
			k = pilaAux.get(pilaAux.size()-1).getRight();
			t = pilaAux.get(pilaAux.size()-3).getRight();
			a = pilaAux.get(pilaAux.size()-4).getRight();

			a.setTipo(t.getTipo() + k.getTipo());

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 29:/* A -> lambda */
			a = pilaAux.get(pilaAux.size()-1).getRight();

			a.setTipo("");
			break;

		case 30:/* K -> , T id K1 */
			k1 = pilaAux.get(pilaAux.size()-1).getRight();
			t = pilaAux.get(pilaAux.size()-3).getRight();
			k = pilaAux.get(pilaAux.size()-5).getRight();

			k.setTipo(","+t.getTipo() + k1.getTipo());

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 31:/* K -> lambda */
			k = pilaAux.get(pilaAux.size()-1).getRight();

			k.setTipo("");
			break;

		case 32:/* E -> R D */
			d = pilaAux.get(pilaAux.size()-1).getRight();
			r = pilaAux.get(pilaAux.size()-2).getRight();
			e = pilaAux.get(pilaAux.size()-3).getRight();

			if ((r.getTipo().equals("logico") && d.getTipo().equals("logico")) || d.getTipo().isEmpty()) {
				e.setTipo(r.getTipo());
			} else {
				e.setTipo("error");
				gestorErrores.generarError("Semantico", "El tipo de los elementos de la expresion no coincide",r.getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(150), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 33:/* D -> || R D1 */
			d1 = pilaAux.get(pilaAux.size()-1).getRight();
			r = pilaAux.get(pilaAux.size()-2).getRight();
			d = pilaAux.get(pilaAux.size()-4).getRight();

			if (r.getTipo().equals("logico") && (d1.getTipo().equals("logico") || d1.getTipo().isEmpty())) {
				d.setTipo("logico");
			} else {
				d.setTipo("error");
				gestorErrores.generarError("Semantico", "Se esperaba tipo logico pero se ha recibido otro",pilaAux.get(pilaAux.size()-3).getRight().getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(121), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 34:/* D -> lambda */
			d = pilaAux.get(pilaAux.size()-1).getRight();

			d.setTipo("");
			break;

		case 35:/* R -> U G */
			g = pilaAux.get(pilaAux.size()-1).getRight();
			u = pilaAux.get(pilaAux.size()-2).getRight();
			r = pilaAux.get(pilaAux.size()-3).getRight();

			r.setNlin(u.getNlin());
			if (u.getTipo().equals("entero") && g.getTipo().equals("entero")) {
				r.setTipo("logico");
			} else if(g.getTipo().isEmpty()){
				r.setTipo(u.getTipo());
			} else {
				r.setTipo("error");
				gestorErrores.generarError("Semantico", "El tipo de los elementos de la expresion no coincide",r.getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(123), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 36:/* G -> == U G1 */
			g1 = pilaAux.get(pilaAux.size()-1).getRight();
			u = pilaAux.get(pilaAux.size()-2).getRight();
			g = pilaAux.get(pilaAux.size()-4).getRight();

			if (u.getTipo().equals("entero") && (g1.getTipo().equals("entero") || g1.getTipo().isEmpty())) {
				g.setTipo("entero");
			} else {
				g.setTipo("error");
				gestorErrores.generarError("Semantica", "Se esperaba tipo entero pero se ha recibido otro",pilaAux.get(pilaAux.size()-3).getRight().getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(157), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 37:/* G -> lambda */
			g = pilaAux.get(pilaAux.size()-1).getRight();

			g.setTipo("");
			break;

		case 38:/* U -> V I */
			i = pilaAux.get(pilaAux.size()-1).getRight();
			v = pilaAux.get(pilaAux.size()-2).getRight();
			u = pilaAux.get(pilaAux.size()-3).getRight();	

			u.setNlin(v.getNlin());
			if (v.getTipo().equals("entero") && i.getTipo().equals("entero") || i.getTipo().isEmpty()) {
				u.setTipo(v.getTipo());
			} else {
				u.setTipo("error");
				gestorErrores.generarError("Semantico", "El tipo de los elementos de la operacion no coincide",u.getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(126), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 39:/* I -> + V I1 */
			i1 = pilaAux.get(pilaAux.size()-1).getRight();
			v = pilaAux.get(pilaAux.size()-2).getRight();
			i = pilaAux.get(pilaAux.size()-4).getRight();

			if (v.getTipo().equals("entero") && (i1.getTipo().equals("entero") || i1.getTipo().isEmpty())) {
				i.setTipo("entero");
			} else {
				i.setTipo("error");
				gestorErrores.generarError("Semantico", "Se esperaba tipo entero pero se ha recibido otro",pilaAux.get(pilaAux.size()-3).getRight().getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(128), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 40:/* I -> lambda */
			i = pilaAux.get(pilaAux.size()-1).getRight();

			i.setTipo("");
			break;

		case 41:/* V -> W J */
			j = pilaAux.get(pilaAux.size()-1).getRight();
			w = pilaAux.get(pilaAux.size()-2).getRight();
			v = pilaAux.get(pilaAux.size()-3).getRight();

			v.setNlin(w.getNlin());
			if (w.getTipo().equals("entero") && j.getTipo().equals("entero") || j.getTipo().isEmpty()) {
				v.setTipo(w.getTipo());
			} else {
				v.setTipo("error");
				gestorErrores.generarError("Semantico", "El tipo de los elementos de la expresion no coincide",v.getNlin());
			}

			intermedio.ejecutarIntermedio(Integer.toString(130), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 42:/* J -> * W J1 y J -> / W J1 */
			j1 = pilaAux.get(pilaAux.size()-1).getRight();
			w = pilaAux.get(pilaAux.size()-2).getRight();
			j = pilaAux.get(pilaAux.size()-4).getRight();

			if (w.getTipo().equals("entero") && (j1.getTipo().equals("entero") || j1.getTipo().isEmpty())) {
				j.setTipo("entero");
			} else {
				j.setTipo("error");
				gestorErrores.generarError("Semantico", "Se esperaba tipo entero pero se ha recibido otro",pilaAux.get(pilaAux.size()-3).getRight().getNlin());
			}	

			if(pilaAux.get(pilaAux.size()-3).getLeft().equals("*"))
				intermedio.ejecutarIntermedio(Integer.toString(132), pila);
			else
				intermedio.ejecutarIntermedio(Integer.toString(134), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 43:/* J -> lambda */
			j = pilaAux.get(pilaAux.size()-1).getRight();

			j.setTipo("");
			break;

		case 44:/* W -> ( E ) */
			e = pilaAux.get(pilaAux.size()-2).getRight();
			w = pilaAux.get(pilaAux.size()-4).getRight();

			w.setTipo(e.getTipo());
			w.setNlin(pilaAux.get(pilaAux.size()-3).getRight().getNlin());

			intermedio.ejecutarIntermedio(Integer.toString(135), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 45:/* W -> entero */
			w = pilaAux.get(pilaAux.size()-2).getRight();

			w.setTipo("entero");
			w.setNlin(pilaAux.get(pilaAux.size()-1).getRight().getNlin());

			intermedio.ejecutarIntermedio(Integer.toString(136), pila);

			pilaAux.pop();
			break;

		case 46:/* W -> cadena */
			w = pilaAux.get(pilaAux.size()-2).getRight();

			w.setTipo("cadena");
			w.setNlin(pilaAux.get(pilaAux.size()-1).getRight().getNlin());	

			intermedio.ejecutarIntermedio(Integer.toString(137), pila);

			pilaAux.pop();
			break;

		case 47:/* W -> id Z */
			z = pilaAux.get(pilaAux.size()-1).getRight();
			id = pilaAux.get(pilaAux.size()-2).getRight();
			w = pilaAux.get(pilaAux.size()-3).getRight();

			w.setNlin(pilaAux.get(pilaAux.size()-2).getRight().getNlin());
			if (z.getTipo().isEmpty()) {
				w.setTipo(gestorTablas.buscarTipoTS(id.getPosLexema(), id.getTemporal().isEnGlobal()));
			}else if(gestorTablas.buscarTipoParametrosTS(id.getPosLexema()).equals(z.getTipo())){
				w.setTipo(gestorTablas.buscarTipoRetornoTS(id.getPosLexema()));
			} else {
				w.setTipo("error");
				gestorErrores.generarError("Semantico", "Los tipos de los parametros de la funcion llamada no coinciden con los utilizados",pilaAux.get(pilaAux.size()-1).getRight().getNlin());
			}	

			intermedio.ejecutarIntermedio(Integer.toString(156), pila);

			pilaAux.pop();
			pilaAux.pop();
			break;

		case 48:/* W -> true y W -> false */
			w = pilaAux.get(pilaAux.size()-2).getRight();

			w.setTipo("logico");
			w.setNlin(pilaAux.get(pilaAux.size()-1).getRight().getNlin());

			if(pilaAux.get(pilaAux.size()-1).getLeft().equals("true"))
				intermedio.ejecutarIntermedio(Integer.toString(138), pila);
			else
				intermedio.ejecutarIntermedio(Integer.toString(139), pila);

			pilaAux.pop();
			break;

		case 49:/* Z -> ( L ) */
			l = pilaAux.get(pilaAux.size()-2).getRight();
			z = pilaAux.get(pilaAux.size()-4).getRight();

			z.setTipo(l.getTipo());

			intermedio.ejecutarIntermedio(Integer.toString(140), pila);

			pilaAux.pop();
			pilaAux.pop();
			pilaAux.pop();
			break;

		case 50:/* Z -> lambda */
			z = pilaAux.get(pilaAux.size()-1).getRight();

			z.setTipo("");
			break;

		case 70: // un pop por necesidad por si se declara una variable justo ahi
			pilaAux.pop();

		default:
			break;
		}
	}

	public boolean enZonaDeclarativa() {
		return zonaDeclarativa;
	}

	public void setIntermedio(AccionesIntermedio intermedio) {
		this.intermedio = intermedio;
	}

	public boolean getEnFuncion() {
		return this.enFuncion;
	}
}
