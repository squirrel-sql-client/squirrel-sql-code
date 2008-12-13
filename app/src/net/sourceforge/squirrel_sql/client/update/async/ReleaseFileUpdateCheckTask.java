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
package net.sourceforge.squirrel_sql.client.update.async;

import static java.lang.System.currentTimeMillis;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.RELEASE_XML_FILENAME;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * We need to check to see if the release is current in several places. First, we must check when the user
 * prompts us to which is running on the event dispatch thread. Since this is a potentially a long-running
 * operation, this class uses the thread-pool to off-load it's work from the UI thread. Second, we must check
 * that the release is current when the update check timer says to, based on user preferences. So, this the
 * thread-pool isn't needed in that case and this class is used synchronously, since the timer thread is
 * already not the UI thread.
 * 
 * @author manningr
 */
public class ReleaseFileUpdateCheckTask implements Runnable
{

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(ReleaseFileUpdateCheckTask.class);

	private UpdateCheckRunnableCallback _callback = null;

	private IUpdateSettings _settings = null;

	private UpdateUtil _util = null;

	private IApplication _app = null;

	private boolean isUpToDate = false;

	public ReleaseFileUpdateCheckTask(UpdateCheckRunnableCallback callback, IUpdateSettings settings,
		UpdateUtil util, IApplication app)
	{
		Utilities.checkNull("ReleaseFileUpdateCheckRunnable", "settings", settings, "util", util, "app", app);
		_callback = callback;
		_settings = settings;
		_util = util;
		_app = app;
	}

	public void start()
	{
		if (_app == null) { throw new IllegalStateException(
			"_app was null - cannot access the thread pool for asynchronous use"); }
		_app.getThreadPool().addTask(this);
	}

	/**
	 * Results in a boolean value (isUpToDate) indicating whether or not there are updates available to be
	 * installed. This will also produce the installed and current ChannelXmlBeans and if used asynchronously
	 * (callback is not null), then this will invoke the appropriate callback method. One side effect is that
	 * the last update check time is stored in update settings so that the update check timer knows when to
	 * schedule another check at startup. The sequence of steps involved is : 1. Find the local release.xml
	 * file 2. Load the local release.xml file as a ChannelXmlBean. 3. Get the release.xml file as a
	 * ChannelXmlBean from the server or local filesystem 3a. For server, Determine the channel that the user
	 * has (stable or snapshot) 5. Determine if it is the same as the local copy, which was placed either by
	 * the installer or the last update?
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		String releaseFilename = null;

		// 1. Find the local release.xml file. Bail with exception if we cannot find it.
		try
		{
			releaseFilename = _util.getLocalReleaseFile().getAbsolutePath();
		}
		catch (Exception e)
		{
			s_log.error("Unexpected exception while attempting to find local release file: "+e.getMessage(), e);
			if (_callback != null) {
				_callback.updateCheckFailed(e);
			}
			return;
		}

		// 2. Load the local release.xml file as a ChannelXmlBean.
		ChannelXmlBean installedChannelBean = _util.getLocalReleaseInfo(releaseFilename);

		// 3. & 3a. Get the release.xml file as a ChannelXmlBean from the server or
		// filesystem.
		ChannelXmlBean currentChannelBean = getCurrentChannelXmlBean(installedChannelBean);

		// Record now as the last time we checked for updates.
		_settings.setLastUpdateCheckTimeMillis("" + currentTimeMillis());
		_app.getSquirrelPreferences().setUpdateSettings(_settings);

		// 5. Is it the same as the local copy, which was placed either by the
		// installer or the last update?
		if (currentChannelBean == null)
		{
			s_log.warn("run: currentChannelBean was null - it is inconclusive whether or not the software "
				+ "is current : assuming that it is for now");
			if (_callback != null) {
				_callback.updateCheckFailed(null);
			}
		}
		else
		{
			isUpToDate = currentChannelBean.equals(installedChannelBean);
			if (_callback != null)
			{
				_callback.updateCheckComplete(isUpToDate, installedChannelBean, currentChannelBean);
			}
		}
	}

	/**
	 * Uses settings to determine where to get the current release.xml that is available and retrieves it as an
	 * XML bean. This will return null (and log a warning) if it couldn't be retrieved.
	 * 
	 * @param installedChannelBean
	 *           the XML bean that represents what is installed.
	 * @return an XML bean that represents what release is currently available or null if it couldn't be
	 *         downloaded
	 */
	private ChannelXmlBean getCurrentChannelXmlBean(ChannelXmlBean installedChannelBean)
	{
		ChannelXmlBean currentChannelBean = null;
		if (_settings.isRemoteUpdateSite())
		{
			// 3a. For server, Determine the channel that the user has (stable or snapshot)
			String channelName = getDesiredChannel(_settings, installedChannelBean);

			try
			{
				StringBuilder releasePath = new StringBuilder("/");
				releasePath.append(_settings.getUpdateServerPath());
				releasePath.append("/");
				releasePath.append(channelName);
				releasePath.append("/");

				currentChannelBean =
					_util.downloadCurrentRelease(_settings.getUpdateServer(),
						Integer.parseInt(_settings.getUpdateServerPort()), releasePath.toString(),
						RELEASE_XML_FILENAME, _app.getSquirrelPreferences().getProxySettings());
			}
			catch (Exception e)
			{
				s_log.error("Unexpected exception: " + e.getMessage(), e);
			}
		}
		else
		{
			currentChannelBean = _util.loadUpdateFromFileSystem(_settings.getFileSystemUpdatePath());
		}
		return currentChannelBean;
	}

	/**
	 * This method takes a look at preference for channel and the channel that the user currently has installed
	 * and logs an info if switching from one to channel to another.
	 * 
	 * @return the name of the channel that the user wants.
	 */
	private String getDesiredChannel(final IUpdateSettings settings, final ChannelXmlBean _installedChannelBean)
	{
		String desiredChannel = settings.getUpdateServerChannel().toLowerCase();
		String currentChannelName = _installedChannelBean.getName();

		if (!currentChannelName.equals(desiredChannel))
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("getDesiredChannel: User is switching distribution channel from "
					+ "installed channel (" + currentChannelName + ") to new channel (" + desiredChannel + ")");
			}
		}
		return desiredChannel;
	}

	/**
	 * @return the isUpToDate
	 */
	public boolean isUpToDate()
	{
		return isUpToDate;
	}

}
