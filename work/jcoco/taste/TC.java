package Taste;

import java.io.*;

class IntStream {
	StreamTokenizer t;
	boolean eof;

// Portability - use the following for Java 1.0
//	IntStream (DataInputStream s) { // Java 1.0
// Portability - use the following for Java 1.1
//	IntStream (BufferedReader s) { // Java 1.1

	IntStream (BufferedReader s) { // Java 1.1

		t = new StreamTokenizer(s);
		eof = false;
	}

	int readInt() {
		for (;;) {
			try {
				t.nextToken();
			} catch (IOException e) {eof = true; return 0;}
			if (t.ttype == t.TT_NUMBER) return (int) t.nval;
			if (t.ttype == t.TT_EOF) {eof = true; return 0;}
		}
	}
}


class TC {

// opcodes
static final int ADD = 0;
static final int SUB = 1;
static final int MUL = 2;
static final int DIVI = 3;
static final int EQU = 4;
static final int LSS = 5;
static final int GTR = 6;
static final int LOAD = 7;
static final int LIT = 8;
static final int STO = 9;
static final int CALL = 10;
static final int RET = 11;
static final int RES = 12;
static final int JMP = 13;
static final int FJMP = 14;
static final int HALTc = 15;
static final int NEG = 16;
static final int READ = 17;
static final int WRITE = 18;

static int progStart;  // address of first instruction of main program
static int pc;         // program counter
static final int memSize = 15000;
static char code[] = new char[memSize];
static boolean generatingCode = true;

// data for Interpret
private static int stack[] = new int[1000];
private static int top;
private static int base;

static void Emit (int op) {
	if (generatingCode) {
		if (pc >= memSize - 4) {Parser.SemError(9); generatingCode = false;}
		else code[pc++] = (char) op;
	}
}

static void Emit2 (int op, int val) {
	if (generatingCode) {
		Emit(op);
		code[pc] = (char)(val / 256); code[pc+1] = (char)(val % 256);
		pc = pc + 2;
	}
}

static void Emit3 (int op, int level, int val) {
	if (generatingCode) {
		Emit(op);
		code[pc] = (char)level;
		code[pc+1] = (char)(val / 256); code[pc+2] = (char)(val % 256);
		pc = pc + 3;
	}
}

static void Fixup (int adr) {
	if (generatingCode) {
		code[adr] = (char)(pc / 256); code[adr+1] = (char)(pc % 256);
	}
}

static private int Next () {
	return (int) code[pc++];
}

static private int Next2 () {
	int x, y;
	x = (int)code[pc]; y = (int)code[pc+1];
	pc = pc + 2;
	return x * 256 + y;
}

static private int Int (boolean b) {
	if (b) return 1; else return 0;
}

static private void Push (int val) {
	stack[top++] = val;
}

static private int Pop() {
	return stack[--top];
}

static private int Up (int level) {
	int b;
	b = base;
	while (level > 0) {b = stack[b]; level--;}
	return b;
}

// Portability - use the following for Java 1.0
//    static void Interpret (DataInputStream data) { // Java 1.1
// Portability - use the following for Java 1.1
//    static void Interpret (BufferedReader data) { // Java 1.1

static void Interpret (BufferedReader data) { // Java 1.1
	int val, a, lev;
	System.out.println("Interpreting");
	IntStream in = new IntStream(data);
	pc = progStart; base = 0; top = 3; val = 0;
	for (;;)
		switch (Next()) {
			case LOAD:  {lev = Next(); a = Next2(); Push(stack[Up(lev)+a]); break;}
			case LIT:   {Push(Next2()); break;}
			case STO:   {lev = Next(); a = Next2(); stack[Up(lev)+a] = Pop(); break;}
			case ADD:   {val = Pop(); Push(Pop() + val); break;}
			case SUB:   {val = Pop(); Push(Pop() - val); break;}
			case DIVI:  {val = Pop();
			             if (val != 0) {Push(Pop() / val); break;}
			             else {System.out.println("Divide by zero"); System.exit(0);}
			            }
			case MUL:   {val = Pop(); Push(Pop() * val); break;}
			case EQU:   {val = Pop(); Push(Int(Pop() == val)); break;}
			case LSS:   {val = Pop(); Push(Int(Pop() < val)); break;}
			case GTR:   {val = Pop(); Push(Int(Pop() > val)); break;}
			case CALL:  {Push(Up(Next())); Push(base); Push(pc+2);
			             pc = Next2(); base = top-3; break;}
			case RET:   {top = base; base = stack[top+1]; pc = stack[top+2]; break;}
			case RES:   {top = top + Next2(); break;}
			case JMP:   {pc = Next2(); break;}
			case FJMP:  {a = Next2(); if (Pop()==0) pc = a; break;}
			case HALTc: {return;}
			case NEG:   {Push(-Pop()); break;}
			case READ:  {lev = Next(); a = Next2();
			             stack[Up(lev)+a] = in.readInt(); break;}
			case WRITE: {System.out.println(Pop()); break;}
			default:     System.exit(0);
		}
}

static void Init () {
	pc = 1;
}

}
