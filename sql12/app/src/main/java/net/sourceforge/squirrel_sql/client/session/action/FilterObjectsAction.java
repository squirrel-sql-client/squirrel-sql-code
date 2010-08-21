package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FilterObjectsAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public FilterObjectsAction(IApplication app)
   {
      super(app);
   }

   public void setSession(ISession session)
   {
      _session = session;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_session != null)
      {
         new SessionPropertiesCommand(_session, 1).execute();
      }
   }
}
