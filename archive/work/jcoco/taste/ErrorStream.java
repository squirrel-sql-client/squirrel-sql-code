package Taste;

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
			case 2: {s = "number expected"; break;}
			case 3: {s = "\"PROGRAM\" expected"; break;}
			case 4: {s = "\";\" expected"; break;}
			case 5: {s = "\".\" expected"; break;}
			case 6: {s = "\"VAR\" expected"; break;}
			case 7: {s = "\":\" expected"; break;}
			case 8: {s = "\"PROCEDURE\" expected"; break;}
			case 9: {s = "\"BEGIN\" expected"; break;}
			case 10: {s = "\"END\" expected"; break;}
			case 11: {s = "\"INTEGER\" expected"; break;}
			case 12: {s = "\"BOOLEAN\" expected"; break;}
			case 13: {s = "\"=\" expected"; break;}
			case 14: {s = "\"IF\" expected"; break;}
			case 15: {s = "\"THEN\" expected"; break;}
			case 16: {s = "\"ELSE\" expected"; break;}
			case 17: {s = "\"WHILE\" expected"; break;}
			case 18: {s = "\"DO\" expected"; break;}
			case 19: {s = "\"READ\" expected"; break;}
			case 20: {s = "\"WRITE\" expected"; break;}
			case 21: {s = "\"TRUE\" expected"; break;}
			case 22: {s = "\"FALSE\" expected"; break;}
			case 23: {s = "\"-\" expected"; break;}
			case 24: {s = "\"*\" expected"; break;}
			case 25: {s = "\"/\" expected"; break;}
			case 26: {s = "\"+\" expected"; break;}
			case 27: {s = "\"<\" expected"; break;}
			case 28: {s = "\">\" expected"; break;}
			case 29: {s = "not expected"; break;}
			case 30: {s = "invalid MulOp"; break;}
			case 31: {s = "invalid Factor"; break;}
			case 32: {s = "invalid AddOp"; break;}
			case 33: {s = "invalid RelOp"; break;}
			case 34: {s = "invalid Stat"; break;}
			case 35: {s = "invalid TypeId"; break;}

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
		System.out.println(s); System.exit(0);
	}

	void Summarize (String s) {
		switch (count) {
			case 0 : System.out.println("No errors detected"); break;
			case 1 : System.out.println("1 error detected"); break;
			default: System.out.println(count + " errors detected"); break;
		}
	}

}
