package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public final class HibernatePluginResources extends PluginResources
{
	HibernatePluginResources(IPlugin plugin)
	{
		super(HibernatePluginResources.class.getName(), plugin);
	}

	public interface IKeys
	{
		String CONNECTED_IMAGE = "connected";
		String CONNECTING_IMAGE = "connecting";
		String DISCONNECTED_IMAGE = "disconnected";

		String HQL_IMAGE = "hql";
		String HIBERNATE_IMAGE = "hibernate";
		String PROPERTY_IMAGE = "property";
		String CLOSE_IMAGE = "close";
		String RUN_IMAGE = "run";

		String JAR_IMAGE = "jar";
		String JAR_FROM_CLIP_IMAGE = "jarfromclip";
		String JAR_DIRECTORY_IMAGE = "jardirectory";
      String SQL_COPY_IMAGE = "sqlcopy";

		String ADD_IMAGE = "add";
		String DELETE_IMAGE = "delete";
		String COPY_IMAGE = "copy";

		String NEXT_NAV_IMAGE = "next_nav";
		String PREV_NAV_IMAGE = "prev_nav";
		String REPLACE_IMAGE = "replace";

		String DISPLAY_CHOICE_IMAGE = "displayChoice";
	}

}
