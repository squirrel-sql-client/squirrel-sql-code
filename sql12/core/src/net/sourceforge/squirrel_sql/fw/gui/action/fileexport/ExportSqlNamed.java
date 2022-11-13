package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

public class ExportSqlNamed
{
   private String _sql;
   private String _exportNameFileNormalized;

   public ExportSqlNamed(String sql, String exportNameFileNormalized)
   {
      _sql = sql;
      _exportNameFileNormalized = exportNameFileNormalized;
   }

   public String getSql()
   {
      return _sql;
   }

   public String getExportNameFileNormalized()
   {
      return _exportNameFileNormalized;
   }
}
