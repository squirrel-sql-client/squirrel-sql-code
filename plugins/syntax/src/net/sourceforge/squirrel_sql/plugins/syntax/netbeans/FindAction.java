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

public class FindAction extends SquirrelAction implements ISQLPanelAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FindAction.class);


   private ISession _session;
   private ISQLEntryPanel _sqlEntryPanel;

   public FindAction(IApplication app, SyntaxPluginResources rsrc, ISQLEntryPanel sqlEntryPanel)
	{
		this(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;
   }

   public FindAction(IApplication app, SyntaxPluginResources rsrc)
	{
		super(app, rsrc);
	}
   


   public void actionPerformed(ActionEvent evt)
	{
      if(null != _sqlEntryPanel)
      {
         doActionPerformed(_sqlEntryPanel, evt);
      }
      else if(null != _session)
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
            //i18n[syntax.findNetbeansOnly=Find is only available when the Netbeans editor is used.\nSee menu File --> New Session Properties --> Tab Syntax]
            s_stringMgr.getString("syntax.findNetbeansOnly");
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
         return;
      }

      NetbeansSQLEntryPanel nsep = (NetbeansSQLEntryPanel) sqlEntryPanel;
      nsep.showFindDialog(evt);
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
