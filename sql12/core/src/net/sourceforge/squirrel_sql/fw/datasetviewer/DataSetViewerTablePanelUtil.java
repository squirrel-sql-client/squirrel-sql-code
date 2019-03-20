package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;

public class DataSetViewerTablePanelUtil
{
   public static List<ExtTableColumn> getTableColumns(DataSetViewerTable table)
   {
      ArrayList<ExtTableColumn> ret = new ArrayList<ExtTableColumn>();

      for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
      {
         TableColumn col = table.getColumnModel().getColumn(i);
         if (col instanceof ExtTableColumn)
         {
            ret.add((ExtTableColumn) col);
         }
      }

      return ret;

   }
}
