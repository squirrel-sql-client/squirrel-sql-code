package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;

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

   public static DataSetViewerTablePanel createDataSetViewerTablePanel(List<Object[]> allRows, List<ColumnDisplayDefinition> columnDisplayDefinitions, ISession session)
   {
      try
      {
         DataSetViewerTablePanel dsv = new DataSetViewerTablePanel();
         dsv.init(null, new DataModelImplementationDetails(session), session);

         SimpleDataSet ods = new SimpleDataSet(allRows, columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]));
         dsv.show(ods);
         return dsv;
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }
}
