package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ReplaceAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public ReplaceAction(IApplication app, SyntaxPluginResources rsrc)
			throws IllegalArgumentException
	{
		super(app, rsrc);
	}

	public void actionPerformed(ActionEvent evt)
	{
      if(null != _session)
      {
         if(false == _session.getSQLEntryPanel() instanceof NetbeansSQLEntryPanel)
         {
            String msg =
               "Replace is only available when the Netbeans editor is used.\n" +
               "See menu File --> New Session Properties --> Tab Syntax";
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            return;
         }

         NetbeansSQLEntryPanel nsep = (NetbeansSQLEntryPanel) _session.getSQLEntryPanel();
         nsep.showReplaceDialog(evt);


      }

	}

	public void setSession(ISession session)
	{
      _session = session;
	}
}
