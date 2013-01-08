package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
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

	/**
	 * Ctor specifying Application API.
	 *
	 * @param	app	Application API.
	 */
	public RerunCurrentSQLResultTabAction(IApplication app)
	{
		super(app);
		if (app == null)
		{
			s_log.warn("RerunCurrentSQLResultTabAction(): IApplication == null");
		}
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		if (panel == null)
		{
			s_log.warn("setSQLPanel(): ISQLPanelAPI == null");
		}
		_panel = panel;
		setEnabled(_panel != null);
	}

	/**
	 * Close the current result tab
	 *
	 * @param	evt		Event being executed.
	 */
	public synchronized void actionPerformed(ActionEvent evt)
	{
		if (_panel != null)
		{
			ISQLResultExecuter sqlResultExecuter = _panel.getSQLResultExecuter();
			if (sqlResultExecuter != null)
			{
				IResultTab selectedResultTab = sqlResultExecuter.getSelectedResultTab();
				if (selectedResultTab != null)
				{
					selectedResultTab.reRunSQL();
				}
				else
				{
					s_log.warn("actionPerformed(): IResultTab == null");
				}
			}
			else
			{
				s_log.warn("actionPerformed(): ISQLResultExecuter == null");
			}
		}
		else
		{
			s_log.warn("actionPerformed(): ISQLPanelAPI == null");
		}
	}
}

