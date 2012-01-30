package net.sourceforge.squirrel_sql.client.session.parser.kernel;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorStream;

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
   
	/**
	 * The constants in this array are very confusing.
	 * I don't think, that the constants don't mean, what their name said. e.g. for the '*' character, the constant ParsingConstants.KW_UNION is used.
	 * Seems, that this is the result of a refactoring task.  
	 */
	private static final int[] start = {
    ParsingConstants.KW_AS,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
     0,  1,  4,  1,  1,  0,  0,  ParsingConstants.KIND_OPENING_BRAKET,  ParsingConstants.KW_UNION, 22, ParsingConstants.KW_ALL, ParsingConstants.KW_INSERT, ParsingConstants.KW_DISTINCT, ParsingConstants.KW_UPDATE,  2, ParsingConstants.KIND_EQUALS,
     ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT, ParsingConstants.KW_SET,  ParsingConstants.KW_INTERSECT, ParsingConstants.KW_INTO, ParsingConstants.KW_MINUS, ParsingConstants.KW_FROM,  0,
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1,
     0};


    // set of characters to be ignored by the scanner
    private static BitSet ignore = new BitSet(128);
   static {
      ignore.set(1); ignore.set(2); ignore.set(3); ignore.set(4);
      ignore.set(ParsingConstants.KIND_OPENING_BRAKET); ignore.set(6); ignore.set(ParsingConstants.KW_UNION); ignore.set(ParsingConstants.KW_EXCEPT);
      ignore.set(ParsingConstants.KW_INTERSECT); ignore.set(ParsingConstants.KW_MINUS); ignore.set(ParsingConstants.KW_ALL); ignore.set(ParsingConstants.KW_UPDATE);
      ignore.set(ParsingConstants.KW_SET); ignore.set(ParsingConstants.KIND_EQUALS); ignore.set(ParsingConstants.KW_INSERT); ignore.set(ParsingConstants.KW_INTO);
      ignore.set(ParsingConstants.KW_VALUES); ignore.set(ParsingConstants.KW_DELETE); ignore.set(ParsingConstants.KW_FROM); ignore.set(ParsingConstants.KW_SELECT);
      ignore.set(ParsingConstants.KW_DISTINCT); ignore.set(22); ignore.set(ParsingConstants.KW_AS); ignore.set(ParsingConstants.KW_JOIN);
      ignore.set(ParsingConstants.KW_CROSS); ignore.set(ParsingConstants.KW_NATURAL); ignore.set(ParsingConstants.KW_INNER); ignore.set(ParsingConstants.KW_FULL);
      ignore.set(ParsingConstants.KW_LEFT); ignore.set(ParsingConstants.KW_RIGHT); ignore.set(ParsingConstants.KW_OUTER); ignore.set(ParsingConstants.KW_ON);

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
		}
	}

	private final boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart; char startCh;
		NextCh();
		if (ch == '-') {
			NextCh();
			for(;;) {
				if (ch == ParsingConstants.KW_MINUS) {
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
				if (t.val.equals("ACTION")) t.kind = ParsingConstants.KW_ACTION;
				else if (t.val.equals("ADD")) t.kind = ParsingConstants.KW_ADD;
				else if (t.val.equals("ALL")) t.kind = ParsingConstants.KW_ALL;
				else if (t.val.equals("ALTER")) t.kind = ParsingConstants.KW_ALTER;
				else if (t.val.equals("AND")) t.kind = ParsingConstants.KW_AND;
				else if (t.val.equals("AS")) t.kind = ParsingConstants.KW_AS;
				else if (t.val.equals("ASC")) t.kind = ParsingConstants.KW_ASC;
				else if (t.val.equals("AVG")) t.kind = ParsingConstants.KW_AVG;
				break;}
			case 'B': {
				if (t.val.equals("BETWEEN")) t.kind = ParsingConstants.KW_BETWEEN;
				else if (t.val.equals("BY")) t.kind = ParsingConstants.KW_BY;
				break;}
			case 'C': {
				if (t.val.equals("CASCADE")) t.kind = ParsingConstants.KW_CASCADE;
				else if (t.val.equals("CHAR")) t.kind = ParsingConstants.KW_CHAR;
				else if (t.val.equals("CHARACTER")) t.kind = ParsingConstants.KW_CHARACTER;
				else if (t.val.equals("CASE")) t.kind = ParsingConstants.KW_CASE;
				else if (t.val.equals("CHECK")) t.kind = ParsingConstants.KW_CHECK;
				else if (t.val.equals("COMMIT")) t.kind = ParsingConstants.KW_COMMIT;
				else if (t.val.equals("CONSTRAINT")) t.kind = ParsingConstants.KW_CONSTRAINT;
				else if (t.val.equals("COUNT")) t.kind = ParsingConstants.KW_COUNT;
				else if (t.val.equals("CREATE")) t.kind = ParsingConstants.KW_CREATE;
				else if (t.val.equals("CROSS")) t.kind = ParsingConstants.KW_CROSS;
				break;}
			case 'D': {
				if (t.val.equals("DATE")) t.kind = ParsingConstants.KW_DATE;
				else if (t.val.equals("DEFAULT")) t.kind = ParsingConstants.KW_DEFAULT;
				else if (t.val.equals("DELETE")) t.kind = ParsingConstants.KW_DELETE;
				else if (t.val.equals("DESC")) t.kind = ParsingConstants.KW_DESC;
				else if (t.val.equals("DISTINCT")) t.kind = ParsingConstants.KW_DISTINCT;
				else if (t.val.equals("DROP")) t.kind = ParsingConstants.KW_DROP;
				break;}
			case 'E': {
            if (t.val.equals("ELSE")) t.kind = ParsingConstants.KW_ELSE;
            else if (t.val.equals("END")) t.kind = ParsingConstants.KW_END;
				else if (t.val.equals("ESCAPE")) t.kind = ParsingConstants.KW_ESCAPE;
				else if (t.val.equals("EXCEPT")) t.kind = ParsingConstants.KW_EXCEPT;
				break;}
			case 'F': {
				if (t.val.equals("FOREIGN")) t.kind = ParsingConstants.KW_FOREIGN;
				else if (t.val.equals("FROM")) t.kind = ParsingConstants.KW_FROM;
				else if (t.val.equals("FULL")) t.kind = ParsingConstants.KW_FULL;
				break;}
			case 'G': {
				if (t.val.equals("GROUP")) t.kind = ParsingConstants.KW_GROUP;
				break;}
			case 'H': {
				if (t.val.equals("HAVING")) t.kind = ParsingConstants.KW_HAVING;
				break;}
			case 'I': {
				if (t.val.equals("IN")) t.kind = ParsingConstants.KW_IN;
				else if (t.val.equals("INDEX")) t.kind = ParsingConstants.KW_INDEX;
				else if (t.val.equals("INNER")) t.kind = ParsingConstants.KW_INNER;
				else if (t.val.equals("INSERT")) t.kind = ParsingConstants.KW_INSERT;
				else if (t.val.equals("INT")) t.kind = ParsingConstants.KW_INT;
				else if (t.val.equals("INTEGER")) t.kind = ParsingConstants.KW_INTEGER;
				else if (t.val.equals("INTERSECT")) t.kind = ParsingConstants.KW_INTERSECT;
				else if (t.val.equals("INTO")) t.kind = ParsingConstants.KW_INTO;
				else if (t.val.equals("IS")) t.kind = ParsingConstants.KW_IS;
				break;}
			case 'J': {
				if (t.val.equals("JOIN")) t.kind = ParsingConstants.KW_JOIN;
				break;}
			case 'K': {
				if (t.val.equals("KEY")) t.kind = ParsingConstants.KW_KEY;
				break;}
			case 'L': {
				if (t.val.equals("LEFT")) t.kind = ParsingConstants.KW_LEFT;
				else if (t.val.equals("LIKE")) t.kind = ParsingConstants.KW_LIKE;
				break;}
			case 'M': {
				if (t.val.equals("MATCH")) t.kind = ParsingConstants.KW_MATCH;
				else if (t.val.equals("MAX")) t.kind = ParsingConstants.KW_MAX;
				else if (t.val.equals("MIN")) t.kind = ParsingConstants.KW_MIN;
				else if (t.val.equals("MINUS")) t.kind = ParsingConstants.KW_MINUS;
				else if (t.val.equals("MONTH")) t.kind = ParsingConstants.KW_MONTH;
				break;}
			case 'N': {
				if (t.val.equals("NATURAL")) t.kind = ParsingConstants.KW_NATURAL;
				else if (t.val.equals("NO")) t.kind = ParsingConstants.KW_NO;
				else if (t.val.equals("NOT")) t.kind = ParsingConstants.KW_NOT;
				else if (t.val.equals("NULL")) t.kind = ParsingConstants.KW_NULL;
				else if (t.val.equals("NUMERIC")) t.kind = ParsingConstants.KW_NUMERIC;
				break;}
			case 'O': {
				if (t.val.equals("ON")) t.kind = ParsingConstants.KW_ON;
				else if (t.val.equals("OR")) t.kind = ParsingConstants.KW_OR;
				else if (t.val.equals("ORDER")) t.kind = ParsingConstants.KW_ORDER;
				else if (t.val.equals("OUTER")) t.kind = ParsingConstants.KW_OUTER;
				break;}
			case 'P': {
				if (t.val.equals("PARTIAL")) t.kind = ParsingConstants.KW_PARTIAL;
				else if (t.val.equals("PRIMARY")) t.kind = ParsingConstants.KW_PRIMARY;
				break;}
			case 'R': {
				if (t.val.equals("REFERENCES")) t.kind = ParsingConstants.KW_REFERENCES;
				else if (t.val.equals("RESTRICT")) t.kind = ParsingConstants.KW_RESTRICT;
				else if (t.val.equals("RIGHT")) t.kind = ParsingConstants.KW_RIGHT;
				else if (t.val.equals("ROLLBACK")) t.kind = ParsingConstants.KW_ROLLBACK;
				break;}
			case 'S': {
				if (t.val.equals("SELECT")) t.kind = ParsingConstants.KW_SELECT;
				else if (t.val.equals("SET")) t.kind = ParsingConstants.KW_SET;
				else if (t.val.equals("SMALLINT")) t.kind = ParsingConstants.KW_SMALLINT;
				else if (t.val.equals("SUM")) t.kind = ParsingConstants.KW_SUM;
				break;}
			case 'T': {
				if (t.val.equals("TABLE")) t.kind = ParsingConstants.KW_TABLE;
				else if (t.val.equals("THEN")) t.kind = ParsingConstants.KW_THEN;
				else if (t.val.equals("TIME")) t.kind = ParsingConstants.KW_TIME;
				else if (t.val.equals("TIMESTAMP")) t.kind = ParsingConstants.KW_TIMESTAMP;
				break;}
			case 'U': {
				if (t.val.equals("UNION")) t.kind = ParsingConstants.KW_UNION;
				else if (t.val.equals("UNIQUE")) t.kind = ParsingConstants.KW_UNIQUE;
				else if (t.val.equals("UPDATE")) t.kind = ParsingConstants.KW_UPDATE;
				else if (t.val.equals("UPPER")) t.kind = ParsingConstants.KW_UPPER;
				else if (t.val.equals("USING")) t.kind = ParsingConstants.KW_USING;
				break;}
			case 'V': {
				if (t.val.equals("VALUES")) t.kind = ParsingConstants.KW_VALUES;
				else if (t.val.equals("VARCHAR")) t.kind = ParsingConstants.KW_VARCHAR;
				else if (t.val.equals("VIEW")) t.kind = ParsingConstants.KW_VIEW;
				break;}
			case 'W': {
				if (t.val.equals("WHERE")) t.kind = ParsingConstants.KW_WHERE;
				else if (t.val.equals("WHEN")) t.kind = ParsingConstants.KW_WHEN;
				else if (t.val.equals("WORK")) t.kind = ParsingConstants.KW_WORK;
				break;}
			case 'Y': {
				if (t.val.equals("YEAR")) t.kind = ParsingConstants.KW_YEAR;
				break;}
		}
	}

	Token Scan() {
		while (ignore.get((int)ch)) NextCh();
		if (ch == '-' && Comment0()  || ch == '/' && Comment1() ) return Scan();
		t = new Token();
		t.pos = pos; t.col = pos - lineStart + 1; t.line = line;
		StringBuffer buf = new StringBuffer();
		int state;
		if(ch < start.length){
			// the start state is only defined for the first 128 characters.
			state = start[ch];
		}else{
			// for the rest, assume the value 1;
			state = 1;
		}
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
				case ParsingConstants.KIND_OPENING_BRAKET:
					if ((ch >= ' ' && ch <= '&'
					  || ch >= '(')) {break;}
					else if (ch == 39) {state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 6:
					{t.kind = 4; break loop;}
				case ParsingConstants.KW_UNION:
					{t.kind = ParsingConstants.KIND_OPENING_BRAKET; break loop;}
				case ParsingConstants.KW_EXCEPT:
					if ((ch >= '0' && ch <= '9')) {break;}
					else if (ch == '.') {state = 2; break;}
					else {t.kind = 2; break loop;}
				case ParsingConstants.KW_INTERSECT:
					{t.kind = 6; break loop;}
				case ParsingConstants.KW_MINUS:
					{t.kind = ParsingConstants.KIND_EQUALS; break loop;}
				case ParsingConstants.KW_ALL:
					{t.kind = ParsingConstants.KIND_ASTERISK; break loop;}
				case ParsingConstants.KW_UPDATE:
					{t.kind = ParsingConstants.KW_MINUS_SIGN; break loop;}
				case ParsingConstants.KW_SET:
					{t.kind = 53; break loop;}
				case ParsingConstants.KIND_EQUALS:
					{t.kind = 55; break loop;}
				case ParsingConstants.KW_INSERT:
					{t.kind = 56; break loop;}
				case ParsingConstants.KW_INTO:
					if (ch == '>') {state = ParsingConstants.KW_VALUES; break;}
					else if (ch == '=') {state = ParsingConstants.KW_DELETE; break;}
					else {t.kind = 63; break loop;}
				case ParsingConstants.KW_VALUES:
					{t.kind = 62; break loop;}
				case ParsingConstants.KW_DELETE:
					{t.kind = 64; break loop;}
				case ParsingConstants.KW_FROM:
					if (ch == '=') {state = ParsingConstants.KW_SELECT; break;}
					else {t.kind = 65; break loop;}
				case ParsingConstants.KW_SELECT:
					{t.kind = 66; break loop;}
				case ParsingConstants.KW_DISTINCT:
					{t.kind = ParsingConstants.KIND_COMMA; break loop;}
				case 22:
					{t.kind = ParsingConstants.KIND_CLOSING_BRAKET; break loop;}
				case ParsingConstants.KW_AS:
					{t.kind = 0; break loop;}
			}
		}
		t.str = buf.toString();
		t.val = t.str.toUpperCase();
		return t;
	}
}
