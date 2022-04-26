package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import java.awt.event.ActionEvent;

public class FindInPreferencesAction extends SquirrelAction
{
	public FindInPreferencesAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent evt)
	{
		PrefsFindInfo prefsFindInfo = ComponentInfoByPathUtil.createPrefsFindInfo();

		new FindInPreferencesCtrl(prefsFindInfo);
	}
}
