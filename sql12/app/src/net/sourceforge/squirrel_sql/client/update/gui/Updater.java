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
package net.sourceforge.squirrel_sql.client.update.gui;

import java.io.File;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.update.ArtifactInstaller;
import net.sourceforge.squirrel_sql.client.update.InstallEventType;
import net.sourceforge.squirrel_sql.client.update.InstallStatusEvent;
import net.sourceforge.squirrel_sql.client.update.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This is a small application that will be launched each time SQuirreL is 
 * started to quickly check to see if updates are available to be applied.
 * 
 * @author manningr
 *
 */
public class Updater {

   private static String MESSAGE;
   private static String TITLE;
   
   /* Internationalized strings for this class */
   private static StringManager s_stringMgr;
   
   private static UpdateUtil util = null;
   
   /** Logger for this class. */
   private static ILogger s_log;
   
   static {
      ApplicationArguments.initialize(new String[0]);
      LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(false));
      s_log = LoggerController.createLogger(Updater.class);
      
      util = new UpdateUtilImpl();
      
      s_stringMgr = 
         StringManagerFactory.getStringManager(Updater.class);

      // i18n[Updater.message=Updates are ready to be installed.  Install them 
      // now?]      
      MESSAGE = s_stringMgr.getString("Updater.message");

      // i18n[Updater.title=Updates Available]
      TITLE = s_stringMgr.getString("Updater.title");
   
   }
   
   /**
    * Ask the user if they want to apply the updates.
    * 
    * @return true if they said YES; false otherwise.
    */
   public static boolean showConfirmDialog() {
      int choice = 
         JOptionPane.showConfirmDialog(
         null, MESSAGE, TITLE, JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE);
      return choice == JOptionPane.YES_OPTION;
   }
   
   public static void installUpdates(File changeList) throws Exception {
      ArtifactInstaller installer = new ArtifactInstaller(util, changeList);
      installer.addListener(new MyInstallStatusListener());      
      installer.backupFiles();
      installer.installFiles();      
   }
   
   /**
    * @param args
    */
   public static void main(String[] args) {
      boolean prompt = false;
      if (args != null && args.length > 0){
         if (args[0].equals("-prompt")) {
            prompt=true;
         }
      }
      try {
         File changeListFile = util.getChangeListFile();
         if (changeListFile.exists()) {
            if (s_log.isInfoEnabled()) {
               s_log.info("Updater detected a changeListFile to be processed");
            }            
            if (prompt) {
               if (showConfirmDialog()) {
                  installUpdates(changeListFile);
               } else {
                  if (s_log.isInfoEnabled()) {
                     s_log.info("User cancelled update installation");
                  }
               }
            } else {
               installUpdates(changeListFile);            
            }
         }
      } catch (Throwable e) {
         s_log.error("Unexpected error while attempting to install updates: "
               + e.getMessage(), e);         
      } finally {
         if (s_log.isInfoEnabled()) {
            s_log.info("Updater finished");
         }
         LoggerController.shutdown();
         System.exit(0);
      }
   }
   
   private static class MyInstallStatusListener implements InstallStatusListener {

      /**
       * @see net.sourceforge.squirrel_sql.client.update.InstallStatusListener#handleInstallStatusEvent(net.sourceforge.squirrel_sql.client.update.InstallStatusEvent)
       */
      public void handleInstallStatusEvent(InstallStatusEvent evt) {
         if (evt.getType() == InstallEventType.BACKUP_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_BACKUP_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_BACKUP_COMPLETE) {}
         if (evt.getType() == InstallEventType.FILE_INSTALL_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_INSTALL_COMPLETE) {}
         if (evt.getType() == InstallEventType.BACKUP_COMPLETE) {}
      }
      
   }
}
