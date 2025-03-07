package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;

public class TableParseInfo
{
   private String _tableName;
   private TableQualifier _tableQualifier;
   private int _statBegin;
   private int _statEnd;

   public TableParseInfo(String tableName, int statBegin, int statEnd)
   {
      _tableName = tableName;
      _tableQualifier = new TableQualifier(tableName);
      _statBegin = statBegin;
      _statEnd = statEnd;
   }

   public String getTableName()
   {
      return _tableName;
   }

   public TableQualifier getTableQualifier()
   {
      return _tableQualifier;
   }

   public int getStatBegin()
   {
      return _statBegin;
   }

   public int getStatEnd()
   {
      return _statEnd;
   }

   public boolean matches(TableQualifier tableQualifier, int statBegin)
   {
      return _tableQualifier.matches(tableQualifier) && _statBegin == statBegin;
   }
}
