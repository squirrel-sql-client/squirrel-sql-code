package Coco;
import java.io.*;
import java.util.*;

class Token {
	int kind;    // token kind
	int pos;     // token position in the source text (starting at 0)
	int col;     // token column (starting at 0)
	int line;    // token line (starting at 1)
	String str;  // exact string value
	String val;  // token string value (uppercase if ignoreCase)
}

class Buffer {

	static char[] buf;  // Java 1.1

	static int bufLen;
	static int pos;
	static final int eof = 65535;

	static void Fill(File f) throws IOException
	{
        bufLen = (int) f.length();

        BufferedReader s = new BufferedReader(new FileReader(f), bufLen);
        buf = new char[bufLen];  // Java 1.1

        int n = s.read(buf); pos = 0;
	}

	static void Set(int position) {
		if (position < 0) position = 0; else if (position >= bufLen) position = bufLen;
		pos = position;
	}

	static int read() {
		if (pos < bufLen) return (int) buf[pos++]; else return eof;
	}
}

class Scanner {

	static ErrorStream err;  // error messages

	private static final char EOF = '\0';
	private static final char CR  = '\r';
	private static final char LF  = '\n';
	private static final int noSym = 45;
	private static final int[] start = {
	 30,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  6,  0,  5,  0,  0,  7, 16, 17,  0, 13, 12, 14, 11,  0,
	  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  0,  0, 18, 10, 22,  0,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 20,  0, 21, 19,  1,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 26, 25, 27,  0,  0,
	  0};

	private static Token t;        // current token
	private static char strCh;     // current input character (original)
	private static char ch;        // current input character (for token)
	private static char lastCh;    // last input character
	private static int pos;        // position of current character
	private static int line;       // line number of current character
	private static int lineStart;  // start position of current line
	private static BitSet ignore;  // set of characters to be ignored by the scanner

	static void setPos(int position) {
	    Buffer.Set(position);
	}

	static void Init (File file, ErrorStream e)
	{
		err = e;
		err.fileName = file.getPath();
	    try {
    		Buffer.Fill(file);
		} catch (IOException x) {
			err.Exception("--- cannot open file " + file.getPath());
		}
		pos = -1; line = 1; lineStart = 0; lastCh = 0;
		NextCh();
		ignore = new BitSet(128);
		ignore.set(9); ignore.set(10); ignore.set(13); ignore.set(32); 
		
	}

	static void Init (File file) {
		Init(file, new ErrorStream());
	}

	private static void NextCh() {
		lastCh = ch;
		strCh = (char) Buffer.read(); pos++;
		ch = strCh;
		if (ch == '\uffff') ch = EOF;
		else if (ch == CR) {line++; lineStart = pos + 1;}
		else if (ch == LF) {
			if (lastCh != CR) line++;
			lineStart = pos + 1;
		} else if (ch > '\u007f') {
			Scanner.err.StoreError(0, line, pos - lineStart + 1, "invalid character in source file");
			Scanner.err.count++; ch = ' ';
		}
	}

	private static final boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart; char startCh;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == '/') {
						level--;
						if (level == 0) {NextCh(); return true;}
						NextCh();
					}
				} else if (ch == '/') {
					NextCh();
					if (ch == '*') {
						level++; NextCh();
					}
					} else if (ch == EOF) return false;
					else NextCh();
				}
		} else {
			if (ch == CR || ch == LF) {line--; lineStart = lineStart0;}
			pos = pos - 2; setPos(pos+1); NextCh();
		}
		return false;
	}


	private static void CheckLiteral(StringBuffer buf) {
		t.val = buf.toString();
		switch (t.val.charAt(0)) {
			case 'A': {
				if (t.val.equals("ANY")) t.kind = 26;
				break;}
			case 'C': {
				if (t.val.equals("CASE")) t.kind = 22;
				else if (t.val.equals("CHARACTERS")) t.kind = 13;
				else if (t.val.equals("CHR")) t.kind = 27;
				else if (t.val.equals("COMMENTS")) t.kind = 17;
				else if (t.val.equals("COMPILER")) t.kind = 5;
				else if (t.val.equals("CONTEXT")) t.kind = 42;
				break;}
			case 'E': {
				if (t.val.equals("END")) t.kind = 9;
				break;}
			case 'F': {
				if (t.val.equals("FROM")) t.kind = 18;
				break;}
			case 'I': {
				if (t.val.equals("IGNORE")) t.kind = 21;
				else if (t.val.equals("IMPORT")) t.kind = 11;
				break;}
			case 'N': {
				if (t.val.equals("NAMES")) t.kind = 15;
				else if (t.val.equals("NESTED")) t.kind = 20;
				break;}
			case 'P': {
				if (t.val.equals("PACKAGE")) t.kind = 10;
				else if (t.val.equals("PRAGMAS")) t.kind = 16;
				else if (t.val.equals("PRODUCTIONS")) t.kind = 6;
				break;}
			case 'S': {
				if (t.val.equals("SYNC")) t.kind = 41;
				break;}
			case 'T': {
				if (t.val.equals("TO")) t.kind = 19;
				else if (t.val.equals("TOKENS")) t.kind = 14;
				break;}
			case 'W': {
				if (t.val.equals("WEAK")) t.kind = 38;
				break;}
		}
	}

	static Token Scan() {
		while (ignore.get((int)ch)) NextCh();
		if (ch == '/' && Comment0() ) return Scan();
		t = new Token();
		t.pos = pos; t.col = pos - lineStart + 1; t.line = line;
		StringBuffer buf = new StringBuffer();
		int state = start[ch];
		int apx = 0;
		loop: for (;;) {
			buf.append(strCh);
			NextCh();
			switch (state) {
				case 0:
					{t.kind = noSym; break loop;} // NextCh already done
				case 1:
					if ((ch >= '0' && ch <= '9'
					  || ch >= 'A' && ch <= 'Z'
					  || ch == '_'
					  || ch >= 'a' && ch <= 'z')) {break;}
					else {t.kind = 1; CheckLiteral(buf); break loop;}
				case 2:
					{t.kind = 2; break loop;}
				case 3:
					{t.kind = 3; break loop;}
				case 4:
					if ((ch >= '0' && ch <= '9')) {break;}
					else {t.kind = 4; break loop;}
				case 5:
					if ((ch >= '0' && ch <= '9'
					  || ch >= 'A' && ch <= 'Z'
					  || ch == '_'
					  || ch >= 'a' && ch <= 'z')) {break;}
					else {t.kind = 46; break loop;}
				case 6:
					if ((ch >= ' ' && ch <= '!'
					  || ch >= '#' && ch <= '['
					  || ch >= ']')) {break;}
					else if ((ch == 92)) {state = 8; break;}
					else if ((ch == 10
					  || ch == 13)) {state = 3; break;}
					else if (ch == '"') {state = 2; break;}
					else {t.kind = noSym; break loop;}
				case 7:
					if ((ch >= ' ' && ch <= '&'
					  || ch >= '(' && ch <= '['
					  || ch >= ']')) {break;}
					else if ((ch == 92)) {state = 9; break;}
					else if ((ch == 10
					  || ch == 13)) {state = 3; break;}
					else if (ch == 39) {state = 2; break;}
					else {t.kind = noSym; break loop;}
				case 8:
					if ((ch >= ' ')) {state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 9:
					if ((ch >= ' ')) {state = 7; break;}
					else {t.kind = noSym; break loop;}
				case 10:
					{t.kind = 7; break loop;}
				case 11:
					if (ch == '.') {state = 15; break;}
					else if (ch == '>') {state = 24; break;}
					else if (ch == ')') {state = 29; break;}
					else {t.kind = 8; break loop;}
				case 12:
					{t.kind = 12; break loop;}
				case 13:
					{t.kind = 23; break loop;}
				case 14:
					{t.kind = 24; break loop;}
				case 15:
					{t.kind = 25; break loop;}
				case 16:
					if (ch == '.') {state = 28; break;}
					else {t.kind = 28; break loop;}
				case 17:
					{t.kind = 29; break loop;}
				case 18:
					if (ch == '.') {state = 23; break;}
					else {t.kind = 30; break loop;}
				case 19:
					{t.kind = 31; break loop;}
				case 20:
					{t.kind = 32; break loop;}
				case 21:
					{t.kind = 33; break loop;}
				case 22:
					{t.kind = 34; break loop;}
				case 23:
					{t.kind = 35; break loop;}
				case 24:
					{t.kind = 36; break loop;}
				case 25:
					{t.kind = 37; break loop;}
				case 26:
					{t.kind = 39; break loop;}
				case 27:
					{t.kind = 40; break loop;}
				case 28:
					{t.kind = 43; break loop;}
				case 29:
					{t.kind = 44; break loop;}
				case 30:
					{t.kind = 0; break loop;}
			}
		}
		t.str = buf.toString();
		t.val = t.str;
		return t;
	}
}
