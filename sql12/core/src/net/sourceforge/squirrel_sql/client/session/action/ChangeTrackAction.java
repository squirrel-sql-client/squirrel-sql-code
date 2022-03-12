package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackTypeEnum;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.SQLPanelApiChangedListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ChangeTrackAction extends SquirrelAction  implements ISQLPanelAction, IToolsPopupDescription
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackAction.class);

	private ISQLPanelAPI _panel;
	private ArrayList<SQLPanelApiChangedListener> _sqlPanelApiChangedListeners = new ArrayList<>();


	public ChangeTrackAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(ChangeTrackTypeEnum.getPreference() == ChangeTrackTypeEnum.FILE)
		{
			// Although the toolbar button cannot be clicked when it is set to type FILE
			// the action might still be triggered by he menu.
			return;
		}

		_panel.getChangeTracker().rebaseChangeTrackingOnToolbarButtonOrMenu();
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;

		boolean enabled = null != _panel && _panel.getChangeTracker().isEnabled();
		setEnabled(enabled);



		for (SQLPanelApiChangedListener l : _sqlPanelApiChangedListeners.toArray(new SQLPanelApiChangedListener[0]))
		{
			if (null != _panel && _panel.getChangeTracker().isEnabled())
			{
				l.activeSqlPanelApiChanged(_panel.getChangeTracker().getChangeTrackType());
			}
			else
			{
				l.activeSqlPanelApiChanged(null);
			}
		}
	}

	public void changeTrackTypeChangedForCurrentSqlPanel(ChangeTrackTypeEnum selectedType)
	{
		_panel.getChangeTracker().changeTrackTypeChanged(selectedType);
	}

	public void addSQLPanelApiChangedListener(SQLPanelApiChangedListener sqlPanelApiChangedListener)
	{
		_sqlPanelApiChangedListeners.remove(sqlPanelApiChangedListener);
		_sqlPanelApiChangedListeners.add(sqlPanelApiChangedListener);
	}

	public void removeSQLPanelApiChangedListener(SQLPanelApiChangedListener sqlPanelApiChangedListener)
	{
		_sqlPanelApiChangedListeners.remove(sqlPanelApiChangedListener);
	}

	@Override
	public String getToolsPopupDescription()
	{
		return s_stringMgr.getString("ChangeTrackAction.tools.popup.description");
	}
}
