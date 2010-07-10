package Coco;
import java.util.*;

class Parser {
	private static final int maxT = 45;
	private static final int maxP = 46;

	private static final boolean T = true;
	private static final boolean x = false;
	private static final int minErrDist = 2;
	private static int errDist = minErrDist;

	static Token token;   // last recognized token
	static Token t;       // lookahead token

	private static final int ident = 0;
	private static final int string = 1;

	private static boolean genScanner = true, ignoreCase = false, genNames = false;
	private static boolean startedDFA = false;

	private static void MatchLiteral(int sp) {
	// store string either as token or as literal
		Symbol sym = Tab.Sym(sp);
		int matchedSp = DFA.MatchedDFA(sym.name, sp);
		if (matchedSp == Tab.noSym) sym.struct = Tab.classToken;
		else {
			Symbol sym1 = Tab.Sym(matchedSp); sym1.struct = Tab.classLitToken;
			sym.struct = Tab.litToken;
		}
	}

	private static void SetCtx(int p) {
	// set transition code to contextTrans
		while (p > 0) {
			GraphNode n = Tab.Node(p);
			if (n.typ == Tab.chr || n.typ == Tab.clas) {
				n.p2 = Tab.contextTrans;
			} else if (n.typ == Tab.opt || n.typ == Tab.iter) {
				SetCtx(n.p1);
			} else if (n.typ == Tab.alt) {
				SetCtx(n.p1); SetCtx(n.p2);
			}
			p = n.next;
		}
	}

	private static String FixString(String s) {
		if (ignoreCase) s = s.toUpperCase();
		char[] a = s.toCharArray();
		int len = a.length;
		if (len == 2) SemError(29);
		boolean spaces = false;
		int start = a[0];
		for (int i = 1; i <= len-2; i++) {
			if (a[i] <= ' ') spaces = true;
			if (a[i] == '\\') {
				if (a[i+1] == '\\' || a[i+1] == '\'' || a[i+1] == '\"') {
					for (int j = i; j < len - 1; j++) a[j] = a[j+1]; len--;
				}
			}
		}
		a[0] = '"'; a[len-1] = '"';
		if (spaces) SemError(24);
		return new String(a, 0, len);
	}

/*-------------------------------------------------------------------------*/



	static void Error(int n) {
		if (errDist >= minErrDist) Scanner.err.ParsErr(n, t.line, t.col);
		errDist = 0;
	}

	static void SemError(int n) {
		if (errDist >= minErrDist) Scanner.err.SemErr(n, token.line, token.col);
		errDist = 0;
	}

	static boolean Successful() {
		return Scanner.err.count == 0;
	}

	static String LexString() {
		return token.str;
	}

	static String LexName() {
		return token.val;
	}

	static String LookAheadString() {
		return t.str;
	}

	static String LookAheadName() {
		return t.val;
	}

	private static void Get() {
		for (;;) {
			token = t;
			t = Scanner.Scan();
			if (t.kind <= maxT) {errDist++; return;}
			if (t.kind == 46) {
				Tab.SetDDT(t.val);
			}

			t = token;
		}
	}

	private static void Expect(int n) {
		if (t.kind == n) Get(); else Error(n);
	}

	private static boolean StartOf(int s) {
		return set[s][t.kind];
	}

	private static void ExpectWeak(int n, int follow) {
		if (t.kind == n) Get();
		else {
			Error(n);
			while (!StartOf(follow)) Get();
		}
	}

	private static boolean WeakSeparator(int n, int syFol, int repFol) {
		boolean[] s = new boolean[maxT+1];
		if (t.kind == n) {Get(); return true;}
		else if (StartOf(repFol)) return false;
		else {
			for (int i = 0; i <= maxT; i++) {
				s[i] = set[syFol][i] || set[repFol][i] || set[0][i];
			}
			Error(n);
			while (!s[t.kind]) Get();
			return StartOf(syFol);
		}
	}

	private static final void AttrRest1(GraphNode n) {
		int beg, col;
		beg = t.pos; col = t.col;
		while (StartOf(1)) {
			if (StartOf(2)) {
				Get();
			} else {
				Get();
				SemError(18);
			}
		}
		Expect(36);
		if (token.pos > beg) {
		    n.pos = new Position();
		    n.pos.beg = beg; n.pos.col = col;
		    n.pos.len = token.pos - beg;
		}
	}

	private static final void AttrRest(GraphNode n) {
		int beg, col;
		beg = t.pos; col = t.col;
		while (StartOf(3)) {
			if (StartOf(4)) {
				Get();
			} else {
				Get();
				SemError(18);
			}
		}
		Expect(34);
		if (token.pos > beg) {
		  n.pos = new Position();
		  n.pos.beg = beg; n.pos.col = col;
		  n.pos.len = token.pos - beg;
		}
	}

	private static Graph TokenFactor() {
		Graph g;
		String name; int kind; SymInfo s;
		g = new Graph();
		if (t.kind == 1 || t.kind == 2) {
			s = Sym();
			if (s.kind == ident) {
			  int c = Tab.ClassWithName(s.name);
			  if (c < 0) {
			    SemError(15);
			    c = Tab.NewClass(s.name, new BitSet());
			  }
			  g.l = Tab.NewNode(Tab.clas, c, 0);
			  g.r = g.l;
			} else g = Tab.StrToGraph(s.name); // str
		} else if (t.kind == 28) {
			Get();
			g = TokenExpr();
			Expect(29);
		} else if (t.kind == 32) {
			Get();
			g = TokenExpr();
			Expect(33);
			g = Tab.Option(g);
		} else if (t.kind == 39) {
			Get();
			g = TokenExpr();
			Expect(40);
			g = Tab.Iteration(g);
		} else Error(46);
		return g;
	}

	private static Graph TokenTerm() {
		Graph g;
		Graph g2;
		g = TokenFactor();
		while (StartOf(5)) {
			g2 = TokenFactor();
			g = Tab.Sequence(g, g2);
		}
		if (t.kind == 42) {
			Get();
			Expect(28);
			g2 = TokenExpr();
			SetCtx(g2.l); g = Tab.Sequence(g, g2);
			Expect(29);
		}
		return g;
	}

	private static final void Attribs1(GraphNode n) {
		int beg, col;
		Expect(35);
		if (t.kind == 31) {
			Get();
			beg = t.pos;
			while (StartOf(6)) {
				if (StartOf(7)) {
					Get();
				} else {
					Get();
					SemError(18);
				}
			}
			n.retVar = ParserGen.GetString(beg, t.pos);
			if (t.kind == 12) {
				Get();
				AttrRest1(n);
			} else if (t.kind == 36) {
				Get();
			} else Error(47);
		} else if (StartOf(8)) {
			AttrRest1(n);
		} else Error(48);
	}

	private static final void Attribs(GraphNode n) {
		int beg, col;
		Expect(30);
		if (t.kind == 31) {
			Get();
			beg = t.pos;
			while (StartOf(9)) {
				if (StartOf(10)) {
					Get();
				} else {
					Get();
					SemError(18);
				}
			}
			n.retVar = ParserGen.GetString(beg, t.pos);
			if (t.kind == 12) {
				Get();
				AttrRest(n);
			} else if (t.kind == 34) {
				Get();
			} else Error(49);
		} else if (StartOf(8)) {
			AttrRest(n);
		} else Error(50);
	}

	private static Graph Factor() {
		Graph g;
		GraphNode n;
		SymInfo s;
		Symbol sym;
		Position pos;
		BitSet set;
		int sp;
		boolean undef, weak = false;
		g = new Graph();
		switch (t.kind) {
		case 1: case 2: case 38: {
			if (t.kind == 38) {
				Get();
				weak = true;
			}
			s = Sym();
			sp = Tab.FindSym(s.name); undef = sp == Tab.noSym;
			if (undef) {
			  if (s.kind == ident)
			    sp = Tab.NewSym(Tab.nt, s.name, 0); // forward nt
			  else if (genScanner) {
			    sp = Tab.NewSym(Tab.t, s.name, token.line);
			    MatchLiteral(sp);
			  } else { // undefined string in production
			    SemError(6); sp = 0;
			  }
			}
			sym = Tab.Sym(sp);
			int typ = sym.typ;
			if (typ != Tab.t && typ != Tab.nt) SemError(4);
			if (weak)
			    if (sym.typ == Tab.t) typ = Tab.wt; else SemError(23);
			g.l = Tab.NewNode(typ, sp, token.line); g.r = g.l;
			n = Tab.Node(g.l);
			if (t.kind == 30 || t.kind == 35) {
				if (t.kind == 30) {
					Attribs(n);
				} else {
					Attribs1(n);
				}
				if (typ != Tab.nt) SemError(3);
			}
			if (undef) {
			  sym.attrPos = n.pos; sym.retVar = n.retVar; // dummies
			} else
			  if (n.pos != null && sym.attrPos == null
			    || n.retVar != null && sym.retVar == null
			    || n.pos == null && sym.attrPos != null
			    || n.retVar == null && sym.retVar != null) SemError(5);
			break;
		}
		case 28: {
			Get();
			g = Expression();
			Expect(29);
			break;
		}
		case 32: {
			Get();
			g = Expression();
			Expect(33);
			g = Tab.Option(g);
			break;
		}
		case 39: {
			Get();
			g = Expression();
			Expect(40);
			g = Tab.Iteration(g);
			break;
		}
		case 43: {
			pos = SemText();
			g.l = Tab.NewNode(Tab.sem, 0, 0);
			g.r = g.l;
			n = Tab.Node(g.l); n.pos = pos;
			break;
		}
		case 26: {
			Get();
			set = Sets.FullSet(Tab.maxTerminals);
			set.clear(Tab.eofSy);
			g.l = Tab.NewNode(Tab.any, Tab.NewSet(set), 0);
			g.r = g.l;
			break;
		}
		case 41: {
			Get();
			g.l = Tab.NewNode(Tab.sync, 0, 0);
			g.r = g.l;
			break;
		}
		default: Error(51);
		}
		return g;
	}

	private static Graph Term() {
		Graph g;
		Graph g2;
		g = new Graph();
		if (StartOf(11)) {
			g = Factor();
			while (StartOf(11)) {
				g2 = Factor();
				g = Tab.Sequence(g, g2);
			}
		} else if (StartOf(12)) {
			g = new Graph();
			g.l = Tab.NewNode(Tab.eps, 0, 0);
			g.r = g.l;
		} else Error(52);
		return g;
	}

	private static SymInfo Sym() {
		SymInfo s;
		s = new SymInfo();
		if (t.kind == 1) {
			Get();
			s.kind = ident;
		} else if (t.kind == 2) {
			Get();
			s.kind = string;
		} else Error(53);
		s.name = token.val;
		if (s.kind == string) s.name = FixString(s.name);
		return s;
	}

	private static int SingleChar() {
		int n;
		String name;
		n = 0;
		Expect(27);
		Expect(28);
		if (t.kind == 4) {
			Get();
			n = Integer.parseInt(token.val, 10);
			if (n > 127) SemError(2); n %= 128;
			if (ignoreCase && n >= 'a' && n <= 'z') n -= 32;
		} else if (t.kind == 2) {
			Get();
			name = token.val;
			if (name.length() != 3) SemError(2);
			if (ignoreCase) name = name.toUpperCase();
			n = name.charAt(1);
		} else Error(54);
		Expect(29);
		return n;
	}

	private static BitSet SimSet() {
		BitSet s;
		String name; int c, n1, n2;
		s = new BitSet(128);
		if (t.kind == 1) {
			Get();
			c = Tab.ClassWithName(token.val);
			if (c < 0) SemError(15); else s.or(Tab.Class(c));
		} else if (t.kind == 2) {
			Get();
			name = token.val;
			for (int i = 1; name.charAt(i) != name.charAt(0); i++)
			  if (ignoreCase) s.set((int) Character.toUpperCase(name.charAt(i)));
			  else s.set((int) name.charAt(i));
		} else if (t.kind == 27) {
			n1 = SingleChar();
			s.set(n1);
			if (t.kind == 25) {
				Get();
				n2 = SingleChar();
				for (int i = n1; i <= n2; i++) s.set(i);
			}
		} else if (t.kind == 26) {
			Get();
			s = Sets.FullSet(127);
		} else Error(55);
		return s;
	}

	private static BitSet Set() {
		BitSet s;
		BitSet s2;
		s = SimSet();
		while (t.kind == 23 || t.kind == 24) {
			if (t.kind == 23) {
				Get();
				s2 = SimSet();
				s.or(s2);
			} else {
				Get();
				s2 = SimSet();
				Sets.Differ(s, s2);
			}
		}
		return s;
	}

	private static Graph TokenExpr() {
		Graph g;
		Graph g2;
		g = TokenTerm();
		boolean first = true;
		while (WeakSeparator(37, 5, 13)) {
			g2 = TokenTerm();
			if (first) {g = Tab.FirstAlt(g); first = false;}
			g = Tab.Alternative(g, g2);
		}
		return g;
	}

	private static final void NameDecl() {
		String alias;
		Expect(1);
		alias = token.val;
		Expect(7);
		if (t.kind == 1) {
			Get();
			Tab.NewName(alias, token.val);
		} else if (t.kind == 2) {
			Get();
			Tab.NewName(alias, FixString(token.val));
		} else Error(56);
		Expect(8);
	}

	private static final void TokenDecl(int typ) {
		SymInfo s; int sp; Position pos; Graph g;
		s = Sym();
		if (Tab.FindSym(s.name) != Tab.noSym) {SemError(7); sp = 0;}
		else {
		    sp = Tab.NewSym(typ, s.name, token.line);
		    Tab.Sym(sp).struct = Tab.classToken;
		}
		while (!(StartOf(14))) {Error(57); Get();}
		if (t.kind == 7) {
			Get();
			g = TokenExpr();
			if (s.kind != ident) SemError(13);
			Tab.CompleteGraph(g.r);
			DFA.ConvertToStates(g.l, sp);
			Expect(8);
		} else if (StartOf(15)) {
			if (s.kind == ident) genScanner = false;
			else MatchLiteral(sp);
		} else Error(58);
		if (t.kind == 43) {
			pos = SemText();
			if (typ == Tab.t) SemError(14);
			Tab.Sym(sp).semPos = pos;
		}
	}

	private static final void SetDecl() {
		BitSet s;
		Expect(1);
		String name = token.val;
		int c = Tab.ClassWithName(name);
		if (c >= 0) SemError(7);
		Expect(7);
		s = Set();
		if (Sets.Size(s) == 0) SemError(1);
		c = Tab.NewClass(name, s);
		Expect(8);
	}

	private static Graph Expression() {
		Graph g;
		Graph g2;
		g = Term();
		boolean first = true;
		while (WeakSeparator(37, 16, 17)) {
			g2 = Term();
			if (first) {g = Tab.FirstAlt(g); first = false;}
			g = Tab.Alternative(g, g2);
		}
		return g;
	}

	private static Position SemText() {
		Position pos;
		Expect(43);
		pos = new Position();
		pos.beg = t.pos; pos.col = t.col;
		while (StartOf(18)) {
			if (StartOf(19)) {
				Get();
			} else if (t.kind == 3) {
				Get();
				SemError(18);
			} else {
				Get();
				SemError(19);
			}
		}
		Expect(44);
		pos.len = token.pos - pos.beg;
		return pos;
	}

	private static final void AttrDecl1(Symbol sym) {
		int beg, col, dim; StringBuffer buf;
		Expect(35);
		if (t.kind == 31) {
			Get();
			Expect(1);
			buf = new StringBuffer(token.val); dim = 0;
			while (t.kind == 32) {
				Get();
				Expect(33);
				dim++;
			}
			Expect(1);
			sym.retVar = token.val;
			while (t.kind == 32) {
				Get();
				Expect(33);
				dim++;
			}
			while (dim > 0) { buf.append("[]"); dim--; }
			sym.retType = buf.toString();
			if (t.kind == 12) {
				Get();
			}
		}
		beg = t.pos; col = t.col;
		while (StartOf(1)) {
			if (StartOf(2)) {
				Get();
			} else {
				Get();
				SemError(18);
			}
		}
		Expect(36);
		if (token.pos > beg) {
		    sym.attrPos = new Position();
		    sym.attrPos.beg = beg; sym.attrPos.col = col;
		    sym.attrPos.len = token.pos - beg;
		}
	}

	private static final void AttrDecl(Symbol sym) {
		int beg, col, dim; StringBuffer buf;
		Expect(30);
		if (t.kind == 31) {
			Get();
			Expect(1);
			buf = new StringBuffer(token.val); dim = 0;
			while (t.kind == 32) {
				Get();
				Expect(33);
				dim++;
			}
			Expect(1);
			sym.retVar = token.val;
			while (t.kind == 32) {
				Get();
				Expect(33);
				dim++;
			}
			while (dim > 0) { buf.append("[]"); dim--; }
			sym.retType = buf.toString();
			if (t.kind == 12) {
				Get();
			}
		}
		beg = t.pos; col = t.col;
		while (StartOf(3)) {
			if (StartOf(4)) {
				Get();
			} else {
				Get();
				SemError(18);
			}
		}
		Expect(34);
		if (token.pos > beg) {
		    sym.attrPos = new Position();
		    sym.attrPos.beg = beg; sym.attrPos.col = col;
		    sym.attrPos.len = token.pos - beg;
		}
	}

	private static final void Declaration() {
		Graph g1, g2; boolean nested = false;
		switch (t.kind) {
		case 13: {
			Get();
			while (t.kind == 1) {
				SetDecl();
			}
			break;
		}
		case 14: {
			Get();
			while (t.kind == 1 || t.kind == 2) {
				TokenDecl(Tab.t);
			}
			break;
		}
		case 15: {
			Get();
			while (t.kind == 1) {
				NameDecl();
			}
			genNames = true;
			break;
		}
		case 16: {
			Get();
			while (t.kind == 1 || t.kind == 2) {
				TokenDecl(Tab.pr);
			}
			break;
		}
		case 17: {
			Get();
			Expect(18);
			g1 = TokenExpr();
			Expect(19);
			g2 = TokenExpr();
			if (t.kind == 20) {
				Get();
				nested = true;
			}
			new Comment(g1.l, g2.l, nested);
			break;
		}
		case 21: {
			Get();
			if (t.kind == 22) {
				Get();
				if (startedDFA) SemError(30); ignoreCase = true;
			} else if (StartOf(20)) {
				Tab.ignored = Set();
				if (Tab.ignored.get(0)) SemError(9);
			} else Error(59);
			break;
		}
		default: Error(60);
		}
		startedDFA = true;
	}

	private static final void ImportList() {
		Expect(11);
		Expect(2);
		Tab.addImport(token.val);
		while (t.kind == 12) {
			Get();
			Expect(2);
			Tab.addImport(token.val);
		}
	}

	private static final void PackageName() {
		Expect(10);
		Expect(2);
		Tab.setPackageName(token.val);
	}

	private static final void Coco() {
		int eofSy;
		boolean undef, noAttrs, noRet, ok, ok1;
		String gramName;
		Symbol sym;
		Graph g;
		if (t.kind == 10) {
			PackageName();
		}
		if (t.kind == 11) {
			ImportList();
		}
		Expect(5);
		eofSy = Tab.NewSym(Tab.t, "EOF", 0);
		Tab.ignored = new BitSet();
		Expect(1);
		gramName = token.val;
		Tab.semDeclPos = new Position();
		Tab.semDeclPos.beg = t.pos;
		while (StartOf(21)) {
			Get();
		}
		Tab.semDeclPos.len = t.pos - Tab.semDeclPos.beg;
		Tab.semDeclPos.col = 0;
		while (StartOf(22)) {
			Declaration();
		}
		while (!(t.kind == 0 || t.kind == 6)) {Error(61); Get();}
		Expect(6);
		Tab.ignored.set(32); /*' ' is always ignored*/
		if (genScanner) DFA.MakeDeterministic();
		Tab.nNodes = 0;
		while (t.kind == 1) {
			Get();
			int sp = Tab.FindSym(token.val);
			undef = sp == Tab.noSym;
			if (undef) {
			  sp = Tab.NewSym(Tab.nt, token.val, token.line);
			  sym = Tab.Sym(sp);
			} else {
			  sym = Tab.Sym(sp);
			  if (sym.typ == Tab.nt) {
			    if (sym.struct > 0) SemError(7);
			  } else SemError(8);
			  sym.line = token.line;
			}
			noAttrs = sym.attrPos == null; sym.attrPos = null;
			noRet = sym.retVar == null; sym.retVar = null;
			if (t.kind == 30 || t.kind == 35) {
				if (t.kind == 30) {
					AttrDecl(sym);
				} else {
					AttrDecl1(sym);
				}
			}
			if (!undef)
			    if (noAttrs && sym.attrPos != null
			    || noRet && sym.retVar != null
			    || !noAttrs && sym.attrPos == null
			    || !noRet && sym.retVar == null) SemError(5);
			if (t.kind == 43) {
				sym.semPos = SemText();
			}
			ExpectWeak(7, 23);
			g = Expression();
			sym.struct = g.l;
			Tab.CompleteGraph(g.r);
			ExpectWeak(8, 24);
		}
		Expect(9);
		Expect(1);
		if (Tab.ddt[2]) Tab.PrintGraph();
		Tab.gramSy = Tab.FindSym(gramName);
		if (Tab.gramSy == Tab.noSym) SemError(11);
		else {
		  sym = Tab.Sym(Tab.gramSy);
		  if (sym.attrPos != null) SemError(12);
		}
		if (!gramName.equals(token.val)) SemError(17);
		if (Scanner.err.count == 0) {
		  System.out.println("Checking");
		  Tab.CompSymbolSets();
		  ok = Tab.NtsComplete()
		       && Tab.AllNtReached()
		       && Tab.NoCircularProductions()
		       && Tab.AllNtToTerm();
		  if (ok) ok1 = Tab.LL1();
		  if (Tab.ddt[7]) Tab.XRef();
		  if (ok) {
		    if (!Tab.ddt[5]) {
		      ParserGen.WriteParser(genNames || Tab.ddt[10]);
		      System.out.print("Parser"); System.out.flush();
		      if (genScanner) {
		        System.out.print(" + Scanner"); System.out.flush();
		        DFA.WriteScanner(ignoreCase);
		        if (Tab.ddt[0]) DFA.PrintStates();
		      }
		      if (Tab.ddt[11]) {
		        DriverGen.WriteDriver();
		        System.out.print(" + Driver");
		      }
		      System.out.println(" generated");
		    }
		  if (Tab.ddt[8]) ParserGen.WriteStatistics();
		  }
		}
		if (Tab.ddt[6]) Tab.PrintSymbolTable();
		System.out.println();
		Expect(8);
	}



	static void Parse() {
		t = new Token();
		Get();
		Coco();

	}

	private static boolean[][] set = {
	{T,T,T,x, x,x,T,T, x,x,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, T,T,T,T, T,T,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, T,x,x,x, x,x,T,T, x,T,x,T, x,x,x},
	{x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,T,x,x, T,x,x,x, x,x,x},
	{x,x,x,x, x,x,T,x, T,x,x,x, x,T,T,T, T,T,x,T, T,T,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, T,x,x,x, x,x,x},
	{T,T,T,x, x,x,T,T, x,x,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
	{x,T,T,x, x,x,T,x, x,x,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
	{x,T,T,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,T,x,x, T,T,x,x, x,T,T,T, T,T,x,T, x,x,x},
	{x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, T,x,x,x, x,x,x},
	{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x},
	{x,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,T,x},
	{x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
	{x,T,T,T, T,T,x,T, T,T,T,T, T,x,x,x, x,x,T,T, T,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
	{T,T,T,x, x,x,T,T, T,x,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,T,x, T,x,x,x, T,x,x,x, x,T,T,T, x,T,x,T, x,x,x},
	{T,T,T,x, x,x,T,T, x,T,x,x, x,T,T,T, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x}

	};
}
