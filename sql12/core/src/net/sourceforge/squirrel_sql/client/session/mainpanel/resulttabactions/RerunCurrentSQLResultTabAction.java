package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

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
      _resultTabProvider.setSQLPanelAPI(panel);
      doEnable();
	}

   private void doEnable()
   {
      setEnabled(_resultTabProvider.hasResultTab());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (false == _resultTabProvider.hasResultTab())
      {
         return;
      }

      switch(RerunResultTabMode.getCurrentMode())
      {
         case DEFAULT -> _resultTabProvider.getResultTab().reRunSQL();
         case TIMER_REPEATS -> rerunWithTimerRepeats();
      }
   }

   private void rerunWithTimerRepeats()
   {
      IResultTab resultTab = _resultTabProvider.getResultTab();
      if(null == resultTab)
      {
         return;
      }

      Frame parentWindow;

      ISQLPanelAPI sqlPanelApi = _resultTabProvider.getSqlPanelApiOrNull();
      if(null == sqlPanelApi)
      {
         parentWindow = Main.getApplication().getMainFrame();
      }
      else
      {
         parentWindow = sqlPanelApi.getOwningFrame();
      }

      RerunWithTimerRepeatsCtrl rerunWithTimerRepeatsCtrl = new RerunWithTimerRepeatsCtrl(parentWindow);

      if(false == rerunWithTimerRepeatsCtrl.isOk())
      {
         return;
      }

      int repeatSeconds = rerunWithTimerRepeatsCtrl.getRepeatSeconds();

      if(0 == repeatSeconds)
      {
         resultTab.reRunSQL();
      }
      else
      {
         resultTab.reRunSqlWithTimerRepeats(repeatSeconds);
      }
   }

   public void setResultTab(ResultTab resultTab)
   {
      _resultTabProvider.setResultTab(resultTab);
      doEnable();
   }
}

