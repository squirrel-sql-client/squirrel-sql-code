package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class SessionSaveAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public SessionSaveAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		ArrayList<SQLPanel> sqlPanels = new ArrayList<>(_panel.getSession().getSessionPanel().getAllSQLPanels());

		final IWidget[] allWidgets = Main.getApplication().getMainFrame().getDesktopContainer().getAllWidgets();
		for (IWidget widget : allWidgets)
		{
			if(widget instanceof SQLInternalFrame)
			{
				final ISession sessionOfSqlInternaFrame = ((SQLInternalFrame) widget).getSQLPanel().getSession();
				if(_panel.getSession().getIdentifier().equals(sessionOfSqlInternaFrame.getIdentifier()))
				{

				}
			}
		}

	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);
	}

}
