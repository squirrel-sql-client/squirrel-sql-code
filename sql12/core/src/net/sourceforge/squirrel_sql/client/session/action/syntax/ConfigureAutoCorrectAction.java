package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;


public class ConfigureAutoCorrectAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public ConfigureAutoCorrectAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public void actionPerformed(ActionEvent e)
   {
      new AutoCorrectController(SessionUtils.getOwningFrame(_session));
   }

   public void setSession(ISession session)
   {
      _session = session;

      setEnabled(null != _session);
   }
}
