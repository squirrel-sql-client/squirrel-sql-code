package net.sourceforge.squirrel_sql.fw.gui.action.colorrows;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow.CopyRowsToNewWindowService;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class CopyColoredRowsToNewWindowCommand
{
   private final DataSetViewerTable _table;
   private final ISession _session;

   public CopyColoredRowsToNewWindowCommand(DataSetViewerTable table, ISession session)
   {
      _table = table;
      _session = session;
   }

   public void execute()
   {

      Set<Integer> modelRowIndexes = _table.getColoringService().getUserColorHandler().getAllColoredRows();

      TreeMap<Integer, Object[]> rowsSortedByViewIndex = new TreeMap<>();

      for (Integer modelRowIndex : modelRowIndexes)
      {
         rowsSortedByViewIndex.put(_table.getSortableTableModel().transformToViewRow(modelRowIndex), _table.getDataSetViewerTableModel().getRowAt(modelRowIndex));
      }

      new CopyRowsToNewWindowService(_table, _session).copyRowsToNewWindow(new ArrayList<>(rowsSortedByViewIndex.values()));
   }
}
