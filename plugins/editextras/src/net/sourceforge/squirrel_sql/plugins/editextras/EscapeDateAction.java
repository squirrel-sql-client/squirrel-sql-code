package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;


public class EscapeDateAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public EscapeDateAction(IApplication app, net.sourceforge.squirrel_sql.fw.util.Resources rsrc)
   {
      super(app, rsrc);
   }

   public void actionPerformed(ActionEvent e)
   {
      new EscapeDateController(_session, getApplication().getMainFrame());
   }

   public void setSession(ISession session)
   {
      _session = session;
   }
}
