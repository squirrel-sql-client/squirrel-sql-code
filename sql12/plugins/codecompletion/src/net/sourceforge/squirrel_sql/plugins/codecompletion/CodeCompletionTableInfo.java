package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionColumnInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Vector;

public class CodeCompletionTableInfo extends CodeCompletionInfo
{
   private String _tableName;
   private String _tableType;
   private CodeCompletionColumnInfo[] _colInfos;


   public CodeCompletionTableInfo(String tableName, String tableType)
   {
      _tableName = tableName;
      _tableType = tableType;
   }

   public String getCompletionString()
   {
      return _tableName;
   }

   public CodeCompletionInfo[] getColumns(DatabaseMetaData jdbcMetaData, String colNamePattern)
      throws SQLException
   {
      if(null == _colInfos)
      {
         Vector infos = new Vector();
         ResultSet res = jdbcMetaData.getColumns(null, null, _tableName, "%");
         while(res.next())
         {
            String columnName = res.getString("COLUMN_NAME");
            String columnType = res.getString("TYPE_NAME");
            int columnSize = res.getInt("COLUMN_SIZE");
            boolean nullable = "YES".equals(res.getString("IS_NULLABLE"));
            CodeCompletionColumnInfo buf = new CodeCompletionColumnInfo(columnName, columnType, columnSize, nullable);
            infos.add(buf);
         }
         _colInfos = (CodeCompletionColumnInfo[])infos.toArray(new CodeCompletionColumnInfo[0]);
      }

      String upperCaseColNamePattern = colNamePattern.toUpperCase().trim();

      if("".equals(upperCaseColNamePattern))
      {
         return _colInfos;
      }

      Vector ret = new Vector();
      for(int i=0; i < _colInfos.length; ++i)
      {
         if(_colInfos[i].upperCaseCompletionStringStartsWith(upperCaseColNamePattern))
         {
            ret.add(_colInfos[i]);
         }
      }

      return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
   }

   public boolean hasColumns()
   {
      return true;
   }


   public String toString()
   {
      if(null != _tableType && !"TABLE".equals(_tableType))
      {
         return _tableName  + " (" + _tableType + ")";
      }
      else
      {
         return _tableName;
      }
   }
}
