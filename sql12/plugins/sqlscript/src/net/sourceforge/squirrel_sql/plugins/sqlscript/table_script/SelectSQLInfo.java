package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportSqlNamed;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.ExportUtil;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectSQLInfo
{
   private final String _tableName;
   private final String _selectStatement;

   public SelectSQLInfo(ITableInfo tableInfo, String selectStatement)
   {
      this(tableInfo.getSimpleName(), selectStatement);
   }

   public SelectSQLInfo(String  tableName, String selectStatement)
   {
      _tableName = tableName;
      _selectStatement = selectStatement;
   }

   public static List<ExportSqlNamed> toExportSqlsNamed(List<SelectSQLInfo> selectSQLInfo)
   {
      return selectSQLInfo.stream().map(i -> new ExportSqlNamed(i._selectStatement, i._tableName)).collect(Collectors.toList());
   }

   public String getTableName()
   {
      return _tableName;
   }

   public String getSelectStatement()
   {
      return _selectStatement;
   }

   public static String toJoinedSQLs(ISession session, List<SelectSQLInfo> selectSQLInfos)
   {
      final String sqlsJoined;
      String statSep = session.getProperties().getSQLStatementSeparator();

      if(1 == statSep.length())
      {
         sqlsJoined = String.join(statSep + "\n", selectSQLInfos.stream().map(i -> i._selectStatement).collect(Collectors.toList()));
      }
      else
      {
         sqlsJoined = String.join(" " + statSep + "\n", selectSQLInfos.stream().map(i -> i._selectStatement).collect(Collectors.toList()));
      }

      return sqlsJoined;
   }


   public static List<SelectSQLInfo> of(List<String> sqls)
   {
      List<SelectSQLInfo> ret = new ArrayList<>();

      for (int i = 0; i < sqls.size(); i++)
      {
         ret.add(new SelectSQLInfo(ExportUtil.createDefaultExportName(i), sqls.get(i)));
      }

      return ret;
   }

   public static List<String> toSQLs(List<SelectSQLInfo> selectSQLInfos)
   {
      return new ArrayList<>(selectSQLInfos.stream().map(i -> i._selectStatement).collect(Collectors.toList()));
   }

}
