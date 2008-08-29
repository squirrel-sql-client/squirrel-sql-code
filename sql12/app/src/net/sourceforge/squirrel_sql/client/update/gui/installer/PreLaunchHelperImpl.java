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

import java.io.IOException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This is a bean that the prelaunch app uses.  The pre-launch app main class (PreLaunchUpdateApplication)
 * loads the spring context, and therefore can't managed by spring.  So, it is very small and most of it's 
 * logic for doing updates resides here.
 * 
 * @author manningr
 */
public class PreLaunchHelperImpl implements PreLaunchHelper
{

	/** The message we show the user in the update dialog that is shown when there are updates to install */
	private static String INSTALL_UPDATES_MESSAGE;

	/** 
	 * The title of the dialect that  we show the user in the update dialog that is shown when there are 
	 * updates to install 
	 */
	private static String INSTALL_UPDATES_TITLE;

	private static String RESTORE_FROM_BACKUP_TITLE;
	
	private static String RESTORE_FROM_BACKUP_MESSAGE;
	
	private static String RESTORE_FAILED_MESSAGE;
	
	private static String BACKUP_FAILED_MESSAGE;
	
	private static String INSTALL_FAILED_MESSAGE;
	
	/** Internationalized strings for this class */
	private StringManager s_stringMgr;

	/** Logger for this class. */
	private ILogger s_log;
	
	/* --------------------------- Spring=injected dependencies --------------------------------------------*/
	
	/* Spring-injected */
	private UpdateUtil updateUtil = null;
	public void setUpdateUtil(UpdateUtil util) { this.updateUtil = util; }
	
	/* Spring-injected */	
	private ArtifactInstallerFactory artifactInstallerFactory = null;
	public void setArtifactInstallerFactory(ArtifactInstallerFactory artifactInstallerFactory)
	{
		this.artifactInstallerFactory = artifactInstallerFactory;
	}

	/* ----------------------------------- Public API ------------------------------------------------------*/
	
	public PreLaunchHelperImpl() throws IOException {
		
		s_log = LoggerController.createLogger(PreLaunchHelperImpl.class);
		s_stringMgr = StringManagerFactory.getStringManager(PreLaunchHelperImpl.class);

		// i18n[PreLaunchHelperImpl.installUpdatesTitle=Updates Available]
		INSTALL_UPDATES_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesTitle");		
		
		// i18n[PreLaunchHelperImpl.installUpdatesMessage=Updates are ready to be installed. Install them now?]
		INSTALL_UPDATES_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installUpdatesMessage");

		//i18n[PreLaunchHelperImpl.restoreFromBackupTitle=Confirm Restore From Backup
		RESTORE_FROM_BACKUP_TITLE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupTitle");
		
		//i18n[PreLaunchHelperImpl.restoreFromBackupMessage=Restore SQuirreL to previous version before 
		//last update?]
		RESTORE_FROM_BACKUP_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFromBackupMessage");
		
		//i18n[PreLaunchHelperImpl.backupFailedMessage=Backup of existing files failed. Installation cannot 
		//proceed]
		BACKUP_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.backupFailedMessage");
		
		//i18n[PreLaunchHelperImpl.installFailedMessage=Unexpected error while attempting to install updates]
		INSTALL_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.installFailedMessage");
		
		//i18n[PreLaunchHelperImpl.restoreFailedMessage=Restore from backup failed.  Re-installation may be 
		//required.
		RESTORE_FAILED_MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.restoreFailedMessage");
		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelper#installUpdates(boolean)
	 */
	public void installUpdates(boolean prompt)
	{
		try
		{
			FileWrapper changeListFile = updateUtil.getChangeListFile();
			if (changeListFile.exists())
			{
				logInfo("Pre-launch update app detected a changeListFile to be processed");
				if (prompt)
				{
					if (showConfirmDialog(INSTALL_UPDATES_MESSAGE, INSTALL_UPDATES_TITLE))
					{
						installUpdates(changeListFile);
					} else
					{
						logInfo("User cancelled update installation");
					}
				} else
				{
					installUpdates(changeListFile);
				}
			} else {
				logInfo("installUpdates: changeList file ("+changeListFile+") doesn't exist.");
			}
		} catch (Throwable e)
		{
			String message = INSTALL_FAILED_MESSAGE + ": " + e.getMessage();
			s_log.error(message, e);
			showErrorDialog(message);
		}
		shutdown("Pre-launch update app finished");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelper#restoreFromBackup()
	 */
	public void restoreFromBackup()
	{
		if (showConfirmDialog(RESTORE_FROM_BACKUP_MESSAGE, RESTORE_FROM_BACKUP_TITLE)) {
			
			try {
				FileWrapper backupDir = updateUtil.getBackupDir();
				FileWrapper changeListFile = updateUtil.getFile(backupDir, UpdateUtil.CHANGE_LIST_FILENAME);
				ChangeListXmlBean changeList = updateUtil.getChangeList(changeListFile);
			
				ArtifactInstaller installer = artifactInstallerFactory.create(changeList);
				if (!installer.restoreBackupFiles()) {
					showErrorDialog(RESTORE_FAILED_MESSAGE);
					s_log.error("restoreFromBackup: "+RESTORE_FAILED_MESSAGE);
				}
				
			} catch (Throwable e) {
				s_log.error("Unexpected error while attempting restore from backup: " + e.getMessage(), e);
				showErrorDialog(RESTORE_FAILED_MESSAGE);
			}
			
		}
		shutdown("Pre-launch update app finished");
	}
	
	
	/* ------------------------------------- Helper methods ------------------------------------------------*/	
		
	/**
	 * Shuts down this small pre-launch helper application.
	 */
	private void shutdown(String message) {
		if (s_log.isInfoEnabled())
		{
			s_log.info(message);
		}
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
	private void installUpdates(FileWrapper changeList) throws Exception
	{
		ArtifactInstaller installer = artifactInstallerFactory.create(changeList);
		installer.addListener(new InstallStatusListenerImpl());
		if (installer.backupFiles()) {
			installer.installFiles();
		} else {
			showErrorDialog(BACKUP_FAILED_MESSAGE);
		}
	}
	
	
	/**
	 * Ask the user a question
	 * 
	 * @param message the question to ask
	 * @param title the title of the dialog
	 * 
	 * @return true if they said YES; false otherwise.
	 */
	private boolean showConfirmDialog(String message, String title)
	{
		int choice =
			JOptionPane.showConfirmDialog(null,
				message,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}

	/**
	 * Show the user an error dialog.
	 * 
	 * @param message the message to give in the dialog.
	 */
	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void logInfo(String message) {
		if (s_log.isInfoEnabled()) {
			s_log.info(message);
		}
	}
}
