/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class provides default handling for session internal frame opened callbacks which simply logs a debug
 * message that the plugin doesn't handle these callbacks.
 */
public class PluginSessionCallbackAdaptor implements PluginSessionCallback
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(PluginSessionCallbackAdaptor.class);

	/** The plugin that this class is handling session callbacks for. */
	IPlugin _plugin = null;

	/**
	 * Constructor which accepts the plugin for which this class is providing default session callback
	 * behavior.
	 * 
	 * @param plugin
	 *           the plugin - must not be null.
	 * @throws IllegalArgumentException
	 *            if the specified plugin is null.
	 */
	public PluginSessionCallbackAdaptor(IPlugin plugin)
	{
		Utilities.checkNull("PluginSessionCallbackAdaptor.init", "plugin", plugin);
		_plugin = plugin;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback#
	 *     
	 *     
	 *      objectTreeInternalFrameOpened(net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame,
	 *      net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
	{
		if (s_log.isDebugEnabled())
		{
			s_log.debug("objectTreeInternalFrameOpened: " + _plugin.getDescriptiveName() + " doesn't provide "
				+ "special handling for newly opened interal ObjectTree frames");
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback#
	 *      sqlInternalFrameOpened(net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame,
	 *      net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
	{
		if (s_log.isDebugEnabled())
		{
			s_log.debug("objectTreeInternalFrameOpened: " + _plugin.getDescriptiveName() + " doesn't provide "
				+ "special handling for newly opened interal SQL frames");
		}
	}

}
