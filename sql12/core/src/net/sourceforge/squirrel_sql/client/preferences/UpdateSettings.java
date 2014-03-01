/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.preferences;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.Version;

/**
 * Software update settings.
 */
public class UpdateSettings implements Cloneable, IUpdateSettings, Serializable
{
	private static final long serialVersionUID = -1305655179503568153L;

	/** Name of software update server. */
	private String updateServer = IUpdateSettings.DEFAULT_UPDATE_SERVER;

	/** Port for software update server. */
	private String updateServerPort = IUpdateSettings.DEFAULT_UPDATE_PORT;

	/** Path to channel directories on software update server. */
	private String updateServerPath = IUpdateSettings.DEFAULT_UPDATE_PATH;

	/** Update channel on the software update server. */
	private String updateServerChannel = "stable";

	/** Whether or not to periodically check for software updates */
	private boolean enableAutomaticUpdates = true;

	/** How often to check for updates - at startup / weekly */
	private String updateCheckFrequency = "WEEKLY";

	/** The last time an update check was made in milliseconds */
	private String lastUpdateCheckTimeMillis = "0";

	/** Whether or not the update site is on a remote server */
	private boolean isRemoteUpdateSite = true;

	private String fileSystemUpdatePath = "";

	public UpdateSettings()
	{
		if (Version.isSnapshotVersion())
		{
			updateServerChannel = "snapshot";
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	/**
	 * Copy Constructor
	 * 
	 * @param updateSettings
	 *           a <code>UpdateSettings</code> object
	 */
	public UpdateSettings(IUpdateSettings updateSettings)
	{
		String server = updateSettings.getUpdateServer();
		String port = updateSettings.getUpdateServerPort();
		String path = updateSettings.getUpdateServerPath();
		if (server != null && server.equalsIgnoreCase("www.squirrel-sql.net")) {
			server = "sourceforge.net";
			port = "80";
			path = "projects/squirrel-sql/files/updates";
		}
		
		
		this.updateServer = server;
		this.updateServerPort = port;
		this.updateServerPath = path;
		this.updateServerChannel = updateSettings.getUpdateServerChannel();
		this.enableAutomaticUpdates = updateSettings.isEnableAutomaticUpdates();
		this.updateCheckFrequency = updateSettings.getUpdateCheckFrequency();
		this.lastUpdateCheckTimeMillis = updateSettings.getLastUpdateCheckTimeMillis();
		this.isRemoteUpdateSite = updateSettings.isRemoteUpdateSite();
		this.fileSystemUpdatePath = updateSettings.getFileSystemUpdatePath();
	}

	/**
	 * @return the updateServer
	 */
	public String getUpdateServer()
	{
		return updateServer;
	}

	/**
	 * @param updateServer
	 *           the updateServer to set
	 */
	public void setUpdateServer(String updateServer)
	{
		this.updateServer = updateServer;
	}

	/**
	 * @return the updateServerPort
	 */
	public String getUpdateServerPort()
	{
		return updateServerPort;
	}

	/**
	 * @param updateServerPort
	 *           the updateServerPort to set
	 */
	public void setUpdateServerPort(String updateServerPort)
	{
		this.updateServerPort = updateServerPort;
	}

	/**
	 * @return the updateServerPath
	 */
	public String getUpdateServerPath()
	{
		return updateServerPath;
	}

	/**
	 * @param updateServerPath
	 *           the updateServerPath to set
	 */
	public void setUpdateServerPath(String updateServerPath)
	{
		this.updateServerPath = updateServerPath;
	}

	/**
	 * @return the updateServerChannel
	 */
	public String getUpdateServerChannel()
	{
		return updateServerChannel;
	}

	/**
	 * @param updateServerChannel
	 *           the updateServerChannel to set
	 */
	public void setUpdateServerChannel(String updateServerChannel)
	{
		this.updateServerChannel = updateServerChannel;
	}

	/**
	 * @return the enableAutomaticUpdates
	 */
	public boolean isEnableAutomaticUpdates()
	{
		return enableAutomaticUpdates;
	}

	/**
	 * @param enableAutomaticUpdates
	 *           the enableAutomaticUpdates to set
	 */
	public void setEnableAutomaticUpdates(boolean enableAutomaticUpdates)
	{
		this.enableAutomaticUpdates = enableAutomaticUpdates;
	}

	/**
	 * @return the updateCheckFrequency
	 */
	public String getUpdateCheckFrequency()
	{
		return updateCheckFrequency;
	}

	/**
	 * @param updateCheckFrequency
	 *           the updateCheckFrequency to set
	 */
	public void setUpdateCheckFrequency(String updateCheckFrequency)
	{
		this.updateCheckFrequency = updateCheckFrequency;
	}

	/**
	 * @return the lastUpdateCheckTimeMillis
	 */
	public String getLastUpdateCheckTimeMillis()
	{
		return lastUpdateCheckTimeMillis;
	}

	/**
	 * @param lastUpdateCheckTimeMillis
	 *           the lastUpdateCheckTimeMillis to set
	 */
	public void setLastUpdateCheckTimeMillis(String lastUpdateCheckTimeMillis)
	{
		this.lastUpdateCheckTimeMillis = lastUpdateCheckTimeMillis;
	}

	/**
	 * Returns a boolean value to indicate whether or not the user wants to use a remote site. If this is
	 * false, it is assumed to mean that the user wants to specify a local directory on the filesystem
	 * 
	 * @return true if remote site; false otherwise.
	 */
	public boolean isRemoteUpdateSite()
	{
		return this.isRemoteUpdateSite;
	}

	/**
	 * Sets a boolean value to indicate whether or not the user wants to use a remote site. If this is false,
	 * it is assumed to mean that the user wants to specify a local directory on the filesystem
	 * 
	 * @param true for remote site; false for local directory.
	 */
	public void setRemoteUpdateSite(boolean isRemoteUpdateSite)
	{
		this.isRemoteUpdateSite = isRemoteUpdateSite;
	}

	/**
	 * @return the fileSystemUpdatePath
	 */
	public String getFileSystemUpdatePath()
	{
		return fileSystemUpdatePath;
	}

	/**
	 * @param fileSystemUpdatePath
	 *           the fileSystemUpdatePath to set
	 */
	public void setFileSystemUpdatePath(String fileSystemUpdatePath)
	{
		this.fileSystemUpdatePath = fileSystemUpdatePath;
	}

}