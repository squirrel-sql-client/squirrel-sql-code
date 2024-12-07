package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FunctionController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FunctionController.class);

   public static final String PREFS_KEY_MEAN_CHECKED = "FunctionController.mean.checked";
   public static final String PREFS_KEY_DEVIATION_CHECKED = "FunctionController.deviation.checked";

   private FunctionDialog _dlg;
   private DataSetViewerTable _currentTableToSum;

   public FunctionController(JPanel parent)
   {
      _dlg = new FunctionDialog(GUIUtils.getOwningFrame(parent), s_stringMgr.getString("FunctionController.title"));

      _dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      _dlg.chkMean.setSelected(Props.getBoolean(PREFS_KEY_MEAN_CHECKED, false));
      _dlg.chkDeviation.setSelected(Props.getBoolean(PREFS_KEY_DEVIATION_CHECKED, false));
      _dlg.chkMean.addActionListener(e -> onCheckMean());
      _dlg.chkDeviation.addActionListener(e -> onCheckDeviation());

      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.initLocation(_dlg, 250, 150);

      _dlg.setVisible(true);
   }

   private ArrayList<SumAndColumn> onCheckDeviation()
   {
      Props.putBoolean(PREFS_KEY_DEVIATION_CHECKED, _dlg.chkDeviation.isSelected());
      return update(_currentTableToSum);
   }

   private ArrayList<SumAndColumn> onCheckMean()
   {
      Props.putBoolean(PREFS_KEY_MEAN_CHECKED, _dlg.chkMean.isSelected());
      return update(_currentTableToSum);
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
         if(null == table)
         {
            return new ArrayList<>();
         }

         _currentTableToSum = table;

         ArrayList<SumAndColumn> sums = Calculator.calculateSums(table);
         ArrayList<MeanAndColumn> means = Calculator.calculateMeans(table);
         ArrayList<DeviationAndColumn> deviations = Calculator.calculateDeviations(table);

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

         if(_dlg.chkMean.isSelected())
         {
            Object[] meanRow = new Object[sums.size() + 1];
            meanRow[0] = s_stringMgr.getString("FunctionController.mean");
            for (int i = 0; i < means.size(); i++)
            {
               meanRow[i+1] = means.get(i).getMean();
            }
            rows.add(meanRow);
         }

         if(_dlg.chkDeviation.isSelected())
         {
            Object[] deviationRow = new Object[sums.size() + 1];
            deviationRow[0] = s_stringMgr.getString("FunctionController.deviation");
            for (int i = 0; i < deviations.size(); i++)
            {
               deviationRow[i+1] = deviations.get(i).getDeviation();
            }
            rows.add(deviationRow);
         }

         SimpleDataSet simpleDataSet = new SimpleDataSet(rows, columns.toArray(new ColumnDisplayDefinition[0]));

         _dlg.functionValuesTablePanel.show(simpleDataSet);

         return sums;
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
