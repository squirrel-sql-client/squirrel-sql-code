package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;
import net.sourceforge.squirrel_sql.plugins.graph.TableFramesModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SortedColumnsPanelCtrl
{

   private SortedColumnsOrderPanel _panel;

   public SortedColumnsPanelCtrl(HideDockButtonHandler hideDockButtonHandler, SortedColumnsTableModel sortedColumnsTableModel, String labelText)
   {
      _panel = new SortedColumnsOrderPanel(hideDockButtonHandler, labelText);
      SortedColumnsTableModel tableModel = sortedColumnsTableModel;
      _panel.tblOrder.setModel(tableModel);
      _panel.tblOrder.setColumnModel(tableModel.getColumnModel());

      _panel.btnUp.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveUp();
         }
      });

      _panel.btnDown.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onMoveDown();
         }
      });
   }

   public JPanel getSortedColumnsPanel()
   {
      return _panel;
   }

   public SortedColumn[] syncSortedColumns(Class<? extends SortedColumn> clazz, ArrayList<SortedColumn> sortedCols)
   {

      SortedColumnsTableModel tableModel = (SortedColumnsTableModel) _panel.tblOrder.getModel();
      tableModel.updateOrderCols(sortedCols);
      return tableModel.getSortedCols(clazz);
   }

   private void onMoveUp()
   {
      int[] selRows = _panel.tblOrder.getSelectedRows();

      if (null == selRows || 0 == selRows.length)
      {
         return;
      }

      SortedColumnsTableModel model = (SortedColumnsTableModel) _panel.tblOrder.getModel();
      int[] newSelRows = model.moveUp(selRows);


      DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) _panel.tblOrder.getSelectionModel();
      selectionModel.clearSelection();

      for (int newSelRow : newSelRows)
      {
         selectionModel.addSelectionInterval(newSelRow, newSelRow);
      }
      _panel.tblOrder.scrollRectToVisible(_panel.tblOrder.getCellRect(newSelRows[0], 0, false));
   }

   private void onMoveDown()
   {
      int[] selRows = _panel.tblOrder.getSelectedRows();

      if (null == selRows || 0 == selRows.length)
      {
         return;
      }

      SortedColumnsTableModel model = (SortedColumnsTableModel) _panel.tblOrder.getModel();
      int[] newSelRows = model.moveDown(selRows);



      DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) _panel.tblOrder.getSelectionModel();
      selectionModel.clearSelection();

      for (int newSelRow : newSelRows)
      {
         selectionModel.addSelectionInterval(newSelRow, newSelRow);
      }
      _panel.tblOrder.scrollRectToVisible(_panel.tblOrder.getCellRect(newSelRows[newSelRows.length-1], 0, false));

   }

   public SortedColumn[] getSortedColumns(Class<? extends SortedColumn> clazz)
   {
      SortedColumnsTableModel tableModel = (SortedColumnsTableModel) _panel.tblOrder.getModel();
      return tableModel.getSortedCols(clazz);
   }

}
