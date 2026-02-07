package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.sql.Types;
import javax.swing.JTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class CellDataDialogState
{
   private final String _columnName;
   private final ColumnDisplayDefinition _colDef;
   private final Object _valueToDisplay;
   private final boolean _isModelEditable;
   private final boolean _pinned;
   private final CellDataDialogEditableState _cellDataDialogEditableState;

   public CellDataDialogState(String columnName, ColumnDisplayDefinition colDef, Object value, boolean pinned, boolean isModelEditable, JTable table, int rowIx, int colIx)
   {
      _columnName = columnName;
      _colDef = colDef;
      _valueToDisplay = value;
      _isModelEditable = isModelEditable;
      _pinned = pinned;

      _cellDataDialogEditableState = new CellDataDialogEditableState(table, rowIx, colIx);
   }

   public CellDataDialogState(String dialogTitlePostfix, String value)
   {
      _columnName = dialogTitlePostfix;
      _valueToDisplay = value;

      _colDef = new ColumnDisplayDefinition(100, dialogTitlePostfix);
      _colDef.setSqlType(Types.VARCHAR);
      _colDef.setSqlTypeName("VARCHAR");

      _isModelEditable = false;
      _pinned = false;
      _cellDataDialogEditableState = null;
   }

   public String getCellName()
   {
      return _columnName;
   }

   public boolean isEditable()
   {
      return _isModelEditable;
   }

   public ColumnDisplayDefinition getColDispDef()
   {
      return _colDef;
   }

   public Object getValueToDisplay()
   {
      return _valueToDisplay;
   }

   public boolean isPinned()
   {
      return _pinned;
   }

   public CellDataDialogEditableState getEditableState()
   {
      if(false == isEditable())
      {
         throw new IllegalStateException("Only to be called when updateable");
      }

      return _cellDataDialogEditableState;
   }
}
