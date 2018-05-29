package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.rowcolor.RowColorHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FindColorHandler;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import javax.swing.JTable;
import java.awt.Color;


/**
 * Central class to color result tables.
 *
 * The several functions that use coloring are explicitly
 * named and declared here so this class can clearly define coloring priorities.
 */
public class ColoringService
{
   private RowColorHandler _rowColorHandler;
   private FindColorHandler _findColorHandler;
   private MarkDuplicatesHandler _markDuplicatesHandler;

   public ColoringService(DataSetViewerTable dataSetViewerTable)
   {
      _rowColorHandler = new RowColorHandler(dataSetViewerTable);
      _findColorHandler = new FindColorHandler();
      _markDuplicatesHandler = new MarkDuplicatesHandler(dataSetViewerTable);
   }

   public void colorCell(CellRenderer cellRenderer, IDataTypeComponent dataTypeObject, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
   {
      if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX ==  table.getColumnModel().getColumn(column).getModelIndex())
      {
         return;
      }

      Color customBackground = null;

      // if text cannot be edited in the cell but can be edited in
      //				the popup, show that by changing the text colors.
      if (dataTypeObject != null &&
            dataTypeObject.isEditableInCell(value) == false &&
            dataTypeObject.isEditableInPopup(value) == true)
      {
         // Use a CYAN background to indicate that the cell is
         // editable in the popup
         customBackground = SquirrelConstants.MULTI_LINE_CELL_COLOR;
      }
      else
      {
         // since the previous entry might have changed the color,
         // we need to reset the color back to default value for table cells,
         // taking into account whether the cell is selected or not.
         customBackground = _rowColorHandler.getBackgroundForRow(row, isSelected);
      }

      Color findBackground = _findColorHandler.getBackgroundForCell(row, column);
      if(null != findBackground)
      {
         customBackground = findBackground;
      }

      Color markDuplicateBackground = _markDuplicatesHandler.getBackgroundForCell(row, column, value);
      if(null != markDuplicateBackground)
      {
         customBackground = markDuplicateBackground;
      }



      if (null != customBackground)
      {
         cellRenderer.setBackground(customBackground);
      }

   }

   public RowColorHandler getRowColorHandler()
   {
      return _rowColorHandler;
   }

   public FindColorHandler getFindColorHandler()
   {
      return _findColorHandler;
   }

   public MarkDuplicatesHandler getMarkDuplicatesHandler()
   {
      return _markDuplicatesHandler;
   }
}
