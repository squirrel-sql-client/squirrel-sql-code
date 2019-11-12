package net.sourceforge.squirrel_sql.plugins.dataimport.action;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dataimport.EDTMessageBoxUtil;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileUtils;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportFileDialogCtrl;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.ConfigurationPanel;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.FileImporterFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;

/**
 * This command shows the necessary dialogs to import a file.
 *
 * @author Thorsten Mürell
 */
public class ImportTableDataCommand
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ImportTableDataCommand.class);

   /**
    * Logger for this class.
    */
   private final static ILogger s_log = LoggerController.createLogger(ImportTableDataCommand.class);

   private static final String PREFS_KEY_LAST_IMPORT_DIRECTORY = "squirrelsql_dataimport_last_import_directory";


   private ISession session;
   private ITableInfo table;


   public ImportTableDataCommand(ISession session)
   {
      this(session, null);
   }

   /**
    * @param session The session to work in
    * @param table   The table to import the data
    */
   public ImportTableDataCommand(ISession session, ITableInfo table)
   {
      this.session = session;
      this.table = table;
   }

   /**
    * This is the command action.
    * <p>
    * It shows a file open dialog and then the specific import options for the file
    * importer.
    * <p>
    * Then the column mapping dialog is shown.
    */
   public void execute()
   {
      JFileChooser openFile = new JFileChooser(Props.getString(PREFS_KEY_LAST_IMPORT_DIRECTORY, System.getProperty("user.home")));

      int res = openFile.showOpenDialog(session.getApplication().getMainFrame());

      if (res == JFileChooser.APPROVE_OPTION)
      {
         File importFile = openFile.getSelectedFile();

         if (null != importFile.getParent())
         {
            Props.putString(PREFS_KEY_LAST_IMPORT_DIRECTORY, importFile.getParent());
         }

         try
         {

            ImportFileType type = ImportFileUtils.determineType(importFile);


            IFileImporter importer = FileImporterFactory.createImporter(type, importFile);

            ConfigurationPanel configurationPanel = importer.createConfigurationPanel();

            if (configurationPanel != null)
            {
               //i18n[ImportTableDataCommand.settingsDialogTitle=Import file settings]
               final JDialog dialog = new JDialog(Main.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.settingsDialogTitle"), true);
               StateListener dialogState = new StateListener(dialog);
               dialog.setLayout(new BorderLayout());
               dialog.add(configurationPanel, BorderLayout.CENTER);
               OkClosePanel buttons = new OkClosePanel();

               buttons.getCloseButton().setText(stringMgr.getString("ImportTableDataCommand.cancel"));
               buttons.addListener(dialogState);
               dialog.add(buttons, BorderLayout.SOUTH);
               dialog.pack();
               GUIUtils.centerWithinParent(dialog);
               dialog.setVisible(true);
               if (dialogState.isOkPressed())
               {
                  configurationPanel.apply();
               }
               else
               {
                  return;
               }
            }


            ImportFileDialogCtrl importFileDialogCtrl;

            importFileDialogCtrl = new ImportFileDialogCtrl(session, importFile, importer, table);

            importFileDialogCtrl.setPreviewData(importer.getPreview(10));

            importFileDialogCtrl.show();

         }
         catch (IOException e)
         {
            s_log.error("execute: unexpected exception - " + e.getMessage(), e);
            EDTMessageBoxUtil.showMessageDialogOnEDT(stringMgr.getString("ImportTableDataCommand.ioErrorOccured"), stringMgr.getString("ImportTableDataCommand.error"));
         }
      }
   }

   private class StateListener implements IOkClosePanelListener
   {
      private boolean okPressed = false;
      private JDialog dialog = null;

      /**
       * The constructor
       *
       * @param dialog The dialog
       */
      public StateListener(JDialog dialog)
      {
         this.dialog = dialog;
      }

      /**
       * Invoked on cancel press
       *
       * @param evt The event
       */
      public void cancelPressed(OkClosePanelEvent evt)
      { /* Not needed */ }

      /**
       * Invoked on close press
       *
       * @param evt The event
       */
      public void closePressed(OkClosePanelEvent evt)
      {
         okPressed = false;
         dialog.dispose();
      }

      /**
       * Invoked on ok press
       *
       * @param evt The event
       */
      public void okPressed(OkClosePanelEvent evt)
      {
         okPressed = true;
         dialog.dispose();
      }

      /**
       * Returns if the OK button was pressed.
       *
       * @return true or false
       */
      public boolean isOkPressed()
      {
         return okPressed;
      }
   }

}
