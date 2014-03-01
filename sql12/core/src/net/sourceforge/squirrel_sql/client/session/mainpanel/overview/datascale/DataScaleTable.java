package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class DataScaleTable extends SortableTable
{
   private List<Object[]> _allRows;
   private ColumnDisplayDefinition[] _columnDefinitions;

   private DataScaleTable _parent;
   private DataScaleTable _kid;
   private DataSetViewerTablePanel _kidSimpleTable;

   public DataScaleTable(DataScaleTableModel dataScaleTableModel, List<Object[]> allRows, ColumnDisplayDefinition[] columnDefinitions)
   {
      super(dataScaleTableModel);
      _allRows = allRows;
      _columnDefinitions = columnDefinitions;

      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      new DataScaleTablePopupHandler(this);

      new DataScaleTableColumnWidthsPersister(getTableHeader());
   }


   @Override
   public TableCellRenderer getCellRenderer(int row, int column)
   {
      String headerValue = (String) getColumnModel().getColumn(column).getHeaderValue();
      if(DataScaleTableModel.COL_NAME_COLUMN.equals(headerValue))
      {
         return super.getCellRenderer(row, column);
      }
      else
      {
         DataScaleTableModel model = getDataScaleTableModel();
         return createScaleDataCellRenderer(model);
      }
   }

   @Override
   public TableCellEditor getCellEditor(int row, int column)
   {
      String headerValue = (String) getColumnModel().getColumn(column).getHeaderValue();
      if(DataScaleTableModel.COL_NAME_COLUMN.equals(headerValue))
      {
         return super.getCellEditor(row, column);
      }
      else
      {
         DataScaleTableModel model = getDataScaleTableModel();

         SortableTableModel sortableTableModel = (SortableTableModel) getModel();
         return new DataScaleTableCellEditor(model.getDataScaleAt(sortableTableModel.transfromToModelRow(row)));
      }
   }

   public DataScaleTableModel getDataScaleTableModel()
   {
      TableModel ret = ((SortableTableModel) getModel()).getActualModel();

      while(ret instanceof SortableTableModel)
      {
         ret = ((SortableTableModel)ret).getActualModel();
      }

      return (DataScaleTableModel) ret;
   }


   private TableCellRenderer createScaleDataCellRenderer(final DataScaleTableModel dataScaleTableModel)
   {
      return new TableCellRenderer()
      {
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
         {
            SortableTableModel sortableTableModel = (SortableTableModel) getModel();
            return dataScaleTableModel.getDataScaleAt(sortableTableModel.transfromToModelRow(row)).getPanel();
         }
      };
   }

   public List<Object[]> getAllRows()
   {
      return _allRows;
   }

   public ColumnDisplayDefinition[] getColumnDisplayDefinitions()
   {
      return _columnDefinitions;
   }

   public void setParentScaleTable(DataScaleTable parent)
   {
      TableState st = new TableState(parent);
      st.apply(this);

      _parent = parent;

      _parent.setKidScaleTable(this);
   }

   public void setKidScaleTable(DataScaleTable kid)
   {
      _kid = kid;
      _kidSimpleTable = null;
   }

   public DataScaleTable getKidScaleTable()
   {
      return _kid;
   }

   public DataScaleTable getParentScaleTable()
   {
      return _parent;
   }

   public void setKidSimpleTable(DataSetViewerTablePanel simpleTable)
   {
      _kidSimpleTable = simpleTable;
      _kid = null;
   }

   public DataSetViewerTablePanel getKidSimpleTable()
   {
      return _kidSimpleTable;
   }
}
