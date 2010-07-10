package net.sourceforge.jcomplete.parser;
import net.sourceforge.jcomplete.completions.*;
import net.sourceforge.jcomplete.*;
import java.util.*;

public class Parser {
	private static final int maxT = 103;
	private static final int maxP = 103;

	private static final boolean T = true;
	private static final boolean x = false;
	private static final int minErrDist = 2;

	private int errDist = minErrDist;

    protected Scanner scanner;  // input scanner
	protected Token token;      // last recognized token
	protected Token t;          // lookahead token

public List statements = new ArrayList();
    public SQLSchema rootSchema;

    private Stack stack;

    protected void addRootStatement(SQLStatement statement)
    {
        statement.setSqlSchema(rootSchema);
        statements.add(statement);
        stack = new Stack();
        stack.push(statement);
    }

    private SQLStatementContext getContext()
    {
        return (SQLStatementContext)stack.peek();
    }

    private void pushContext(SQLStatementContext context)
    {
        SQLStatementContext parent = (SQLStatementContext)stack.peek();
        parent.addContext(context);
        stack.push(context);
    }

    private SQLStatementContext popContext()
    {
        return (SQLStatementContext)stack.pop();
    }



	void Error(int n) {
		if (errDist >= minErrDist) scanner.err.ParsErr(n, t.line, t.col);
		errDist = 0;
	}

	void SemError(int n) {
		if (errDist >= minErrDist) scanner.err.SemErr(n, token.line, token.col);
		errDist = 0;
	}

	boolean Successful() {
		return scanner.err.count == 0;
	}

	String LexString() {
		return token.str;
	}

	String LexName() {
		return token.val;
	}

	String LookAheadString() {
		return t.str;
	}

	String LookAheadName() {
		return t.val;
	}

	private void Get() {
		for (;;) {
			token = t;
			t = scanner.Scan();
			if (t.kind <= maxT) {errDist++; return;}

			t = token;
		}
	}

	private void Expect(int n) {
		if (t.kind == n) Get(); else Error(n);
	}

	private boolean StartOf(int s) {
		return set[s][t.kind];
	}

	private void ExpectWeak(int n, int follow) {
		if (t.kind == n) Get();
		else {
			Error(n);
			while (!StartOf(follow)) Get();
		}
	}

	private boolean WeakSeparator(int n, int syFol, int repFol) {
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

	private final void IndexName() {
		Expect(1);
	}

	private final void IndexColumnList() {
		IndexColumn();
		while (t.kind == 101) {
			ItemSeparator();
			IndexColumn();
		}
	}

	private final void IndexColumn() {
		SimpleColumnName();
		if (t.kind == 50 || t.kind == 51) {
			if (t.kind == 51) {
				Get();
			} else {
				Get();
			}
		}
	}

	private final void DropPart() {
		Expect(96);
		if (t.kind == 1) {
			SimpleColumnName();
			CascadeRestrict();
		} else if (t.kind == 82) {
			Get();
			Expect(83);
		} else if (t.kind == 84) {
			Get();
			Expect(83);
			RelationName();
		} else if (t.kind == 99) {
			Get();
			ConstraintName();
			CascadeRestrict();
		} else Error(104);
	}

	private final void Alter() {
		Expect(98);
		SimpleColumnName();
		if (t.kind == 96) {
			Get();
			Expect(81);
		} else if (t.kind == 13) {
			Get();
			ColumnDefault();
		} else Error(105);
	}

	private final void Add() {
		Expect(97);
		if (t.kind == 1) {
			ColumnDefList();
		} else if (t.kind == 82) {
			PrimaryKey();
		} else if (t.kind == 84) {
			ForeignKey();
		} else if (t.kind == 91) {
			Unique();
		} else if (t.kind == 92) {
			CheckConstraint();
		} else Error(106);
	}

	private final void IndexAndName() {
		Expect(100);
		IndexName();
	}

	private final void DropTable() {
		Expect(94);
		QualifiedTable();
		if (t.kind == 88 || t.kind == 95) {
			CascadeRestrict();
		}
	}

	private final void CascadeRestrict() {
		if (t.kind == 88) {
			Get();
		} else if (t.kind == 95) {
			Get();
		} else Error(107);
	}

	private final void CreateIndex() {
		if (t.kind == 91) {
			Get();
		}
		IndexAndName();
		Expect(32);
		Table(null);
		Expect(5);
		IndexColumnList();
		CloseParens();
	}

	private final void CreateTable() {
		Expect(94);
		Table(null);
		Expect(5);
		CreatePart();
		while (t.kind == 101) {
			ItemSeparator();
			CreatePart();
		}
		CloseParens();
	}

	private final void CreatePart() {
		if (t.kind == 1) {
			ColumnDef();
		} else if (t.kind == 82) {
			PrimaryKey();
		} else if (t.kind == 84) {
			ForeignKey();
		} else if (t.kind == 91) {
			Unique();
		} else if (t.kind == 92) {
			CheckConstraint();
		} else Error(108);
	}

	private final void CheckConstraint() {
		Expect(92);
		Expect(5);
		Expression();
		CloseParens();
	}

	private final void Unique() {
		Expect(91);
		SimpleColumnParam();
	}

	private final void ForeignKey() {
		Expect(84);
		Expect(83);
		RelationName();
		SimpleColumnParam();
		Expect(85);
		Table(null);
		if (t.kind == 86) {
			Get();
			if (t.kind == 28) {
				Get();
			} else if (t.kind == 87) {
				Get();
			} else Error(109);
		}
		while (t.kind == 32 || t.kind == 89) {
			if (t.kind == 32) {
				Get();
				if (t.kind == 18) {
					Get();
				} else if (t.kind == 12) {
					Get();
				} else Error(110);
				if (t.kind == 88) {
					Get();
				} else if (t.kind == 13) {
					Get();
					if (t.kind == 49) {
						Get();
					} else if (t.kind == 81) {
						Get();
					} else Error(111);
				} else Error(112);
			} else {
				Get();
				Expect(90);
			}
		}
	}

	private final void ConstraintName() {
		Expect(1);
	}

	private final void RelationName() {
		Expect(1);
	}

	private final void PrimaryKey() {
		Expect(82);
		Expect(83);
		SimpleColumnParam();
	}

	private final void ColumnDef() {
		SimpleColumnName();
		DataType();
		while (t.kind == 54 || t.kind == 81) {
			if (t.kind == 81) {
				ColumnDefault();
			} else {
				NotOperator();
				Expect(49);
			}
		}
	}

	private final void ColumnDefList() {
		ColumnDef();
		while (t.kind == 101) {
			ItemSeparator();
			ColumnDef();
		}
	}

	private final void ColumnDefault() {
		Expect(81);
		if (t.kind == 4) {
			Get();
		} else if (t.kind == 2) {
			Get();
		} else if (t.kind == 3) {
			Get();
		} else Error(113);
	}

	private final void DataType() {
		switch (t.kind) {
		case 72: case 73: {
			if (t.kind == 72) {
				Get();
			} else {
				Get();
			}
			lenParam();
			break;
		}
		case 74: {
			Get();
			lenParam();
			break;
		}
		case 75: case 76: {
			if (t.kind == 75) {
				Get();
			} else {
				Get();
			}
			break;
		}
		case 77: {
			Get();
			break;
		}
		case 78: {
			Get();
			Expect(5);
			precision();
			CloseParens();
			break;
		}
		case 79: {
			Get();
			break;
		}
		case 80: {
			Get();
			lenParam();
			break;
		}
		case 40: {
			Get();
			lenParam();
			break;
		}
		default: Error(114);
		}
	}

	private final void precision() {
		Expect(2);
		ItemSeparator();
		Expect(2);
	}

	private final void lenParam() {
		Expect(5);
		len();
		CloseParens();
	}

	private final void len() {
		Expect(2);
	}

	private final void BetweenExpr() {
		Expect(67);
		Field();
		Expect(57);
		Field();
	}

	private final void InSetExpr() {
		Expect(68);
		Expect(5);
		if (StartOf(1)) {
			FieldList();
		} else if (t.kind == 20) {
			SelectStmt();
		} else Error(115);
		CloseParens();
	}

	private final void NullTest() {
		Expect(61);
		if (t.kind == 54) {
			NotOperator();
		}
		Expect(49);
	}

	private final void LikeTest() {
		Expect(59);
		if (t.kind == 4) {
			Get();
		} else if (t.kind == 53) {
			Param();
		} else Error(116);
		if (t.kind == 60) {
			Get();
			Expect(4);
		}
	}

	private final void WordOperator() {
		if (t.kind == 57) {
			Get();
		} else if (t.kind == 58) {
			Get();
		} else Error(117);
	}

	private final void MathOperator() {
		if (t.kind == 39) {
			Get();
		} else if (t.kind == 55) {
			Get();
		} else if (t.kind == 56) {
			Get();
		} else if (t.kind == 52) {
			Get();
		} else Error(118);
	}

	private final void TestExpr() {
		if (t.kind == 61) {
			NullTest();
		} else if (StartOf(2)) {
			if (t.kind == 54) {
				NotOperator();
			}
			if (t.kind == 68) {
				InSetExpr();
			} else if (t.kind == 67) {
				BetweenExpr();
			} else if (t.kind == 59) {
				LikeTest();
			} else Error(119);
		} else Error(120);
	}

	private final void Operator() {
		if (StartOf(3)) {
			MathOperator();
		} else if (t.kind == 57 || t.kind == 58) {
			WordOperator();
		} else Error(121);
	}

	private final void Term() {
		if (t.kind == 52) {
			Get();
		}
		if (StartOf(1)) {
			Field();
			if (StartOf(4)) {
				TestExpr();
			}
		} else if (StartOf(5)) {
			ColumnFunction();
		} else if (StartOf(6)) {
			FunctionExpr();
		} else if (t.kind == 5) {
			Get();
			if (StartOf(7)) {
				Expression();
			} else if (t.kind == 20) {
				SelectStmt();
			} else Error(122);
			CloseParens();
		} else Error(123);
	}

	private final void NotOperator() {
		Expect(54);
	}

	private final void Relation() {
		switch (t.kind) {
		case 14: {
			Get();
			break;
		}
		case 62: {
			Get();
			break;
		}
		case 63: {
			Get();
			break;
		}
		case 64: {
			Get();
			break;
		}
		case 65: {
			Get();
			break;
		}
		case 66: {
			Get();
			break;
		}
		default: Error(124);
		}
	}

	private final void SimpleExpression() {
		if (t.kind == 54) {
			NotOperator();
		}
		Term();
		while (StartOf(8)) {
			Operator();
			if (t.kind == 54) {
				NotOperator();
			}
			Term();
		}
	}

	private final void OrderByField() {
		if (t.kind == 1) {
			ColumnName();
		} else if (t.kind == 2) {
			Get();
		} else Error(125);
		if (t.kind == 50 || t.kind == 51) {
			if (t.kind == 50) {
				Get();
			} else {
				Get();
			}
		}
	}

	private final void Param() {
		Expect(53);
		Expect(1);
	}

	private final void Field() {
		switch (t.kind) {
		case 1: {
			ColumnName();
			break;
		}
		case 49: {
			Get();
			break;
		}
		case 3: {
			Get();
			break;
		}
		case 2: {
			Get();
			break;
		}
		case 4: {
			Get();
			break;
		}
		case 53: {
			Param();
			break;
		}
		default: Error(126);
		}
	}

	private final void SimpleColumnParam() {
		Expect(5);
		SimpleColumnList();
		CloseParens();
	}

	private final void SimpleColumnList() {
		SimpleColumnName();
		while (t.kind == 101) {
			ItemSeparator();
			SimpleColumnName();
		}
	}

	private final void SimpleColumnName() {
		Expect(1);
	}

	private final void ColumnFunction() {
		if (t.kind == 44) {
			Get();
		} else if (t.kind == 45) {
			Get();
		} else if (t.kind == 46) {
			Get();
		} else if (t.kind == 47) {
			Get();
		} else if (t.kind == 48) {
			Get();
		} else Error(127);
		Expect(5);
		if (t.kind == 39) {
			Get();
		} else if (StartOf(9)) {
			if (t.kind == 21) {
				Get();
			}
			Expression();
		} else Error(128);
		CloseParens();
	}

	private final void FunctionExpr() {
		if (t.kind == 40) {
			Get();
		} else if (t.kind == 41) {
			Get();
		} else if (t.kind == 42) {
			Get();
		} else if (t.kind == 43) {
			Get();
		} else Error(129);
		Expect(5);
		Expression();
		while (t.kind == 101) {
			ItemSeparator();
			Expression();
		}
		CloseParens();
	}

	private final void SelectField() {
		if (StartOf(7)) {
			Expression();
			if (t.kind == 23) {
				Get();
				Alias();
			}
		} else if (t.kind == 39) {
			Get();
		} else Error(130);
	}

	private final void OrderByFldList() {
		OrderByField();
		while (t.kind == 101) {
			ItemSeparator();
			OrderByField();
		}
	}

	private final void SearchCondition() {
		Expression();
	}

	private final void JoinExpr() {
		if (t.kind == 32) {
			Get();
			Expression();
		} else if (t.kind == 33) {
			Get();
			Expect(5);
			ColumnList();
			CloseParens();
		} else Error(131);
	}

	private final void JoinType() {
		if (t.kind == 26) {
			Get();
		}
		if (t.kind == 27) {
			Get();
		} else if (t.kind == 28 || t.kind == 29 || t.kind == 30) {
			if (t.kind == 28) {
				Get();
			} else if (t.kind == 29) {
				Get();
			} else {
				Get();
			}
			if (t.kind == 31) {
				Get();
			}
		} else Error(132);
	}

	private final void CrossJoin() {
		Expect(25);
		Expect(24);
		QualifiedTable();
	}

	private final void Alias() {
		Expect(1);
	}

	private final void JoinStmt() {
		if (t.kind == 25) {
			CrossJoin();
		} else if (StartOf(10)) {
			if (StartOf(11)) {
				JoinType();
			}
			Expect(24);
			QualifiedTable();
			if (t.kind == 32 || t.kind == 33) {
				JoinExpr();
			}
		} else Error(133);
	}

	private final void QualifiedTable() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		SQLTable table = new SQLTable(statement, t.pos);
		statement.addTable(table);
		boolean wasSet = false;
		
		Expect(1);
		if(t.val.equals("."))
		    table.setSchema(token.str, token.pos);
		else
		    table.setName(token.str, token.pos);
		
		if (t.kind == 22) {
			Get();
			Expect(1);
			table.setName(token.str, token.pos);
		}
		if (t.kind == 1 || t.kind == 23) {
			if (t.kind == 23) {
				Get();
			}
			table.setAlias(t.str, t.pos);
			wasSet = true;
			if(statement.setTable(table) == false)
			    SemError(10);
			
			Alias();
		}
		if(!wasSet && statement.setTable(table) == false)
		    SemError(10);
		
	}

	private final void FromTableList() {
		QualifiedTable();
		while (StartOf(12)) {
			if (t.kind == 101) {
				ItemSeparator();
				QualifiedTable();
			} else {
				JoinStmt();
			}
		}
	}

	private final void SelectFieldList() {
		SelectField();
		while (t.kind == 101) {
			ItemSeparator();
			SelectField();
		}
	}

	private final void OrderByClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		statement.setOrderByStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 38)) {Error(134); Get();}
		Expect(38);
		Expect(36);
		OrderByFldList();
		statement.setOrderByEnd(t.pos);
	}

	private final void HavingClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		statement.setHavingStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 37)) {Error(135); Get();}
		Expect(37);
		SearchCondition();
		statement.setHavingEnd(t.pos);
	}

	private final void GroupByClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		statement.setGroupByStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 35)) {Error(136); Get();}
		Expect(35);
		Expect(36);
		FieldList();
		statement.setGroupByEnd(t.pos);
	}

	private final void FromClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		statement.setFromStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 19)) {Error(137); Get();}
		Expect(19);
		FromTableList();
		statement.setFromEnd(t.pos);
	}

	private final void SelectClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getContext();
		statement.setSelectListStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 20)) {Error(138); Get();}
		Expect(20);
		if (t.kind == 11 || t.kind == 21) {
			if (t.kind == 21) {
				Get();
			} else {
				Get();
			}
		}
		SelectFieldList();
		statement.setSelectListEnd(t.pos);
	}

	private final void FieldList() {
		Field();
		while (t.kind == 101) {
			ItemSeparator();
			Field();
		}
	}

	private final void CloseParens() {
		ExpectWeak(102, 13);
	}

	private final void ColumnList() {
		ColumnName();
		while (t.kind == 101) {
			ItemSeparator();
			ColumnName();
		}
	}

	private final void Expression() {
		SimpleExpression();
		while (StartOf(14)) {
			Relation();
			SimpleExpression();
		}
	}

	private final void ColumnName() {
		SQLStatementContext context = getContext();
		SQLColumn column = new SQLColumn(context, t.pos);
		context.addColumn(column);
		if(scanner.ch == '.')
		    column.setQualifier(t.str, t.pos);
		else
		    column.setColumn(t.str, t.pos);
		
		Expect(1);
		if (t.kind == 22) {
			Get();
			if (t.kind == 1) {
				Get();
				column.setColumn(token.str, token.pos);
			} else if (t.kind == 39) {
				Get();
			} else Error(139);
		}
	}

	private final void ItemSeparator() {
		ExpectWeak(101, 15);
	}

	private final void UpdateField() {
		ColumnName();
		Expect(14);
		Expression();
	}

	private final void WhereClause() {
		SQLStatement statement = (SQLStatement)getContext();
		SQLWhere where = new SQLWhere(statement, t.pos);
		pushContext(where);
		
		while (!(t.kind == 0 || t.kind == 34)) {Error(140); Get();}
		Expect(34);
		SearchCondition();
		where.setEndPosition(t.pos);
		popContext();
		
	}

	private final void UpdateFieldList() {
		UpdateField();
		while (t.kind == 101) {
			ItemSeparator();
			UpdateField();
		}
	}

	private final void Table(SQLTable table) {
		if(table != null)
		    table.setName(t.str, t.pos);
		
		Expect(1);
	}

	private final void SetOperator() {
		if (t.kind == 7) {
			Get();
		} else if (t.kind == 8) {
			Get();
		} else if (t.kind == 9) {
			Get();
		} else if (t.kind == 10) {
			Get();
		} else Error(141);
		if (t.kind == 11) {
			Get();
		}
	}

	private final void SimpleSelect() {
		SQLSelectStatement statement = new SQLSelectStatement(t.pos);
		pushContext(statement);
		
		SelectClause();
		FromClause();
		if (t.kind == 34) {
			WhereClause();
		}
		if (t.kind == 35) {
			GroupByClause();
		}
		if (t.kind == 37) {
			HavingClause();
		}
		if (t.kind == 38) {
			OrderByClause();
		}
		popContext();
	}

	private final void Transaction() {
		if (t.kind == 69) {
			Get();
		} else if (t.kind == 70) {
			Get();
		} else Error(142);
		if (t.kind == 71) {
			Get();
		}
	}

	private final void AlterTable() {
		Expect(98);
		Expect(94);
		QualifiedTable();
		if (t.kind == 97) {
			Add();
		} else if (t.kind == 98) {
			Alter();
		} else if (t.kind == 96) {
			DropPart();
		} else Error(143);
	}

	private final void Drop() {
		Expect(96);
		if (t.kind == 94) {
			DropTable();
		} else if (t.kind == 100) {
			IndexAndName();
		} else Error(144);
	}

	private final void CreateStmt() {
		Expect(93);
		if (t.kind == 94) {
			CreateTable();
		} else if (t.kind == 91 || t.kind == 100) {
			CreateIndex();
		} else Error(145);
	}

	private final void DeleteStmt() {
		SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
		pushContext(statement);
		
		Expect(18);
		SQLTable table = new SQLTable(statement, scanner.pos+1);
		statement.addTable(table);
		
		Expect(19);
		table.setName(t.str, t.pos);
		Table(table);
		if (t.kind == 34) {
			WhereClause();
		}
		statement.setEndPosition(token.pos);
		popContext();
		
	}

	private final void UpdateStmt() {
		SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
		SQLTable table = new SQLTable(statement, scanner.pos+1);
		statement.addTable(table);
		pushContext(statement);
		
		Expect(12);
		table.setName(t.str, t.pos);
		Table(null);
		statement.setUpdateListStart(t.pos+4);
		Expect(13);
		UpdateFieldList();
		statement.setUpdateListEnd(token.pos);
		if (t.kind == 34) {
			WhereClause();
		}
		statement.setEndPosition(token.pos);
		popContext();
		
	}

	private final void InsertStmt() {
		SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
		pushContext(statement);
		
		Expect(15);
		SQLTable table = new SQLTable(statement, scanner.pos+1);
		statement.addTable(table);
		
		Expect(16);
		SQLColumn column = new SQLColumn(statement, scanner.pos+2);
		table.setName(t.str, t.pos);
		column.setRepeatable(true);
		statement.addColumn(column);
		
		Table(table);
		if (t.kind == 5) {
			Get();
			ColumnList();
			CloseParens();
		}
		column.setEndPosition(token.pos);
		if (t.kind == 17) {
			Get();
			Expect(5);
			FieldList();
			CloseParens();
		} else if (t.kind == 20) {
			SelectStmt();
		} else Error(146);
		statement.setEndPosition(token.pos);
		popContext();
		
	}

	private final void SelectStmt() {
		pushContext(new SQLStatement(token.pos));
		SimpleSelect();
		while (StartOf(16)) {
			SetOperator();
			SimpleSelect();
		}
		popContext();
	}

	private final void SQLStatement() {
		addRootStatement(new SQLStatement(token.pos));
		switch (t.kind) {
		case 20: {
			SelectStmt();
			break;
		}
		case 15: {
			InsertStmt();
			break;
		}
		case 12: {
			UpdateStmt();
			break;
		}
		case 18: {
			DeleteStmt();
			break;
		}
		case 93: {
			CreateStmt();
			break;
		}
		case 96: {
			Drop();
			break;
		}
		case 98: {
			AlterTable();
			break;
		}
		case 69: case 70: {
			Transaction();
			break;
		}
		default: Error(147);
		}
		if (t.kind == 6) {
			Get();
		}
	}

	private final void squirrelSQL() {
		SQLStatement();
		while (StartOf(17)) {
			SQLStatement();
		}
		Expect(0);
	}



	public Parser(Scanner _scanner)
	{
	    scanner = _scanner;
		t = new Token();
    }

    public void parse()
    {
		Get();
		squirrelSQL();

	}

	private static boolean[][] set = {
	{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,T,
	 x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,T, T,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,T,
	 x,T,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,x,x, T,T,T,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,T, T,T,T,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,x,x, T,T,T,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x},
	{T,x,x,x, x,x,T,T, T,T,T,x, T,x,T,T, x,T,T,T, T,x,x,T, T,T,T,T, T,T,T,x, x,x,T,T, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, T,T,T,x,
	 x,x,T,T, T,T,T,x, x,T,T,x, x,x,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,T,x,x, T,x,T,x, x,T,T,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,T,T,T, T,T,T,T, T,T,T,T, T,T,x,x, T,T,T,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
	{x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,T, x,x,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, T,x,T,x, x,x,x,x, x}

	};
}
