package net.sourceforge.squirrel_sql.client.update;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Listener for download events and handle them appropriately.  Specifically, this controls a ProgressMonitor
 * showing progress to the user as the artifact downloader reports it.  Also, a check is made each time that
 * the downloader sends an event to see if the user canceled the download using the ProgressMonitor's cancel
 * button.  In that case, the downloader is notified that it should stop and will do so the next opportunity
 * it gets.
 * 
 * @author manningr
 */
public class DownloadStatusEventHandler implements DownloadStatusListener
{

	/** I18n strings for this class */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DownloadStatusEventHandler.class);
	
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(DownloadStatusEventHandler.class);

	private static interface i18n
	{
		// i18n[DownloadStatusEventHandler.downloadingUpdatesMsg=Downloading Files]
		String DOWNLOADING_UPDATES_MSG = s_stringMgr.getString("DownloadStatusEventHandler.downloadingUpdatesMsg");

		// i18n[DownloadStatusEventHandler.changesRecordedTitle=Changes Recorded]
		String CHANGES_RECORDED_TITLE = s_stringMgr.getString("DownloadStatusEventHandler.changesRecordedTitle");

		// i18n[DownloadStatusEventHandler.changesRecordedMsg=Requested changes will be made when
		// SQuirreL is restarted]
		String CHANGES_RECORDED_MSG = s_stringMgr.getString("DownloadStatusEventHandler.changesRecordedMsg");

		// i18n[DownloadStatusEventHandler.updateDownloadFailedTitle=Update Download Failed]
		String UPDATE_DOWNLOAD_FAILED_TITLE =
			s_stringMgr.getString("DownloadStatusEventHandler.updateDownloadFailedTitle");

		// i18n[DownloadStatusEventHandler.updateDownloadFailedMsg=Please consult the log for details]
		String UPDATE_DOWNLOAD_FAILED_MSG =
			s_stringMgr.getString("DownloadStatusEventHandler.updateDownloadFailedMsg");

	   // i18n[DownloadStatusEventHandler.fileLabel=File]
		String FILE_LABEL = 
			s_stringMgr.getString("DownloadStatusEventHandler.fileLabel");
	}

	ProgressMonitor progressMonitor = null;

	int currentFile = 0;

	int totalFiles = 0;

	private ArtifactDownloader downloader = null;

	private UpdateController controller = null;

	public DownloadStatusEventHandler(UpdateController controller)
	{
		this.controller = controller;
	}

	/**
	 * @param downloader the artifact downloader that will be sending events
	 */
	public void setDownloader(ArtifactDownloader downloader)
	{
		this.downloader = downloader;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener#
	 *     
	 *     
	 *      handleDownloadStatusEvent(net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent)
	 */
	public void handleDownloadStatusEvent(DownloadStatusEvent evt)
	{
		logDebug("handleDownloadStatusEvent: processing event: "+evt);
		
		if (progressMonitor != null && progressMonitor.isCanceled())
		{
			downloader.stopDownload();
			return;
		}
		if (evt.getType() == DownloadEventType.DOWNLOAD_STARTED)
		{
			totalFiles = evt.getFileCountTotal();
			currentFile = 0;
			handleDownloadStarted();
		}
		if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_STARTED)
		{
			setNote(i18n.FILE_LABEL + ": " + evt.getFilename());
		}

		if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_COMPLETED)
		{
			setProgress(++currentFile);
		}

		if (evt.getType() == DownloadEventType.DOWNLOAD_STOPPED)
		{
			setProgress(totalFiles);
		}

		// When all updates are retrieved, tell the user that the updates will be installed upon the
		// next startup.
		if (evt.getType() == DownloadEventType.DOWNLOAD_COMPLETED)
		{
			controller.showMessage(i18n.CHANGES_RECORDED_TITLE, i18n.CHANGES_RECORDED_MSG);
			setProgress(totalFiles);
		}
		if (evt.getType() == DownloadEventType.DOWNLOAD_FAILED)
		{
			controller.showErrorMessage(i18n.UPDATE_DOWNLOAD_FAILED_TITLE, i18n.UPDATE_DOWNLOAD_FAILED_MSG);
			setProgress(totalFiles);
		}
	}

	private void setProgress(final int value)
	{
		logDebug("setProgress: value=", value);
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				progressMonitor.setProgress(value);
			}
		});
	}

	private void setNote(final String note)
	{
		logDebug("setNote: value=", note);
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				progressMonitor.setNote(note);
			}
		});
	}

	private void handleDownloadStarted()
	{
		logDebug("handleDownloadStarted: launching progress monitor");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				final JFrame frame = controller.getMainFrame();
				progressMonitor =
					new ProgressMonitor(frame, i18n.DOWNLOADING_UPDATES_MSG, i18n.DOWNLOADING_UPDATES_MSG, 0,
						totalFiles);
				setProgress(0);
			}
		});
	}

	private void logDebug(Object ... msgs) {
		StringBuilder tmp = new StringBuilder();
		for (Object msg : msgs) {
			tmp.append(msg.toString());
		}
		if (s_log.isDebugEnabled()) {
			s_log.debug(tmp.toString());
		}
	}
	
}