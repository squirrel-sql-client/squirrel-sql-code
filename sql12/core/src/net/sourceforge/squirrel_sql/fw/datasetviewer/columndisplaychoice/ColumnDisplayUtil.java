package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class ColumnDisplayUtil
{
   public static String getColumnName(ColumnDisplayDefinition selColDisp)
   {
      String colName = "";
      if(StringUtilities.isNotEmpty(selColDisp.getTableName(), true))
      {
         colName += selColDisp.getTableName() + ".";
      }
      colName += selColDisp.getColumnName();
      return colName;
   }
}
