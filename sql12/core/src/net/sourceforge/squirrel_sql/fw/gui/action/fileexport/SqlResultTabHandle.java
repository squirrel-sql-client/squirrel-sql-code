package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;
import net.sourceforge.squirrel_sql.fw.gui.ComponentIndicator;

public class SqlResultTabHandle
{
   private final ResultTab _resultTab;
   private final SQLResultExecutorPanel _sqlResultExecutor;

   public SqlResultTabHandle(ResultTab resultTab, SQLResultExecutorPanel sqlResultExecutor)
   {
      _resultTab = resultTab;
      _sqlResultExecutor = sqlResultExecutor;
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
}
