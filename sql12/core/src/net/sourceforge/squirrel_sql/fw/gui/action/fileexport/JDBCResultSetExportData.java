package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import java.sql.Connection;
import java.util.List;

public class JDBCResultSetExportData
{
   private List<String> _originalSqlsToExport;

   private Connection _con;
   private DialectType _dialect;
   private boolean _limitRows;

   private List<ExportSqlNamed> _exportSqlsNamed;
   private boolean _exportSingleFile;
   private int _maxRows;


   public JDBCResultSetExportData(List<String> originalSqlsToExport, List<ExportSqlNamed> exportSqlsNamed, Connection con, DialectType dialect)
   {
      _originalSqlsToExport = originalSqlsToExport;
      _exportSqlsNamed = exportSqlsNamed;
      _con = con;
      _dialect = dialect;
   }


   public List<String> getOriginalSqlsToExport()
   {
      return _originalSqlsToExport;
   }

   public Connection getCon()
   {
      return _con;
   }

   public DialectType getDialect()
   {
      return _dialect;
   }

   public boolean isLimitRows()
   {
      return _limitRows;
   }

   public void setLimitRows(boolean limitRows)
   {
      _limitRows = limitRows;
   }

   public List<ExportSqlNamed> getExportSqlsNamed()
   {
      return _exportSqlsNamed;
   }

   public void setExportSqlsNamed(List<ExportSqlNamed> exportSqlsNamed)
   {
      _exportSqlsNamed = exportSqlsNamed;
   }

   public void setExportSingleFile(boolean exportSingleFile)
   {
      _exportSingleFile = exportSingleFile;
   }

   public int getMaxRows()
   {
      return _maxRows;
   }

   public void setMaxRows(int maxRows)
   {
      _maxRows = maxRows;
   }
}
