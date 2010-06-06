package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;


public class DataScaleTableCellEditor implements TableCellEditor
{
   private DataScale _dataScale;

   public DataScaleTableCellEditor(DataScale dataScale)
   {
      _dataScale = dataScale;
   }


   @Override
   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
   {
      DataScale ds = (DataScale) value;
      return ds.getPanel();
   }

   @Override
   public Object getCellEditorValue()
   {
      return _dataScale;
   }

   @Override
   public boolean isCellEditable(EventObject anEvent)
   {
      return true;
   }

   @Override
   public boolean shouldSelectCell(EventObject anEvent)
   {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public boolean stopCellEditing()
   {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void cancelCellEditing()
   {
   }

   @Override
   public void addCellEditorListener(CellEditorListener l)
   {
   }

   @Override
   public void removeCellEditorListener(CellEditorListener l)
   {
   }
}
