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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Loops through a list of artifacts and downloads each one into the appropriate directory. Notifies listeners
 * of important events.
 * 
 * @author manningr
 */
public class ArtifactDownloaderImpl implements Runnable, ArtifactDownloader
{
   /** Logger for this class. */
   private final static ILogger s_log = 
      LoggerController.createLogger(ArtifactDownloaderImpl.class);
	
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

	public ArtifactDownloaderImpl(List<ArtifactStatus> artifactStatus)
	{
		_artifactStatus = artifactStatus;
		downloadThread = new Thread(this, "ArtifactDownloadThread");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#start()
	 */
	public void start()
	{
		downloadThread.start();
	}	
	
	/**
	 * Runnable interface method implementation
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

				if (_util.isPresentInDownloadsDirectory(status)) {
					sendDownloadFileCompleted(status.getName());
					continue;
				}
				
				boolean result = true;
				if (_isRemoteUpdateSite)
				{
					try
					{
						_util.downloadHttpFile(_host, _port, fileToGet, destDir, status.getSize(),
							status.getChecksum());
					}
					catch (Exception e)
					{
						s_log.error("run: encountered exception while attempting to download file ("+fileToGet+
										"): "+e.getMessage(),e);
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
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#stopDownload()
	 */
	public void stopDownload()
	{
		_stopped = true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#getArtifactStatus()
	 */
	public List<ArtifactStatus> getArtifactStatus()
	{
		return _artifactStatus;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setArtifactStatus(java.util.List)
	 */
	public void setArtifactStatus(List<ArtifactStatus> status)
	{
		_artifactStatus = status;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#isRemoteUpdateSite()
	 */
	public boolean isRemoteUpdateSite()
	{
		return _isRemoteUpdateSite;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setIsRemoteUpdateSite(boolean)
	 */
	public void setIsRemoteUpdateSite(boolean remoteUpdateSite)
	{
		_isRemoteUpdateSite = remoteUpdateSite;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#getHost()
	 */
	public String getHost()
	{
		return _host;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setHost(java.lang.String)
	 */
	public void setHost(String host)
	{
		this._host = host;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#getPath()
	 */
	public String getPath()
	{
		return _path;
	}

//	/**
//	 * @param path
//	 *           the _path to set
//	 */
//	String downloadHttpFile(String host, int port, String path, String fileToGet, String destDir)
//		throws Exception
//	{
//		return _util.downloadHttpFile(host, port, fileToGet, destDir, -1, -1);
//	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setPath(java.lang.String)
	 */
	public void setPath(String path)
	{
		this._path = path;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#getUtil()
	 */
	public UpdateUtil getUtil()
	{
		return _util;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#addDownloadStatusListener(net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener)
	 */
	public void addDownloadStatusListener(DownloadStatusListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#removeDownloadListener(net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener)
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
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#getFileSystemUpdatePath()
	 */
	public String getFileSystemUpdatePath()
	{
		return _fileSystemUpdatePath;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setFileSystemUpdatePath(java.lang.String)
	 */
	public void setFileSystemUpdatePath(String systemUpdatePath)
	{
		_fileSystemUpdatePath = systemUpdatePath;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setPort(int)
	 */
	public void setPort(int updateServerPort)
	{
		_port = updateServerPort;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setChannelName(java.lang.String)
	 */
	public void setChannelName(String name)
	{
		_channelName = name;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setUtil(net.sourceforge.squirrel_sql.client.update.UpdateUtil)
	 */
	public void setUtil(UpdateUtil util)
	{
		this._util = util;
	}
	
}