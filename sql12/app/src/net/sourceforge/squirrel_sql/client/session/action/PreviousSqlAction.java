package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.IApplication;

import java.awt.event.ActionEvent;

public class PreviousSqlAction extends SquirrelAction  implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public PreviousSqlAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		_panel.getSQLEntryPanel().moveCaretToPreviousSQLBegin();
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);
	}
}
