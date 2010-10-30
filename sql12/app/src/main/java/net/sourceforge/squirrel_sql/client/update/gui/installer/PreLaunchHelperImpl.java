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
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.ScriptLineFixer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.springframework.beans.factory.annotation.Required;

/**
 * This is a bean that the prelaunch app uses. The pre-launch app main class (PreLaunchUpdateApplication)
 * loads the spring context, and therefore can't managed by spring. So, it is very small and most of it's
 * logic for doing updates resides here.
 * 
 * @author manningr
 */
public class PreLaunchHelperImpl implements PreLaunchHelper
{

	/** The message we show the user in the update dialog that is shown when there are updates to install */
	private String INSTALL_UPDATES_MESSAGE;

	/**
	 * The title of the dialect that we show the user in the update dialog that is shown when there are updates
	 * to install
	 */
	private String INSTALL_UPDATES_TITLE;

	private String RESTORE_FROM_BACKUP_TITLE;

	private String RESTORE_FROM_BACKUP_MESSAGE;

	private String RESTORE_FAILED_MESSAGE;

	private String BACKUP_FAILED_MESSAGE;

	private String INSTALL_FAILED_MESSAGE;

	/** Internationalized strings for this class */
	private StringManager s_stringMgr;

	/** Logger for this class. */
	private ILogger s_log;

	/** Used to override logic for calculating script location for testing purposes */
	private String scriptLocation = null;

	/* --------------------------- Spring=injected dependencies --------------------------------------------*/

	/* Spring-injected */
	private UpdateUtil updateUtil = null;

	@Required
	public void setUpdateUtil(UpdateUtil util)
	{
		Utilities.checkNull("setUpdateUtil", "util", util);
		this.updateUtil = util;
	}

	/* Spring-injected */
	private ArtifactInstallerFactory artifactInstallerFactory = null;

	@Required
	public void setArtifactInstallerFactory(ArtifactInstallerFactory artifactInstallerFactory)
	{
		Utilities.checkNull("setArtifactInstallerFactory", "artifactInstallerFactory", artifactInstallerFactory);
		this.artifactInstallerFactory = artifactInstallerFactory;
	}

	/* Spring-injected */
	private List<ScriptLineFixer> scriptLineFixers = null;

	@Required
	public void setScriptLineFixers(List<ScriptLineFixer> scriptLineFixers)
	{
		Utilities.checkNull("setScriptLineFixers", "scriptLineFixers", scriptLineFixers);
		this.scriptLineFixers = scriptLineFixers;
	}

	/* Spring-injected */
	private IOUtilities ioutils = null;

	/**
	 * @param ioutils
	 *           the ioutils to set
	 */
	@Required
	public void setIoutils(IOUtilities ioutils)
	{
		Utilities.checkNull("setIoutils", "ioutils", ioutils);
		this.ioutils = ioutils;
	}

	/* ----------------------------------- Public API ------------------------------------------------------*/

	public PreLaunchHelperImpl() throws IOException
	{

		s_log = LoggerController.createLogger(PreLaunchHelperImpl.class);
		s_stringMgr = StringManagerFactory.getStringManager(PreLaunchHelperImpl.class);

		// i18n[PreLaunchHelperImpl.installUpdatesTitle=Updates Available]
		INSTALL_UPDATES_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesTitle");

		// i18n[PreLaunchHelperImpl.installUpdatesMessage=Updates are ready to be installed. Install them now?]
		INSTALL_UPDATES_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesMessage");

		// i18n[PreLaunchHelperImpl.restoreFromBackupTitle=Confirm Restore From Backup
		RESTORE_FROM_BACKUP_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupTitle");

		// i18n[PreLaunchHelperImpl.restoreFromBackupMessage=Restore SQuirreL to previous version before
		// last update?]
		RESTORE_FROM_BACKUP_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupMessage");

		// i18n[PreLaunchHelperImpl.backupFailedMessage=Backup of existing files failed. Installation cannot
		// proceed]
		BACKUP_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.backupFailedMessage");

		// i18n[PreLaunchHelperImpl.installFailedMessage=Unexpected error while attempting to install updates]
		INSTALL_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installFailedMessage");

		// i18n[PreLaunchHelperImpl.restoreFailedMessage=Restore from backup failed. Re-installation may be
		// required.
		RESTORE_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFailedMessage");

	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelper#installUpdates(boolean)
	 */
	@Override
	public void installUpdates(boolean prompt)
	{
		FileWrapper changeListFile = updateUtil.getChangeListFile();
		if (hasChangesToBeApplied(changeListFile))
		{
			logInfo("Pre-launch update app detected a changeListFile to be processed");
			if (prompt)
			{
				if (showConfirmDialog(INSTALL_UPDATES_MESSAGE, INSTALL_UPDATES_TITLE))
				{
					installUpdates(changeListFile);
				}
				else
				{
					logInfo("User cancelled update installation");
				}
			}
			else
			{
				installUpdates(changeListFile);
			}
		}
	}

	/**
	 * Updates the launch script with changes made necessary by the new release.
	 * 
	 * @throws IOException
	 *            if an I/O error occurs
	 */
	@Override
	public void updateLaunchScript() throws IOException
	{
		// 1. determine which script(s) to fix.
		List<String> launchScriptLocations = getLaunchScriptLocations();
		
		for (String launchScript : launchScriptLocations) {
			logInfo("Applying updates to launch script: " + launchScript);

			// 2. Get the lines from the file, applying the line fixers
			List<String> lines = ioutils.getLinesFromFile(launchScript, scriptLineFixers);

			// 3. Write the fixed lines back out to the file.
			ioutils.writeLinesToFile(launchScript, lines);
		}
	}

	private List<String> getLaunchScriptLocations() {
		List<String> result = new ArrayList<String>();
		
		if (scriptLocation != null) {
			result.add(scriptLocation);
			return result;
		}
		
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().startsWith("windows")) {
			result.add("squirrel-sql.bat");
			// And for cygwin users on Windows
			result.add("squirrel-sql.sh");
		}
		else if (os != null && os.toLowerCase().startsWith("mac"))
		{
			// Java on Mac OS X doesn't find squirrel-sql.sh; so construct the absolute path.
			result.add(getMacOSContentsPath() + "/MacOS/squirrel-sql.sh");
		}
		else {
			result.add("squirrel-sql.sh");
		}
		
		return result;
	}
	
	private String getMacOSContentsPath() {
		// The user.dir property on the Mac is /Applications/SQuirreLSQL.app/Contents/Resources/Java
		String userdir = System.getProperty("user.dir");
		String[] parts = userdir.split("Contents");
		return parts[0] + "/Contents";
	}
	
	@Override
	public void copySplashImage() throws IOException
	{
		// The user.dir property on the Mac is /Applications/SQuirreLSQL.app/Contents/Resources/Java
		String squirrelHome = System.getProperty("user.dir");

		
		String jarFilename = squirrelHome + "/update/downloads/core/squirrel-sql.jar";
		String resourceName = "splash.jpg";
		String pathToIconsDir  = squirrelHome + "/icons";
		String destinationFile = pathToIconsDir + "/" + resourceName;
		
		File iconsDir = new File(pathToIconsDir);
		if (!iconsDir.exists()) {
			logInfo("Icons directory ("+pathToIconsDir+") doesn't exist, so attempting to create it.");
			boolean result = iconsDir.mkdir();
			if (!result) {
				s_log.error("Failed to create icons directory ("+pathToIconsDir+")");
			}
		}		
		
		logInfo("Copying splash.jpg from jarfile ("+jarFilename+") to "+destinationFile);
		
		ioutils.copyResourceFromJarFile(jarFilename, resourceName, destinationFile);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelper#restoreFromBackup()
	 */
	@Override
	public void restoreFromBackup()
	{
		if (showConfirmDialog(RESTORE_FROM_BACKUP_MESSAGE, RESTORE_FROM_BACKUP_TITLE))
		{

			try
			{
				FileWrapper backupDir = updateUtil.getBackupDir();
				FileWrapper changeListFile = updateUtil.getFile(backupDir, UpdateUtil.CHANGE_LIST_FILENAME);
				ChangeListXmlBean changeList = updateUtil.getChangeList(changeListFile);

				ArtifactInstaller installer = artifactInstallerFactory.create(changeList, null);
				if (!installer.restoreBackupFiles())
				{
					showErrorDialog(RESTORE_FAILED_MESSAGE);
					s_log.error("restoreFromBackup: " + RESTORE_FAILED_MESSAGE);
				}

			}
			catch (Throwable e)
			{
				s_log.error("Unexpected error while attempting restore from backup: " + e.getMessage(), e);
				showErrorDialog(RESTORE_FAILED_MESSAGE);
			}

		}
		shutdown("Pre-launch update app finished");
	}

	/* ------------------------------------- Helper methods ------------------------------------------------*/

	/**
	 * Peeks into the changelist file to see if there are artifacts to change. This is precautionary as the GUI
	 * should prevent the changeList file from being created if there are no artifacts to be changed.
	 * 
	 * @param changeListFile
	 *           the file that contains the changeList
	 * @return true if the changeListFile exists and has changes to be applied; false otherwise.
	 */
	private boolean hasChangesToBeApplied(final FileWrapper changeListFile)
	{
		boolean result = false;
		try
		{
			if (changeListFile.exists())
			{
				final ChangeListXmlBean changeListBean = updateUtil.getChangeList(changeListFile);
				final List<ArtifactStatus> changeList = changeListBean.getChanges();
				final int changeListSize = changeList.size();
				logInfo("hasChangesToBeApplied: changeListFile (" + changeListSize + ") has " + changeListSize
					+ " changes to be applied");

				if (changeList != null && changeListSize > 0)
				{
					result = true;
				}
				else
				{
					logInfo("Aborting update: changeList was found with no updates");
				}
			}
			else
			{
				logInfo("installUpdates: changeList file (" + changeListFile + ") doesn't exist");
			}
		}
		catch (FileNotFoundException e)
		{
			s_log.error("hasChangesToBeApplied: unable to get change list from file (" + changeListFile + "): "
				+ e.getMessage());
		}

		return result;
	}

	/**
	 * Shuts down this small pre-launch helper application.
	 */
	private void shutdown(String message)
	{
		logInfo(message);
		LoggerController.shutdown();
		System.exit(0);
	}

	/**
	 * Install the updates, taking care to backup the originals first.
	 * 
	 * @param changeList
	 *           the xml file describing the changes to be made.
	 * @throws Exception
	 *            if any error occurs
	 */
	private void installUpdates(final FileWrapper changeList)
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ProgressDialogController controller = new ProgressDialogControllerImpl();
					InstallStatusListener listener = new InstallStatusListenerImpl(controller);
					ArtifactInstaller installer = artifactInstallerFactory.create(changeList, listener);
					if (installer.backupFiles())
					{
						installer.installFiles();
					}
					else
					{
						showErrorDialog(BACKUP_FAILED_MESSAGE);
					}
					controller.hideProgressDialog();
					shutdown("Pre-launch update app finished");
				}
				catch (Throwable e)
				{
					String message = INSTALL_FAILED_MESSAGE + ": " + e.getMessage();
					s_log.error(message, e);
					showErrorDialog(message);
				}

			}

		});
		t.setName("Update Installer Thread");
		t.start();
	}

	/**
	 * Ask the user a question
	 * 
	 * @param message
	 *           the question to ask
	 * @param title
	 *           the title of the dialog
	 * @return true if they said YES; false otherwise.
	 */
	private boolean showConfirmDialog(String message, String title)
	{
		int choice =
			JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}

	/**
	 * Show the user an error dialog.
	 * 
	 * @param message
	 *           the message to give in the dialog.
	 */
	private void showErrorDialog(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void logInfo(String message)
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info(message);
		}
	}

	/**
	 * @param scriptLocation
	 *           the scriptLocation to set
	 */
	public void setScriptLocation(String scriptLocation)
	{
		this.scriptLocation = scriptLocation;
	}

	/**
	 * @return the scriptLocation
	 */
	public String getScriptLocation()
	{
		return scriptLocation;
	}

}
