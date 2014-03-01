package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ErrorStream {

   int count;  // number of errors detected
	public String fileName;

    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ErrorStream.class);

    // i18n[ErrorStream.expected=expected]
    private static final String EXPECTED_STR = 
        s_stringMgr.getString("ErrorStream.expected");
    
    // i18n[ErrorStream.notexpected=not expected]
    private static final String NOT_EXPECTED_STR = 
        s_stringMgr.getString("ErrorStream.notexpected");
    
    // i18n[ErrorStream.invalid=invalid]
    private static final String INVALID_STR = 
        s_stringMgr.getString("ErrorStream.invalid");
    
    // i18n[ErrorStream.unexpectedsymbol=this symbol not expected in]
    private static final String UNEXPECTED_SYMBOL_STR = 
        s_stringMgr.getString("ErrorStream.unexpectedsymbol");
    
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
			case 0: {s = "EOF "+EXPECTED_STR; break;}
			case 1: {s = "ident "+EXPECTED_STR; break;}
			case 2: {s = "intValue "+EXPECTED_STR; break;}
			case 3: {s = "float "+EXPECTED_STR; break;}
			case 4: {s = "SQLString "+EXPECTED_STR; break;}
			case ParsingConstants.KIND_OPENING_BRAKET: {s = "OpenParens "+EXPECTED_STR; break;}
			case 6: {s = "\";\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_UNION: {s = "\"UNION\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_EXCEPT: {s = "\"EXCEPT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INTERSECT: {s = "\"INTERSECT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MINUS: {s = "\"MINUS\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ALL: {s = "\"ALL\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_UPDATE: {s = "\"UPDATE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_SET: {s = "\"SET\" "+EXPECTED_STR; break;}
			case ParsingConstants.KIND_EQUALS: {s = "\"=\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INSERT: {s = "\"INSERT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INTO: {s = "\"INTO\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_VALUES: {s = "\"VALUES\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DELETE: {s = "\"DELETE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_FROM: {s = "\"FROM\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_SELECT: {s = "\"SELECT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DISTINCT: {s = "\"DISTINCT\" "+EXPECTED_STR; break;}
			case 22: {s = "\".\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_AS: {s = "\"AS\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_JOIN: {s = "\"JOIN\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CROSS: {s = "\"CROSS\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_NATURAL: {s = "\"NATURAL\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INNER: {s = "\"INNER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_FULL: {s = "\"FULL\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_LEFT: {s = "\"LEFT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_RIGHT: {s = "\"RIGHT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_OUTER: {s = "\"OUTER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ON: {s = "\"ON\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_USING: {s = "\"USING\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_WHERE: {s = "\"WHERE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_GROUP: {s = "\"GROUP\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_BY: {s = "\"BY\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_HAVING: {s = "\"HAVING\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ORDER: {s = "\"ORDER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KIND_ASTERISK: {s = "\"*\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_TIMESTAMP: {s = "\"TIMESTAMP\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_UPPER: {s = "\"UPPER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MONTH: {s = "\"MONTH\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_YEAR: {s = "\"YEAR\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_COUNT: {s = "\"COUNT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_SUM: {s = "\"SUM\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MAX: {s = "\"MAX\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MIN: {s = "\"MIN\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_AVG: {s = "\"AVG\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_NULL: {s = "\"NULL\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DESC: {s = "\"DESC\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ASC: {s = "\"ASC\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MINUS_SIGN: {s = "\"-\" "+EXPECTED_STR; break;}
			case 53: {s = "\":\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_NOT: {s = "\"NOT\" "+EXPECTED_STR; break;}
			case 55: {s = "\"/\" "+EXPECTED_STR; break;}
			case 56: {s = "\"+\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_AND: {s = "\"AND\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_OR: {s = "\"OR\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_LIKE: {s = "\"LIKE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ESCAPE: {s = "\"ESCAPE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_IS: {s = "\"IS\" "+EXPECTED_STR; break;}
			case 62: {s = "\"<>\" "+EXPECTED_STR; break;}
			case 63: {s = "\"<\" "+EXPECTED_STR; break;}
			case 64: {s = "\"<=\" "+EXPECTED_STR; break;}
			case 65: {s = "\">\" "+EXPECTED_STR; break;}
			case 66: {s = "\">=\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_BETWEEN: {s = "\"BETWEEN\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_IN: {s = "\"IN\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_COMMIT: {s = "\"COMMIT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ROLLBACK: {s = "\"ROLLBACK\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_WORK: {s = "\"WORK\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CHAR: {s = "\"CHAR\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CHARACTER: {s = "\"CHARACTER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_VARCHAR: {s = "\"VARCHAR\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INTEGER: {s = "\"INTEGER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INT: {s = "\"INT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_SMALLINT: {s = "\"SMALLINT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_NUMERIC: {s = "\"NUMERIC\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DATE: {s = "\"DATE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_TIME: {s = "\"TIME\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DEFAULT : {s = "\"DEFAULT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_PRIMARY: {s = "\"PRIMARY\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_KEY: {s = "\"KEY\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_FOREIGN: {s = "\"FOREIGN\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_REFERENCES: {s = "\"REFERENCES\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_MATCH: {s = "\"MATCH\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_PARTIAL: {s = "\"PARTIAL\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CASCADE: {s = "\"CASCADE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_NO: {s = "\"NO\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ACTION: {s = "\"ACTION\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_UNIQUE: {s = "\"UNIQUE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CHECK: {s = "\"CHECK\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CREATE: {s = "\"CREATE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_TABLE: {s = "\"TABLE\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_RESTRICT: {s = "\"RESTRICT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_DROP: {s = "\"DROP\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ADD: {s = "\"ADD\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_ALTER: {s = "\"ALTER\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_CONSTRAINT: {s = "\"CONSTRAINT\" "+EXPECTED_STR; break;}
			case ParsingConstants.KW_INDEX: {s = "\"INDEX\" "+EXPECTED_STR; break;}
			case ParsingConstants.KIND_COMMA: {s = "\",\" "+EXPECTED_STR; break;}
			case ParsingConstants.KIND_CLOSING_BRAKET: {s = "\")\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_CASE: {s = "\"CASE\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_WHEN: {s = "\"WHEN\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_THEN: {s = "\"THEN\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_ELSE: {s = "\"ELSE\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_END: {s = "\"END\" "+EXPECTED_STR; break;}
         case ParsingConstants.KW_VIEW: {s = "\"VIEW\" "+EXPECTED_STR; break;}
         case 103: {s = NOT_EXPECTED_STR; break;}
			case 104: {s = INVALID_STR +" DropPart"; break;}
			case 105: {s = INVALID_STR +" Alter"; break;}
			case 106: {s = INVALID_STR +" Add"; break;}
			case 107: {s = INVALID_STR +" CascadeRestrict"; break;}
			case 108: {s = INVALID_STR +" CreatePart"; break;}
			case 109: {s = INVALID_STR +" ForeignKey"; break;}
			case 110: {s = INVALID_STR +" ForeignKey"; break;}
			case 111: {s = INVALID_STR +" ForeignKey"; break;}
			case 112: {s = INVALID_STR +" ForeignKey"; break;}
			case 113: {s = INVALID_STR +" ColumnDefault"; break;}
			case 114: {s = INVALID_STR +" DataType"; break;}
			case ParsingConstants.KW_INSET: {s = INVALID_STR +" InSetExpr"; break;}
			case 116: {s = INVALID_STR +" LikeTest"; break;}
			case 117: {s = INVALID_STR +" WordOperator"; break;}
			case 118: {s = INVALID_STR +" MathOperator"; break;}
			case 119: {s = INVALID_STR +" TestExpr"; break;}
			case 120: {s = INVALID_STR +" TestExpr"; break;}
			case 121: {s = INVALID_STR +" Operator"; break;}
			case 122: {s = INVALID_STR +" Term"; break;}
			case 123: {s = INVALID_STR +" Term"; break;}
			case 124: {s = INVALID_STR +" Relation"; break;}
			case 125: {s = INVALID_STR +" OrderByField"; break;}
			case 126: {s = INVALID_STR +" Field"; break;}
			case 127: {s = INVALID_STR +" ColumnFunction"; break;}
			case 128: {s = INVALID_STR +" ColumnFunction"; break;}
			case 129: {s = INVALID_STR +" FunctionExpr"; break;}
			case 130: {s = INVALID_STR +" SelectField"; break;}
			case 131: {s = INVALID_STR +" JoinExpr"; break;}
			case 132: {s = INVALID_STR +" JoinType"; break;}
			case 133: {s = INVALID_STR +" JoinStmt"; break;}
			case 134: {s = UNEXPECTED_SYMBOL_STR +" OrderByClause"; break;}
			case 135: {s = UNEXPECTED_SYMBOL_STR +" HavingClause"; break;}
			case 136: {s = UNEXPECTED_SYMBOL_STR +" GroupByClause"; break;}
			case 137: {s = UNEXPECTED_SYMBOL_STR +" FromClause"; break;}
			case 138: {s = UNEXPECTED_SYMBOL_STR +" SelectClause"; break;}
			case 139: {s = INVALID_STR +" ColumnName"; break;}
			case 140: {s = UNEXPECTED_SYMBOL_STR +" WhereClause"; break;}
			case 141: {s = INVALID_STR +" SetOperator"; break;}
			case 142: {s = INVALID_STR +" Transaction"; break;}
			case 143: {s = INVALID_STR +" AlterTable"; break;}
			case 144: {s = INVALID_STR +" Drop"; break;}
			case 145: {s = INVALID_STR +" CreateStmt"; break;}
			case 146: {s = INVALID_STR +" InsertStmt"; break;}
			case 147: {s = INVALID_STR +" SQLStatement"; break;}

            // i18n[ErrorStream.error.syntax=Syntax error {0}]
            default: s = s_stringMgr.getString("ErrorStream.error.syntax", 
                                               Integer.valueOf(n));
		}
		StoreError(n, line, col, s);
	}

	protected void SemErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			// for example: case 0: s = "invalid character"; break;
			// perhaps insert application specific error messages here
			default:
                // i18n[ErrorStream.error.semantic=Semantic error {0}]
                s = s_stringMgr.getString("ErrorStream.error.semantic", 
                                          Integer.valueOf(n)); 
                break;
		}
		StoreError(n, line, col, s);
	}

	protected void Exception (String s) {
        throw new RuntimeException(s);
	}

	protected void Summarize () {
		switch (count) {
            
			case 0 :
                // i18n[ErrorStream.noerrors=No errors detected]
                ParserLogger.log(s_stringMgr.getString("ErrorStream.noerrors")); 
                break;
			case 1 : 
                // i18n[ErrorStream.oneerror=1 error detected]
                ParserLogger.log(s_stringMgr.getString("ErrorStream.oneerror")); 
                break;
			default:
                // i18n[ErrorStream.multipleerrors={0} errors detected]
                String msg = s_stringMgr.getString("ErrorStream.multipleerrors",
                                                   Integer.valueOf(count));
                ParserLogger.log(msg); 
                break;
		}
	}

}
