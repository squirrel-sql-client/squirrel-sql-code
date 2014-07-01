package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class ExportedKey
{
   private final String _catalog;
   private final String _schema;
   private final String _table;
   private final String _column;
   private String _inStat;

   public ExportedKey(String catalog, String schema, String table, String column, String inStat)
   {
      _catalog = catalog;
      _schema = schema;
      _table = table;
      _column = column;
      _inStat = inStat;
   }

   @Override
   public String toString()
   {
      return SQLUtilities.getQualifiedTableName(_catalog, _schema, _table) + "->" + _column;
   }

   public void setInStat(String inStat)
   {
      _inStat = inStat;
   }

   public String getInStat()
   {
      return _inStat;
   }

   public ResultMetaDataTable getResultMetaDataTable()
   {
      return new ResultMetaDataTable(_catalog, _schema, _table);
   }
}
