package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.SelectStructureXmlBean;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Arrays;

public class QuerySelectTableModel extends SortedColumnsTableModel<SelectCol>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QuerySelectTableModel.class);

   private DefaultTableColumnModel _colModel;

   public QuerySelectTableModel(SelectStructureXmlBean selecStructure)
   {
      _colModel = new DefaultTableColumnModel();

      TableColumn col;

      col = new TableColumn(0);
      col.setHeaderValue(s_stringMgr.getString("graph.QueryOrderTableModel.Column"));
      col.setPreferredWidth(250);
      _colModel.addColumn(col);

      if(null != selecStructure)
      {
         addCols(Arrays.asList(selecStructure.getSelectCols()));
      }
   }

   @Override
   public Object getValueAt(int row, int column)
   {
      return getSortedCol(row).getQualifiedCol();
   }


   @Override
   public boolean isCellEditable(int row, int column)
   {
      return false;
   }


   @Override
   public int getColumnCount()
   {
      return 1;
   }

   protected void updateCol(SelectCol toBeUpdated, SelectCol update)
   {
   }


   public TableColumnModel getColumnModel()
   {
      return _colModel;
   }


}
