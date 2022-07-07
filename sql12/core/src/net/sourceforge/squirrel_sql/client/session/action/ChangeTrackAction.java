package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.IToolsPopupDescription;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackActionUpdateListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackTypeEnum;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ChangeTrackAction extends SquirrelAction  implements ISQLPanelAction, IToolsPopupDescription
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackAction.class);

	private ISQLPanelAPI _panel;
	private ArrayList<ChangeTrackActionUpdateListener> _changeTrackActionUpdateListeners = new ArrayList<>();


	public ChangeTrackAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(ChangeTrackTypeEnum.getPreference() == ChangeTrackTypeEnum.FILE)
		{
			// Although the toolbar button cannot be clicked when it is set to type FILE
			// the action might still be triggered by the menu.
			return;
		}

		_panel.getChangeTracker().rebaseChangeTrackingOnToolbarButtonOrMenuClicked();
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;

		boolean enabled = null != _panel && _panel.getChangeTracker().isEnabled();
		setEnabled(enabled);



		for (ChangeTrackActionUpdateListener l : _changeTrackActionUpdateListeners.toArray(new ChangeTrackActionUpdateListener[0]))
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

	public void addChangeTrackActionUpdateListener(ChangeTrackActionUpdateListener changeTrackActionUpdateListener)
	{
		_changeTrackActionUpdateListeners.remove(changeTrackActionUpdateListener);
		_changeTrackActionUpdateListeners.add(changeTrackActionUpdateListener);
	}

	public void removeSQLPanelApiChangedListener(ChangeTrackActionUpdateListener changeTrackActionUpdateListener)
	{
		_changeTrackActionUpdateListeners.remove(changeTrackActionUpdateListener);
	}

	@Override
	public String getToolsPopupDescription()
	{
		return s_stringMgr.getString("ChangeTrackAction.tools.popup.description");
	}

	public void setChangeTrackTypeForCurrentSqlPanel(ChangeTrackTypeEnum type)
	{
		if(null == _panel)
		{
			return;
		}

		for (ChangeTrackActionUpdateListener l : _changeTrackActionUpdateListeners.toArray(new ChangeTrackActionUpdateListener[0]))
		{
			l.externallySetChangeTrackType(type);
		}
	}
}
