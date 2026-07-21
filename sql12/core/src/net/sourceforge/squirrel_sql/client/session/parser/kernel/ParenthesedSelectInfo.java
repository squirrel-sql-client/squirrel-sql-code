package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class ParenthesedSelectInfo
{
   private StatementBounds _statementBounds;
   private ArrayList<ErrorInfo> _errorInfosBuffer;
   private String _aliasName;
   private List<TableColumnInfo> _columns = new ArrayList<>();

   public ParenthesedSelectInfo(StatementBounds statementBounds, ArrayList<ErrorInfo> errorInfosBuffer, String aliasName, List<TableColumnInfo> columns)
   {
      this._statementBounds = statementBounds;
      this._errorInfosBuffer = errorInfosBuffer;
      this._aliasName = aliasName;
      this._columns = columns;
   }

   public String getAlias()
   {
      return _aliasName;
   }

   public List<ExtendedColumnInfo> getColumns()
   {
      return _columns.stream().map(c -> new ExtendedColumnInfo(c, _aliasName)).toList();
   }

   public boolean isInStatementOfParenthesedSelect(int pos)
   {
      return _statementBounds.getBeginPos() <= pos && pos < _statementBounds.getEndPos();
   }
}
