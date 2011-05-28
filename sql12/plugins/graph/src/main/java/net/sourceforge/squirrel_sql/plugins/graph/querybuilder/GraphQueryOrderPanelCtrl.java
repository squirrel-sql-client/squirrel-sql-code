package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.plugins.graph.*;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GraphQueryOrderPanelCtrl
{
   private GraphQueryOrderPanel _panel;

   public GraphQueryOrderPanelCtrl(HideDockButtonHandler hideDockButtonHandler, OrderStructureXmlBean orderStructure)
   {
      _panel = new GraphQueryOrderPanel(hideDockButtonHandler);
      QueryOrderTableModel tableModel = new QueryOrderTableModel(orderStructure);
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

   public JPanel getGraphQueryOrderPanel()
   {
      return _panel;
   }

   public OrderStructure syncOrderCols(TableFramesModel tableFramesModel)
   {
      ArrayList<OrderCol> newOrderCols = new ArrayList<OrderCol>();

      for (TableFrameController tfc : tableFramesModel.getTblCtrls())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            if(columnInfo.getQueryData().isSorted())
            {
               newOrderCols.add(new OrderCol(tfc.getTableInfo().getSimpleName(), columnInfo));
            }
         }
      }

      QueryOrderTableModel tableModel = (QueryOrderTableModel) _panel.tblOrder.getModel();
      tableModel.updateOrderCols(newOrderCols);

      return new OrderStructure(tableModel.getOrderCols());
   }


   private void onMoveUp()
   {
      int[] selRows = _panel.tblOrder.getSelectedRows();

      if (null == selRows || 0 == selRows.length)
      {
         return;
      }

      QueryOrderTableModel model = (QueryOrderTableModel) _panel.tblOrder.getModel();
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

      QueryOrderTableModel model = (QueryOrderTableModel) _panel.tblOrder.getModel();
      int[] newSelRows = model.moveDown(selRows);



      DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) _panel.tblOrder.getSelectionModel();
      selectionModel.clearSelection();

      for (int newSelRow : newSelRows)
      {
         selectionModel.addSelectionInterval(newSelRow, newSelRow);
      }
      _panel.tblOrder.scrollRectToVisible(_panel.tblOrder.getCellRect(newSelRows[newSelRows.length-1], 0, false));

   }

   public OrderStructureXmlBean getOrderStructure()
   {
      QueryOrderTableModel model = (QueryOrderTableModel) _panel.tblOrder.getModel();
      return new OrderStructureXmlBean(model.getOrderCols());
   }
}
