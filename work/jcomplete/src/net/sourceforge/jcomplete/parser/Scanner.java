package net.sourceforge.jcomplete.parser;
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

public class Scanner
{
    public abstract static class Buffer
    {
        public static final char eof = 65535;

        int _bufLen;
        int _pos;

        protected void setIndex(int position) {
            if (position < 0) position = 0; else if (position >= _bufLen) position = _bufLen;
            _pos = position;
        }
        protected abstract char read();
    }

    static class FBuffer extends Buffer
    {
        static char[] buf;

        FBuffer(File file) throws IOException
        {
            _bufLen = (int) file.length();

            FileReader fr = new FileReader(file);
            buf = new char[_bufLen];

            fr.read(buf);
            _pos = 0;
        }
        protected char read() {
            if (_pos < _bufLen) return buf[_pos++];
            else return eof;
        }
    }

    static class SBuffer extends Buffer
    {
        String chars;

        SBuffer(String string)
        {
            _bufLen = string.length();
            chars = string;
            _pos = 0;
        }
        protected char read() {
            if (_pos < _bufLen)
                return chars.charAt(_pos++);
            else return eof;
        }
    }

	private static final char EOF = '\0';
	private static final char CR  = '\r';
	private static final char LF  = '\n';
	private static final int noSym = 103;
	private static final int[] start = {
	 23,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  1,  4,  1,  1,  0,  0,  5,  7, 22, 11, 15, 21, 12,  2, 14,
	  8,  8,  8,  8,  8,  8,  8,  8,  8,  8, 13,  9, 16, 10, 19,  0,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
	  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1,
	  0};


    // set of characters to be ignored by the scanner
    private static BitSet ignore = new BitSet(128);
	static {
		ignore.set(1); ignore.set(2); ignore.set(3); ignore.set(4); 
		ignore.set(5); ignore.set(6); ignore.set(7); ignore.set(8); 
		ignore.set(9); ignore.set(10); ignore.set(11); ignore.set(12); 
		ignore.set(13); ignore.set(14); ignore.set(15); ignore.set(16); 
		ignore.set(17); ignore.set(18); ignore.set(19); ignore.set(20); 
		ignore.set(21); ignore.set(22); ignore.set(23); ignore.set(24); 
		ignore.set(25); ignore.set(26); ignore.set(27); ignore.set(28); 
		ignore.set(29); ignore.set(30); ignore.set(31); ignore.set(32); 
		
    }

	ErrorStream err;  // error messages

    private Buffer buf;        // data, random accessible
	protected Token t;           // current token
	protected char strCh;        // current input character (original)
	protected char ch;           // current input character (for token)
	protected char lastCh;       // last input character
	protected int pos;           // position of current character
	protected int line;          // line number of current character
	protected int lineStart;     // start position of current line

	public Scanner (File file, ErrorStream e) throws IOException
	{
		buf = new FBuffer(file);
        init(e, file.getName());
	}

	public Scanner (String parseString, ErrorStream e)
	{
		buf = new SBuffer(parseString);
        init(e, "");
	}

	public Scanner (Buffer buff, ErrorStream e)
	{
		this.buf = buff;
        init(e, "");
	}

	private void init(ErrorStream e, String eName) {
		err = e;
		err.fileName = eName;

		pos = -1; line = 1; lineStart = 0; lastCh = 0;
		NextCh();
	}

	void setPos(int position) {
	    buf.setIndex(position);
	}

	private void NextCh() {
		lastCh = ch;
		strCh = buf.read(); pos++;
		ch = Character.toUpperCase(strCh);
		if (ch == '\uffff') ch = EOF;
		else if (ch == CR) {line++; lineStart = pos + 1;}
		else if (ch == LF) {
			if (lastCh != CR) line++;
			lineStart = pos + 1;
		} else if (ch > '\u007f') {
			err.StoreError(0, line, pos - lineStart + 1, "invalid character in source file");
			err.count++; ch = ' ';
		}
	}

	private final boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart; char startCh;
		NextCh();
		if (ch == '-') {
			NextCh();
			for(;;) {
				if (ch == 13) {
					level--;
					if (level == 0) {NextCh(); return true;}
					NextCh();
					} else if (ch == EOF) return false;
					else NextCh();
				}
		} else {
			if (ch == CR || ch == LF) {line--; lineStart = lineStart0;}
			pos = pos - 2; setPos(pos+1); NextCh();
		}
		return false;
	}
	private final boolean Comment1() {
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


	private void CheckLiteral(StringBuffer buf) {
		t.val = buf.toString().toUpperCase();
		switch (t.val.charAt(0)) {
			case 'A': {
				if (t.val.equals("ACTION")) t.kind = 90;
				else if (t.val.equals("ADD")) t.kind = 97;
				else if (t.val.equals("ALL")) t.kind = 11;
				else if (t.val.equals("ALTER")) t.kind = 98;
				else if (t.val.equals("AND")) t.kind = 57;
				else if (t.val.equals("AS")) t.kind = 23;
				else if (t.val.equals("ASC")) t.kind = 51;
				else if (t.val.equals("AVG")) t.kind = 48;
				break;}
			case 'B': {
				if (t.val.equals("BETWEEN")) t.kind = 67;
				else if (t.val.equals("BY")) t.kind = 36;
				break;}
			case 'C': {
				if (t.val.equals("CASCADE")) t.kind = 88;
				else if (t.val.equals("CHAR")) t.kind = 72;
				else if (t.val.equals("CHARACTER")) t.kind = 73;
				else if (t.val.equals("CHECK")) t.kind = 92;
				else if (t.val.equals("COMMIT")) t.kind = 69;
				else if (t.val.equals("CONSTRAINT")) t.kind = 99;
				else if (t.val.equals("COUNT")) t.kind = 44;
				else if (t.val.equals("CREATE")) t.kind = 93;
				else if (t.val.equals("CROSS")) t.kind = 25;
				break;}
			case 'D': {
				if (t.val.equals("DATE")) t.kind = 79;
				else if (t.val.equals("DEFAULT")) t.kind = 81;
				else if (t.val.equals("DELETE")) t.kind = 18;
				else if (t.val.equals("DESC")) t.kind = 50;
				else if (t.val.equals("DISTINCT")) t.kind = 21;
				else if (t.val.equals("DROP")) t.kind = 96;
				break;}
			case 'E': {
				if (t.val.equals("ESCAPE")) t.kind = 60;
				else if (t.val.equals("EXCEPT")) t.kind = 8;
				break;}
			case 'F': {
				if (t.val.equals("FOREIGN")) t.kind = 84;
				else if (t.val.equals("FROM")) t.kind = 19;
				else if (t.val.equals("FULL")) t.kind = 28;
				break;}
			case 'G': {
				if (t.val.equals("GROUP")) t.kind = 35;
				break;}
			case 'H': {
				if (t.val.equals("HAVING")) t.kind = 37;
				break;}
			case 'I': {
				if (t.val.equals("IN")) t.kind = 68;
				else if (t.val.equals("INDEX")) t.kind = 100;
				else if (t.val.equals("INNER")) t.kind = 27;
				else if (t.val.equals("INSERT")) t.kind = 15;
				else if (t.val.equals("INT")) t.kind = 76;
				else if (t.val.equals("INTEGER")) t.kind = 75;
				else if (t.val.equals("INTERSECT")) t.kind = 9;
				else if (t.val.equals("INTO")) t.kind = 16;
				else if (t.val.equals("IS")) t.kind = 61;
				break;}
			case 'J': {
				if (t.val.equals("JOIN")) t.kind = 24;
				break;}
			case 'K': {
				if (t.val.equals("KEY")) t.kind = 83;
				break;}
			case 'L': {
				if (t.val.equals("LEFT")) t.kind = 29;
				else if (t.val.equals("LIKE")) t.kind = 59;
				break;}
			case 'M': {
				if (t.val.equals("MATCH")) t.kind = 86;
				else if (t.val.equals("MAX")) t.kind = 46;
				else if (t.val.equals("MIN")) t.kind = 47;
				else if (t.val.equals("MINUS")) t.kind = 10;
				else if (t.val.equals("MONTH")) t.kind = 42;
				break;}
			case 'N': {
				if (t.val.equals("NATURAL")) t.kind = 26;
				else if (t.val.equals("NO")) t.kind = 89;
				else if (t.val.equals("NOT")) t.kind = 54;
				else if (t.val.equals("NULL")) t.kind = 49;
				else if (t.val.equals("NUMERIC")) t.kind = 78;
				break;}
			case 'O': {
				if (t.val.equals("ON")) t.kind = 32;
				else if (t.val.equals("OR")) t.kind = 58;
				else if (t.val.equals("ORDER")) t.kind = 38;
				else if (t.val.equals("OUTER")) t.kind = 31;
				break;}
			case 'P': {
				if (t.val.equals("PARTIAL")) t.kind = 87;
				else if (t.val.equals("PRIMARY")) t.kind = 82;
				break;}
			case 'R': {
				if (t.val.equals("REFERENCES")) t.kind = 85;
				else if (t.val.equals("RESTRICT")) t.kind = 95;
				else if (t.val.equals("RIGHT")) t.kind = 30;
				else if (t.val.equals("ROLLBACK")) t.kind = 70;
				break;}
			case 'S': {
				if (t.val.equals("SELECT")) t.kind = 20;
				else if (t.val.equals("SET")) t.kind = 13;
				else if (t.val.equals("SMALLINT")) t.kind = 77;
				else if (t.val.equals("SUM")) t.kind = 45;
				break;}
			case 'T': {
				if (t.val.equals("TABLE")) t.kind = 94;
				else if (t.val.equals("TIME")) t.kind = 80;
				else if (t.val.equals("TIMESTAMP")) t.kind = 40;
				break;}
			case 'U': {
				if (t.val.equals("UNION")) t.kind = 7;
				else if (t.val.equals("UNIQUE")) t.kind = 91;
				else if (t.val.equals("UPDATE")) t.kind = 12;
				else if (t.val.equals("UPPER")) t.kind = 41;
				else if (t.val.equals("USING")) t.kind = 33;
				break;}
			case 'V': {
				if (t.val.equals("VALUES")) t.kind = 17;
				else if (t.val.equals("VARCHAR")) t.kind = 74;
				break;}
			case 'W': {
				if (t.val.equals("WHERE")) t.kind = 34;
				else if (t.val.equals("WORK")) t.kind = 71;
				break;}
			case 'Y': {
				if (t.val.equals("YEAR")) t.kind = 43;
				break;}
		}
	}

	Token Scan() {
		while (ignore.get((int)ch)) NextCh();
		if (ch == '-' && Comment0()  || ch == '/' && Comment1() ) return Scan();
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
					if ((ch == '!'
					  || ch >= '#' && ch <= '$'
					  || ch >= '0' && ch <= '9'
					  || ch >= '@' && ch <= '{'
					  || ch >= '}')) {break;}
					else {t.kind = 1; CheckLiteral(buf); break loop;}
				case 2:
					if ((ch >= '0' && ch <= '9')) {state = 3; break;}
					else {t.kind = 22; break loop;}
				case 3:
					if ((ch >= '0' && ch <= '9')) {break;}
					else {t.kind = 3; break loop;}
				case 4:
					if ((ch >= ' ' && ch <= '!'
					  || ch >= '#')) {break;}
					else if (ch == '"') {state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 5:
					if ((ch >= ' ' && ch <= '&'
					  || ch >= '(')) {break;}
					else if (ch == 39) {state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 6:
					{t.kind = 4; break loop;}
				case 7:
					{t.kind = 5; break loop;}
				case 8:
					if ((ch >= '0' && ch <= '9')) {break;}
					else if (ch == '.') {state = 2; break;}
					else {t.kind = 2; break loop;}
				case 9:
					{t.kind = 6; break loop;}
				case 10:
					{t.kind = 14; break loop;}
				case 11:
					{t.kind = 39; break loop;}
				case 12:
					{t.kind = 52; break loop;}
				case 13:
					{t.kind = 53; break loop;}
				case 14:
					{t.kind = 55; break loop;}
				case 15:
					{t.kind = 56; break loop;}
				case 16:
					if (ch == '>') {state = 17; break;}
					else if (ch == '=') {state = 18; break;}
					else {t.kind = 63; break loop;}
				case 17:
					{t.kind = 62; break loop;}
				case 18:
					{t.kind = 64; break loop;}
				case 19:
					if (ch == '=') {state = 20; break;}
					else {t.kind = 65; break loop;}
				case 20:
					{t.kind = 66; break loop;}
				case 21:
					{t.kind = 101; break loop;}
				case 22:
					{t.kind = 102; break loop;}
				case 23:
					{t.kind = 0; break loop;}
			}
		}
		t.str = buf.toString();
		t.val = t.str.toUpperCase();
		return t;
	}
}
