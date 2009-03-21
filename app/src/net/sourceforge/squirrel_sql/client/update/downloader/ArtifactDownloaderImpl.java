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
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
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
	private final static ILogger s_log = LoggerController.createLogger(ArtifactDownloaderImpl.class);

	/** This is the pattern that all translation jars (i18n) begin with */
	public static final String TRANSLATION_JAR_PREFIX_PATTERN = "squirrel-sql_.*";

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

	private PathUtils _pathUtils = new PathUtilsImpl();

	private IProxySettings _proxySettings = null;

	private boolean releaseVersionWillChange = false;

	private RetryStrategy _retryStrategy = new DefaultRetryStrategyImpl();
	
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
		long totalBytesDownloaded = 0;
		try
		{

			prepareDownloadsDirectory();

			sendDownloadStarted(_artifactStatus.size());
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

				if (_util.isPresentInDownloadsDirectory(status))
				{
					if (s_log.isInfoEnabled())
					{
						s_log.info("run: Skipping download of file (" + fileToGet + ") which is already present "
							+ "in the downloads directory.");
					}
					sendDownloadFileCompleted(status.getName());
					continue;
				}

				boolean result = true;
				if (_isRemoteUpdateSite)
				{
					int count = 0;
					boolean success = false;
					while (_retryStrategy.shouldTryAgain(count++) && !success)
					{
						success = attemptFileDownload(fileToGet, destDir, status);
						if (!success)
						{
							Utilities.sleep(_retryStrategy.getTimeToWaitBeforeRetrying(count));
						}
					}
					if (!success)
					{
						sendDownloadFailed();
						return;
					}
				}
				else
				{
					fileToGet =
						_pathUtils.buildPath(false, this._fileSystemUpdatePath, status.getType(), status.getName());
					result = _util.downloadLocalUpdateFile(fileToGet, destDir);
				}
				if (result == false)
				{
					sendDownloadFailed();
					return;
				}
				else
				{
					sendDownloadFileCompleted(status.getName());
					totalBytesDownloaded += status.getSize();
				}
			}
		}
		catch (FileNotFoundException e)
		{
			s_log.error("run: Unexpected exception: " + e.getMessage(), e);
			sendDownloadFailed();
			return;
		}
		catch (IOException e)
		{
			s_log.error("run: Unexpected exception: " + e.getMessage(), e);
			sendDownloadFailed();
			return;
		}
		if (s_log.isInfoEnabled())
		{
			s_log.info("run: Downloaded " + totalBytesDownloaded + " bytes total for all update files.");
		}
		sendDownloadComplete();
	}

	private void prepareDownloadsDirectory() throws FileNotFoundException, IOException
	{
		// if the release version doesn't change, we won't be pulling down core artifacts. So, we just
		// need to make sure that all core files have been copied from their installed locations into the
		// corresponding directory in download, which is in the CLASSPATH of the updater. This covers the
		// case where the update is being run for the first time after install, and no new version is
		// available, but the user wants to install/remove plugins and/or translations.
		if (!releaseVersionWillChange)
		{
			// Copy core files minus any i18n jars to core downloads directory
			_util.copyDir(_util.getSquirrelLibraryDir(), TRANSLATION_JAR_PREFIX_PATTERN, false,
				_util.getCoreDownloadsDir());

			// Copy i18n files to i18n downloads directory
			_util.copyDir(_util.getSquirrelLibraryDir(), TRANSLATION_JAR_PREFIX_PATTERN, true,
				_util.getI18nDownloadsDir());

			// Copy the app module jar to core downloads directory
			_util.copyFile(_util.getInstalledSquirrelMainJarLocation(), _util.getCoreDownloadsDir());
		}
		// Move any i18n files that are located in the core downloads dir to the i18n downloads dir. The spring
		// application context will not load properly (for some unknown reason) when there are i18n jars in the
		// classpath. So as a work-around, we simply ensure that they are where they should be anyway.
		// Previously we were not as careful about this, so it is possible that i18n jars were copied into the
		// core downloads directory.
		_util.moveFiles(_util.getCoreDownloadsDir(), TRANSLATION_JAR_PREFIX_PATTERN, true,
			_util.getI18nDownloadsDir());
	}

	private boolean attemptFileDownload(String fileToGet, String destDir, ArtifactStatus status)
	{
		boolean success = true;

		try
		{
			_util.downloadHttpUpdateFile(_host, _port, fileToGet, destDir, status.getSize(),
				status.getChecksum(), _proxySettings);
		}
		catch (Exception e)
		{
			s_log.error("run: encountered exception while attempting to download file (" + fileToGet + "): "
				+ e.getMessage(), e);
			success = false;
		}
		return success;
	}

	private String getArtifactDownloadDestDir(ArtifactStatus status)
	{

		FileWrapper destDir = _util.getCoreDownloadsDir();
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

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader#setProxySettings(net.sourceforge.squirrel_sql.fw.util.IProxySettings)
	 */
	public void setProxySettings(IProxySettings settings)
	{
		_proxySettings = settings;
	}

	/**
	 * @return the releaseVersionWillChange
	 */
	public boolean isReleaseVersionWillChange()
	{
		return releaseVersionWillChange;
	}

	/**
	 * @param releaseVersionWillChange
	 *           the releaseVersionWillChange to set
	 */
	public void setReleaseVersionWillChange(boolean releaseVersionWillChange)
	{
		this.releaseVersionWillChange = releaseVersionWillChange;
	}

	/**
	 * @param strategy the _retryStrategy to set
	 */
	public void setRetryStrategy(RetryStrategy strategy)
	{
		_retryStrategy = strategy;
	}
	
}