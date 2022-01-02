package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;

import java.awt.event.ActionEvent;


public class DeleteCurrentLineAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public DeleteCurrentLineAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		final int[] bounds = SQLEntryPanelUtil.getLineBoundsAtCursor(_panel.getTextComponent());

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
