package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class ExportedKey
{
   private final String _catalog;
   private final String _schema;
   private final String _table;
   private final String _fkColumn;
   private String _pkColumn;
   private String _inStat;

   public ExportedKey(String catalog, String schema, String table, String fkColumn, String pkColumn,String inStat)
   {
      _catalog = catalog;
      _schema = schema;
      _table = table;
      _fkColumn = fkColumn;
      _pkColumn = pkColumn;
      _inStat = inStat;
   }

   @Override
   public String toString()
   {
      // return SQLUtilities.getQualifiedTableName(_catalog, _schema, _table) + "->" + _fkColumn;
      return _table + "->" + _fkColumn;
   }

   public String getInStat()
   {
      return _inStat;
   }

   public ResultMetaDataTable getResultMetaDataTable()
   {
      return new ResultMetaDataTable(_catalog, _schema, _table);
   }

   public String getFkColumn()
   {
      return _fkColumn;
   }

   public String getTablesPrimaryKey()
   {
      return _pkColumn;
   }

   public boolean hasSingleColumnPk()
   {
      return null != _pkColumn;
   }
}
