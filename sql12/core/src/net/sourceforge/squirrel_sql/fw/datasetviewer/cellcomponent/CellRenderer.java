package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.ColoringService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * The base component of a DefaultTableCellRenderer is a JLabel.
 *
 * @author gwg
 */
public final class CellRenderer extends DefaultTableCellRenderer implements SquirrelTableCellRenderer
{
   private final IDataTypeComponent _dataTypeObject;
   private ColoringService _coloringService;

   CellRenderer(IDataTypeComponent dataTypeObject)
   {
      _dataTypeObject = dataTypeObject;
   }

   /**
    * Returns the default table cell renderer - overridden from DefaultTableCellRenderer.
    *
    * @param table      the <code>JTable</code>
    * @param value      the value to assign to the cell at
    *                   <code>[row, column]</code>
    * @param isSelected true if cell is selected
    * @param hasFocus   true if cell has focus
    * @param row        the row of the cell to render
    * @param column     the column of the cell to render
    * @return the default table cell renderer
    */
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
   {
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      label.putClientProperty("html.disable", Boolean.TRUE);

      _coloringService.colorCell(this, _dataTypeObject, table, value, isSelected, hasFocus, row, column);

      return label;
   }


   public void setValue(Object value)
   {
      // default behavior if no DataType object is to use the
      // DefaultColumnRenderer with no modification.
      if (_dataTypeObject != null)
         super.setValue(_dataTypeObject.renderObject(value));
      else super.setValue(DefaultColumnRenderer.getInstance().renderObject(value));
   }

   public Object renderValue(Object value)
   {
      if (_dataTypeObject != null)
      {
         return _dataTypeObject.renderObject(value);
      }
      else
      {
         return DefaultColumnRenderer.getInstance().renderObject(value);
      }
   }

   public void setColoringService(ColoringService coloringService)
   {
      _coloringService = coloringService;
   }
}
