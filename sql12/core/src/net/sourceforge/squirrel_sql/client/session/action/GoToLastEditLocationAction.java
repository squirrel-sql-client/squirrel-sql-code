package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;


public class GoToLastEditLocationAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public GoToLastEditLocationAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(null == _panel)
		{
			return;
		}

		_panel.getSQLEntryPanel().goToLastEditLocation();
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);
	}

}
