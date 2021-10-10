package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.event.ActionEvent;


public class DeleteSqlAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public DeleteSqlAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		int[] bounds = _panel.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

		if(bounds[0] == bounds[1])
		{
			return;
		}

		_panel.getSQLEntryPanel().setSelectionStart(bounds[0]);
		_panel.getSQLEntryPanel().setSelectionEnd(bounds[1]);
		_panel.getSQLEntryPanel().replaceSelection("");
		
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);
	}

}
