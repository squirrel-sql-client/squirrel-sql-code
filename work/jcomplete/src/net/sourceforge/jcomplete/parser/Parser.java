package net.sourceforge.jcomplete.parser;
import net.sourceforge.jcomplete.completions.*;
import net.sourceforge.jcomplete.*;
import java.util.*;

public class Parser {
	private static final int maxT = 100;
	private static final int maxP = 100;

	private static final boolean T = true;
	private static final boolean x = false;
	private static final int minErrDist = 2;

	private int errDist = minErrDist;

    protected Scanner scanner;  // input scanner
	protected Token token;      // last recognized token
	protected Token t;          // lookahead token

public List statements = new ArrayList();
    public SQLSchema rootSchema;

    private Stack statementStack;

    protected void addRootStatement(SQLStatement statement)
    {
        statement.setSqlSchema(rootSchema);
        statements.add(statement);
        statementStack = new Stack();
        statementStack.push(statement);
    }

    private SQLStatement getParent()
    {
        return (SQLStatement)statementStack.peek();
    }

    private void pushStatement(SQLStatement statement)
    {
        SQLStatement parent = (SQLStatement)statementStack.peek();
        parent.addStatement(statement);
        statementStack.push(statement);
    }

    private SQLStatement popStatement()
    {
        return (SQLStatement)statementStack.pop();
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
		while (t.kind == 98) {
			ItemSeparator();
			IndexColumn();
		}
	}

	private final void IndexColumn() {
		SimpleColumnName();
		if (t.kind == 47 || t.kind == 48) {
			if (t.kind == 48) {
				Get();
			} else {
				Get();
			}
		}
	}

	private final void DropPart() {
		Expect(93);
		if (t.kind == 1) {
			SimpleColumnName();
			CascadeRestrict();
		} else if (t.kind == 79) {
			Get();
			Expect(80);
		} else if (t.kind == 81) {
			Get();
			Expect(80);
			RelationName();
		} else if (t.kind == 96) {
			Get();
			ConstraintName();
			CascadeRestrict();
		} else Error(101);
	}

	private final void Alter() {
		Expect(95);
		SimpleColumnName();
		if (t.kind == 93) {
			Get();
			Expect(78);
		} else if (t.kind == 10) {
			Get();
			ColumnDefault();
		} else Error(102);
	}

	private final void Add() {
		Expect(94);
		if (t.kind == 1) {
			ColumnDefList();
		} else if (t.kind == 79) {
			PrimaryKey();
		} else if (t.kind == 81) {
			ForeignKey();
		} else if (t.kind == 88) {
			Unique();
		} else if (t.kind == 89) {
			CheckConstraint();
		} else Error(103);
	}

	private final void IndexAndName() {
		Expect(97);
		IndexName();
	}

	private final void DropTable() {
		Expect(91);
		QualifiedTable();
		if (t.kind == 85 || t.kind == 92) {
			CascadeRestrict();
		}
	}

	private final void CascadeRestrict() {
		if (t.kind == 85) {
			Get();
		} else if (t.kind == 92) {
			Get();
		} else Error(104);
	}

	private final void CreateIndex() {
		if (t.kind == 88) {
			Get();
		}
		IndexAndName();
		Expect(29);
		Table();
		Expect(5);
		IndexColumnList();
		CloseParens();
	}

	private final void CreateTable() {
		Expect(91);
		Table();
		Expect(5);
		CreatePart();
		while (t.kind == 98) {
			ItemSeparator();
			CreatePart();
		}
		CloseParens();
	}

	private final void CreatePart() {
		if (t.kind == 1) {
			ColumnDef();
		} else if (t.kind == 79) {
			PrimaryKey();
		} else if (t.kind == 81) {
			ForeignKey();
		} else if (t.kind == 88) {
			Unique();
		} else if (t.kind == 89) {
			CheckConstraint();
		} else Error(105);
	}

	private final void CheckConstraint() {
		Expect(89);
		Expect(5);
		Expression();
		CloseParens();
	}

	private final void Unique() {
		Expect(88);
		SimpleColumnParam();
	}

	private final void ForeignKey() {
		Expect(81);
		Expect(80);
		RelationName();
		SimpleColumnParam();
		Expect(82);
		Table();
		if (t.kind == 83) {
			Get();
			if (t.kind == 25) {
				Get();
			} else if (t.kind == 84) {
				Get();
			} else Error(106);
		}
		while (t.kind == 29 || t.kind == 86) {
			if (t.kind == 29) {
				Get();
				if (t.kind == 15) {
					Get();
				} else if (t.kind == 9) {
					Get();
				} else Error(107);
				if (t.kind == 85) {
					Get();
				} else if (t.kind == 10) {
					Get();
					if (t.kind == 46) {
						Get();
					} else if (t.kind == 78) {
						Get();
					} else Error(108);
				} else Error(109);
			} else {
				Get();
				Expect(87);
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
		Expect(79);
		Expect(80);
		SimpleColumnParam();
	}

	private final void ColumnDef() {
		SimpleColumnName();
		DataType();
		while (t.kind == 51 || t.kind == 78) {
			if (t.kind == 78) {
				ColumnDefault();
			} else {
				NotOperator();
				Expect(46);
			}
		}
	}

	private final void ColumnDefList() {
		ColumnDef();
		while (t.kind == 98) {
			ItemSeparator();
			ColumnDef();
		}
	}

	private final void ColumnDefault() {
		Expect(78);
		if (t.kind == 4) {
			Get();
		} else if (t.kind == 2) {
			Get();
		} else if (t.kind == 3) {
			Get();
		} else Error(110);
	}

	private final void DataType() {
		switch (t.kind) {
		case 69: case 70: {
			if (t.kind == 69) {
				Get();
			} else {
				Get();
			}
			lenParam();
			break;
		}
		case 71: {
			Get();
			lenParam();
			break;
		}
		case 72: case 73: {
			if (t.kind == 72) {
				Get();
			} else {
				Get();
			}
			break;
		}
		case 74: {
			Get();
			break;
		}
		case 75: {
			Get();
			Expect(5);
			precision();
			CloseParens();
			break;
		}
		case 76: {
			Get();
			break;
		}
		case 77: {
			Get();
			lenParam();
			break;
		}
		case 37: {
			Get();
			lenParam();
			break;
		}
		default: Error(111);
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
		Expect(64);
		Field();
		Expect(54);
		Field();
	}

	private final void InSetExpr() {
		Expect(65);
		Expect(5);
		if (StartOf(1)) {
			FieldList();
		} else if (t.kind == 17) {
			SelectStmt();
		} else Error(112);
		CloseParens();
	}

	private final void NullTest() {
		Expect(58);
		if (t.kind == 51) {
			NotOperator();
		}
		Expect(46);
	}

	private final void LikeTest() {
		Expect(56);
		if (t.kind == 4) {
			Get();
		} else if (t.kind == 50) {
			Param();
		} else Error(113);
		if (t.kind == 57) {
			Get();
			Expect(4);
		}
	}

	private final void WordOperator() {
		if (t.kind == 54) {
			Get();
		} else if (t.kind == 55) {
			Get();
		} else Error(114);
	}

	private final void MathOperator() {
		if (t.kind == 36) {
			Get();
		} else if (t.kind == 52) {
			Get();
		} else if (t.kind == 53) {
			Get();
		} else if (t.kind == 49) {
			Get();
		} else Error(115);
	}

	private final void TestExpr() {
		if (t.kind == 58) {
			NullTest();
		} else if (StartOf(2)) {
			if (t.kind == 51) {
				NotOperator();
			}
			if (t.kind == 65) {
				InSetExpr();
			} else if (t.kind == 64) {
				BetweenExpr();
			} else if (t.kind == 56) {
				LikeTest();
			} else Error(116);
		} else Error(117);
	}

	private final void Operator() {
		if (StartOf(3)) {
			MathOperator();
		} else if (t.kind == 54 || t.kind == 55) {
			WordOperator();
		} else Error(118);
	}

	private final void Term() {
		if (t.kind == 49) {
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
			} else if (t.kind == 17) {
				SelectStmt();
			} else Error(119);
			CloseParens();
		} else Error(120);
	}

	private final void NotOperator() {
		Expect(51);
	}

	private final void Relation() {
		switch (t.kind) {
		case 11: {
			Get();
			break;
		}
		case 59: {
			Get();
			break;
		}
		case 60: {
			Get();
			break;
		}
		case 61: {
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
		default: Error(121);
		}
	}

	private final void SimpleExpression() {
		if (t.kind == 51) {
			NotOperator();
		}
		Term();
		while (StartOf(8)) {
			Operator();
			if (t.kind == 51) {
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
		} else Error(122);
		if (t.kind == 47 || t.kind == 48) {
			if (t.kind == 47) {
				Get();
			} else {
				Get();
			}
		}
	}

	private final void Param() {
		Expect(50);
		Expect(1);
	}

	private final void Field() {
		switch (t.kind) {
		case 1: {
			ColumnName();
			break;
		}
		case 46: {
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
		case 50: {
			Param();
			break;
		}
		default: Error(123);
		}
	}

	private final void SimpleColumnParam() {
		Expect(5);
		SimpleColumnList();
		CloseParens();
	}

	private final void SimpleColumnList() {
		SimpleColumnName();
		while (t.kind == 98) {
			ItemSeparator();
			SimpleColumnName();
		}
	}

	private final void SimpleColumnName() {
		Expect(1);
	}

	private final void ColumnFunction() {
		if (t.kind == 41) {
			Get();
		} else if (t.kind == 42) {
			Get();
		} else if (t.kind == 43) {
			Get();
		} else if (t.kind == 44) {
			Get();
		} else if (t.kind == 45) {
			Get();
		} else Error(124);
		Expect(5);
		if (t.kind == 36) {
			Get();
		} else if (StartOf(9)) {
			if (t.kind == 18) {
				Get();
			}
			Expression();
		} else Error(125);
		CloseParens();
	}

	private final void FunctionExpr() {
		if (t.kind == 37) {
			Get();
		} else if (t.kind == 38) {
			Get();
		} else if (t.kind == 39) {
			Get();
		} else if (t.kind == 40) {
			Get();
		} else Error(126);
		Expect(5);
		Expression();
		while (t.kind == 98) {
			ItemSeparator();
			Expression();
		}
		CloseParens();
	}

	private final void SelectField() {
		if (StartOf(7)) {
			Expression();
			if (t.kind == 20) {
				Get();
				Alias();
			}
		} else if (t.kind == 36) {
			Get();
		} else Error(127);
	}

	private final void OrderByFldList() {
		OrderByField();
		while (t.kind == 98) {
			ItemSeparator();
			OrderByField();
		}
	}

	private final void SearchCondition() {
		Expression();
	}

	private final void JoinExpr() {
		if (t.kind == 29) {
			Get();
			Expression();
		} else if (t.kind == 30) {
			Get();
			Expect(5);
			ColumnList();
			CloseParens();
		} else Error(128);
	}

	private final void JoinType() {
		if (t.kind == 23) {
			Get();
		}
		if (t.kind == 24) {
			Get();
		} else if (t.kind == 25 || t.kind == 26 || t.kind == 27) {
			if (t.kind == 25) {
				Get();
			} else if (t.kind == 26) {
				Get();
			} else {
				Get();
			}
			if (t.kind == 28) {
				Get();
			}
		} else Error(129);
	}

	private final void CrossJoin() {
		Expect(22);
		Expect(21);
		QualifiedTable();
	}

	private final void Alias() {
		Expect(1);
	}

	private final void JoinStmt() {
		if (t.kind == 22) {
			CrossJoin();
		} else if (StartOf(10)) {
			if (StartOf(11)) {
				JoinType();
			}
			Expect(21);
			QualifiedTable();
			if (t.kind == 29 || t.kind == 30) {
				JoinExpr();
			}
		} else Error(130);
	}

	private final void QualifiedTable() {
		SQLTable table = new SQLTable(getParent(), t.pos);
		getParent().addChild(table);
		
		Expect(1);
		if(t.val.equals("."))
		    table.schema = token.str;
		else
		    table.name = token.str;
		
		if (t.kind == 19) {
			Get();
			Expect(1);
			table.name = token.str;
		}
		if (t.kind == 1 || t.kind == 20) {
			if (t.kind == 20) {
				Get();
			}
			Alias();
			table.alias = token.str;
		}
		if(getParent().setTable(table) == false)
		    SemError(0);
		
	}

	private final void FromTableList() {
		QualifiedTable();
		while (StartOf(12)) {
			if (t.kind == 98) {
				ItemSeparator();
				QualifiedTable();
			} else {
				JoinStmt();
			}
		}
	}

	private final void SelectFieldList() {
		SelectField();
		while (t.kind == 98) {
			ItemSeparator();
			SelectField();
		}
	}

	private final void OrderByClause() {
		while (!(t.kind == 0 || t.kind == 33)) {Error(131); Get();}
		Expect(33);
		Expect(34);
		OrderByFldList();
	}

	private final void HavingClause() {
		while (!(t.kind == 0 || t.kind == 32)) {Error(132); Get();}
		Expect(32);
		SearchCondition();
	}

	private final void GroupByClause() {
		while (!(t.kind == 0 || t.kind == 35)) {Error(133); Get();}
		Expect(35);
		Expect(34);
		FieldList();
	}

	private final void FromClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getParent();
		statement.setFromStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 16)) {Error(134); Get();}
		Expect(16);
		FromTableList();
		statement.setFromEnd(t.pos);
	}

	private final void SelectClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getParent();
		statement.setSelectListStart(scanner.pos);
		
		while (!(t.kind == 0 || t.kind == 17)) {Error(135); Get();}
		Expect(17);
		if (t.kind == 8 || t.kind == 18) {
			if (t.kind == 18) {
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
		while (t.kind == 98) {
			ItemSeparator();
			Field();
		}
	}

	private final void CloseParens() {
		ExpectWeak(99, 13);
	}

	private final void ColumnList() {
		ColumnName();
		while (t.kind == 98) {
			ItemSeparator();
			ColumnName();
		}
	}

	private final void ItemSeparator() {
		ExpectWeak(98, 14);
	}

	private final void Expression() {
		SimpleExpression();
		while (StartOf(15)) {
			Relation();
			SimpleExpression();
		}
	}

	private final void ColumnName() {
		SQLColumn column = new SQLColumn(getParent(), t.pos);
		getParent().addChild(column);
		if(scanner.ch == '.')
		    column.setAlias(t.str, scanner.pos);
		else
		    column.setColumn(t.str, scanner.pos);
		
		Expect(1);
		if (t.kind == 19) {
			Get();
			if (t.kind == 1) {
				Get();
				column.setColumn(token.str, t.pos);
			} else if (t.kind == 36) {
				Get();
			} else Error(136);
		}
	}

	private final void UpdateField() {
		ColumnName();
		Expect(11);
		Expression();
	}

	private final void WhereClause() {
		SQLSelectStatement statement = (SQLSelectStatement)getParent();
		statement.setWhereStart(scanner.pos);
		while (!(t.kind == 0 || t.kind == 31)) {Error(137); Get();}
		Expect(31);
		SearchCondition();
		statement.setWhereEnd(t.pos);
	}

	private final void UpdateFieldList() {
		UpdateField();
		while (t.kind == 98) {
			ItemSeparator();
			UpdateField();
		}
	}

	private final void Table() {
		Expect(1);
	}

	private final void SimpleSelect() {
		SQLSelectStatement stmt = new SQLSelectStatement(t.pos);
		pushStatement(stmt);
		
		SelectClause();
		FromClause();
		if (t.kind == 31) {
			WhereClause();
		}
		if (t.kind == 35) {
			GroupByClause();
		}
		if (t.kind == 32) {
			HavingClause();
		}
		if (t.kind == 33) {
			OrderByClause();
		}
		popStatement();
	}

	private final void Transaction() {
		if (t.kind == 66) {
			Get();
		} else if (t.kind == 67) {
			Get();
		} else Error(138);
		if (t.kind == 68) {
			Get();
		}
	}

	private final void AlterTable() {
		Expect(95);
		Expect(91);
		QualifiedTable();
		if (t.kind == 94) {
			Add();
		} else if (t.kind == 95) {
			Alter();
		} else if (t.kind == 93) {
			DropPart();
		} else Error(139);
	}

	private final void Drop() {
		Expect(93);
		if (t.kind == 91) {
			DropTable();
		} else if (t.kind == 97) {
			IndexAndName();
		} else Error(140);
	}

	private final void CreateStmt() {
		Expect(90);
		if (t.kind == 91) {
			CreateTable();
		} else if (t.kind == 88 || t.kind == 97) {
			CreateIndex();
		} else Error(141);
	}

	private final void DeleteStmt() {
		Expect(15);
		Expect(16);
		Table();
		if (t.kind == 31) {
			WhereClause();
		}
	}

	private final void UpdateStmt() {
		Expect(9);
		Table();
		Expect(10);
		UpdateFieldList();
		if (t.kind == 31) {
			WhereClause();
		}
	}

	private final void InsertStmt() {
		Expect(12);
		Expect(13);
		Table();
		if (t.kind == 5) {
			Get();
			ColumnList();
			CloseParens();
		}
		if (t.kind == 14) {
			Get();
			Expect(5);
			FieldList();
			CloseParens();
		} else if (t.kind == 17) {
			SelectStmt();
		} else Error(142);
	}

	private final void SelectStmt() {
		pushStatement(new SQLStatement(token.pos));
		SimpleSelect();
		while (t.kind == 7) {
			Get();
			if (t.kind == 8) {
				Get();
			}
			SimpleSelect();
		}
		popStatement();
	}

	private final void SQLStatement() {
		addRootStatement(new SQLStatement(token.pos));
		switch (t.kind) {
		case 17: {
			SelectStmt();
			break;
		}
		case 12: {
			InsertStmt();
			break;
		}
		case 9: {
			UpdateStmt();
			break;
		}
		case 15: {
			DeleteStmt();
			break;
		}
		case 90: {
			CreateStmt();
			break;
		}
		case 93: {
			Drop();
			break;
		}
		case 95: {
			AlterTable();
			break;
		}
		case 66: case 67: {
			Transaction();
			break;
		}
		default: Error(143);
		}
		if (t.kind == 6) {
			Get();
		}
	}

	private final void squirrelSQL() {
		SQLStatement();
		while (StartOf(16)) {
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
	{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,T,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, T,x,x,x,
	 x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, T,T,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, T,x,T,x,
	 x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,x, x,T,T,T, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, T,T,T,T, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,x, x,T,T,T, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x},
	{T,x,x,x, x,x,T,T, x,T,x,T, T,x,T,T, T,T,x,x, T,T,T,T, T,T,T,T, x,x,x,T, T,T,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, T,T,T,T, x,x,x,T,
	 T,T,T,T, x,x,T,T, x,x,x,x, x,x,x,x, x,x,T,x, x,x,T,x, x,x,x,x, x,x,T,x, x,T,x,T, x,x,T,T, x,x},
	{T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,T,x, x,T,T,T, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T,
	 T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
	{x,x,x,x, x,x,x,x, x,T,x,x, T,x,x,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x,
	 x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,x,T, x,x,x,x, x,x}

	};
}
