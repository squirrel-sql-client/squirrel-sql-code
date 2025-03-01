package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.RSyntaxSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class RepeatLastFindAction extends SquirrelAction implements ISQLPanelAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RepeatLastFindAction.class);


   private ISession _session;
   private ISQLEntryPanel _sqlEntryPanel;

   public RepeatLastFindAction(ISQLEntryPanel sqlEntryPanel)
	{
		this();
      _sqlEntryPanel = sqlEntryPanel;
   }

   public RepeatLastFindAction()
	{
		super(Main.getApplication(), Main.getApplication().getResources());
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
         rsep.repeatLastFind(evt);
      }
      else
      {
         String msg = s_stringMgr.getString("syntax.repeatLastfindOnlyOnRecommendedEditors");
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
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