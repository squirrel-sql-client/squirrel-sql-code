package net.sourceforge.squirrel_sql.client.session.action.dataimport.action;
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.EDTMessageBoxUtil;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.ImportFileUtils;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.gui.ImportFileDialogCtrl;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.ConfigurationPanel;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.FileImporterFactory;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This command shows the necessary dialogs to import a file.
 *
 * @author Thorsten Mürell
 */
public class ImportTableDataCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportTableDataCommand.class);

   private final static ILogger s_log = LoggerController.createLogger(ImportTableDataCommand.class);



   private ISession _session;
   private IResources _resources;
   private ITableInfo _table;


   public ImportTableDataCommand(ISession session, IResources resources)
   {
      this(session, resources, null);
   }

   /**
    * @param session The session to work in
    * @param table   The table to import the data
    */
   public ImportTableDataCommand(ISession session, IResources resources, ITableInfo table)
   {
      _session = session;
      _resources = resources;
      _table = table;
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
      try
      {
         ImportFileChooserDialog importFileChooser = new ImportFileChooserDialog(_resources,_table);

         FileDisplayWrapper fileDisplayWrapper;

         if(importFileChooser.isImportFromClipBoard())
         {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tran = clipboard.getContents(null);
            if (tran != null && tran.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
               String clipContent = (String) tran.getTransferData(DataFlavor.stringFlavor);

               Path tempFile = Files.createTempFile("squirrel-clipboard-import", ".csv");
               tempFile.toFile().deleteOnExit();

               try (BufferedWriter writer = Files.newBufferedWriter(tempFile))
               {
                  writer.write(clipContent);
               }

               fileDisplayWrapper = new FileDisplayWrapper(tempFile.toFile(), true);
            }
            else
            {
               throw new IllegalStateException("Failed to interpret clipboard as String");
            }
         }
         else
         {
            if (null ==  importFileChooser.getImportFile())
            {
               return;
            }

            fileDisplayWrapper = new FileDisplayWrapper(importFileChooser.getImportFile(), false);
         }


         ImportFileType type = ImportFileUtils.determineType(fileDisplayWrapper.getFile());

         IFileImporter importer = FileImporterFactory.createImporter(type, fileDisplayWrapper.getFile());

         ConfigurationPanel configurationPanel = importer.createConfigurationPanel();

         if (configurationPanel != null)
         {
            //i18n[ImportTableDataCommand.settingsDialogTitle=Import file settings]
            final JDialog dialog = new JDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("ImportTableDataCommand.settingsDialogTitle"), true);
            StateListener dialogState = new StateListener(dialog);
            dialog.setLayout(new BorderLayout());
            dialog.add(configurationPanel, BorderLayout.CENTER);
            OkClosePanel buttons = new OkClosePanel();

            buttons.getCloseButton().setText(s_stringMgr.getString("ImportTableDataCommand.cancel"));
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

         importFileDialogCtrl = new ImportFileDialogCtrl(_session, fileDisplayWrapper, importer, _table);

         importFileDialogCtrl.setPreviewData(importer.getPreview(10));

         importFileDialogCtrl.show();



      }
      catch (Exception e)
      {
         s_log.error("execute: unexpected exception - " + e.getMessage(), e);
         EDTMessageBoxUtil.showMessageDialogOnEDT(s_stringMgr.getString("ImportTableDataCommand.ioErrorOccured"), s_stringMgr.getString("ImportTableDataCommand.error"));
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
