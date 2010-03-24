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
	}

}
