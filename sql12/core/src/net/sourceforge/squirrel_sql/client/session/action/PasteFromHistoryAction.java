package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.pastefromhistory.PasteFromHistoryController;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import java.awt.event.ActionEvent;


public class PasteFromHistoryAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public PasteFromHistoryAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		new PasteFromHistoryController(_panel);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
	}

}
