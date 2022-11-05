package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.ComponentIndicator;

public class SqlResultTabHandle
{
   private final ResultTab _resultTab;
   private final SQLResultExecutorPanel _sqlResultExecutor;
   private ISession _session;

   public SqlResultTabHandle(ResultTab resultTab, SQLResultExecutorPanel sqlResultExecutor, ISession session)
   {
      _resultTab = resultTab;
      _sqlResultExecutor = sqlResultExecutor;
      _session = session;
   }

   public void indicateTabComponent()
   {
      for (int i = 0; i < _sqlResultExecutor.getTabbedPane().getTabCount(); i++)
      {
         if(_resultTab == _sqlResultExecutor.getTabbedPane().getComponentAt(i))
         {
            _sqlResultExecutor.getTabbedPane().getTabComponentAt(i);
            new ComponentIndicator().init(_sqlResultExecutor.getTabbedPane().getTabComponentAt(i), 2);
            break;
         }
      }
   }

   public void selectResultTab()
   {
      _sqlResultExecutor.getTabbedPane().setSelectedComponent(_resultTab);
   }

   public IDataSetViewer getSQLResultDataSetViewer()
   {
      return _resultTab.getSQLResultDataSetViewer();
   }

   public boolean isAlive()
   {
      if(_session.isClosed())
      {
         return false;
      }

      if(-1 == _sqlResultExecutor.getTabbedPane().indexOfComponent(_resultTab))
      {
         return false;
      }

      // TODO
      return true;
   }

   public boolean isResultsVisible()
   {
      return _sqlResultExecutor.getTabbedPane().isShowing();
   }
}
