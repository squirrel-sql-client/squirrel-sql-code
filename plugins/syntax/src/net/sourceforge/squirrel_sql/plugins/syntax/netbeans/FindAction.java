package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FindAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   public FindAction(IApplication app, SyntaxPluginResources rsrc)
			throws IllegalArgumentException
	{
		super(app, rsrc);
	}

	public void actionPerformed(ActionEvent evt)
	{
      if(null != _session)
      {

         ISQLEntryPanel sqlEntryPanel = _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();

         if(false == sqlEntryPanel instanceof NetbeansSQLEntryPanel)
         {
            String msg =
               "Find is only available when the Netbeans editor is used.\n" +
               "See menu File --> New Session Properties --> Tab Syntax";
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            return;
         }

         NetbeansSQLEntryPanel nsep = (NetbeansSQLEntryPanel) sqlEntryPanel;
         nsep.showFindDialog(evt);
      }

	}

	public void setSession(ISession session)
	{
      _session = session;
	}
}
