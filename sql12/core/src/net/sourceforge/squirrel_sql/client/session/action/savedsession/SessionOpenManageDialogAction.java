package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;

/**
 * For Session menu only as substitute for {@link SessionManageAction}.
 */
public class SessionOpenManageDialogAction extends SquirrelAction implements ISessionAction
{

   public SessionOpenManageDialogAction()
   {
      super(Main.getApplication());
   }

   public void actionPerformed(ActionEvent ae)
   {
      ((SessionOpenAction)Main.getApplication().getActionCollection().get(SessionOpenAction.class)).onOpenSavedSessionsMoreDialog();
   }

   @Override
   public void setSession(ISession session)
   {
   }

}