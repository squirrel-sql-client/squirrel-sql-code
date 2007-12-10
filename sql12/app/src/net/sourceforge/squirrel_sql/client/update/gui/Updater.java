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
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is a small application that will be launched to quickly check to see if
 * updates are available to be applied.
 * 
 * @author manningr
 *
 */
public class Updater {

   private static String MESSAGE;
   private static String TITLE;
   
   /* Internationalized strings for this class */
   private static StringManager s_stringMgr;
   
   static {
      ApplicationArguments.initialize(new String[0]);

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
   
   public static void installUpdates() {
      // 1. Show a progress dialog based on how many files to backup.
      
      // 2. Launch a thread to start backing up files that will be 
      //    removed or updated, and report status back to the progress
      //    dialog.
      
      // 3. Show another progress dialog for copying over the changes.
      
      // 4. Start a new thread to copy over the changes and report status
      //    back to the progress dialog.      
   }
   
   /**
    * @param args
    */
   public static void main(String[] args) {
      boolean prompt = false;
      if (args.length > 0){
         if (args[0].equals("-prompt")) {
            prompt=true;
         }
      }
      
      UpdateUtilImpl util = new UpdateUtilImpl();
      File f = util.getChangeListFile();
      if (f.exists()) {
         if (prompt) {
            if (showConfirmDialog()) {
               installUpdates();
            }
         } else {
            installUpdates();            
         }
      }
      

   }

   
}
