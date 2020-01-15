package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.*;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;

import javax.swing.*;
import java.util.ArrayList;

public class GraphQueryOrderPanelCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQueryOrderPanelCtrl.class);

   private SortedColumnsPanelCtrl _delegate;


   public GraphQueryOrderPanelCtrl(HideDockButtonHandler hideDockButtonHandler, OrderStructureXmlBean orderStructure)
   {
      _delegate = new SortedColumnsPanelCtrl(hideDockButtonHandler, new QueryOrderTableModel(orderStructure), s_stringMgr.getString("graph.GraphQueryOrderPanel.orderLabel"));
   }

   public JPanel getGraphQueryOrderPanel()
   {
      return _delegate.getSortedColumnsPanel();
   }

   public OrderStructure syncOrderCols(TableFramesModel tableFramesModel)
   {
      return new OrderStructure((OrderCol[]) _delegate.syncSortedColumns(OrderCol.class, getOrderedCols(tableFramesModel)));
   }

   private ArrayList<SortedColumn> getOrderedCols(TableFramesModel tableFramesModel)
   {
      ArrayList<SortedColumn> newOrderCols = new ArrayList<SortedColumn>();

      for (TableFrameController tfc : tableFramesModel.getTblCtrls())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            if(columnInfo.getQueryData().isSorted())
            {
               newOrderCols.add(new OrderCol(tfc, columnInfo));
            }
         }
      }
      return newOrderCols;
   }


   public OrderStructureXmlBean getOrderStructure()
   {
      return new OrderStructureXmlBean((OrderCol[]) _delegate.getSortedColumns(OrderCol.class));
   }
}
