package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class QueryOrderTableModel extends SortedColumnsTableModel<OrderCol>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryOrderTableModel.class);

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
      col.setHeaderValue(s_stringMgr.getString("graph.QueryOrderTableModel.Descending"));

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
         addCols(Arrays.asList(orderStructure.getOrderCols()));
      }
   }

   @Override
   public Object getValueAt(int row, int column)
   {
      if(0 == column)
      {
         return getSortedCol(row).getQualifiedCol();
      }
      else
      {
         return getSortedCol(row).isDescending();
      }
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

   protected void updateCol(OrderCol toBeUpdated, OrderCol update)
   {
      toBeUpdated.setDescending(update.isDescending());
      toBeUpdated.setAggregated(update.isAggregated());
   }


   public TableColumnModel getColumnModel()
   {
      return _colModel;
   }


}
