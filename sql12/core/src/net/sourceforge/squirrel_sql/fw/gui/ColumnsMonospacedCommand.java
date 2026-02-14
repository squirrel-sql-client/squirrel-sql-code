package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;

public class ColumnsMonospacedCommand
{
   private final DataSetViewerTable _table;

   public ColumnsMonospacedCommand(DataSetViewerTable table)
   {
      _table = table;
   }

   public void execute()
   {
      int[] selectedColumns = _table.getSelectedColumns();

      _table.getFontService().toggleMonoSpaced(selectedColumns);

   }
}
