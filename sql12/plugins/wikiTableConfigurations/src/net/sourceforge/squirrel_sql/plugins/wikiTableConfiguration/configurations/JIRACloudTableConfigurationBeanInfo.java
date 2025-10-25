package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;

public class JIRACloudTableConfigurationBeanInfo extends SimpleBeanInfo{
	private interface IPropNames extends PluginInfo.IPropertyNames
	{
		// Empty body, purely to shorten the interface name for convienience.
	}

	/**
	 * See http://tinyurl.com/63no6t for discussion of the proper thread-safe way to implement
	 * getPropertyDescriptors().
	 * 
	 * @see SimpleBeanInfo#getPropertyDescriptors()
	 */
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] s_descr = new PropertyDescriptor[1];

			s_descr[0] = new PropertyDescriptor("enabled", JIRACloudTableConfiguration.class, "isEnabled", "setEnabled");
			return s_descr;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
