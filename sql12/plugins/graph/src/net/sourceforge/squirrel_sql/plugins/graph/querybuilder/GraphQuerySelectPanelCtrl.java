package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;
import net.sourceforge.squirrel_sql.plugins.graph.TableFramesModel;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.SelectStructureXmlBean;

import javax.swing.*;
import java.util.ArrayList;

public class GraphQuerySelectPanelCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQuerySelectPanelCtrl.class);

   private SortedColumnsPanelCtrl _delegate;


   public GraphQuerySelectPanelCtrl(HideDockButtonHandler hideDockButtonHandler, SelectStructureXmlBean selectStructure)
   {
      _delegate = new SortedColumnsPanelCtrl(hideDockButtonHandler, new QuerySelectTableModel(selectStructure), s_stringMgr.getString("graph.GraphQueryOrderPanel.selectLabel"));
   }

   public JPanel getGraphQueryOrderPanel()
   {
      return _delegate.getSortedColumnsPanel();
   }

   public SelectStructure syncSelectCols(TableFramesModel tableFramesModel)
   {
      return new SelectStructure((SelectCol[]) _delegate.syncSortedColumns(SelectCol.class, getSelectCols(tableFramesModel)));
   }

   private ArrayList<SortedColumn> getSelectCols(TableFramesModel tableFramesModel)
   {
      ArrayList<SortedColumn> newOrderCols = new ArrayList<SortedColumn>();

      for (TableFrameController tfc : tableFramesModel.getTblCtrls())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            if(columnInfo.getQueryData().isInSelectClause())
            {
               newOrderCols.add(new SelectCol(tfc.getTableInfo().getSimpleName(), columnInfo));
            }
         }
      }
      return newOrderCols;
   }



   public SelectStructureXmlBean getSelectStructure()
   {
      return new SelectStructureXmlBean((SelectCol[]) _delegate.getSortedColumns(SelectCol.class));
   }
}
