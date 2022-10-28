package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.event.SQLResultExecuterTabEvent;

import java.util.ArrayList;
import java.util.List;

public class SQLPanelListenerManager
{
   private List<ISQLPanelListener> _sqlPanelListener = new ArrayList<>();
   private List<ISQLResultExecuterTabListener> _sqlResultExecuterTabListener = new ArrayList<>();

   public synchronized void addSQLPanelListener(ISQLPanelListener lis)
   {
      if (lis == null)
      {
         throw new IllegalArgumentException("null ISQLPanelListener passed");
      }
      _sqlPanelListener.add(lis);
   }

   public synchronized void removeSQLPanelListener(ISQLPanelListener lis)
   {
      _sqlPanelListener.remove(lis);
   }

   public void addExecuterTabListener(ISQLResultExecuterTabListener lis)
   {
      if (lis == null)
      {
         throw new IllegalArgumentException("ISQLExecutionListener == null");
      }
      _sqlResultExecuterTabListener.add(lis);
   }

   public synchronized void removeExecuterTabListener(ISQLResultExecuterTabListener lis)
   {
      if (lis == null)
      {
         throw new IllegalArgumentException("ISQLResultExecuterTabListener == null");
      }
      _sqlResultExecuterTabListener.remove(lis);
   }

   public void fireSQLEntryAreaInstalled()
   {
      SQLPanelEvent evt = new SQLPanelEvent();
      for (ISQLPanelListener isqlPanelListener : _sqlPanelListener.toArray(new ISQLPanelListener[0]))
      {
         isqlPanelListener.sqlEntryAreaInstalled(evt);
      }
   }

   public void fireSQLEntryAreaClosed()
   {
      SQLPanelEvent evt = new SQLPanelEvent();
      for (ISQLPanelListener isqlPanelListener : _sqlPanelListener.toArray(new ISQLPanelListener[0]))
      {
         isqlPanelListener.sqlEntryAreaClosed(evt);
      }
   }

   public void fireExecuterTabAdded(ISQLResultExecutor exec)
   {
      SQLResultExecuterTabEvent evt = new SQLResultExecuterTabEvent(exec);
      for (ISQLResultExecuterTabListener isqlResultExecuterTabListener : _sqlResultExecuterTabListener.toArray(new ISQLResultExecuterTabListener[0]))
      {
         isqlResultExecuterTabListener.executerTabAdded(evt);
      }
   }

   public void fireExecuterTabActivated(ISQLResultExecutor exec)
   {
      SQLResultExecuterTabEvent evt = new SQLResultExecuterTabEvent(exec);
      for (ISQLResultExecuterTabListener isqlResultExecuterTabListener : _sqlResultExecuterTabListener.toArray(new ISQLResultExecuterTabListener[0]))
      {
         isqlResultExecuterTabListener.executerTabActivated(evt);
      }
   }

   public void fireSQLPanelParentClosing()
   {
      for (ISQLPanelListener isqlPanelListener : _sqlPanelListener)
      {
         isqlPanelListener.panelParentClosing();
      }
   }
}
