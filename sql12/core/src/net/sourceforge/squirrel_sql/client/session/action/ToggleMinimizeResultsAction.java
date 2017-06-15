package net.sourceforge.squirrel_sql.client.session.action;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import java.awt.event.ActionEvent;

public class ToggleMinimizeResultsAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _isqlPanelAPI;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public ToggleMinimizeResultsAction(IApplication app)
	{
		super(app);
	}


	@Override
	public void setSQLPanel(ISQLPanelAPI isqlPanelAPI)
	{
		_isqlPanelAPI = isqlPanelAPI;
	}

	/**
	 * Perform this action. Uses the <TT>CloseAllSQLResultWindowsCommand</TT>.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if(null == _isqlPanelAPI)
		{
			return;
		}

	   _isqlPanelAPI.toggleMinimizeResults();
	}
}
