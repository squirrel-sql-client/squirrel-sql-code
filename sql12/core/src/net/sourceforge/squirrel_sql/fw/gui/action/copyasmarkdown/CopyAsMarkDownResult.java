package net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown;

public class CopyAsMarkDownResult
{
   public static final CopyAsMarkDownResult EMPTY = new CopyAsMarkDownResult(null, null);

   private final String _markDownString;
   private final RawDataTable _rawDataTable;

   public CopyAsMarkDownResult(String markDownString, RawDataTable rawDataTable)
   {
      _markDownString = markDownString;
      _rawDataTable = rawDataTable;
   }

   public String getMarkDownString()
   {
      return _markDownString;
   }

   public String[] getColNames()
   {
      return _rawDataTable.getColNames();
   }

   public String getRawColumnString(String colName)
   {
      return _rawDataTable.getRawColumnString(colName);
   }

   public boolean isEmpty()
   {
      return this == EMPTY;
   }
}
