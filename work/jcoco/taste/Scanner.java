package Taste;
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

// Portability - use the following for Java 1.0
//	static byte[] buf;  // Java 1.0
// Portability - use the following for Java 1.1
//	static char[] buf;  // Java 1.1

	static char[] buf;  // Java 1.1

	static int bufLen;
	static int pos;
	static final int eof = 65535;

	static void Fill(String name) {
		try {
			File f = new File(name); bufLen = (int) f.length();

// Portability - use the following for Java 1.0
//			BufferedInputStream s = new BufferedInputStream(new FileInputStream(f), bufLen);
//			buf = new byte[bufLen];  // Java 1.0
// Portability - use the following for Java 1.1
//			BufferedReader s = new BufferedReader(new FileReader(f), bufLen);
//			buf = new char[bufLen];  // Java 1.1

			BufferedReader s = new BufferedReader(new FileReader(f), bufLen);
			buf = new char[bufLen];  // Java 1.1

			int n = s.read(buf); pos = 0;
		} catch (IOException e) {
			System.out.println("--- cannot open file " + name);
			System.exit(0);
		}
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
	private static final int noSym = 29;
	private static final int[] start = {
	 13,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8, 10,  0,  7,  4,  9,
	  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  5,  3, 11,  6, 12,  0,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,
	  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,  0,
	  0};

	private static Token t;        // current token
	private static char strCh;     // current input character (original)
	private static char ch;        // current input character (for token)
	private static char lastCh;    // last input character
	private static int pos;        // position of current character
	private static int line;       // line number of current character
	private static int lineStart;  // start position of current line
	private static BitSet ignore;  // set of characters to be ignored by the scanner

	static void Init (String fileName, ErrorStream e) {
		Buffer.Fill(fileName);
		e.fileName = fileName;
		pos = -1; line = 1; lineStart = 0; lastCh = 0;
		NextCh();
		ignore = new BitSet(128);
		ignore.set(9); ignore.set(10); ignore.set(13); ignore.set(32); 
		
		err = e;
	}

	static void Init (String fileName) {
		Init(fileName, new ErrorStream());
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

	private static boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart; char startCh;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == ')') {
						level--;
						if (level == 0) {NextCh(); return true;}
						NextCh();
					}
				} else if (ch == '(') {
					NextCh();
					if (ch == '*') {
						level++; NextCh();
					}
					} else if (ch == EOF) return false;
					else NextCh();
				}
		} else {
			if (ch == CR || ch == LF) {line--; lineStart = lineStart0;}
			pos = pos - 2; Buffer.Set(pos+1); NextCh();
		}
		return false;
	}


	private static void CheckLiteral(StringBuffer buf) {
		t.val = buf.toString();
		switch (t.val.charAt(0)) {
			case 'B': {
				if (t.val.equals("BEGIN")) t.kind = 9;
				else if (t.val.equals("BOOLEAN")) t.kind = 12;
				break;}
			case 'D': {
				if (t.val.equals("DO")) t.kind = 18;
				break;}
			case 'E': {
				if (t.val.equals("ELSE")) t.kind = 16;
				else if (t.val.equals("END")) t.kind = 10;
				break;}
			case 'F': {
				if (t.val.equals("FALSE")) t.kind = 22;
				break;}
			case 'I': {
				if (t.val.equals("IF")) t.kind = 14;
				else if (t.val.equals("INTEGER")) t.kind = 11;
				break;}
			case 'P': {
				if (t.val.equals("PROCEDURE")) t.kind = 8;
				else if (t.val.equals("PROGRAM")) t.kind = 3;
				break;}
			case 'R': {
				if (t.val.equals("READ")) t.kind = 19;
				break;}
			case 'T': {
				if (t.val.equals("THEN")) t.kind = 15;
				else if (t.val.equals("TRUE")) t.kind = 21;
				break;}
			case 'V': {
				if (t.val.equals("VAR")) t.kind = 6;
				break;}
			case 'W': {
				if (t.val.equals("WHILE")) t.kind = 17;
				else if (t.val.equals("WRITE")) t.kind = 20;
				break;}
		}
	}

	static Token Scan() {
		while (ignore.get((int)ch)) NextCh();
		if (ch == '(' && Comment0() ) return Scan();
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
					  || ch >= 'a' && ch <= 'z')) {break;}
					else {t.kind = 1; CheckLiteral(buf); break loop;}
				case 2:
					if ((ch >= '0' && ch <= '9')) {break;}
					else {t.kind = 2; break loop;}
				case 3:
					{t.kind = 4; break loop;}
				case 4:
					{t.kind = 5; break loop;}
				case 5:
					{t.kind = 7; break loop;}
				case 6:
					{t.kind = 13; break loop;}
				case 7:
					{t.kind = 23; break loop;}
				case 8:
					{t.kind = 24; break loop;}
				case 9:
					{t.kind = 25; break loop;}
				case 10:
					{t.kind = 26; break loop;}
				case 11:
					{t.kind = 27; break loop;}
				case 12:
					{t.kind = 28; break loop;}
				case 13:
					{t.kind = 0; break loop;}
			}
		}
		t.str = buf.toString();
		t.val = t.str;
		return t;
	}
}
