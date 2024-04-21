package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;

public class SaveAndManageGroupOfSavedSessionsAction extends SquirrelAction implements ISessionAction
{

   public SaveAndManageGroupOfSavedSessionsAction()
   {
      super(Main.getApplication());
      setEnabled(false);
   }

   @Override
   public void setSession(ISession session)
   {
      setEnabled(2 <= Main.getApplication().getSessionManager().getOpenSessions().size());
   }


   @Override
   public void actionPerformed(ActionEvent evt)
   {
      new GroupOfSavedSessionsCtrl();
   }

}
