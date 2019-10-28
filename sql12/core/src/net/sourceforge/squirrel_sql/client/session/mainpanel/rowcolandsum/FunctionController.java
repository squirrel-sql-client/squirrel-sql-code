package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FunctionController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FunctionController.class);
   private JDialog _dlg;
   private final DataSetViewerTablePanel _functionValuesTablePanel;

   public FunctionController(JPanel parent)
   {
      _dlg = new JDialog(GUIUtils.getOwningFrame(parent), s_stringMgr.getString("FunctionController.title"));

      _dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      _dlg.getContentPane().setLayout(new GridLayout(1,1));

      _functionValuesTablePanel = new DataSetViewerTablePanel();
      _functionValuesTablePanel.init(null, null);

      _dlg.getContentPane().add(new JScrollPane(_functionValuesTablePanel.getComponent()));


      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.initLocation(_dlg, 200, 50);

      _dlg.setVisible(true);


   }

   public boolean isDisposed()
   {
      if(false == _dlg.isVisible())
      {
         _dlg.dispose();
         return true;
      }

      return false;
   }

   public ArrayList<SumAndColumn> update(DataSetViewerTable table)
   {
      try
      {
         ArrayList<SumAndColumn> sums = Calculator.calculateSums(table);

         ArrayList<ColumnDisplayDefinition> columns = new ArrayList<>();
         columns.add(new ColumnDisplayDefinition(10, s_stringMgr.getString("FunctionController.function")));
         columns.addAll(sums.stream().map( s -> s.getColumnDisplayDefinition()).collect(Collectors.toList()));


         ArrayList<Object[]> rows = new ArrayList<>();
         Object[] sumRow = new Object[sums.size() + 1];

         sumRow[0] = s_stringMgr.getString("FunctionController.sums");
         for (int i = 0; i < sums.size(); i++)
         {
            sumRow[i+1] = sums.get(i).getSum();
         }
         rows.add(sumRow);

         SimpleDataSet simpleDataSet = new SimpleDataSet(rows, columns.toArray(new ColumnDisplayDefinition[0]));

         _functionValuesTablePanel.show(simpleDataSet);

         return sums;
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
