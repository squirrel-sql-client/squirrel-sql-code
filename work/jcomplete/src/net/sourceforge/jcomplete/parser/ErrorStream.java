package net.sourceforge.jcomplete.parser;

public class ErrorStream {

	int count;  // number of errors detected
	public String fileName;

	public ErrorStream() {
		count = 0;
	}

	protected void StoreError(int n, int line, int col, String s) {
		System.out.println(fileName + " (" + line + ", " + col + ") " + s);
	}

	protected void ParsErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			case 0: {s = "EOF expected"; break;}
			case 1: {s = "ident expected"; break;}
			case 2: {s = "intValue expected"; break;}
			case 3: {s = "float expected"; break;}
			case 4: {s = "SQLString expected"; break;}
			case 5: {s = "OpenParens expected"; break;}
			case 6: {s = "\";\" expected"; break;}
			case 7: {s = "\"UNION\" expected"; break;}
			case 8: {s = "\"ALL\" expected"; break;}
			case 9: {s = "\"UPDATE\" expected"; break;}
			case 10: {s = "\"SET\" expected"; break;}
			case 11: {s = "\"=\" expected"; break;}
			case 12: {s = "\"INSERT\" expected"; break;}
			case 13: {s = "\"INTO\" expected"; break;}
			case 14: {s = "\"VALUES\" expected"; break;}
			case 15: {s = "\"DELETE\" expected"; break;}
			case 16: {s = "\"FROM\" expected"; break;}
			case 17: {s = "\"SELECT\" expected"; break;}
			case 18: {s = "\"DISTINCT\" expected"; break;}
			case 19: {s = "\".\" expected"; break;}
			case 20: {s = "\"AS\" expected"; break;}
			case 21: {s = "\"JOIN\" expected"; break;}
			case 22: {s = "\"CROSS\" expected"; break;}
			case 23: {s = "\"NATURAL\" expected"; break;}
			case 24: {s = "\"INNER\" expected"; break;}
			case 25: {s = "\"FULL\" expected"; break;}
			case 26: {s = "\"LEFT\" expected"; break;}
			case 27: {s = "\"RIGHT\" expected"; break;}
			case 28: {s = "\"OUTER\" expected"; break;}
			case 29: {s = "\"ON\" expected"; break;}
			case 30: {s = "\"USING\" expected"; break;}
			case 31: {s = "\"WHERE\" expected"; break;}
			case 32: {s = "\"HAVING\" expected"; break;}
			case 33: {s = "\"ORDER\" expected"; break;}
			case 34: {s = "\"BY\" expected"; break;}
			case 35: {s = "\"GROUP\" expected"; break;}
			case 36: {s = "\"*\" expected"; break;}
			case 37: {s = "\"TIMESTAMP\" expected"; break;}
			case 38: {s = "\"UPPER\" expected"; break;}
			case 39: {s = "\"MONTH\" expected"; break;}
			case 40: {s = "\"YEAR\" expected"; break;}
			case 41: {s = "\"COUNT\" expected"; break;}
			case 42: {s = "\"SUM\" expected"; break;}
			case 43: {s = "\"MAX\" expected"; break;}
			case 44: {s = "\"MIN\" expected"; break;}
			case 45: {s = "\"AVG\" expected"; break;}
			case 46: {s = "\"NULL\" expected"; break;}
			case 47: {s = "\"DESC\" expected"; break;}
			case 48: {s = "\"ASC\" expected"; break;}
			case 49: {s = "\"-\" expected"; break;}
			case 50: {s = "\":\" expected"; break;}
			case 51: {s = "\"NOT\" expected"; break;}
			case 52: {s = "\"/\" expected"; break;}
			case 53: {s = "\"+\" expected"; break;}
			case 54: {s = "\"AND\" expected"; break;}
			case 55: {s = "\"OR\" expected"; break;}
			case 56: {s = "\"LIKE\" expected"; break;}
			case 57: {s = "\"ESCAPE\" expected"; break;}
			case 58: {s = "\"IS\" expected"; break;}
			case 59: {s = "\"<>\" expected"; break;}
			case 60: {s = "\"<\" expected"; break;}
			case 61: {s = "\"<=\" expected"; break;}
			case 62: {s = "\">\" expected"; break;}
			case 63: {s = "\">=\" expected"; break;}
			case 64: {s = "\"BETWEEN\" expected"; break;}
			case 65: {s = "\"IN\" expected"; break;}
			case 66: {s = "\"COMMIT\" expected"; break;}
			case 67: {s = "\"ROLLBACK\" expected"; break;}
			case 68: {s = "\"WORK\" expected"; break;}
			case 69: {s = "\"CHAR\" expected"; break;}
			case 70: {s = "\"CHARACTER\" expected"; break;}
			case 71: {s = "\"VARCHAR\" expected"; break;}
			case 72: {s = "\"INTEGER\" expected"; break;}
			case 73: {s = "\"INT\" expected"; break;}
			case 74: {s = "\"SMALLINT\" expected"; break;}
			case 75: {s = "\"NUMERIC\" expected"; break;}
			case 76: {s = "\"DATE\" expected"; break;}
			case 77: {s = "\"TIME\" expected"; break;}
			case 78: {s = "\"DEFAULT\" expected"; break;}
			case 79: {s = "\"PRIMARY\" expected"; break;}
			case 80: {s = "\"KEY\" expected"; break;}
			case 81: {s = "\"FOREIGN\" expected"; break;}
			case 82: {s = "\"REFERENCES\" expected"; break;}
			case 83: {s = "\"MATCH\" expected"; break;}
			case 84: {s = "\"PARTIAL\" expected"; break;}
			case 85: {s = "\"CASCADE\" expected"; break;}
			case 86: {s = "\"NO\" expected"; break;}
			case 87: {s = "\"ACTION\" expected"; break;}
			case 88: {s = "\"UNIQUE\" expected"; break;}
			case 89: {s = "\"CHECK\" expected"; break;}
			case 90: {s = "\"CREATE\" expected"; break;}
			case 91: {s = "\"TABLE\" expected"; break;}
			case 92: {s = "\"RESTRICT\" expected"; break;}
			case 93: {s = "\"DROP\" expected"; break;}
			case 94: {s = "\"ADD\" expected"; break;}
			case 95: {s = "\"ALTER\" expected"; break;}
			case 96: {s = "\"CONSTRAINT\" expected"; break;}
			case 97: {s = "\"INDEX\" expected"; break;}
			case 98: {s = "\",\" expected"; break;}
			case 99: {s = "\")\" expected"; break;}
			case 100: {s = "not expected"; break;}
			case 101: {s = "invalid DropPart"; break;}
			case 102: {s = "invalid Alter"; break;}
			case 103: {s = "invalid Add"; break;}
			case 104: {s = "invalid CascadeRestrict"; break;}
			case 105: {s = "invalid CreatePart"; break;}
			case 106: {s = "invalid ForeignKey"; break;}
			case 107: {s = "invalid ForeignKey"; break;}
			case 108: {s = "invalid ForeignKey"; break;}
			case 109: {s = "invalid ForeignKey"; break;}
			case 110: {s = "invalid ColumnDefault"; break;}
			case 111: {s = "invalid DataType"; break;}
			case 112: {s = "invalid InSetExpr"; break;}
			case 113: {s = "invalid LikeTest"; break;}
			case 114: {s = "invalid WordOperator"; break;}
			case 115: {s = "invalid MathOperator"; break;}
			case 116: {s = "invalid TestExpr"; break;}
			case 117: {s = "invalid TestExpr"; break;}
			case 118: {s = "invalid Operator"; break;}
			case 119: {s = "invalid Term"; break;}
			case 120: {s = "invalid Term"; break;}
			case 121: {s = "invalid Relation"; break;}
			case 122: {s = "invalid OrderByField"; break;}
			case 123: {s = "invalid Field"; break;}
			case 124: {s = "invalid ColumnFunction"; break;}
			case 125: {s = "invalid ColumnFunction"; break;}
			case 126: {s = "invalid FunctionExpr"; break;}
			case 127: {s = "invalid SelectField"; break;}
			case 128: {s = "invalid JoinExpr"; break;}
			case 129: {s = "invalid JoinType"; break;}
			case 130: {s = "invalid JoinStmt"; break;}
			case 131: {s = "this symbol not expected in OrderByClause"; break;}
			case 132: {s = "this symbol not expected in HavingClause"; break;}
			case 133: {s = "this symbol not expected in GroupByClause"; break;}
			case 134: {s = "this symbol not expected in FromClause"; break;}
			case 135: {s = "this symbol not expected in SelectClause"; break;}
			case 136: {s = "invalid ColumnName"; break;}
			case 137: {s = "this symbol not expected in WhereClause"; break;}
			case 138: {s = "invalid Transaction"; break;}
			case 139: {s = "invalid AlterTable"; break;}
			case 140: {s = "invalid Drop"; break;}
			case 141: {s = "invalid CreateStmt"; break;}
			case 142: {s = "invalid InsertStmt"; break;}
			case 143: {s = "invalid SQLStatement"; break;}

			default: s = "Syntax error " + n;
		}
		StoreError(n, line, col, s);
	}

	protected void SemErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			// for example: case 0: s = "invalid character"; break;
			// perhaps insert application specific error messages here
			default: s = "Semantic error " + n; break;
		}
		StoreError(n, line, col, s);
	}

	protected void Exception (String s) {
        throw new RuntimeException(s);
	}

	protected void Summarize () {
		switch (count) {
			case 0 : System.out.println("No errors detected"); break;
			case 1 : System.out.println("1 error detected"); break;
			default: System.out.println(count + " errors detected"); break;
		}
	}

}
