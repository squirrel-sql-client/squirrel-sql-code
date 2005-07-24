package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;

import java.awt.event.ActionEvent;


public class EscapeDateAction extends SquirrelAction implements ISQLPanelAction
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

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         _session = panel.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }

}
