package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

		TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath = new TreeMap<>( (p1, p2) -> comparePaths(p1,p2) );

		for (Map.Entry<Integer, Component> entry : globalPreferencesDialogFindInfo.getTabComponentByTabIndex().entrySet())
		{
			final String tabName = globalPreferencesDialogFindInfo.getTabName(entry.getKey());
			GlobalPreferencesPanelVisitor.visit(tabName, entry.getValue(), vi -> onVisitFindableComponent(vi, componentInfoByPath));
		}

		new FindInPreferencesCtrl(componentInfoByPath);
	}

	private void onVisitFindableComponent(PrefComponentInfo componentInfo, TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
	{
		if(false == componentInfo.isLeaf() || componentInfo.hasEmptyText())
		{
			return;
		}

		List<String> path = componentInfo.getPath();

		if(componentInfo.isLeaf())
		{
			List<PrefComponentInfo> buf = componentInfoByPath.get(path);

			if(null == buf)
			{
				buf = new ArrayList<>();
				componentInfoByPath.put(path, buf);
			}

			buf.add(componentInfo);
		}
	}

	private int comparePaths(List<String> p1, List<String> p2)
	{
		for (int i = 0; i < Math.min(p1.size(), p2.size()); i++)
		{
			final int res = p1.get(i).compareTo(p2.get(i));
			if(0 != res)
			{
				return res;
			}
		}

		return Integer.compare(p1.size(), p2.size());
	}

}
