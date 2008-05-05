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
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.util.PathUtilsImpl;

/**
 * Loops through a list of artifacts and downloads each one into the appropriate directory. Notifies listeners
 * of important events.
 * 
 * @author manningr
 */
public class ArtifactDownloader implements Runnable
{

	private List<ArtifactStatus> _artifactStatus = null;

	private volatile boolean _stopped = false;

	private boolean _isRemoteUpdateSite = true;

	private String _host = null;

	private String _path = null;

	private String _fileSystemUpdatePath = null;

	private List<DownloadStatusListener> listeners = new ArrayList<DownloadStatusListener>();

	Thread downloadThread = null;

	String _updatesDir = null;

	private int _port = 80;

	/** The name of the channel from which we are downloading artifacts */
	private String _channelName;
	
	private UpdateUtil _util = null;
	
	/** TODO: change this to Spring-injected when this class becomes a Spring bean. */
	private PathUtils _pathUtils = new PathUtilsImpl();

	public ArtifactDownloader(List<ArtifactStatus> artifactStatus)
	{
		_artifactStatus = artifactStatus;
		downloadThread = new Thread(this, "ArtifactDownloadThread");
	}

	public void start()
	{
		downloadThread.start();
	}	
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		sendDownloadStarted(_artifactStatus.size());

		try
		{
			for (ArtifactStatus status : _artifactStatus)
			{
				if (_stopped)
				{
					sendDownloadStopped();
					return;
				}
				else
				{
					sendDownloadFileStarted(status.getName());
				}
				String fileToGet =
					_pathUtils.buildPath(true, _path, _channelName, status.getType(), status.getName());
				String destDir = getArtifactDownloadDestDir(status);

				if (fileWasDownloadedPreviously(status)) {
					continue;
				}
				
				boolean result = true;
				if (_isRemoteUpdateSite)
				{
					try
					{
						_util.downloadHttpFile(_host, _port, fileToGet, destDir);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						sendDownloadFailed();
						return;
					}
				}
				else
				{
					fileToGet = _pathUtils.buildPath(false, this._fileSystemUpdatePath,fileToGet);
					result = _util.downloadLocalFile(fileToGet, destDir);
				}
				if (result == false)
				{
					sendDownloadFailed();
					return;
				}
				else
				{
					sendDownloadFileCompleted(status.getName());
				}
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO: alert the user that downloads failed. Prevent installation
			e.printStackTrace();
			sendDownloadFailed();
			return;
		}
		catch (IOException e)
		{
			// TODO: alert the user that downloads failed. Prevent installation
			e.printStackTrace();
			sendDownloadFailed();
			return;
		}
		sendDownloadComplete();
	}

	private boolean fileWasDownloadedPreviously(ArtifactStatus status)
	{
		boolean result = false;
		
		// Need to expand interface ArtifactStatus to include file size and/or checksum.
		
		return result;
	}

	private String getArtifactDownloadDestDir(ArtifactStatus status) {

		File destDir = _util.getCoreDownloadsDir();		
		if (UpdateUtil.PLUGIN_ARTIFACT_ID.equals(status.getType()))
		{
			destDir = _util.getPluginDownloadsDir();
		}
		if (UpdateUtil.TRANSLATION_ARTIFACT_ID.equals(status.getType()))
		{
			destDir = _util.getI18nDownloadsDir();
		}
		return destDir.getAbsolutePath();
	}
	
	/**
	 * Stop downloading files as soon as possible.
	 */
	public void stopDownload()
	{
		_stopped = true;
	}

	/**
	 * @return the list of ArtifactStatus items that describe each file to downloaded
	 */
	public List<ArtifactStatus> getArtifactStatus()
	{
		return _artifactStatus;
	}

	/**
	 * @param status
	 *           the list of ArtifactStatus items that describe each file to downloaded
	 */
	public void setArtifactStatus(List<ArtifactStatus> status)
	{
		_artifactStatus = status;
	}

	/**
	 * @return a boolean indicating whether a remote site or local dir is being used.
	 */
	public boolean isRemoteUpdateSite()
	{
		return _isRemoteUpdateSite;
	}

	/**
	 * @param remoteUpdateSite
	 *           a boolean indicating whether a remote site or local dir is being used.
	 */
	public void setIsRemoteUpdateSite(boolean remoteUpdateSite)
	{
		_isRemoteUpdateSite = remoteUpdateSite;
	}

	/**
	 * @return the _host
	 */
	public String getHost()
	{
		return _host;
	}

	/**
	 * @param host
	 *           the _host to set
	 */
	public void setHost(String host)
	{
		this._host = host;
	}

	/**
	 * @return the _path
	 */
	public String getPath()
	{
		return _path;
	}

	/**
	 * @param path
	 *           the _path to set
	 */
	String downloadHttpFile(String host, int port, String path, String fileToGet, String destDir)
		throws Exception
	{
		return _util.downloadHttpFile(host, port, fileToGet, destDir);
	}

	public void setPath(String path)
	{
		this._path = path;
	}

	/**
	 * @return the _util
	 */
	public UpdateUtil getUtil()
	{
		return _util;
	}

	/**
	 * Adds the specified listener
	 * 
	 * @param listener
	 */
	public void addDownloadStatusListener(DownloadStatusListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes the specified listener
	 * 
	 * @param listener
	 */
	public void removeDownloadListener(DownloadStatusListener listener)
	{
		listeners.remove(listener);
	}

	private void sendEvent(DownloadStatusEvent evt)
	{
		for (DownloadStatusListener listener : listeners)
		{
			listener.handleDownloadStatusEvent(evt);
		}
	}

	private void sendDownloadStarted(int totalFileCount)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STARTED);
		evt.setFileCountTotal(totalFileCount);
		sendEvent(evt);
	}

	private void sendDownloadStopped()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STOPPED);
		sendEvent(evt);
	}

	private void sendDownloadComplete()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		sendEvent(evt);
	}

	private void sendDownloadFailed()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FAILED);
		sendEvent(evt);
	}

	private void sendDownloadFileStarted(String filename)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_STARTED);
		evt.setFilename(filename);
		sendEvent(evt);
	}

	private void sendDownloadFileCompleted(String filename)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_COMPLETED);
		evt.setFilename(filename);
		sendEvent(evt);
	}

	/**
	 * @return the _fileSystemUpdatePath
	 */
	public String getFileSystemUpdatePath()
	{
		return _fileSystemUpdatePath;
	}

	/**
	 * @param systemUpdatePath
	 *           the _fileSystemUpdatePath to set
	 */
	public void setFileSystemUpdatePath(String systemUpdatePath)
	{
		_fileSystemUpdatePath = systemUpdatePath;
	}

	public void setPort(int updateServerPort)
	{
		_port = updateServerPort;
	}

	public void setChannelName(String name)
	{
		_channelName = name;
	}

	/**
	 * Sets the update utility to use.
	 * 
	 * @param util
	 */
	public void setUtil(UpdateUtil util)
	{
		this._util = util;
	}
	
}