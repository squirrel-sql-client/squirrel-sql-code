package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecutor;

public class ResultTabProvider
{
   private ISQLPanelAPI _panel;
   private IResultTab _resultTab;

   public ResultTabProvider(IResultTab resultTab)
   {
      _resultTab = resultTab;

      if(null != _resultTab)
      {
         _resultTab.addResultTabCloseListener(() -> _resultTab = null);
      }
   }

   public boolean setSQLPanelAPI(ISQLPanelAPI panel)
   {
      _panel = panel;
      return null != _panel;
   }

   public IResultTab getResultTab()
   {
      if (null != _resultTab)
      {
         return _resultTab;
      }

      if (_panel != null)
      {
         ISQLResultExecutor sqlResultExecuter = _panel.getSQLResultExecuter();
         if (sqlResultExecuter != null)
         {
            IResultTab selectedResultTab = sqlResultExecuter.getSelectedResultTab();
            if (selectedResultTab != null)
            {
               return selectedResultTab;
            }
         }
      }

      return null;
   }

   public boolean hasResultTab()
   {
      return null != getResultTab();
   }

   public ISQLPanelAPI getSqlPanelApiOrNull()
   {
      return _panel;
   }
}
