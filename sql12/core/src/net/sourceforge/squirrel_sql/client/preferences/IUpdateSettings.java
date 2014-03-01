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


/**
 * Interface for settings pertaining to software updates. 
 */
public interface IUpdateSettings
{

	public static final String DEFAULT_UPDATE_SERVER = "sourceforge.net";
	public static final String DEFAULT_UPDATE_PORT = "80";
	public static final String DEFAULT_UPDATE_PATH = "projects/squirrel-sql/files/updates";
	
	/**
	 * @return the updateServer
	 */
	public abstract String getUpdateServer();

	/**
	 * @param updateServer the updateServer to set
	 */
	public abstract void setUpdateServer(String updateServer);

	/**
	 * @return the updateServerPort
	 */
	public abstract String getUpdateServerPort();

	/**
	 * @param updateServerPort the updateServerPort to set
	 */
	public abstract void setUpdateServerPort(String updateServerPort);

	/**
	 * @return the updateServerPath
	 */
	public abstract String getUpdateServerPath();

	/**
	 * @param updateServerPath the updateServerPath to set
	 */
	public abstract void setUpdateServerPath(String updateServerPath);

	/**
	 * @return the updateServerChannel
	 */
	public abstract String getUpdateServerChannel();

	/**
	 * @param updateServerChannel the updateServerChannel to set
	 */
	public abstract void setUpdateServerChannel(String updateServerChannel);

	/**
	 * @return the enableAutomaticUpdates
	 */
	public abstract boolean isEnableAutomaticUpdates();

	/**
	 * @param enableAutomaticUpdates the enableAutomaticUpdates to set
	 */
	public abstract void setEnableAutomaticUpdates(boolean enableAutomaticUpdates);

	/**
	 * @return the updateCheckFrequency
	 */
	public abstract String getUpdateCheckFrequency();

	/**
	 * @param updateCheckFrequency the updateCheckFrequency to set
	 */
	public abstract void setUpdateCheckFrequency(String updateCheckFrequency);

	/**
	 * @return the lastUpdateCheckTimeMillis
	 */
	public abstract String getLastUpdateCheckTimeMillis();

	/**
	 * @param lastUpdateCheckTimeMillis the lastUpdateCheckTimeMillis to set
	 */
	public abstract void setLastUpdateCheckTimeMillis(String lastUpdateCheckTimeMillis);

	/**
	 * Returns a boolean value to indicate whether or not the user wants to use 
	 * a remote site.  If this is false, it is assumed to mean that the user 
	 * wants to specify a local directory on the filesystem
	 * 
	 * @return true if remote site; false otherwise.
	 */
	public abstract boolean isRemoteUpdateSite();

	/**
	 * Sets a boolean value to indicate whether or not the user wants to use 
	 * a remote site.  If this is false, it is assumed to mean that the user 
	 * wants to specify a local directory on the filesystem
	 * 
	 * @param true for remote site; false for local directory.
	 */
	public abstract void setRemoteUpdateSite(boolean isRemoteUpdateSite);

	/**
	 * @return the fileSystemUpdatePath
	 */
	public abstract String getFileSystemUpdatePath();

	/**
	 * @param fileSystemUpdatePath the fileSystemUpdatePath to set
	 */
	public abstract void setFileSystemUpdatePath(String fileSystemUpdatePath);

}