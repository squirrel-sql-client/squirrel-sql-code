package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
public class PluginLoadInfo
{
	private IPlugin _plugin;
	private long _creationStart;
	private long _creationEnd;
	private long _startLoading;
	private long _endLoading;
	private long _startInitializing;
	private long _endInitializing;

	public PluginLoadInfo()
	{
		super();
		_creationStart = System.currentTimeMillis();
	}

	public String getInternalName()
	{
		return _plugin.getInternalName();
	}

	public long getCreationTime()
	{
		return _creationEnd - _creationStart;
	}

	public long getLoadTime()
	{
		return _endLoading - _startLoading;
	}

	public long getInitializeTime()
	{
		return _endInitializing - _startInitializing;
	}

	void pluginCreated(IPlugin plugin)
	{
		_creationEnd = System.currentTimeMillis();
		_plugin = plugin;
	}

	void startLoading()
	{
		_startLoading = System.currentTimeMillis();
	}

	void endLoading()
	{
		_endLoading = System.currentTimeMillis();
	}

	void startInitializing()
	{
		_startInitializing = System.currentTimeMillis();
	}

	void endInitializing()
	{
		_endInitializing = System.currentTimeMillis();
	}
}

