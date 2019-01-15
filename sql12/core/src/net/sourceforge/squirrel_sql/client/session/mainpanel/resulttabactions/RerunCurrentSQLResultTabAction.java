package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

import java.awt.event.ActionEvent;

public class RerunCurrentSQLResultTabAction extends SquirrelAction implements ISQLPanelAction
{
   private ResultTabProvider _resultTabProvider;

	public RerunCurrentSQLResultTabAction(ResultTab resultTab)
	{
		super(Main.getApplication());
      _resultTabProvider = new ResultTabProvider(resultTab);
   }

	public RerunCurrentSQLResultTabAction()
	{
      this(null);
   }

	public void setSQLPanel(ISQLPanelAPI panel)
	{
      setEnabled(_resultTabProvider.setSQLPanelAPI(panel));
	}

   public synchronized void actionPerformed(ActionEvent evt)
   {
      if (_resultTabProvider.hasResultTab())
      {
         _resultTabProvider.getResultTab().reRunSQL();
      }
   }
}

