package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class ExecuteSqlInAllSessionsAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public ExecuteSqlInAllSessionsAction()
   {
      super(Main.getApplication());
      setEnabled(false);
   }

   @Override
   public void actionPerformed(ActionEvent evt)
   {
      String sqlToBeExecuted = _panel.getSQLScriptToBeExecuted();

      _panel.executeSQL(sqlToBeExecuted);

      for(ISession openSession : Main.getApplication().getSessionManager().getOpenSessions())
      {
         if(openSession == _panel.getSession())
         {
            continue;
         }

         openSession.getSessionInternalFrame().getMainSQLPanelAPI().executeSQL(sqlToBeExecuted);
      }

      System.out.println("ExecuteSqlInAllSessionsAction.actionPerformed END");
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && getApplication().getSquirrelPreferences().isAllowRunSqlInAllSessions());
   }
}
