package net.sourceforge.squirrel_sql.client.session.mainpanel.crosstable;

import net.sourceforge.squirrel_sql.client.session.DefaultDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;
import java.util.*;

public class CrossTableCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CrossTableCtrl.class);


   private ISession _session;
   private CrossTablePanel _crossTablePanel;

   public CrossTableCtrl(ISession session)
   {
      _session = session;
      _crossTablePanel = new CrossTablePanel(session);
   }

   public static boolean isCrossTablePanel(Component comp)
   {
      return comp instanceof CrossTablePanel;
   }

   public String getTitle()
   {
      return s_stringMgr.getString("crossTable.tab.name");
   }

   public CrossTablePanel getPanel()
   {
      return _crossTablePanel;
   }

   public void init(ResultSetDataSet rsds)
   {

      try
      {

         int rowCount = rsds.getAllDataForReadOnly().size();

         ColumnDisplayDefinition crossColDefs[] = new ColumnDisplayDefinition[rowCount + 1];

         crossColDefs[0] = new ColumnDisplayDefinition(20, s_stringMgr.getString("crossTable.ColName"));
         for (int i = 0; i < rowCount; i++)
         {
            crossColDefs[i + 1] = new ColumnDisplayDefinition(20, s_stringMgr.getString("crossTable.crossCol", i+1));
         }

         ArrayList<Object[]> crossRows = new ArrayList<Object[]>();


         for (int i = 0; i < rsds.getDataSetDefinition().getColumnDefinitions().length; i++)
         {
            ColumnDisplayDefinition columnDisplayDefinition = rsds.getDataSetDefinition().getColumnDefinitions()[i];

            Object[] crossRow = new Object[rowCount + 1];
            crossRow[0] = columnDisplayDefinition.getColumnName();

            for (int j = 0; j < rowCount; j++)
            {
               Object[] row = rsds.getAllDataForReadOnly().get(j);
               crossRow[j + 1] = row[i];
            }

            crossRows.add(crossRow);

         }

         SimpleDataSet simpleDataSet = new SimpleDataSet(crossRows, crossColDefs);

         _crossTablePanel.table.show(simpleDataSet);
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }


   }
}
