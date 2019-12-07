package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.toolbarbuttonchooser.EnabledListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackTypeEnum;

import java.awt.event.ActionEvent;

public class ChangeTrackAction extends SquirrelAction  implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;
	private EnabledListener _enabledListener;

	public ChangeTrackAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent e)
	{
		System.out.println("ChangeTrackAction.actionPerformed: " + ChangeTrackTypeEnum.getSelectedType().name());
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		setEnabled(null != _panel);

		if (null != _enabledListener)
		{
			_enabledListener.enabledChanged(isEnabled());
		}
	}

	public void setEnabledListener(EnabledListener enabledListener)
	{
		_enabledListener = enabledListener;
	}
}
