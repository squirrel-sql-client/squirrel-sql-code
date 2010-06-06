package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import javax.swing.*;
import java.awt.*;

public class ScaleTableState
{
   private int _sortedColumn;
   private boolean _sortedAscending;
   private Rectangle _visibleRect;
   private int[] _columnWidths;
   private int _selectedRow;

   public ScaleTableState(DataScaleTable table)
   {
      _sortedColumn = table.getSortableTableModel().getSortedColumn();
      _sortedAscending = table.getSortableTableModel().isSortedAscending();

      _columnWidths = new int[table.getColumnModel().getColumnCount()];

      _selectedRow = table.getSelectedRow();

      for (int i = 0; i < _columnWidths.length; i++)
      {
         _columnWidths[i] = table.getColumnModel().getColumn(i).getWidth();
      }
      _visibleRect = table.getVisibleRect();



   }

   public void apply(final DataScaleTable table)
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            doApply(table);
         }
      };

      SwingUtilities.invokeLater(runnable);
   }

   private void doApply(DataScaleTable table)
   {
      if(-1 != _sortedColumn)
      {
         table.getSortableTableModel().sortByColumn(_sortedColumn, _sortedAscending);
      }


      for (int i = 0; i < _columnWidths.length; i++)
      {
         table.getColumnModel().getColumn(i).setPreferredWidth(_columnWidths[i]);
      }

      if(-1 != _selectedRow)
      {
         table.getSelectionModel().setSelectionInterval(_selectedRow, _selectedRow);
      }


      table.scrollRectToVisible(_visibleRect);
   }
}
