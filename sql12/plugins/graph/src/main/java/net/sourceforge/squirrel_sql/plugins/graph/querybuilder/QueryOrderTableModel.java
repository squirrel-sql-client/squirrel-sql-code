package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class QueryOrderTableModel extends DefaultTableModel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryOrderTableModel.class);

   private ArrayList<OrderCol> _orderCols = new ArrayList<OrderCol>();
   private DefaultTableColumnModel _colModel;

   public QueryOrderTableModel(OrderStructureXmlBean orderStructure)
   {
      _colModel = new DefaultTableColumnModel();

      TableColumn col;

      col = new TableColumn(0);
      col.setHeaderValue(s_stringMgr.getString("graph.QueryOrderTableModel.Column"));
      col.setPreferredWidth(250);
      _colModel.addColumn(col);

      col = new TableColumn(1);
      col.setHeaderValue(s_stringMgr.getString("graph.QueryOrderTableModel.Ascending"));

      final JCheckBox rendererCheckBox = new JCheckBox();
      DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer()
      {
         @Override
         public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
         {
            rendererCheckBox.setSelected((Boolean) value);
            return rendererCheckBox;
         }
      };

      col.setCellRenderer(cellRenderer);
      _colModel.addColumn(col);

      if(null != orderStructure)
      {
         _orderCols.addAll(Arrays.asList(orderStructure.getOrderCols()));
      }
   }


   @Override
   public Object getValueAt(int row, int column)
   {
      if(0 == column)
      {
         return _orderCols.get(row).getQualifiedCol();
      }
      else
      {
         return _orderCols.get(row).isAscending();
      }
   }

   @Override
   public int getRowCount()
   {
      if(null == _orderCols)
      {
         // This if is here because the method is called from the base class constructor.
         return 0;
      }

      return _orderCols.size();
   }

   @Override
   public boolean isCellEditable(int row, int column)
   {
      return false;
   }


   @Override
   public int getColumnCount()
   {
      return 2;
   }

   public void updateOrderCols(ArrayList<OrderCol> newOrderCols)
   {
      ArrayList<OrderCol> toRemove = new ArrayList<OrderCol>();
      ArrayList<OrderCol> toAdd = new ArrayList<OrderCol>();

      for (OrderCol newOrderCol : newOrderCols)
      {
         boolean found = false;
         for (OrderCol orderCol : _orderCols)
         {
            if(orderCol.equals(newOrderCol))
            {
               updateCol(orderCol, newOrderCol);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            toAdd.add(newOrderCol);
         }
      }

      for (OrderCol orderCol : _orderCols)
      {
         boolean found = false;
         for (OrderCol newOrderCol : newOrderCols)
         {
            if(orderCol.equals(newOrderCol))
            {
               updateCol(orderCol, newOrderCol);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            toRemove.add(orderCol);
         }
      }

      _orderCols.removeAll(toRemove);
      _orderCols.addAll(toAdd);

      fireTableDataChanged();
   }

   private void updateCol(OrderCol toBeUpdated, OrderCol update)
   {
      toBeUpdated.setAscending(update.isAscending());
      toBeUpdated.setAggregated(update.isAggregated());
   }

   public TableColumnModel getColumnModel()
   {
      return _colModel;
   }

   public int[] moveUp(int[] selRows)
   {
      for (int i : selRows)
      {
         if (0 == i)
         {
            return selRows;
         }
      }

      int[] newSelRows = new int[selRows.length];
      for (int i = 0; i < selRows.length; ++i)
      {
         OrderCol col = _orderCols.remove(selRows[i]);
         newSelRows[i] = selRows[i] - 1;
         _orderCols.add(newSelRows[i], col);
      }

      return newSelRows;
   }

   public int[] moveDown(int[] selRows)
   {
      for (int i : selRows)
      {
         if (_orderCols.size() - 1 == i)
         {
            return selRows;
         }
      }

      int[] newSelIx = new int[selRows.length];
      for (int i = selRows.length - 1; i >= 0; --i)
      {
         OrderCol col = (OrderCol) _orderCols.remove(selRows[i]);
         newSelIx[i] = selRows[i] + 1;
         _orderCols.add(newSelIx[i], col);
      }

      return newSelIx;
   }

   public OrderCol[] getOrderCols()
   {
      return _orderCols.toArray(new OrderCol[_orderCols.size()]);
   }
}
