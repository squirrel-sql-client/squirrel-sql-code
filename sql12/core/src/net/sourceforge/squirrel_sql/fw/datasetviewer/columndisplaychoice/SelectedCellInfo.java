package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class SelectedCellInfo
{
   private final ColumnDisplayDefinition _columnDisplayDefinition;
   private boolean _hasSelectedCell;
   private boolean _extTableColumnCellSelected;

   public SelectedCellInfo(ColumnDisplayDefinition columnDisplayDefinition)
   {
      _columnDisplayDefinition = columnDisplayDefinition;
      _hasSelectedCell = true;
      _extTableColumnCellSelected = true;
   }

   public SelectedCellInfo()
   {
      this(null);
   }

   public boolean hasSelectedCell()
   {
      return _hasSelectedCell;
   }

   public boolean isExtTableColumnCellSelected()
   {
      return _extTableColumnCellSelected;
   }

   public ColumnDisplayDefinition getSelectedColumnsDisplayDefinition()
   {
      return _columnDisplayDefinition;
   }

   public SelectedCellInfo setHasSelectedCell(boolean b)
   {
      _hasSelectedCell = b;
      return this;
   }

   public SelectedCellInfo setExtTableColumnCellSelected(boolean b)
   {
      _extTableColumnCellSelected = b;
      return this;
   }
}
