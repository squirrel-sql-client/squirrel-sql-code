package Coco;

import java.io.*;
import java.util.*;

class StateSet {       // set of target states returned by GetTargetStates
	BitSet set;           // all target states of an action
	int endOf;            // token that is recognized after this action
	boolean ctx;          // true if target states are reached via context transition
//	boolean correct;      // true if no error occured in GetTargetStates
}

//-----------------------------------------------------------------------------
//  State
//-----------------------------------------------------------------------------

class State {          // state of finite automaton
	static int lastNr;    // highest state number
	int nr;               // state number
	Action firstAction;   // to first action of this state
	int endOf;            // nr. of recognized token if state is final
	boolean ctx;          // true if state is reached via contextTrans
	State next;

	State() {
		nr = ++lastNr; endOf = Tab.noSym;
	}

	void AddAction(Action act) {
		Action lasta = null, a = firstAction;
		while (a != null && act.typ >= a.typ) {lasta = a; a = a.next;}
		// collecting classes at the beginning gives better performance
		act.next = a;
		if (a==firstAction) firstAction = act; else lasta.next = act;
	}

	void DetachAction(Action act) {
		Action lasta = null, a = firstAction;
		while (a != null && a != act) {lasta = a; a = a.next;}
		if (a != null)
			if (a==firstAction) firstAction = a.next; else lasta.next = a.next;
	}

	Action TheAction(char ch) {
		for (Action a=firstAction; a!=null; a=a.next)
			if (a.typ==Tab.chr && ch==a.sym) return a;
			else if (a.typ==Tab.clas && Tab.Class(a.sym).get(ch)) return a;
		return null;
	}

	void MeltWith(State s) { // copy actions of s to state
		Action a;
		for (Action action=s.firstAction; action!=null; action=action.next) {
			a = new Action(action.typ, action.sym, action.tc);
			a.AddTargets(action);
			AddAction(a);
		}
	}

}

//-----------------------------------------------------------------------------
//  Action
//-----------------------------------------------------------------------------

class Action {         // action of finite automaton
	int typ;              // type of action symbol: chr, clas
	int sym;              // action symbol
	int tc;               // transition code: normTrans, contextTrans
	Target target;        // states reached from this action
	Action next;

	Action(int typ, int sym, int tc) {
		this.typ = typ; this.sym = sym; this.tc = tc;
	}

	void AddTarget(Target t) {
		Target p, last = null;
		p = target;
		while (p != null && t.state.nr >= p.state.nr) {
			if (t.state==p.state) return;
			last = p; p = p.next;
		}
		t.next = p;
		if (p==target) target = t; else last.next = t;
	}

	void AddTargets(Action a) { // add copy of a.targets to action.targets
		for (Target p=a.target; p!=null; p=p.next) {
			Target t = new Target(p.state);
			AddTarget(t);
		}
		if (a.tc==Tab.contextTrans) tc = Tab.contextTrans;
	}

	BitSet Symbols() {
		BitSet s;
		if (typ==Tab.clas)
			s = (BitSet) Tab.Class(sym).clone();
		else {s = new BitSet(); s.set(sym);}
		return s;
	}

	void ShiftWith(BitSet s) {
		if (Sets.Size(s)==1) {
			typ = Tab.chr; sym = Sets.First(s);
		} else {
			int i = Tab.ClassWithSet(s);
			if (i < 0) i = Tab.NewClass("#", s); // class with dummy name
			typ = Tab.clas; sym = i;
		}
	}

	StateSet GetTargetStates() { // compute the set of target states
		int stateNr;
		StateSet states = new StateSet();
		states.set = new BitSet(); states.endOf = Tab.noSym; states.ctx = false;
//		states.correct = true;
		for (Target t=target; t!=null; t=t.next) {
			stateNr = t.state.nr;
			if (stateNr <= DFA.lastSimState) states.set.set(stateNr);
			else states.set.or(Melted.Set(stateNr));
			if (t.state.endOf!=Tab.noSym)
				if (states.endOf==Tab.noSym || states.endOf==t.state.endOf)
					states.endOf = t.state.endOf;
				else {
					System.out.println("Tokens " + states.endOf + " and " + t.state.endOf
						+ " cannot be distinguished");
					Scanner.err.count++;
				}
			if (t.state.ctx) {
				states.ctx = true;
				/* The following check seems to be unnecessary. It reported an error
				   if a symbol + context was the prefix of another symbol, e.g.
				     s1 = "a" "b" "c".
				     s2 = "a" CONTEXT("b").
				   But this is ok.
				if (t.state.endOf!=Tab.noSym) {
					System.out.println("Ambiguous context clause");
					Scanner.err.count++;
				}*/
			}
		}
		return states;
	}
}

//-----------------------------------------------------------------------------
//  Target
//-----------------------------------------------------------------------------

class Target {         // set of states that are reached by an action
	State state;          // target state
	Target next;

	Target (State s) {
		state = s;
	}
}

//-----------------------------------------------------------------------------
//  Melted
//-----------------------------------------------------------------------------

class Melted {         // info about melted states
	static Melted first;  // head of melted state list
	BitSet set;           // set of old states
	State state;          // new state
	Melted next;

	Melted(BitSet set, State state) {
		this.set = set; this.state = state;
		this.next = first; first = this;
	}

	static BitSet Set(int nr) {
		Melted m;
		for (m=first; m!=null && m.state.nr!=nr; m=m.next) ;
		return m.set;
	}

	static Melted StateWithSet(BitSet s) {
		for (Melted m=first; m!=null; m=m.next)
			if (s.equals(m.set)) return m;
		return null;
	}

}

//-----------------------------------------------------------------------------
//  Comment
//-----------------------------------------------------------------------------

class Comment {        // info about comment syntax
	static Comment first; // list of comments
	String start;
	String stop;
	boolean nested;
	Comment next;

	private static String Str(int p) {
		StringBuffer s = new StringBuffer();
		BitSet set;
		while (p != 0) {
			GraphNode n = Tab.Node(p);
			if (n.typ==Tab.chr) {
				s.append((char)n.p1);
			} else if (n.typ==Tab.clas) {
				set = Tab.Class(n.p1);
				if (Sets.Size(set) != 1) Parser.SemError(26);
				s.append((char)Sets.First(set));
			} else Parser.SemError(22);
			p = n.next;
		}
		if (s.length() == 0) {Parser.SemError(25); s.append('?');}
		else if (s.length() > 2) Parser.SemError(25);
		return s.toString();
	}

	Comment(int from, int to, boolean nested) {
		start = Str(from);
		stop = Str(to);
		this.nested = nested;
		this.next = first; first = this;
	}

}

//-----------------------------------------------------------------------------
//  DFA
//-----------------------------------------------------------------------------

class DFA {
	static final int maxStates = 300;
	static final int EOF  = -1;
	static final char CR  = '\r';
	static final char LF  = '\n';

	static State firstState;
	static State lastState;  // last allocated state
	static int lastSimState; // last non melted state
    static File inDir;       // input directory (frame file)
    static File outDir;      // output directory
    static boolean dynamic;  // generate dynamic classes
	static int curSy;        // current token to be recognized (in FindTrans)
	static int curGraph;     // start of graph for current token (in FindTrans)
	static boolean dirtyDFA; // DFA may become nondeterministic in MatchedDFA

	static Reader fram;      // scanner frame input    Java 1.1
	static PrintWriter gen;  // generated scanner file Java 1.1

	private static String Int(int n, int len) {
		String s = String.valueOf(n);
		int i = s.length(); if (len < i) len = i;
		int j = 0, d = len - s.length();
		char[] a = new char[len];
		for (i=0; i<d; i++) a[i] = ' ';
		for (j=0; i<len; i++) {a[i] = s.charAt(j); j++;}
		return new String(a, 0, len);
	}

	private static String Ch(char ch) {
		if (ch<' ' || ch>=127 || ch=='\'' || ch=='\\') return String.valueOf((int)ch);
		else return "'" + ch + "'";
	}

	private static String ChCond(char ch) {
		return "ch == " + Ch(ch);
	}

	private static void PutRange(BitSet s) {
		int[] lo = new int[32];
		int[] hi = new int[32];
		// fill lo and hi
		int top = -1;
		int i = 0;
		while (i < 128) {
			if (s.get(i)) {
				top++; lo[top] = i; i++;
				while (i < 128 && s.get(i)) i++;
				hi[top] = i-1;
			} else i++;
		}
		// print ranges
		if (top==1 && lo[0]==0 && hi[1]==127 && hi[0]+2==lo[1]) {
			BitSet s1 = new BitSet(); s1.set(hi[0]+1);
			gen.print("!"); PutRange(s1);
		} else {
			gen.print("(");
			for (i=0; i<=top; i++) {
				if (hi[i]==lo[i]) gen.print("ch == " + Ch((char)lo[i]));
				else if (lo[i]==0) gen.print("ch <= " + Ch((char)hi[i]));
				else if (hi[i]==127) gen.print("ch >= " + Ch((char)lo[i]));
				else gen.print("ch >= " + Ch((char)lo[i]) + " && ch <= " + Ch((char)hi[i]));
				if (i < top) { gen.println(); gen.print("\t\t\t\t\t  || "); }
			}
			gen.print(")");
		}
	}

	private static State NewState() {
		State s = new State();
		if (firstState==null) firstState = s; else lastState.next = s;
		lastState = s;
		return s;
	}

	private static void NewTransition(State from, State to, int typ, int sym, int tc) {
		if (to==firstState) Parser.SemError(21);
		Target t = new Target(to);
		Action a = new Action(typ, sym, tc); a.target = t;
		from.AddAction(a);
	}

	private static void CombineShifts() {
		State state;
		Action a, b, c;
		BitSet seta, setb;
		for (state=firstState; state!=null; state=state.next) {
			for (a=state.firstAction; a!=null; a=a.next) {
				b = a.next;
				while (b != null)
					if (a.target.state==b.target.state && a.tc==b.tc) {
						seta = a.Symbols(); setb = b.Symbols();
						seta.or(setb);
						a.ShiftWith(seta);
						c = b; b = b.next; state.DetachAction(c);
					} else b = b.next;
			}
		}
	}

	private static void FindUsedStates(State state, BitSet used) {
		if (used.get(state.nr)) return;
		used.set(state.nr);
		for (Action a=state.firstAction; a!=null; a=a.next)
			FindUsedStates(a.target.state, used);
	}

	private static void DeleteRedundantStates() {
		State[] newState = new State[maxStates];
		BitSet used = new BitSet();
		FindUsedStates(firstState, used);
		// combine equal final states
		for (State s1=firstState.next; s1!=null; s1=s1.next) // firstState cannot be final
			if (used.get(s1.nr) && s1.endOf!=Tab.noSym && s1.firstAction==null && !s1.ctx)
				for (State s2=s1.next; s2!=null; s2=s2.next)
					if (used.get(s2.nr) && s1.endOf==s2.endOf && s2.firstAction==null & !s2.ctx) {
						used.clear(s2.nr); newState[s2.nr] = s1;
					}
		for (State state=firstState; state!=null; state=state.next)
			if (used.get(state.nr))
				for (Action a=state.firstAction; a!=null; a=a.next)
					if (!used.get(a.target.state.nr))
						a.target.state = newState[a.target.state.nr];
		// delete unused states
		lastState = firstState; State.lastNr = 0; // firstState has number 0
		for (State state=firstState.next; state!=null; state=state.next)
			if (used.get(state.nr)) {state.nr = ++State.lastNr; lastState = state;}
			else lastState.next = state.next;
	}

	private static State TheState(int p) {
		State state;
		if (p==0) {state = NewState(); state.endOf = curSy; return state;}
		else return Tab.Node(p).state;
	}

	private static void Step(State from, int p, BitSet stepped) {
		if (p==0) return;
		stepped.set(p);
		GraphNode n = Tab.Node(p);
		switch (n.typ) {
			case Tab.clas: case Tab.chr: {
				NewTransition(from, TheState(Math.abs(n.next)), n.typ, n.p1, n.p2);
				break;
			}
			case Tab.alt: {
				Step(from, n.p1, stepped); Step(from, n.p2, stepped);
				break;
			}
			case Tab.iter: case Tab.opt: {
				int next = Math.abs(n.next);
				if (!stepped.get(next)) Step(from, next, stepped);
				Step(from, n.p1, stepped);
				break;
			}
		}
	}

	private static void NumberNodes(int p, State state) {
		/* Assigns a state n.state to every node n. There will be a transition from
		   n.state to n.next.state triggered by n.sym. All nodes in an alternative
		   chain are represented by the same state.
		*/
		if (p==0) return;
		GraphNode n = Tab.Node(p);
		if (n.state != null) return; // already visited;
		if (state==null) state = NewState();
		n.state = state;
		if (Tab.DelGraph(p)) state.endOf = curSy;
		switch (n.typ) {
			case Tab.clas: case Tab.chr: {
				NumberNodes(Math.abs(n.next), null);
				break;
			}
			case Tab.opt: {
				NumberNodes(Math.abs(n.next), null); NumberNodes(n.p1, state);
				break;
			}
			case Tab.iter: {
				NumberNodes(Math.abs(n.next), state); NumberNodes(n.p1, state);
				break;
			}
			case Tab.alt: {
				NumberNodes(n.p1, state); NumberNodes(n.p2, state);
				break;
			}
		}
	}

	private static void FindTrans (int p, boolean start, BitSet mark) {
		if (p==0 || mark.get(p)) return;
		mark.set(p);
		GraphNode n = Tab.Node(p);
		if (start) Step(n.state, p, new BitSet(Tab.nNodes+1)); // start of group of equally numbered nodes
		switch (n.typ) {
			case Tab.clas: case Tab.chr: {
				FindTrans(Math.abs(n.next), true, mark);
				break;
			}
			case Tab.opt: {
				FindTrans(Math.abs(n.next), true, mark); FindTrans(n.p1, false, mark);
				break;
			}
			case Tab.iter: {
				FindTrans(Math.abs(n.next), false, mark); FindTrans(n.p1, false, mark);
				break;
			}
			case Tab.alt: {
				FindTrans(n.p1, false, mark); FindTrans(n.p2, false, mark);
				break;
			}
		}
	}

	static void ConvertToStates(int p, int sp) {
		curGraph = p; curSy = sp;
		if (Tab.DelGraph(curGraph)) Parser.SemError(20);
		NumberNodes(curGraph, firstState);
		FindTrans(curGraph, true, new BitSet(Tab.nNodes+1));
	}

	static int MatchedDFA(String s, int sp) {
		int i, len = s.length() - 1;
		boolean weakMatch = false;
		// s has quotes
		State state = firstState;
		for (i=1; i<len; i++) { // try to match s against existing DFA
			Action a = state.TheAction(s.charAt(i));
			if (a==null) break;
			if (a.typ == Tab.clas) weakMatch = true;
			state = a.target.state;
		}
		if (weakMatch && (i != len || state.endOf == Tab.noSym)) {
			state = firstState; i = 1;
			dirtyDFA = true;
		}
		for (;i<len; i++) { // make new DFA for s[i..len-1]
			State to = NewState();
			NewTransition(state, to, Tab.chr, s.charAt(i), Tab.normTrans);
			state = to;
		}
		int matchedSp = state.endOf;
		if (state.endOf==Tab.noSym) state.endOf = sp;
		return matchedSp;
	}

	private static void SplitActions(State state, Action a, Action b) {
		Action c; BitSet seta, setb, setc;
		seta = a.Symbols(); setb = b.Symbols();
		if (seta.equals(setb)) {
			a.AddTargets(b);
			state.DetachAction(b);
		} else if (Sets.Includes(seta, setb)) {
			setc = (BitSet) seta.clone(); Sets.Differ(setc, setb);
			b.AddTargets(a);
			a.ShiftWith(setc);
		} else if (Sets.Includes(setb, seta)) {
			setc = (BitSet) setb.clone(); Sets.Differ(setc, seta);
			a.AddTargets(b);
			b.ShiftWith(setc);
		} else {
			setc = (BitSet) seta.clone(); setc.and(setb);
			Sets.Differ(seta, setc);
			Sets.Differ(setb, setc);
			a.ShiftWith(seta);
			b.ShiftWith(setb);
			c = new Action(0, 0, 0);
			c.AddTargets(a);
			c.AddTargets(b);
			c.ShiftWith(setc);
			state.AddAction(c);
		}
	}

	private static boolean Overlap(Action a, Action b) {
		BitSet seta, setb;
		if (a.typ==Tab.chr)
			if (b.typ==Tab.chr) return a.sym==b.sym;
			else {setb = Tab.Class(b.sym); return setb.get(a.sym);}
		else {
			seta = Tab.Class(a.sym);
			if (b.typ==Tab.chr) return seta.get(b.sym);
			else {setb = Tab.Class(b.sym); return !Sets.Different(seta, setb);}
		}
	}

	private static boolean MakeUnique(State state) { // return true if actions were split
		boolean changed = false;
		for (Action a=state.firstAction; a!=null; a=a.next)
			for (Action b=a.next; b!=null; b=b.next)
				if (Overlap(a, b)) {SplitActions(state, a, b); changed = true;}
		return changed;
	}

	private static void MeltStates(State state) {
		boolean changed;
		StateSet states;
		for (Action action=state.firstAction; action!=null; action=action.next) {
			if (action.target.next != null) {
				states = action.GetTargetStates();
				Melted melt = Melted.StateWithSet(states.set);
				if (melt==null) {
					State s = NewState(); s.endOf = states.endOf; s.ctx = states.ctx;
					for (Target targ=action.target; targ!=null; targ=targ.next)
						s.MeltWith(targ.state);
					do {changed = MakeUnique(s);} while (changed);
					melt = new Melted(states.set, s);
				}
				action.target.next = null;
				action.target.state = melt.state;
			}
		}
	}

	private static void FindCtxStates() {
		for (State state=firstState; state!=null; state=state.next)
			for (Action a=state.firstAction; a!=null; a=a.next)
				if (a.tc==Tab.contextTrans) a.target.state.ctx = true;
	}

	static void MakeDeterministic() {
		State state;
		boolean changed;
		lastSimState = lastState.nr;
		FindCtxStates();
		for (state=firstState; state!=null; state=state.next)
			do {changed = MakeUnique(state);} while (changed);
		for (state=firstState; state!=null; state=state.next)
		MeltStates(state);
		DeleteRedundantStates();
		CombineShifts();
	}

	static void PrintStates() {
		Action action; Target targ;
		Trace.println("\n---------- states ----------");
		for (State state=firstState; state!=null; state=state.next) {
			boolean first = true;
			if (state.endOf==Tab.noSym) Trace.print("     ");
			else Trace.print("E(" + Int(state.endOf, 2) + ")");
			Trace.print(Int(state.nr, 3) + ":");
			if (state.firstAction==null) Trace.println();
			for (action=state.firstAction; action!=null; action=action.next) {
				if (first) {Trace.print(" "); first = false;} else Trace.print("          ");
				if (action.typ==Tab.clas) Trace.print(Tab.ClassName(action.sym));
				else Trace.print(Ch((char)action.sym));
				for (targ=action.target; targ!=null; targ=targ.next)
					Trace.print(" " + targ.state.nr);
				if (action.tc==Tab.contextTrans) Trace.println(" context"); else Trace.println();
			}
		}
		Trace.println("\n---------- character classes ----------");
		for (int i=0; i<=Tab.maxC; i++) {
			BitSet set = Tab.Class(i);
			Trace.println(Tab.ClassName(i) + ": " + set.toString());
		}
	}

	private static void GenComBody(Comment com) {
		gen.println("\t\t\tfor(;;) {");
		gen.println("\t\t\t\tif (" + ChCond(com.stop.charAt(0)) + ") {");
		if (com.stop.length()==1) {
			gen.println("\t\t\t\t\tlevel--;");
			gen.println("\t\t\t\t\tif (level == 0) {NextCh(); return true;}");
			gen.println("\t\t\t\t\tNextCh();");
		} else {
			gen.println("\t\t\t\t\tNextCh();");
			gen.println("\t\t\t\t\tif (" + ChCond(com.stop.charAt(1)) + ") {");
			gen.println("\t\t\t\t\t\tlevel--;");
			gen.println("\t\t\t\t\t\tif (level == 0) {NextCh(); return true;}");
			gen.println("\t\t\t\t\t\tNextCh();");
			gen.println("\t\t\t\t\t}");
		}
		if (com.nested) {
			gen.println("\t\t\t\t} else if (" + ChCond(com.start.charAt(0)) + ") {");
			if (com.start.length()==1)
				gen.println("\t\t\t\t\tlevel++; NextCh();");
			else {
				gen.println("\t\t\t\t\tNextCh();");
				gen.println("\t\t\t\t\tif (" + ChCond(com.start.charAt(1)) + ") {");
				gen.println("\t\t\t\t\t\tlevel++; NextCh();");
				gen.println("\t\t\t\t\t}");
			}
		}
		gen.println("\t\t\t\t\t} else if (ch == EOF) return false;");
		gen.println("\t\t\t\t\telse NextCh();");
		gen.println("\t\t\t\t}");
	}

	private static void GenComment(Comment com, int i) {
		gen.print("\tprivate ");
        if(!dynamic)
            gen.print("static ");
        gen.println("final boolean Comment" + i + "() {");
		gen.println("\t\tint level = 1, line0 = line, lineStart0 = lineStart; char startCh;");
		if (com.start.length()==1) {
			gen.println("\t\tNextCh();");
			GenComBody(com);
		} else {
			gen.println("\t\tNextCh();");
			gen.println("\t\tif (" + ChCond(com.start.charAt(1)) + ") {");
			gen.println("\t\t\tNextCh();");
			GenComBody(com);
			gen.println("\t\t} else {");
			gen.println("\t\t\tif (ch == CR || ch == LF) {line--; lineStart = lineStart0;}");
			gen.println("\t\t\tpos = pos - 2; setPos(pos+1); NextCh();");
			gen.println("\t\t}");
			gen.println("\t\treturn false;");
		}
		gen.println("\t}");
	}

	private static void CopyFramePart(String stop) {
		int last = 0;
		int startCh = stop.charAt(0);
		int endOfStopString = stop.length() - 1;
		try {
			int ch = fram.read();
			while (ch!=EOF)
				if (ch==startCh) {
					int i = 0;
					do {
						if (i==endOfStopString) return; // stop[0..i] found
						ch = fram.read(); i++;
					} while (ch==stop.charAt(i));
					// stop[0..i-1] found; continue with last read character
					gen.print(stop.substring(0, i));
				} else if (ch==LF) { if (last!=CR) gen.println(); last = ch; ch = fram.read();
				} else if (ch==CR) { gen.println(); last = ch; ch = fram.read();
				} else {
					gen.print((char)ch); last = ch; ch = fram.read();
				}
		} catch (IOException e) {
			Scanner.err.Exception("-- error reading Scanner.frame");
		}
		Scanner.err.Exception("-- incomplete or corrupt Scanner.frame");
	}

	private static void PrintTermName(int n) {
		if (Tab.sy[n].symName == null) gen.print(n);
		else gen.print("SYM." + Tab.sy[n].symName);
	}

	private static void GenLiterals(boolean ignoreCase) {
		int i, j, k;
		char ch;
		Symbol sym;
		String[] key = new String[Tab.maxTerminals];
		int[] knr = new int[Tab.maxTerminals];
		// sort literal list (don't consider eofSy)
		k = 0;
		for (i=1; i<=Tab.maxT; i++) {
			sym = Tab.Sym(i);
			if (sym.struct==Tab.litToken) {
				for (j=k-1; j>=0 && sym.name.compareTo(key[j]) < 0; j--) {
					key[j+1] = key[j]; knr[j+1] = knr[j];
				}
				key[j+1] = sym.name; knr[j+1] = i; k++;
			}
		}
		// print switch statement
		i = 0;
		if (ignoreCase) gen.println("\t\tt.val = buf.toString().toUpperCase();");
		else gen.println("\t\tt.val = buf.toString();");
		gen.println("\t\tswitch (t.val.charAt(0)) {");
		while (i < k) {
			ch = key[i].charAt(1); // key[i, 0] is quote
			gen.println("\t\t\tcase " + Ch(ch) + ": {");
			j = i;
			do {
				if (i==j) gen.print("\t\t\t\tif ");
				else gen.print("\t\t\t\telse if ");
				gen.print("(t.val.equals(" + key[i] + ")) t.kind = ");
				PrintTermName(knr[i]);
				gen.println(";");
				i++;
			} while (i<k && key[i].charAt(1)==ch);
			gen.println("\t\t\t\tbreak;}");
		}
		gen.print("\t\t}");
	}

	private static void WriteState(State state) {
		Symbol sym;
		boolean ctxEnd;
		int endOf = state.endOf;
		if (endOf > Tab.maxT)
			endOf = Tab.maxT + Tab.maxSymbols - endOf; // pragmas have been moved
		gen.println("\t\t\t\tcase " + state.nr + ":");
		ctxEnd = state.ctx;
		for (Action action=state.firstAction; action!=null; action=action.next) {
			if (action==state.firstAction) gen.print("\t\t\t\t\tif (");
			else gen.print("\t\t\t\t\telse if (");
			if (action.typ==Tab.chr) gen.print(ChCond((char)action.sym));
			else PutRange(Tab.Class(action.sym));
			gen.print(") {");
			if (action.target.state != state)
				gen.print("state = " + action.target.state.nr + "; ");
			if (action.tc == Tab.contextTrans) {
				gen.print("apx++; "); ctxEnd = false;
			} else if (state.ctx)
				gen.print("apx = 0; ");
			gen.println("break;}");
		}
		if (state.firstAction != null) gen.print("\t\t\t\t\telse ");
		if (endOf==Tab.noSym)
			gen.println("{t.kind = noSym; break loop;}");
		else { // final state
			if (state.firstAction==null)
				gen.print("\t\t\t\t\t{");
			else
				gen.print("{");
			sym = Tab.Sym(endOf);
			if (ctxEnd) { // final context state: cut appendix
				gen.println("pos = pos - apx - 1;");
				gen.println("\t\t\t\t\tbuf.setLength(buf.length() - apx);");
				gen.println("\t\t\t\t\tline = t.line; setPos(pos+1); NextCh();");
				gen.print("\t\t\t\t\t");
			}
			gen.print("t.kind = ");
			PrintTermName(endOf);
			gen.print("; ");
			if (sym.struct==Tab.classLitToken)
				gen.print("CheckLiteral(buf); ");
			gen.println("break loop;}");
		}
	}

	private static void FillStartTab(int[] startTab) {
		startTab[0] = State.lastNr + 1; // eof
		for (Action action= firstState.firstAction; action!=null; action=action.next) {
			int targetState = action.target.state.nr;
			if (action.typ==Tab.chr) startTab[action.sym] = targetState;
			else {
				BitSet s = Tab.Class(action.sym); int max = s.size();
				for (int i=0; i<=max; i++)
					if (s.get(i)) startTab[i] = targetState;
			}
		}
	}

	static void WriteScanner(boolean ignoreCase) {
		int i, j, max;
		int[] startTab = new int[128];
		boolean ok = true;
		Comment com;
		Symbol root = Tab.Sym(Tab.gramSy);

		try {
            fram = new BufferedReader(new FileReader(new File(inDir, "Scanner.frame")));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot open Scanner.frame. " +
			" - must be in application directory.");
		}
		try {
            File packageDir = Tab.getPackageDir(outDir);
			gen = new PrintWriter(
                  new BufferedWriter(
                        new FileWriter(new File(packageDir, "Scanner.java"))));

		} catch (IOException e) {
			Scanner.err.Exception("-- cannot generate scanner file");
		}
		if (dirtyDFA) MakeDeterministic();
		FillStartTab(startTab);
		gen.println("package " + Tab.getPackageName() + ";");
		CopyFramePart("-->declarations");
		gen.println("\tprivate static final int noSym = " + Tab.maxT + ";");
		gen.println("\tprivate static final int[] start = {");
		for (i=0; i<8; i++) {
			gen.print("\t");
			for (j=0; j<16; j++)
				gen.print(Int(startTab[16*i+j], 3) + ",");
			gen.println();
		}
		gen.println("\t  0};");
		CopyFramePart("-->initialization");
		gen.print("\t\t");
		max = Tab.ignored.size(); j = 0;
		for (i=0; i<=max; i++)
			if (Tab.ignored.get(i)) {
				gen.print("ignore.set(" + i + "); ");
				if (++j % 4 == 0) {gen.println(); gen.print("\t\t");}
			}
		CopyFramePart("-->scan0");
		if (ignoreCase) gen.print("\t\tch = Character.toUpperCase(strCh);");
		else gen.print("\t\tch = strCh;");
		CopyFramePart("-->comment");
		com = Comment.first; i = 0;
		while (com != null) {
			GenComment(com, i);
			com = com.next; i++;
		}
		CopyFramePart("-->literals"); GenLiterals(ignoreCase);
		CopyFramePart("-->scan1");
		if (Comment.first!=null) {
			gen.print("\t\tif (");
			com = Comment.first; i = 0;
			while (com != null) {
				gen.print(ChCond(com.start.charAt(0)));
				gen.print(" && Comment" + i + "() ");
				if (com.next != null) gen.print(" || ");
				com = com.next; i++;
			}
			gen.print(") return Scan();");
		}
		CopyFramePart("-->scan2");
		for (State state=firstState.next; state!=null; state=state.next)
			WriteState(state);
		gen.println("\t\t\t\tcase "+(State.lastNr+1)+":");
		gen.print("\t\t\t\t\t{t.kind = ");
		PrintTermName(0); gen.print("; break loop;}");
		CopyFramePart("-->scan3");
		if (ignoreCase) gen.print("\t\tt.val = t.str.toUpperCase();");
		else gen.print("\t\tt.val = t.str;");
		CopyFramePart("$$$");
		gen.flush();
	}

    static void Init(File _inDir, File _outDir, boolean _dynamic) {
        inDir = _inDir;
        outDir = _outDir;
        dynamic = _dynamic;
		firstState = null; lastState = null; State.lastNr = -1;
		firstState = NewState();
		Melted.first = null; Comment.first = null;
		dirtyDFA = false;
	}

}
