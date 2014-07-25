package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;

public class RerunCurrentSQLResultTabAction extends SquirrelAction implements ISQLPanelAction
{

	/** Current panel. */
	private ISQLPanelAPI _panel;

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(RerunCurrentSQLResultTabAction.class);
   private ResultTab _resultTab;

   /**
	 * Ctor specifying Application API.
	 *
    * @param   app   Application API.
    * @param resultTab
    */
	public RerunCurrentSQLResultTabAction(IApplication app, ResultTab resultTab)
	{
		super(app);
      _resultTab = resultTab;
   }

	public void setSQLPanel(ISQLPanelAPI panel)
	{
//		_panel = panel;
//		setEnabled(_panel != null);
	}

	/**
	 * Close the current result tab
	 *
	 * @param	evt		Event being executed.
	 */
	public synchronized void actionPerformed(ActionEvent evt)
	{
      _resultTab.reRunSQL();

//			ISQLResultExecuter sqlResultExecuter = _panel.getSQLResultExecuter();
//			if (sqlResultExecuter != null)
//			{
//				IResultTab selectedResultTab = sqlResultExecuter.getSelectedResultTab();
//				if (selectedResultTab != null)
//				{
//					selectedResultTab.reRunSQL();
//				}
//			}
	}
}

