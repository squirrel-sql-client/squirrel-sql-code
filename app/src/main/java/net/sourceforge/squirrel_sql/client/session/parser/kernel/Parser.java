package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.*;

import java.util.*;

public class Parser
{
	private static final int maxT = 103;

	private static final boolean T = true;
	private static final boolean G = true; // to see Gerds changes
	private static final boolean x = false;
	private static final int minErrDist = 2;

	private int errDist = minErrDist;

	private Vector<ParserListener> parserListeners = new Vector<ParserListener>();
	private Vector<SQLSelectStatementListener> sqlSelectStatlisteners = new Vector<SQLSelectStatementListener>();

	private Scanner scanner;  // input scanner
	private Token token;      // last recognized token
	private Token t;          // lookahead token

	private List<SQLStatement> statements = new ArrayList<SQLStatement>();
	private SQLSchema rootSchema;

	private Stack<SQLStatementContext> stack;

	protected void addRootStatement(SQLStatement statement)
	{
		statement.setSqlSchema(rootSchema);
		fireStatementAdded(statement);
		statements.add(statement);
		stack = new Stack<SQLStatementContext>();
		stack.push(statement);
	}

	private void fireStatementAdded(SQLStatement statement)
	{
		ParserListener[] clone = parserListeners.toArray(new ParserListener[parserListeners.size()]);

		for (int i = 0; i < clone.length; i++)
		{
			clone[i].statementAdded(statement);
		}

	}

	public void addParserListener(ParserListener l)
	{
		parserListeners.add(l);
	}

	public void removeaddParserListener(ParserListener l)
	{
		parserListeners.remove(l);
	}


	public void addSQLSelectStatementListener(SQLSelectStatementListener l)
	{
		sqlSelectStatlisteners.add(l);
	}

	public void removeSQLSelectStatementListener(SQLSelectStatementListener l)
	{
		sqlSelectStatlisteners.remove(l);
	}


	private SQLStatementContext getContext()
	{
		return stack.peek();
	}

	private void pushContext(SQLStatementContext context)
	{
		SQLStatementContext parent = stack.peek();
		parent.addContext(context);
		stack.push(context);
	}

	private SQLStatementContext popContext()
	{
		return stack.pop();
	}


	void Error(int n)
	{
		if (errDist >= minErrDist) scanner.err.ParsErr(n, t.line, t.col);
		errDist = 0;
	}

	void SemError(int n)
	{
		if (errDist >= minErrDist) scanner.err.SemErr(n, token.line, token.col);
		errDist = 0;
	}

	boolean Successful()
	{
		return scanner.err.count == 0;
	}

	String LexString()
	{
		return token.str;
	}

	String LexName()
	{
		return token.val;
	}

	String LookAheadString()
	{
		return t.str;
	}

	String LookAheadName()
	{
		return t.val;
	}

	private void Get()
	{
		for (; ;)
		{
			token = t;
			t = scanner.Scan();
			if (t.kind <= maxT)
			{
				errDist++;
				return;
			}

			t = token;
		}
	}

	private void Expect(int n)
	{
		if (t.kind == n) Get(); else Error(n);
	}

	private boolean StartOf(int s)
	{
      if(   0 > s
         || 0  > t.kind
         || set.length <= s
         || set[0].length <= t.kind)
      {
         return false;
      }

      return set[s][t.kind];
	}

	private void ExpectWeak(int n, int follow)
	{
		if (t.kind == n)
			Get();
		else
		{
			Error(n);
			while (!StartOf(follow)) Get();
		}
	}

	boolean WeakSeparator(int n, int syFol, int repFol)
	{
		boolean[] s = new boolean[maxT + 1];
		if (t.kind == n)
		{
			Get();
			return true;
		}
		else if (StartOf(repFol))
			return false;
		else
		{
			for (int i = 0; i <= maxT; i++)
			{
				s[i] = set[syFol][i] || set[repFol][i] || set[0][i];
			}
			Error(n);
			while (!s[t.kind]) Get();
			return StartOf(syFol);
		}
	}

	private final void IndexName()
	{
		Expect(1);
	}

	private final void IndexColumnList()
	{
		IndexColumn();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			IndexColumn();
		}
	}

	private final void IndexColumn()
	{
		SimpleColumnName();
		if (t.kind == ParsingConstants.KW_DESC || t.kind == ParsingConstants.KW_ASC)
		{
			if (t.kind == ParsingConstants.KW_ASC)
			{
				Get();
			}
			else
			{
				Get();
			}
		}
	}

	private final void DropPart()
	{
		Expect(ParsingConstants.KW_DROP);
		if (t.kind == 1)
		{
			SimpleColumnName();
			CascadeRestrict();
		}
		else if (t.kind == ParsingConstants.KW_PRIMARY)
		{
			Get();
			Expect(ParsingConstants.KW_KEY);
		}
		else if (t.kind == ParsingConstants.KW_FOREIGN)
		{
			Get();
			Expect(ParsingConstants.KW_KEY);
			RelationName();
		}
		else if (t.kind == ParsingConstants.KW_CONSTRAINT)
		{
			Get();
			ConstraintName();
			CascadeRestrict();
		}
		else
			Error(104);
	}

	private final void AlterPart()
	{
		Expect(ParsingConstants.KW_ALTER);
		SimpleColumnName();
		if (t.kind == ParsingConstants.KW_DROP)
		{
			Get();
			Expect(ParsingConstants.KW_DEFAULT );
		}
		else if (t.kind == ParsingConstants.KW_SET)
		{
			Get();
			ColumnDefault();
		}
		else
			Error(105);
	}

	private final void AddPart()
	{
		Expect(ParsingConstants.KW_ADD);
		if (t.kind == 1)
		{
			ColumnDefList();
		}
		else if (t.kind == ParsingConstants.KW_PRIMARY)
		{
			PrimaryKey();
		}
		else if (t.kind == ParsingConstants.KW_CONSTRAINT)
		{
			ForeignKeyAdd();
		}
		else if (t.kind == ParsingConstants.KW_UNIQUE)
		{
			Unique();
		}
		else if (t.kind == 92)
		{
			CheckConstraint();
		}
		else
			Error(106);
	}

	private final void IndexAndName()
	{
		Expect(ParsingConstants.KW_INDEX);
		IndexName();
	}

	private final void DropTable()
	{
		Expect(ParsingConstants.KW_TABLE);
		QualifiedTable();
		if (t.kind == ParsingConstants.KW_CASCADE || t.kind == ParsingConstants.KW_RESTRICT)
		{
			CascadeRestrict();
		}
	}

	private final void CascadeRestrict()
	{
		if (t.kind == ParsingConstants.KW_CASCADE)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_RESTRICT)
		{
			Get();
		}
		else
			Error(107);
	}

	private final void CreateIndex()
	{
		if (t.kind == ParsingConstants.KW_UNIQUE)
		{
			Get();
		}
		IndexAndName();
		Expect(ParsingConstants.KW_ON);
		Table(null);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		IndexColumnList();
		CloseParens();
	}

	private final void CreateTable()
	{
		Expect(ParsingConstants.KW_TABLE);
		Table(null);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		CreatePart();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			CreatePart();
		}
		CloseParens();
	}

	private final void CreatePart()
	{
		if (t.kind == 1)
		{
			ColumnDef();
		}
		else if (t.kind == ParsingConstants.KW_PRIMARY)
		{
			PrimaryKey();
		}
		else if (t.kind == ParsingConstants.KW_FOREIGN)
		{
			ForeignKeyCreatePart();
		}
      else if (t.kind == ParsingConstants.KW_CONSTRAINT)
      {
         Constraint();
      }
		else if (t.kind == ParsingConstants.KW_UNIQUE)
		{
			Unique();
		}
		else if (t.kind == 92)
		{
			CheckConstraint();
		}
		else
			Error(108);
	}

	private final void CheckConstraint()
	{
		Expect(92);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		Expression();
		CloseParens();
	}

	private final void Unique()
	{
		Expect(ParsingConstants.KW_UNIQUE);
		SimpleColumnParam();
	}

   private final void Constraint()
   {
   }

   private final void ForeignKeyAdd()
   {
      Expect(ParsingConstants.KW_CONSTRAINT);
      RelationName();

      if (t.kind == ParsingConstants.KW_FOREIGN)
      {
         Get();
         Expect(ParsingConstants.KW_KEY);
         SimpleColumnParam();
         Expect(ParsingConstants.KW_REFERENCES);
         Table(null);
         if (t.kind == ParsingConstants.KW_MATCH)
         {
            Get();
            if (t.kind == ParsingConstants.KW_FULL)
            {
               Get();
            }
            else if (t.kind == ParsingConstants.KW_PARTIAL)
            {
               Get();
            }
            else
               Error(109);
         }
         while (t.kind == ParsingConstants.KW_ON || t.kind == ParsingConstants.KW_NO)
         {
            if (t.kind == ParsingConstants.KW_ON)
            {
               Get();
               if (t.kind == ParsingConstants.KW_DELETE)
               {
                  Get();
               }
               else if (t.kind == ParsingConstants.KW_UPDATE)
               {
                  Get();
               }
               else
                  Error(110);
               if (t.kind == ParsingConstants.KW_CASCADE)
               {
                  Get();
               }
               else if (t.kind == ParsingConstants.KW_SET)
               {
                  Get();
                  if (t.kind == ParsingConstants.KW_NULL)
                  {
                     Get();
                  }
                  else if (t.kind == ParsingConstants.KW_DEFAULT)
                  {
                     Get();
                  }
                  else
                     Error(111);
               }
               else
                  Error(112);
            }
            else
            {
               Get();
               Expect(ParsingConstants.KW_ACTION);
            }
         }
      }
      else
      {
         Expect(ParsingConstants.KW_UNIQUE);
         SimpleColumnParam();
      }

   }


   private final void ForeignKeyCreatePart()
	{
		Expect(ParsingConstants.KW_FOREIGN);
		Expect(ParsingConstants.KW_KEY);
		RelationName();
		SimpleColumnParam();
		Expect(ParsingConstants.KW_REFERENCES);
		Table(null);
		if (t.kind == ParsingConstants.KW_MATCH)
		{
			Get();
			if (t.kind == ParsingConstants.KW_FULL)
			{
				Get();
			}
			else if (t.kind == ParsingConstants.KW_PARTIAL)
			{
				Get();
			}
			else
				Error(109);
		}
		while (t.kind == ParsingConstants.KW_ON || t.kind == ParsingConstants.KW_NO)
		{
			if (t.kind == ParsingConstants.KW_ON)
			{
				Get();
				if (t.kind == ParsingConstants.KW_DELETE)
				{
					Get();
				}
				else if (t.kind == ParsingConstants.KW_UPDATE)
				{
					Get();
				}
				else
					Error(110);
				if (t.kind == ParsingConstants.KW_CASCADE)
				{
					Get();
				}
				else if (t.kind == ParsingConstants.KW_SET)
				{
					Get();
					if (t.kind == ParsingConstants.KW_NULL)
					{
						Get();
					}
					else if (t.kind == ParsingConstants.KW_DEFAULT )
					{
						Get();
					}
					else
						Error(111);
				}
				else
					Error(112);
			}
			else
			{
				Get();
				Expect(ParsingConstants.KW_ACTION);
			}
		}
	}

	private final void ConstraintName()
	{
		Expect(1);
	}

	private final void RelationName()
	{
		Expect(1);
	}

	private final void PrimaryKey()
	{
		Expect(ParsingConstants.KW_PRIMARY);
		Expect(ParsingConstants.KW_KEY);
		SimpleColumnParam();
	}

	private final void ColumnDef()
	{
		SimpleColumnName();
		DataType();
		while (t.kind == ParsingConstants.KW_NOT || t.kind == ParsingConstants.KW_DEFAULT || t.kind == ParsingConstants.KW_PRIMARY)
		{
			if (t.kind == ParsingConstants.KW_DEFAULT )
			{
				ColumnDefault();
			}
			else if(t.kind == ParsingConstants.KW_NOT )
			{
				NotOperator();
				Expect(ParsingConstants.KW_NULL);
			}
         else
         {
            Expect(ParsingConstants.KW_PRIMARY);
            Expect(ParsingConstants.KW_KEY);
         }
		}
	}

	private final void ColumnDefList()
	{
		ColumnDef();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			ColumnDef();
		}
	}

	private final void ColumnDefault()
	{
		Expect(ParsingConstants.KW_DEFAULT );
		if (t.kind == 4)
		{
			Get();
		}
		else if (t.kind == 2)
		{
			Get();
		}
		else if (t.kind == 3)
		{
			Get();
		}
		else
			Error(113);
	}

	private final void DataType()
	{
		switch (t.kind)
		{
			case 72:
			case 73:
				{
					if (t.kind == ParsingConstants.KW_CHAR)
					{
						Get();
					}
					else
					{
						Get();
					}
					lenParam();
					break;
				}
			case ParsingConstants.KW_VARCHAR:
				{
					Get();
					lenParam();
					break;
				}
			case ParsingConstants.KW_INTEGER:
			case ParsingConstants.KW_INT:
				{
					if (t.kind == ParsingConstants.KW_INTEGER)
					{
						Get();
					}
					else
					{
						Get();
					}
					break;
				}
			case ParsingConstants.KW_SMALLINT:
				{
					Get();
					break;
				}
			case ParsingConstants.KW_NUMERIC:
				{
					Get();
					Expect(ParsingConstants.KIND_OPENING_BRAKET);
					precision();
					CloseParens();
					break;
				}
			case ParsingConstants.KW_DATE:
				{
					Get();
					break;
				}
			case ParsingConstants.KW_TIME:
				{
					Get();
					lenParam();
					break;
				}
			case ParsingConstants.KW_TIMESTAMP:
				{
					Get();
					lenParam();
					break;
				}
			default:
				Error(114);
		}
	}

	private final void precision()
	{
		Expect(2);
		ItemSeparator();
		Expect(2);
	}

	private final void lenParam()
	{
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		len();
		CloseParens();
	}

	private final void len()
	{
		Expect(2);
	}

	private final void BetweenExpr()
	{
		Expect(ParsingConstants.KW_BETWEEN);
		Field();
		Expect(ParsingConstants.KW_AND);
		Field();
	}

	private final void InSetExpr()
	{
		Expect(ParsingConstants.KW_IN);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		if (StartOf(1))
		{
			FieldList();
		}
		else if (t.kind == ParsingConstants.KW_SELECT)
		{
			SelectStmt();
		}
		else
			Error(ParsingConstants.KW_INSET);
		CloseParens();
	}

	private final void NullTest()
	{
		Expect(ParsingConstants.KW_IS);
		if (t.kind == ParsingConstants.KW_NOT)
		{
			NotOperator();
		}
		Expect(ParsingConstants.KW_NULL);
	}

	private final void LikeTest()
	{
		Expect(ParsingConstants.KW_LIKE);
		if (t.kind == 4)
		{
			Get();
		}
		else if (t.kind == 53)
		{
			Param();
		}
		else
			Error(116);
		if (t.kind == ParsingConstants.KW_ESCAPE)
		{
			Get();
			Expect(4);
		}
	}

	private final void WordOperator()
	{
		if (t.kind == ParsingConstants.KW_AND)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_OR)
		{
			Get();
		}
		else
			Error(117);
	}

	private final void MathOperator()
	{
		if (t.kind == ParsingConstants.KIND_ASTERISK)
		{
			Get();
		}
		else if (t.kind == 55)
		{
			Get();
		}
		else if (t.kind == 56)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_MINUS_SIGN)
		{
			Get();
		}
		else
			Error(118);
	}

	private final void TestExpr()
	{
		if (t.kind == ParsingConstants.KW_IS)
		{
			NullTest();
		}
		else if (StartOf(2))
		{
			if (t.kind == ParsingConstants.KW_NOT)
			{
				NotOperator();
			}
			if (t.kind == ParsingConstants.KW_IN)
			{
				InSetExpr();
			}
			else if (t.kind == ParsingConstants.KW_BETWEEN)
			{
				BetweenExpr();
			}
			else if (t.kind == ParsingConstants.KW_LIKE)
			{
				LikeTest();
			}
			else
				Error(119);
		}
		else
			Error(120);
	}

	private final void Operator()
	{
		if (StartOf(3))
		{
			MathOperator();
		}
		else if (t.kind == ParsingConstants.KW_AND || t.kind == ParsingConstants.KW_OR)
		{
			WordOperator();
		}
		else
			Error(121);
	}

	private final void Term()
	{
		if (t.kind == ParsingConstants.KW_MINUS_SIGN)
		{
			Get();
		}
		if (StartOf(1))
		{
			Field();
			if (StartOf(4))
			{
				TestExpr();
			}
		}
		else if (StartOf(ParsingConstants.KIND_OPENING_BRAKET))
		{
			ColumnFunction();
		}
		else if (StartOf(6))
		{
			FunctionExpr();
		}
		else if (t.kind == ParsingConstants.KIND_OPENING_BRAKET)
		{
			Get();
			if (StartOf(ParsingConstants.KW_UNION))
			{
				Expression();
			}
			else if (t.kind == ParsingConstants.KW_SELECT)
			{
				SelectStmt();
			}
         else if (t.kind == ParsingConstants.KW_CASE)
         {
            CaseStatment();
         }
         else
				Error(122);
			CloseParens();
		}
		else
			Error(123);
	}

	private final void NotOperator()
	{
		Expect(ParsingConstants.KW_NOT);
	}

	private final void Relation()
	{
		switch (t.kind)
		{
			case ParsingConstants.KIND_EQUALS:
				{
					Get();
					break;
				}
			case 62:
				{
					Get();
					break;
				}
			case 63:
				{
					Get();
					break;
				}
			case 64:
				{
					Get();
					break;
				}
			case 65:
				{
					Get();
					break;
				}
			case 66:
				{
					Get();
					break;
				}
			default:
				Error(124);
		}
	}

	private final void SimpleExpression()
	{
		if (t.kind == ParsingConstants.KW_NOT)
		{
			NotOperator();
		}
		Term();
		while (StartOf(ParsingConstants.KW_EXCEPT))
		{
			Operator();
			if (t.kind == ParsingConstants.KW_NOT)
			{
				NotOperator();
			}
			Term();
		}
	}

	private final void OrderByField()
	{
		if (t.kind == 1)
		{
			ColumnName();
		}
		else if (t.kind == 2)
		{
			Get();
		}
		else
			Error(125);
		if (t.kind == ParsingConstants.KW_DESC || t.kind == ParsingConstants.KW_ASC)
		{
			if (t.kind == ParsingConstants.KW_DESC)
			{
				Get();
			}
			else
			{
				Get();
			}
		}
	}

	private final void Param()
	{
		Expect(53);
		Expect(1);
	}

	private final void Field()
	{
		switch (t.kind)
		{
			case 1:
				{
					ColumnName();
					break;
				}
			case ParsingConstants.KW_NULL:
				{
					Get();
					break;
				}
			case 3:
				{
					Get();
					break;
				}
			case 2:
				{
					Get();
					break;
				}
			case 4:
				{
					Get();
					break;
				}
			case ParsingConstants.KW_MINUS_SIGN:
				{
					Get();
					Get();
					break;
				}
			case 53:
				{
					Param();
					break;
				}
			default:
				Error(126);
		}
	}

	private final void SimpleColumnParam()
	{
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		SimpleColumnList();
		CloseParens();
	}

	private final void SimpleColumnList()
	{
		SimpleColumnName();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			SimpleColumnName();
		}
	}

	private final void SimpleColumnName()
	{
		Expect(1);
	}

	private final void ColumnFunction()
	{
		if (t.kind == ParsingConstants.KW_COUNT)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_SUM)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_MAX)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_MIN)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_AVG)
		{
			Get();
		}
		else
			Error(127);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		if (t.kind == ParsingConstants.KIND_ASTERISK)
		{
			Get();
		}
		else if (StartOf(ParsingConstants.KW_INTERSECT))
		{
			if (t.kind == ParsingConstants.KW_DISTINCT)
			{
				Get();
			}
			Expression();
		}
		else
			Error(128);
		CloseParens();
	}

	private final void FunctionExpr()
	{
		if (t.kind == ParsingConstants.KW_TIMESTAMP)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_UPPER)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_MONTH)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_YEAR)
		{
			Get();
		}
		else
			Error(129);
		Expect(ParsingConstants.KIND_OPENING_BRAKET);
		Expression();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			Expression();
		}
		CloseParens();
	}

	private final void SelectField()
	{
		if (StartOf(ParsingConstants.KW_UNION))
		{
			Expression();
			if (t.kind == ParsingConstants.KW_AS)
			{
				Get();
				Alias();
			}
		}
		else if (t.kind == ParsingConstants.KIND_ASTERISK)
		{
			Get();
		}
      else if (t.kind == ParsingConstants.KW_CASE)
      {
         CaseStatment();
      }
      else
			Error(130);
	}

   private void CaseStatment()
   {
      Expect(ParsingConstants.KW_CASE);
      if(t.kind == ParsingConstants.KW_WHEN)
      {
         Expect(ParsingConstants.KW_WHEN);
         Expression();
         Expect(ParsingConstants.KW_THEN);
         Expression();

         while(t.kind == ParsingConstants.KW_WHEN)
         {
            Get();
            Expression();
            Expect(ParsingConstants.KW_THEN);
            Expression();
         }

         Expect(ParsingConstants.KW_ELSE);
         Expression();
         Expect(ParsingConstants.KW_END);

         if (t.kind == ParsingConstants.KW_AS)
         {
            Get();
            Alias();
         }

      }
      else
      {
         ColumnName();
         Expect(ParsingConstants.KW_WHEN);
         Expression();
         Expect(ParsingConstants.KW_THEN);
         Expression();

         while(t.kind == ParsingConstants.KW_WHEN)
         {
            Get();
            Expression();
            Expect(ParsingConstants.KW_THEN);
            Expression();
         }

         Expect(ParsingConstants.KW_ELSE);
         Expression();
         Expect(ParsingConstants.KW_END);

         if (t.kind == ParsingConstants.KW_AS)
         {
            Get();
            Alias();
         }
      }
   }

   private final void OrderByFldList()
   {
      OrderByField();
      while (t.kind == ParsingConstants.KIND_COMMA)
      {
         ItemSeparator();
         OrderByField();
      }
   }

	private final void SearchCondition()
	{
		Expression();
	}

	private final void JoinExpr()
	{
		if (t.kind == ParsingConstants.KW_ON)
		{
			Get();
			Expression();
		}
		else if (t.kind == ParsingConstants.KW_USING)
		{
			Get();
			Expect(ParsingConstants.KIND_OPENING_BRAKET);
			ColumnList();
			CloseParens();
		}
		else
			Error(131);
	}

	private final void JoinType()
	{
		if (t.kind == ParsingConstants.KW_NATURAL)
		{
			Get();
		}
		if (t.kind == ParsingConstants.KW_INNER)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_FULL || t.kind == ParsingConstants.KW_LEFT || t.kind == ParsingConstants.KW_RIGHT)
		{
			if (t.kind == ParsingConstants.KW_FULL)
			{
				Get();
			}
			else if (t.kind == ParsingConstants.KW_LEFT)
			{
				Get();
			}
			else
			{
				Get();
			}
			if (t.kind == ParsingConstants.KW_OUTER)
			{
				Get();
			}
		}
		else
			Error(132);
	}

	private final void CrossJoin()
	{
		Expect(ParsingConstants.KW_CROSS);
		Expect(ParsingConstants.KW_JOIN);
		QualifiedTable();
	}

	private final void Alias()
	{
		Expect(1);
	}

	private final void JoinStmt()
	{
		if (t.kind == ParsingConstants.KW_CROSS)
		{
			CrossJoin();
		}
		else if (StartOf(ParsingConstants.KW_MINUS))
		{
			if (StartOf(ParsingConstants.KW_ALL))
			{
				JoinType();
			}
			Expect(ParsingConstants.KW_JOIN);
			QualifiedTable();
			if (t.kind == ParsingConstants.KW_ON || t.kind == ParsingConstants.KW_USING)
			{
				JoinExpr();
			}
		}
		else
			Error(133);
	}

	private final void QualifiedTable()
	{
      // Fix for Bug #982127
		//SQLSelectStatement statement = (SQLSelectStatement) getContext();
      SQLStatement statement = (SQLStatement) getContext();

		SQLTable table = new SQLTable(statement, t.pos);
		statement.addTable(table);
		boolean wasSet = false;

		Expect(1);
		if (t.val.equals("."))
			table.setSchema(token.str, token.pos);
		else
			table.setName(token.str, token.pos);

		if (t.kind == 22)
		{
			Get();
			Expect(1);
			table.setName(token.str, token.pos);
		}
		if (t.kind == 1 || t.kind == ParsingConstants.KW_AS)
		{
			if (t.kind == ParsingConstants.KW_AS)
			{
				Get();
			}
			table.setAlias(t.str, t.pos);
			wasSet = true;
			if (statement.setTable(table) == false)
				SemError(ParsingConstants.KW_MINUS);

			Alias();
		}
		if (!wasSet && statement.setTable(table) == false)
			SemError(ParsingConstants.KW_MINUS);

	}

	private final void FromTableList()
	{
		QualifiedTable();
		while (StartOf(ParsingConstants.KW_UPDATE))
		{
			if (t.kind == ParsingConstants.KIND_COMMA)
			{
				ItemSeparator();
				QualifiedTable();
			}
			else
			{
				JoinStmt();
			}
		}
	}

	private final void SelectFieldList()
	{
		SelectField();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			SelectField();
		}
	}

	private final void OrderByClause()
	{
		SQLSelectStatement statement = (SQLSelectStatement) getContext();
		statement.setOrderByStart(scanner.pos);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_ORDER))
		{
			Error(134);
			Get();
		}
		Expect(ParsingConstants.KW_ORDER);
		Expect(ParsingConstants.KW_BY);
		OrderByFldList();
		statement.setOrderByEnd(t.pos);
	}

	private final void HavingClause()
	{
		SQLSelectStatement statement = (SQLSelectStatement) getContext();
		statement.setHavingStart(scanner.pos);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_HAVING))
		{
			Error(135);
			Get();
		}
		Expect(ParsingConstants.KW_HAVING);
		SearchCondition();
		statement.setHavingEnd(t.pos);
	}

	private final void GroupByClause()
	{
		SQLSelectStatement statement = (SQLSelectStatement) getContext();
		statement.setGroupByStart(scanner.pos);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_GROUP))
		{
			Error(136);
			Get();
		}
		Expect(ParsingConstants.KW_GROUP);
		Expect(ParsingConstants.KW_BY);
		FieldList();
		statement.setGroupByEnd(t.pos);
	}

	private final void FromClause()
	{
		SQLSelectStatement statement = (SQLSelectStatement) getContext();
		statement.setFromStart(scanner.pos);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_FROM))
		{
			Error(137);
			Get();
		}
		Expect(ParsingConstants.KW_FROM);
		FromTableList();
		statement.setFromEnd(t.pos);
	}

	private final void SelectClause()
	{
		SQLSelectStatement statement = (SQLSelectStatement) getContext();
		statement.setSelectListStart(scanner.pos);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_SELECT))
		{
			Error(138);
			Get();
		}
		Expect(ParsingConstants.KW_SELECT);
		if (t.kind == ParsingConstants.KW_ALL || t.kind == ParsingConstants.KW_DISTINCT)
		{
			if (t.kind == ParsingConstants.KW_DISTINCT)
			{
				Get();
			}
			else
			{
				Get();
			}
		}
		SelectFieldList();
		statement.setSelectListEnd(t.pos);
	}

	private final void FieldList()
	{
		Field();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			Field();
		}
	}

	private final void CloseParens()
	{
		ExpectWeak(ParsingConstants.KIND_CLOSING_BRAKET, ParsingConstants.KW_SET);
	}

	private final void ColumnList()
	{
		ColumnName();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			ColumnName();
		}
	}

	private final void Expression()
	{
		SimpleExpression();
		while (StartOf(ParsingConstants.KIND_EQUALS))
		{
			Relation();
			SimpleExpression();
		}
	}

	private final void ColumnName()
	{
		SQLStatementContext context = getContext();
		SQLColumn column = new SQLColumn(context, t.pos);
		context.addColumn(column);
		if (scanner.ch == '.')
			column.setQualifier(t.str, t.pos);
		else
			column.setColumn(t.str, t.pos);

		Expect(1);
		if (t.kind == 22)
		{
			Get();
			if (t.kind == 1)
			{
				Get();
				column.setColumn(token.str, token.pos);
			}
			else if (t.kind == ParsingConstants.KIND_ASTERISK)
			{
				Get();
			}
			else
				Error(139);
		}
	}

	private final void ItemSeparator()
	{
		ExpectWeak(ParsingConstants.KIND_COMMA, ParsingConstants.KW_INSERT);
	}

	private final void UpdateField()
	{
		ColumnName();
		Expect(ParsingConstants.KIND_EQUALS);
		Expression();
	}

	private final void WhereClause()
	{
		SQLStatement statement = (SQLStatement) getContext();
		SQLWhere where = new SQLWhere(statement, t.pos);
		pushContext(where);

		while (!(t.kind == 0 || t.kind == ParsingConstants.KW_WHERE))
		{
			Error(140);
			Get();
		}
		Expect(ParsingConstants.KW_WHERE);
		SearchCondition();
		where.setEndPosition(t.pos);
		popContext();

	}

	private final void UpdateFieldList()
	{
		UpdateField();
		while (t.kind == ParsingConstants.KIND_COMMA)
		{
			ItemSeparator();
			UpdateField();
		}
	}

	private final void Table(SQLTable table)
	{
		if (table != null)
			table.setName(t.str, t.pos);

		Expect(1);
	}

	private final void SetOperator()
	{
		if (t.kind == ParsingConstants.KW_UNION)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_EXCEPT)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_INTERSECT)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_MINUS)
		{
			Get();
		}
		else
			Error(141);
		if (t.kind == ParsingConstants.KW_ALL)
		{
			Get();
		}
	}

	private final void SimpleSelect()
	{
		SQLSelectStatement statement = new SQLSelectStatement(t.pos);

		SQLSelectStatementListener[] clone = sqlSelectStatlisteners.toArray(new SQLSelectStatementListener[sqlSelectStatlisteners.size()]);

		for (int i = 0; i < clone.length; i++)
		{
			statement.addListener(clone[i]);
		}

		pushContext(statement);

		SelectClause();
		FromClause();
		if (t.kind == ParsingConstants.KW_WHERE)
		{
			WhereClause();
		}
		if (t.kind == ParsingConstants.KW_GROUP)
		{
			GroupByClause();
		}
		if (t.kind == ParsingConstants.KW_HAVING)
		{
			HavingClause();
		}
		if (t.kind == ParsingConstants.KW_ORDER)
		{
			OrderByClause();
		}
		popContext();
	}

	private final void Transaction()
	{
		if (t.kind == ParsingConstants.KW_COMMIT)
		{
			Get();
		}
		else if (t.kind == ParsingConstants.KW_ROLLBACK)
		{
			Get();
		}
		else
			Error(142);
		if (t.kind == ParsingConstants.KW_WORK)
		{
			Get();
		}
	}

	private final void AlterTable()
	{
		Expect(ParsingConstants.KW_ALTER);
		Expect(ParsingConstants.KW_TABLE);
		QualifiedTable();
		if (t.kind == ParsingConstants.KW_ADD)
		{
			AddPart();
		}
		else if (t.kind == ParsingConstants.KW_ALTER)
		{
			AlterPart();
		}
		else if (t.kind == ParsingConstants.KW_DROP)
		{
			DropPart();
		}
		else
			Error(143);
	}

	private final void Drop()
	{
		Expect(ParsingConstants.KW_DROP);
		if (t.kind == ParsingConstants.KW_TABLE)
		{
			DropTable();
		}
		else if (t.kind == ParsingConstants.KW_INDEX)
		{
			IndexAndName();
		}
		else
			Error(144);
	}

	private final void CreateStmt()
	{
		Expect(ParsingConstants.KW_CREATE);
		if (t.kind == ParsingConstants.KW_TABLE)
		{
			CreateTable();
		}
		else if (t.kind == ParsingConstants.KW_UNIQUE || t.kind == ParsingConstants.KW_INDEX)
		{
			CreateIndex();
		}
      else if (t.kind == ParsingConstants.KW_VIEW)
      {
         CreateView();
      }
		else
			Error(145);
	}

   private void CreateView()
   {
      Expect(ParsingConstants.KW_VIEW);
      Table(null);
      Expect(ParsingConstants.KW_AS);
      SelectStmt();
   }

   private final void DeleteStmt()
   {
      SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
      pushContext(statement);

      Expect(ParsingConstants.KW_DELETE);
      SQLTable table = new SQLTable(statement, scanner.pos + 1);
      statement.addTable(table);

      Expect(ParsingConstants.KW_FROM);
      table.setName(t.str, t.pos);
      Table(table);
      if (t.kind == ParsingConstants.KW_WHERE)
      {
         WhereClause();
      }
      statement.setEndPosition(token.pos);
      popContext();

   }

	private final void UpdateStmt()
	{
		SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
		SQLTable table = new SQLTable(statement, scanner.pos + 1);
		statement.addTable(table);
		pushContext(statement);

		Expect(ParsingConstants.KW_UPDATE);
		table.setName(t.str, t.pos);
		Table(null);
		statement.setUpdateListStart(t.pos + 4);
		Expect(ParsingConstants.KW_SET);
		UpdateFieldList();
		statement.setUpdateListEnd(token.pos);
		if (t.kind == ParsingConstants.KW_WHERE)
		{
			WhereClause();
		}
		statement.setEndPosition(token.pos);
		popContext();

	}

	private final void InsertStmt()
	{
		SQLModifyingStatement statement = new SQLModifyingStatement(t.pos);
		pushContext(statement);

		Expect(ParsingConstants.KW_INSERT);
		SQLTable table = new SQLTable(statement, scanner.pos + 1);
		statement.addTable(table);

		Expect(ParsingConstants.KW_INTO);
		SQLColumn column = new SQLColumn(statement, scanner.pos + 2);
		table.setName(t.str, t.pos);
		column.setRepeatable(true);
		statement.addColumn(column);

		Table(table);
		if (t.kind == ParsingConstants.KIND_OPENING_BRAKET)
		{
			Get();
			ColumnList();
			CloseParens();
		}
		column.setEndPosition(token.pos);
		if (t.kind == ParsingConstants.KW_VALUES)
		{
			Get();
			Expect(ParsingConstants.KIND_OPENING_BRAKET);
			FieldList();
			CloseParens();
		}
		else if (t.kind == ParsingConstants.KW_SELECT)
		{
			SelectStmt();
		}
		else
			Error(146);
		statement.setEndPosition(token.pos);
		popContext();

	}

	private final void SelectStmt()
	{
		pushContext(new SQLStatement(token.pos));
		SimpleSelect();
		while (StartOf(ParsingConstants.KW_INTO))
		{
			SetOperator();
			SimpleSelect();
		}
		popContext();
	}

	private final void SQLStatement()
	{
		addRootStatement(new SQLStatement(token.pos));
		switch (t.kind)
		{
			case ParsingConstants.KW_SELECT:
				{
					SelectStmt();
					break;
				}
			case ParsingConstants.KW_INSERT:
				{
					InsertStmt();
					break;
				}
			case ParsingConstants.KW_UPDATE:
				{
					UpdateStmt();
					break;
				}
			case ParsingConstants.KW_DELETE:
				{
					DeleteStmt();
					break;
				}
			case ParsingConstants.KW_CREATE:
				{
					CreateStmt();
					break;
				}
			case ParsingConstants.KW_DROP:
				{
					Drop();
					break;
				}
			case ParsingConstants.KW_ALTER:
				{
					AlterTable();
					break;
				}
			case ParsingConstants.KW_COMMIT:
			case ParsingConstants.KW_ROLLBACK:
				{
					Transaction();
					break;
				}
			default:
				Error(147);
		}
		if (t.kind == 6)
		{
			Get();
		}
	}

	private final void squirrelSQL()
	{
		SQLStatement();
		while (StartOf(ParsingConstants.KW_VALUES))
		{
			SQLStatement();
		}
		Expect(0);
	}


	public Parser(Scanner _scanner, SQLSchema schema)
	{
		scanner = _scanner;
		t = new Token();
      rootSchema = schema;
	}

	public void parse()
	{
		Get();
		squirrelSQL();

	}

	private static boolean[][] set = {
		{T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, G, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, T, x, x, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, T, x, T, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, T, T, T, T, T, x, x, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, T, T, T, T, T, x, x, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x},
		{T, x, x, x, x, x, T, T, T, T, T, x, T, x, T, T, x, T, T, T, T, x, x, T, T, T, T, T, T, T, T, x, x, x, T, T, x, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, T, x, T, T, T, T, T, x, x, x, T, T, T, T, T, x, x, T, T, x, x, x, x, x, x, x, x, x, x, T, x, x, x, T, x, x, x, x, x, x, x, T, x, x, T, x, T, x, x, T, T, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{T, T, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, x, T, T, T, T, T, T, T, T, T, T, T, T, T, x, x, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, T, x, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, T, T, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x},
		{x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, T, x, x, T, x, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x, T, x, x, T, x, T, x, x, x, x, x, x}

	};
}
