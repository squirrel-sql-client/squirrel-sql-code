package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;

import javax.swing.JTable;
import java.util.ArrayList;

public class RowSelectionTableUtil
{
   public static DataSetViewerTableModel getActualTableModel(JTable table)
   {
      return (DataSetViewerTableModel) ((SortableTableModel) table.getModel()).getActualModel();
   }

   public static DataSetViewerTableModel getActualTableModel(DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      return getActualTableModel(dataSetViewerTablePanel.getTable());
   }

   public static ArrayList<Object[]> getSelectedRows(JTable table)
   {
      ArrayList<Object[]> rows = new ArrayList<>();


      SortableTableModel sortableTableModel = (SortableTableModel) table.getModel();

      int[] selectedRows = table.getSelectedRows();

      DataSetViewerTableModel tableModel = getActualTableModel(table);

      for (int i = 0; i < selectedRows.length; i++)
      {
         Object[] row = tableModel.getRowAt(sortableTableModel.transformToModelRow(selectedRows[i]));
         rows.add(row);
      }
      return rows;
   }
}
