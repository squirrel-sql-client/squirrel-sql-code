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

import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.RELEASE_XML_FILENAME;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.CheckUpdateListener;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateManagerDialog;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateSummaryDialog;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.UpdateSettings;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class implements the business logic needed by the view
 * (UpdateManagerDialog), to let the user to install new or updated software
 * (the model)
 * 
 * @author manningr
 */
public class UpdateControllerImpl implements UpdateController,
      CheckUpdateListener {

   /** Logger for this class. */
   private static final ILogger s_log =
      LoggerController.createLogger(UpdateControllerImpl.class);
   
   /** the application and services it provides */
   private IApplication _app = null;

   /** user's preferences about updates */
   private UpdateSettings _settings = null;

   /** utility class for low-level update routines */
   private UpdateUtil _util = null;

   /** the release that we downloaded when we last checked */
   private ChannelXmlBean _currentChannelBean = null;

   /** the release we had installed the last time we checked / updated */
   private ChannelXmlBean _installedChannelBean = null;
   
   /** the time that we last checked the server to see if we were uptodate */
   private long _timeOfLastCheck = -1;

   /**
    * Constructor
    * 
    * @param app
    *           the application and services it provides
    */
   public UpdateControllerImpl(IApplication app) {
      _app = app;
      _settings = app.getSquirrelPreferences().getUpdateSettings();
   }

   /**
    * Sets the utility class for low-level update routines
    * @param util the Update utility class to use.
    */
   public void setUpdateUtil(UpdateUtil util) {
      this._util = util;
      _util.setPluginManager(_app.getPluginManager());
   }
   
   /*
    * (non-Javadoc)
    * 
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#showUpdateDialog()
    */
   public void showUpdateDialog() {
      JFrame parent = _app.getMainFrame();
      UpdateManagerDialog dialog = new UpdateManagerDialog(parent);
      dialog.setUpdateServerName(_settings.getUpdateServer());
      dialog.setUpdateServerPort(_settings.getUpdateServerPort());
      dialog.setUpdateServerPath(_settings.getUpdateServerPath());
      dialog.setUpdateServerChannel(_settings.getUpdateServerChannel());
      dialog.addCheckUpdateListener(this);
      dialog.setVisible(true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#isUpToDate()
    */
   public boolean isUpToDate() throws Exception {
      boolean result = true;

      // 1. Find the local release.xml file
      String releaseFilename = _util.getLocalReleaseFile();

      // 2. Load the local release.xml file as a ChannelXmlBean.
      _installedChannelBean = _util.getLocalReleaseInfo(releaseFilename);

      // 3. Determine the channel that the user has (stable or snapshot)
      String channelName = _installedChannelBean.getName();

      StringBuilder releasePath = new StringBuilder("/");
      releasePath.append(getUpdateServerPath());
      releasePath.append("/");
      releasePath.append(channelName);
      releasePath.append("/");

      // 4. Get the release.xml file as a ChannelXmlBean from the server      
      _currentChannelBean = _util.downloadCurrentRelease(getUpdateServerName(),
                                                         getUpdateServerPortAsInt(),
                                                         releasePath.toString(),
                                                         RELEASE_XML_FILENAME);

      _timeOfLastCheck = System.currentTimeMillis();

      // 5. Is it the same as the local copy, which was placed either by the
      // installer or the last update?
      return _currentChannelBean.equals(_installedChannelBean);
   }

   /**
    * Returns a set of plugins (internal names) of plugins that are currently
    * installed (regardless of whether or not they are enabled).
    * 
    * @return a set of plugin internal names
    */
   public Set<String> getInstalledPlugins() {
      Set<String> result = new HashSet<String>();
      PluginManager pmgr = _app.getPluginManager();
      PluginInfo[] infos = pmgr.getPluginInformation();
      for (PluginInfo info : infos) {
         result.add(info.getInternalName());
      }
      return result;
   }

   /**
    * Go get the files that need to be updated.
    * 
    * @return
    */
   public boolean pullDownUpdateFiles() {
      return true;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#getUpdateServerChannel()
    */
   public String getUpdateServerChannel() {
      return _settings.getUpdateServerChannel();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#getUpdateServerName()
    */
   public String getUpdateServerName() {
      return _settings.getUpdateServer();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#isRemoteUpdateSite()
    */
   public boolean isRemoteUpdateSite() {
      return _settings.isRemoteUpdateSite();
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#getUpdateServerPath()
    */
   public String getUpdateServerPath() {
      return _settings.getUpdateServerPath();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#getUpdateServerPort()
    */
   public String getUpdateServerPort() {
      return _settings.getUpdateServerPort();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#getUpdateServerPortAsInt()
    */
   public int getUpdateServerPortAsInt() {
      return Integer.parseInt(getUpdateServerPort());
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#showMessage(java.lang.String,
    *      java.lang.String)
    */
   public void showMessage(String title, String msg) {
      JOptionPane.showMessageDialog(_app.getMainFrame(),
                                    msg,
                                    title,
                                    JOptionPane.INFORMATION_MESSAGE);

   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#showErrorMessage(java.lang.String, java.lang.String)
    */
   public void showErrorMessage(String title, String msg, Exception e) {
      s_log.error(msg, e);
      JOptionPane.showMessageDialog(_app.getMainFrame(),
                                    msg,
                                    title,
                                    JOptionPane.ERROR_MESSAGE);
      
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#showErrorMessage(java.lang.String, java.lang.String)
    */
   public void showErrorMessage(String title, String msg) {
      showErrorMessage(title, msg, null);
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#checkUpToDate()
    */
   public void checkUpToDate() {
      // TODO: I18n
      try {
         if (isUpToDate()) {
            showMessage("Update Check", "Software is the latest version.");
         } else {
            List<ArtifactStatus> artifactStatusItems = 
               this._util.getArtifactStatus(_currentChannelBean);
            UpdateSummaryDialog dialog = new UpdateSummaryDialog(_app.getMainFrame(),
                                                                 artifactStatusItems,
                                                                 this);
            String installedVersion = 
               _installedChannelBean.getCurrentRelease().getVersion();
            dialog.setInstalledVersion(installedVersion);
            
            String currentVersion =
               _currentChannelBean.getCurrentRelease().getVersion();
            dialog.setAvailableVersion(currentVersion);
            
            GUIUtils.centerWithinParent(_app.getMainFrame());
            dialog.setVisible(true);
         }
      } catch (Exception e) {
         showErrorMessage("Update Check Failed", "Exception was - "
               + e.getClass().getName() + ":" + e.getMessage(), e);
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.UpdateController#applyChanges(java.util.List)
    */
   public void applyChanges(List<ArtifactStatus> artifactStatusList) {
      try {
         // Persists the change list to the update directory.
         _util.saveChangeList(artifactStatusList);
      
         // Kick off a thread to go and fetch the files one-by-one.
         
         // When all updates are retrieved, consult the user to see if they want to install now or upon the 
         // next startup.
         
         // If install now, then backup files to be updated/removed and shutdown
         // so that the updater process can run.
         
         // shutdown and start the updater.
         
         // TODO the updater should be started each time SQuirreL is launched to 
         // quickly check to see if updates need to be applied and prompt the 
         // user each time updates are available to be applied, to see if they
         // want to apply them.
         
      } catch (Exception e) {
         showErrorMessage("Update Failed", "Exception was - "
                          + e.getClass().getName() + ":" + e.getMessage(), e);         
      }
      
   }
   
   
   
}
