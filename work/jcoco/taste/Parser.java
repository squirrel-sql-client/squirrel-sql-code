package Taste;
import java.util.*;

class Parser {
	private static final int maxT = 29;
	private static final int maxP = 29;

	private static final boolean T = true;
	private static final boolean x = false;
	private static final int minErrDist = 2;
	private static int errDist = minErrDist;

	static Token token;   // last recognized token
	static Token t;       // lookahead token

	

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

	private static int MulOp() {
		int op;
		op = -1;
		if (t.kind == 24) {
			Get();
			op = TC.MUL;
		} else if (t.kind == 25) {
			Get();
			op = TC.DIVI;
		} else Error(30);
		return op;
	}

	private static int Factor() {
		int type;
		int n; Obj obj; String name;
		type = TL.undef;
		if (t.kind == 1) {
			name = Ident();
			obj = TL.This(name); type = obj.type;
			if (obj.kind == TL.vars)
			  TC.Emit3(TC.LOAD, TL.curLevel-obj.level, obj.adr);
			else SemError(7);
		} else if (t.kind == 21) {
			Get();
			TC.Emit2(TC.LIT, 1); type = TL.bool;
		} else if (t.kind == 22) {
			Get();
			TC.Emit2(TC.LIT, 0); type = TL.bool;
		} else if (t.kind == 2) {
			Get();
			n = Integer.parseInt(token.val);
			TC.Emit2(TC.LIT, n); type = TL.integer;
		} else if (t.kind == 23) {
			Get();
			type = Factor();
			if (type != TL.integer) {SemError(4); type = TL.integer;}
			TC.Emit(TC.NEG);
		} else Error(31);
		return type;
	}

	private static int AddOp() {
		int op;
		op = -1;
		if (t.kind == 26) {
			Get();
			op = TC.ADD;
		} else if (t.kind == 23) {
			Get();
			op = TC.SUB;
		} else Error(32);
		return op;
	}

	private static int Term() {
		int type;
		int type1, op;
		type = Factor();
		while (t.kind == 24 || t.kind == 25) {
			op = MulOp();
			type1 = Factor();
			if (type != TL.integer || type1 != TL.integer) SemError(4);
			TC.Emit(op);
		}
		return type;
	}

	private static int RelOp() {
		int op;
		op = -1;
		if (t.kind == 13) {
			Get();
			op = TC.EQU;
		} else if (t.kind == 27) {
			Get();
			op = TC.LSS;
		} else if (t.kind == 28) {
			Get();
			op = TC.GTR;
		} else Error(33);
		return op;
	}

	private static int SimExpr() {
		int type;
		int type1, op;
		type = Term();
		while (t.kind == 23 || t.kind == 26) {
			op = AddOp();
			type1 = Term();
			if (type != TL.integer || type1 != TL.integer) SemError(4);
			TC.Emit(op);
		}
		return type;
	}

	private static int Expression() {
		int type;
		int type1, op;
		type = SimExpr();
		if (t.kind == 13 || t.kind == 27 || t.kind == 28) {
			op = RelOp();
			type1 = SimExpr();
			if (type != type1) SemError(5);
			TC.Emit(op); type = TL.bool;
		}
		return type;
	}

	private static void Stat() {
		int type;
		String name;
		Obj obj;
		int fix, fix2, loopstart;
		if (StartOf(1)) {
			if (t.kind == 1) {
				name = Ident();
				obj = TL.This(name);
				if (t.kind == 7) {
					Get();
					Expect(13);
					if (obj.kind != TL.vars) SemError(7);
					type = Expression();
					if (type != obj.type) SemError(5);
					TC.Emit3(TC.STO, TL.curLevel-obj.level, obj.adr);
				} else if (t.kind == 4 || t.kind == 10 || t.kind == 16) {
					if (obj.kind != TL.procs) SemError(8);
					TC.Emit3(TC.CALL, TL.curLevel-obj.level, obj.adr);
				} else Error(34);
			} else if (t.kind == 14) {
				Get();
				type = Expression();
				if (type != TL.bool) SemError(6);
				fix = TC.pc + 1; TC.Emit2(TC.FJMP, 0);
				Expect(15);
				StatSeq();
				if (t.kind == 16) {
					Get();
					fix2 = TC.pc + 1; TC.Emit2(TC.JMP, 0);
					TC.Fixup(fix); fix = fix2;
					StatSeq();
				}
				Expect(10);
				TC.Fixup(fix);
			} else if (t.kind == 17) {
				Get();
				loopstart = TC.pc;
				type = Expression();
				if (type != TL.bool) SemError(6);
				fix = TC.pc + 1; TC.Emit2(TC.FJMP, 0);
				Expect(18);
				StatSeq();
				TC.Emit2(TC.JMP, loopstart); TC.Fixup(fix);
				Expect(10);
			} else if (t.kind == 19) {
				Get();
				name = Ident();
				obj = TL.This(name);
				if (obj.type != TL.integer) SemError(4);
				TC.Emit3(TC.READ, TL.curLevel-obj.level, obj.adr);
			} else {
				Get();
				type = Expression();
				if (type != TL.integer) SemError(4);
				TC.Emit(TC.WRITE);
			}
		}
	}

	private static void StatSeq() {
		Stat();
		while (t.kind == 4) {
			Get();
			Stat();
		}
	}

	private static int TypeId() {
		int type;
		type = TL.undef;
		if (t.kind == 11) {
			Get();
			type = TL.integer;
		} else if (t.kind == 12) {
			Get();
			type = TL.bool;
		} else Error(35);
		return type;
	}

	private static void Body() {
		int fix, type; String name, name1; Obj obj;
		TL.EnterScope(); fix = TC.pc + 1; TC.Emit2(TC.JMP, 0);
		while (t.kind == 6 || t.kind == 8) {
			if (t.kind == 6) {
				Get();
				while (t.kind == 1) {
					name = Ident();
					Expect(7);
					obj = TL.NewObj(name, TL.vars);
					obj.type = TypeId();
					Expect(4);
				}
			} else {
				Get();
				name = Ident();
				Expect(4);
				obj = TL.NewObj(name, TL.procs); obj.adr = TC.pc;
				Body();
				name1 = Ident();
				TC.Emit(TC.RET);
				if (!name.equals(name1)) SemError(3);
				Expect(4);
			}
		}
		Expect(9);
		TC.Fixup(fix); TC.Emit2(TC.RES, TL.DataSpace());
		StatSeq();
		Expect(10);
		TL.LeaveScope();
	}

	private static String Ident() {
		String name;
		Expect(1);
		name = token.val;
		return name;
	}

	private static void Taste() {
		String name, progName;
		Expect(3);
		TC.Init(); TL.Init();
		progName = Ident();
		Expect(4);
		TC.progStart = TC.pc;
		Body();
		name = Ident();
		if (!name.equals(progName)) SemError(3);
		TC.Emit(TC.HALTc);
		Expect(5);
	}



	static void Parse() {
		t = new Token();
		Get();
		Taste();

	}

	private static boolean[][] set = {
	{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
	{x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,x,T, T,x,x,x, x,x,x,x, x,x,x}

	};
}
