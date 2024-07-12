package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class ColumnDisplayUtil
{
   private static final String PREF_KEY_SHOW_CELL_DETAIL = "ColumnDisplayUtil.show.cell.detail";

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

   public static boolean isShowCellDetail()
   {
      return Props.getBoolean(PREF_KEY_SHOW_CELL_DETAIL, false);
   }

   public static void setShowCellDetail(boolean b)
   {
      Props.putBoolean(PREF_KEY_SHOW_CELL_DETAIL, b);
   }
}
