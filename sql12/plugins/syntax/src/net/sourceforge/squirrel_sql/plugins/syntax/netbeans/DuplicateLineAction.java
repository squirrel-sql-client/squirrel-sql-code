package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;


public class DuplicateLineAction  extends SquirrelAction implements ISQLPanelAction
{
   private ISession _session;

   public DuplicateLineAction(IApplication app, SyntaxPluginResources rsrc)
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
               "Duplicate line is only available when the Netbeans editor is used.\n" +
               "See menu File --> New Session Properties --> Tab Syntax";
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            return;
         }


         new NetbeansDuplicateLineAction().actionPerformed(evt, (JTextComponent)sqlEntryPanel.getTextComponent());
      }
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
