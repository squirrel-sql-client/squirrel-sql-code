package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.ArrayList;

public class RowsWindowFrameRegistry
{
   private ArrayList<RowsWindowFrame> _rowsWindowFrames = new ArrayList<>();

   private int _counter = 0;

   public RowsWindowFrameRegistry(ISession session)
   {
      session.addSimpleSessionListener(() -> onSessionClosed());
   }

   private void onSessionClosed()
   {
      for (RowsWindowFrame rowsWindowFrame : _rowsWindowFrames.toArray(new RowsWindowFrame[_rowsWindowFrames.size()]))
      {
         rowsWindowFrame.close();
      }
   }

   public void add(RowsWindowFrame rowsWindowFrame)
   {
      rowsWindowFrame.setMyCounterId(++_counter);
      _rowsWindowFrames.add(rowsWindowFrame);
   }

   public void remove(RowsWindowFrame rowsWindowFrame)
   {
      _rowsWindowFrames.remove(rowsWindowFrame);
   }


   public ArrayList<RowsWindowFrame> getMatchingWindows(ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions)
   {
      ArrayList<RowsWindowFrame> ret = new ArrayList<>();

      for (RowsWindowFrame rowsWindowFrame : _rowsWindowFrames)
      {
         if(rowsWindowFrame.columnsMatch(columnDisplayDefinitions))
         {
            ret.add(rowsWindowFrame);
         }
      }

      return ret;
   }
}
