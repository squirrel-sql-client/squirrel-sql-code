package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ReplaceAction extends SquirrelAction implements ISQLPanelAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ReplaceAction.class);

   private ISession _session;
   private ISQLEntryPanel _isqlEntryPanel;

   public ReplaceAction(IApplication app, SyntaxPluginResources rsrc)
			throws IllegalArgumentException
	{
		super(app, rsrc);
	}

   public ReplaceAction(IApplication app, SyntaxPluginResources rsrc, ISQLEntryPanel isqlEntryPanel)
   {
      this(app, rsrc);
      _isqlEntryPanel = isqlEntryPanel;
   }

   public void actionPerformed(ActionEvent evt)
	{
      if(null != _isqlEntryPanel)
      {
         doActionPerformed(_isqlEntryPanel, evt);
      }
      if(null != _session)
      {
         ISQLEntryPanel sqlEntryPanel = _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
         doActionPerformed(sqlEntryPanel, evt);
      }
	}

   private void doActionPerformed(ISQLEntryPanel sqlEntryPanel, ActionEvent evt)
   {
      if(false == sqlEntryPanel instanceof NetbeansSQLEntryPanel)
      {
         String msg =
            //i18n[syntax.replaceNetbeansOnly=Replace is only available when the Netbeans editor is used.\nSee menu File --> New Session Properties --> Tab Syntax]
            s_stringMgr.getString("syntax.replaceNetbeansOnly");
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
         return;
      }

      NetbeansSQLEntryPanel nsep = (NetbeansSQLEntryPanel) sqlEntryPanel;
      nsep.showReplaceDialog(evt);
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
