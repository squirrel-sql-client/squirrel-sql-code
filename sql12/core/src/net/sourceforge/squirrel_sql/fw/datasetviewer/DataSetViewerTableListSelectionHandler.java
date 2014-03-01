package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;

public class DataSetViewerTableListSelectionHandler
{
   private ArrayList<RowSelectionListener> _rowSelectionListeners = new ArrayList<RowSelectionListener>();
   private DataSetViewerTablePanel.MyJTable _table;

   private int[] _formerSelectedIxs = new int[0];

   public DataSetViewerTableListSelectionHandler(DataSetViewerTablePanel.MyJTable table)
   {
      _table = table;
      _table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            fireListeners(_formerSelectedIxs, _table.getSelectedRows());
            _formerSelectedIxs = _table.getSelectedRows();
         }
      });
   }

   private void fireListeners(int[] formerSelectedIxs, int[] selectedIxs)
   {
      RowSelectionListener[] listeners = _rowSelectionListeners.toArray(new RowSelectionListener[_rowSelectionListeners.size()]);

      for (RowSelectionListener listener : listeners)
      {
         listener.selectionChanged(selectedIxs, formerSelectedIxs);
      }
   }

   public void addRowSelectionListener(RowSelectionListener rowSelectionListener)
   {
      _rowSelectionListeners.add(rowSelectionListener);
   }

   public void removeRowSelectionListener(RowSelectionListener rowSelectionListener)
   {
      _rowSelectionListeners.remove(rowSelectionListener);
   }
}
