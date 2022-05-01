package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesHandler;
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
   private UserColorHandler _userColorHandler;
   private DataSetViewerTable _dataSetViewerTable;
   private FindColorHandler _findColorHandler;
   private MarkDuplicatesHandler _markDuplicatesHandler;
   private NullValueColorHandler _nullValueColorHandler;
   private ColoringCallback _coloringCallback;

   public ColoringService(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
      _userColorHandler = new UserColorHandler(dataSetViewerTable);
      _findColorHandler = new FindColorHandler();
      _markDuplicatesHandler = new MarkDuplicatesHandler(dataSetViewerTable);
      _nullValueColorHandler = new NullValueColorHandler(_dataSetViewerTable);
   }

   public void colorCell(CellRenderer cellRenderer, IDataTypeComponent dataTypeObject, JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIx, int columnIx)
   {
      if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX ==  table.getColumnModel().getColumn(columnIx).getModelIndex())
      {
         return;
      }

      Color customBackground = null;

      // if text cannot be edited in the cell but can be edited in
      // the popup, shows that by changing the text colors.
      // Note: isEditableInCell() and isEditableInPopup() may result in reading Blob types, depending on configurations in preferences.
      //       That is not the problem because when the coloring is done the cell will be shown anyway and thus the read will need occur anyway.
      if(dataTypeObject != null &&
         dataTypeObject.isEditableInCell(value) == false &&
         dataTypeObject.isEditableInPopup(value) == true)
      {
         // Use a CYAN background to indicate that the cell is
         // editable in the popup
         customBackground = SquirrelConstants.MULTI_LINE_CELL_COLOR;
      }
      else if(null == value)
      {
         if(false == isSelected && _nullValueColorHandler.isColorNullValues())
         {
            customBackground = _nullValueColorHandler.getNullValueColor();
         }
      }

      ///////////////////////////////////////////////////////////////////////////////////////////////////////////
      // When adjusting check getExcelExportRelevantColor(), too.
      Color userColor = _userColorHandler.getBackground(columnIx, rowIx, isSelected);
      if(null != userColor)
      {
         customBackground = userColor;
      }

      Color markDuplicateBackground = _markDuplicatesHandler.getBackgroundForCell(rowIx, columnIx, value);
      if(null != markDuplicateBackground)
      {
         customBackground = markDuplicateBackground;
      }

      Color findBackground = _findColorHandler.getBackgroundForCell(rowIx, columnIx);
      if(null != findBackground)
      {
         customBackground = findBackground;
      }
      //
      ////////////////////////////////////////////////////////////////////////////////////////////////////////////

      if(null != _coloringCallback)
      {
         Color buf = _coloringCallback.getCellColor(rowIx, columnIx, isSelected);

         if(null != buf)
         {
            customBackground = buf;
         }
      }

      if (null != customBackground)
      {
         cellRenderer.setBackground(customBackground);
      }
      else
      {
         if (isSelected)
         {
            cellRenderer.setBackground(_dataSetViewerTable.getSelectionBackground());
         }
         else
         {
            cellRenderer.setBackground(_dataSetViewerTable.getBackground());
         }
      }
   }

   public UserColorHandler getUserColorHandler()
   {
      return _userColorHandler;
   }

   public FindColorHandler getFindColorHandler()
   {
      return _findColorHandler;
   }

   public MarkDuplicatesHandler getMarkDuplicatesHandler()
   {
      return _markDuplicatesHandler;
   }

   public void setColoringCallback(ColoringCallback coloringCallback)
   {
      _coloringCallback = coloringCallback;
   }

   public Color getExcelExportRelevantColor(int rowIx, int columnIx, Object cellValue)
   {
      Color customBackground = null;

      Color userColor = _userColorHandler.getBackground(columnIx, rowIx, false);
      if(null != userColor)
      {
         customBackground = userColor;
      }

      Color markDuplicateBackground = _markDuplicatesHandler.getBackgroundForCell(rowIx, columnIx, cellValue);
      if(null != markDuplicateBackground)
      {
         customBackground = markDuplicateBackground;
      }

      Color findBackground = _findColorHandler.getBackgroundForCell(rowIx, columnIx);
      if(null != findBackground)
      {
         customBackground = findBackground;
      }
      return customBackground;
   }
}
