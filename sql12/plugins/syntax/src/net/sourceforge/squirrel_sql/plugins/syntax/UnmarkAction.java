package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.RSyntaxSQLEntryPanel;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;

import java.awt.event.ActionEvent;

public class UnmarkAction extends SquirrelAction implements ISQLPanelAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UnmarkAction.class);


   private ISession _session;
   private ISQLEntryPanel _sqlEntryPanel;

   public UnmarkAction(IApplication app, SyntaxPluginResources rsrc, ISQLEntryPanel sqlEntryPanel)
	{
		this(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;
   }

   public UnmarkAction(IApplication app, SyntaxPluginResources rsrc)
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
      if(sqlEntryPanel instanceof RSyntaxSQLEntryPanel)
      {
         SquirrelRSyntaxTextArea rsep = (SquirrelRSyntaxTextArea) sqlEntryPanel.getTextComponent();
         rsep.unmarkAll();
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
