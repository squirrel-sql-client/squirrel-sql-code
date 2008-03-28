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

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
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
	private String MESSAGE;

	/** 
	 * The title of the dialect that  we show the user in the update dialog that is shown when there are 
	 * updates to install 
	 */
	private String TITLE;

	/* Internationalized strings for this class */
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
	
	public PreLaunchHelperImpl() {
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(false));
		s_log = LoggerController.createLogger(PreLaunchHelperImpl.class);

		//updateUtil = new UpdateUtilImpl();

		s_stringMgr = StringManagerFactory.getStringManager(PreLaunchHelperImpl.class);

		// i18n[Updater.message=Updates are ready to be installed. Install them
		// now?]
		MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.message");

		// i18n[Updater.title=Updates Available]
		TITLE = s_stringMgr.getString("PreLaunchHelperImpl.title");

	}
	
	/**
	 * @param prompt
	 */
	public void installUpdates(boolean prompt)
	{
		try
		{
			File changeListFile = updateUtil.getChangeListFile();
			if (changeListFile.exists())
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("Pre-launch update app detected a changeListFile to be processed");
				}
				if (prompt)
				{
					if (showConfirmDialog())
					{
						installUpdates(changeListFile);
					} else
					{
						if (s_log.isInfoEnabled())
						{
							s_log.info("User cancelled update installation");
						}
					}
				} else
				{
					installUpdates(changeListFile);
				}
			} else {
				if (s_log.isInfoEnabled())
				{
					s_log.info("installUpdates: changeList file ("+changeListFile+") doesn't exist.");
				}				
			}
		} catch (Throwable e)
		{
			s_log.error("Unexpected error while attempting to install updates: " + e.getMessage(), e);
		} finally
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("Pre-launch update app finished");
			}
			LoggerController.shutdown();
			System.exit(0);
		}
	}

	/* ------------------------------------- Helper methods ------------------------------------------------*/	
	
	/**
	 * @param changeList
	 * @throws Exception
	 */
	private void installUpdates(File changeList) throws Exception
	{
		ArtifactInstaller installer = artifactInstallerFactory.create(updateUtil, changeList);
		installer.addListener(new InstallStatusListenerImpl());
		installer.backupFiles();
		installer.installFiles();
	}
	
	/**
	 * Ask the user if they want to apply the updates.
	 * 
	 * @return true if they said YES; false otherwise.
	 */
	private boolean showConfirmDialog()
	{
		int choice =
			JOptionPane.showConfirmDialog(null,
				MESSAGE,
				TITLE,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}
	
}
