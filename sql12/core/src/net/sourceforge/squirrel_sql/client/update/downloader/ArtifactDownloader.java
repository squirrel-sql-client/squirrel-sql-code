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
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;

public interface ArtifactDownloader
{

	void start();

	/**
	 * Stop downloading files as soon as possible.
	 */
	void stopDownload();

	/**
	 * @return the list of ArtifactStatus items that describe each file to downloaded
	 */
	List<ArtifactStatus> getArtifactStatus();

	/**
	 * @param status
	 *           the list of ArtifactStatus items that describe each file to downloaded
	 */
	void setArtifactStatus(List<ArtifactStatus> status);

	/**
	 * @return a boolean indicating whether a remote site or local dir is being used.
	 */
	boolean isRemoteUpdateSite();

	/**
	 * @param remoteUpdateSite
	 *           a boolean indicating whether a remote site or local dir is being used.
	 */
	void setIsRemoteUpdateSite(boolean remoteUpdateSite);

	/**
	 * @return the _host
	 */
	String getHost();

	/**
	 * @param host
	 *           the _host to set
	 */
	void setHost(String host);

	/**
	 * @return the _path
	 */
	String getPath();

	void setPath(String path);

	/**
	 * @return the _util
	 */
	UpdateUtil getUtil();

	/**
	 * Adds the specified listener
	 * 
	 * @param listener
	 */
	void addDownloadStatusListener(DownloadStatusListener listener);

	/**
	 * Removes the specified listener
	 * 
	 * @param listener
	 */
	void removeDownloadListener(DownloadStatusListener listener);

	/**
	 * @return the _fileSystemUpdatePath
	 */
	String getFileSystemUpdatePath();

	/**
	 * @param systemUpdatePath
	 *           the _fileSystemUpdatePath to set
	 */
	void setFileSystemUpdatePath(String systemUpdatePath);

	void setPort(int updateServerPort);

	void setChannelName(String name);

	/**
	 * Sets the update utility to use.
	 * 
	 * @param util
	 */
	void setUtil(UpdateUtil util);

	public void setProxySettings(IProxySettings settings);

	/**
	 * @return the releaseVersionWillChange
	 */
	public boolean isReleaseVersionWillChange();

	/**
	 * @param releaseVersionWillChange the releaseVersionWillChange to set
	 */
	public void setReleaseVersionWillChange(boolean releaseVersionWillChange);

}