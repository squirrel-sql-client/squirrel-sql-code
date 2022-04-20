package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.TreeMap;

public class FindInPreferencesAction extends SquirrelAction
{
	StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInPreferencesAction.class);

	public FindInPreferencesAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent evt)
	{
		final GlobalPreferencesDialogFindInfo globalPreferencesDialogFindInfo = GlobalPreferencesSheet.createPreferencesFinderInfo();

		TreeMap<List<String>, List<PrefComponentInfo>> globalPrefsComponentInfoByPath =
				ComponentInfoByPathUtil.globalPrefsFindInfoToComponentInfoByPath(globalPreferencesDialogFindInfo);

		new FindInPreferencesCtrl(globalPrefsComponentInfoByPath);
	}
}
