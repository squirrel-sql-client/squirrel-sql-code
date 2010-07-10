package Coco;

class ErrorStream {

	int count;  // number of errors detected
	public String fileName;

	ErrorStream() {
		count = 0;
	}

	void StoreError(int n, int line, int col, String s) {
		System.out.println(fileName + " (" + line + ", " + col + ") " + s);
	}

	void ParsErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			case 0: {s = "EOF expected"; break;}
			case 1: {s = "ident expected"; break;}
			case 2: {s = "string expected"; break;}
			case 3: {s = "badString expected"; break;}
			case 4: {s = "number expected"; break;}
			case 5: {s = "\"COMPILER\" expected"; break;}
			case 6: {s = "\"PRODUCTIONS\" expected"; break;}
			case 7: {s = "\"=\" expected"; break;}
			case 8: {s = "\".\" expected"; break;}
			case 9: {s = "\"END\" expected"; break;}
			case 10: {s = "\"PACKAGE\" expected"; break;}
			case 11: {s = "\"IMPORT\" expected"; break;}
			case 12: {s = "\",\" expected"; break;}
			case 13: {s = "\"CHARACTERS\" expected"; break;}
			case 14: {s = "\"TOKENS\" expected"; break;}
			case 15: {s = "\"NAMES\" expected"; break;}
			case 16: {s = "\"PRAGMAS\" expected"; break;}
			case 17: {s = "\"COMMENTS\" expected"; break;}
			case 18: {s = "\"FROM\" expected"; break;}
			case 19: {s = "\"TO\" expected"; break;}
			case 20: {s = "\"NESTED\" expected"; break;}
			case 21: {s = "\"IGNORE\" expected"; break;}
			case 22: {s = "\"CASE\" expected"; break;}
			case 23: {s = "\"+\" expected"; break;}
			case 24: {s = "\"-\" expected"; break;}
			case 25: {s = "\"..\" expected"; break;}
			case 26: {s = "\"ANY\" expected"; break;}
			case 27: {s = "\"CHR\" expected"; break;}
			case 28: {s = "\"(\" expected"; break;}
			case 29: {s = "\")\" expected"; break;}
			case 30: {s = "\"<\" expected"; break;}
			case 31: {s = "\"^\" expected"; break;}
			case 32: {s = "\"[\" expected"; break;}
			case 33: {s = "\"]\" expected"; break;}
			case 34: {s = "\">\" expected"; break;}
			case 35: {s = "\"<.\" expected"; break;}
			case 36: {s = "\".>\" expected"; break;}
			case 37: {s = "\"|\" expected"; break;}
			case 38: {s = "\"WEAK\" expected"; break;}
			case 39: {s = "\"{\" expected"; break;}
			case 40: {s = "\"}\" expected"; break;}
			case 41: {s = "\"SYNC\" expected"; break;}
			case 42: {s = "\"CONTEXT\" expected"; break;}
			case 43: {s = "\"(.\" expected"; break;}
			case 44: {s = "\".)\" expected"; break;}
			case 45: {s = "not expected"; break;}
			case 46: {s = "invalid TokenFactor"; break;}
			case 47: {s = "invalid Attribs1"; break;}
			case 48: {s = "invalid Attribs1"; break;}
			case 49: {s = "invalid Attribs"; break;}
			case 50: {s = "invalid Attribs"; break;}
			case 51: {s = "invalid Factor"; break;}
			case 52: {s = "invalid Term"; break;}
			case 53: {s = "invalid Sym"; break;}
			case 54: {s = "invalid SingleChar"; break;}
			case 55: {s = "invalid SimSet"; break;}
			case 56: {s = "invalid NameDecl"; break;}
			case 57: {s = "this symbol not expected in TokenDecl"; break;}
			case 58: {s = "invalid TokenDecl"; break;}
			case 59: {s = "invalid Declaration"; break;}
			case 60: {s = "invalid Declaration"; break;}
			case 61: {s = "this symbol not expected in Coco"; break;}

			default: s = "Syntax error " + n;
		}
		StoreError(n, line, col, s);
	}

	void SemErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			// for example: case 0: s = "invalid character"; break;
			// perhaps insert application specific error messages here
			default: s = "Semantic error " + n; break;
		}
		StoreError(n, line, col, s);
	}

	void Exception (String s) {
        throw new RuntimeException(s);
	}

	void Summarize () {
		switch (count) {
			case 0 : System.out.println("No errors detected"); break;
			case 1 : System.out.println("1 error detected"); break;
			default: System.out.println(count + " errors detected"); break;
		}
	}

}
