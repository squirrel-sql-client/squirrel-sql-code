package net.sourceforge.squirrel_sql.client.session.action.multicaret;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret.MultiCaretHandler;

import java.awt.event.ActionEvent;


public class CaretAddAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public CaretAddAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent e)
	{
		TextAreaPaintHandler textAreaPaintHandler = _panel.getSQLEntryPanel().getTextAreaPaintHandler();
		MultiCaretHandler multiCaretHandler = textAreaPaintHandler.getMultiCaretHandler();

		multiCaretHandler.createNextCaret();
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);
	}

}
