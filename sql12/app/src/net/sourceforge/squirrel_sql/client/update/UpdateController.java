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
package net.sourceforge.squirrel_sql.client.update;

import java.util.List;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

/**
 * This interface allows the update summary view to send messages to the 
 * controller based on the user's interaction with the view.  This allows the 
 * view to be completely unaware of the UpdateController's implementation; the 
 * business logic is separated from the view.  Any logic that doesn't directly 
 * affect the view in some way, should be located in this interface.
 * 
 * @author manningr
 */
public interface UpdateController {

   /**
    * Returns a boolean value indicating whether or not there are updates
    * available to be installed. The sequence of steps involved is :
    * 
    * 1. Find the local release.xml file 
    * 2. Load the local release.xml file as a ChannelXmlBean. 
    * 3. Determine the channel that the user has (stable or snapshot) 
    * 4. Get the release.xml file as a ChannelXmlBean from the server
    * 5. Determine if it is the same as the local copy, which was placed either
    * by the installer or the last update?
    * 
    * @return true if the installed software is latest; false otherwise.
    */
   //boolean isUpToDate() throws Exception;

   String getUpdateServerName();

   String getUpdateServerPort();

   String getUpdateServerPath();

   String getUpdateServerChannel();

   int getUpdateServerPortAsInt();

   void showMessage(String title, String msg);

   boolean showConfirmMessage(String title, String msg);   
   
   void showErrorMessage(String title, String msg, Exception e);
   
   void showErrorMessage(String title, String msg);
   
   void showUpdateDialog();
   
   /**
    * Ask the UpdateController to check for updated artifacts.
    */
   void checkUpToDate();
   
   /**
    * The user wishes to add/update/remove the specified list of Artifacts. Each
    * artifact status specifies the user's desired action. This will :
    * 
    * 1. Persist the list of actions to a change list file in the update dir.
    * 2. Start a background thread to retrieve each update file from the server.
    * 3. When downloading is complete, ask the user if they want to install now.
    * 4. If the user wants to install now :
    *    a. Backup files that will be removed/updated.
    *    b. shutdown and launch the updater
    *    c. Updater installs updated files
    *    d. SQuirreL starts again.
    *    
    * @param artifactStatusList the list of changes to make to 
    *                           installed/available artifacts. 
    * @param releaseVersionWillChange a boolean value indicating whether or not the changes being applied will
    * 										  change the installed release version
    */
   void applyChanges(List<ArtifactStatus> artifactStatusList, boolean releaseVersionWillChange);
   
   /**
    * Returns a boolean value to indicate whether or not the user wants to use 
    * a remote site.  If this is false, it is assumed to mean that the user 
    * wants to specify a local directory on the filesystem
    * 
    * @return true if remote site; false otherwise.
    */   
   boolean isRemoteUpdateSite();

   /**
    * 
    * @return
    */
	public boolean isTimeToCheckForUpdates();

	public void promptUserToDownloadAvailableUpdates();

	public JFrame getMainFrame();

	

      
}
