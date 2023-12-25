package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.*;
import net.sourceforge.squirrel_sql.client.gui.db.aliasdndtree.AliasDndTreeHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class AliasTransferCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasTransferCtrl.class);

   private static final String PREF_LAST_EXPORT_FILE_DIR = "AliasTransferCtrl.last.export.file.dir";

   private static final String ZIP_ENTRY_SQL_ALIASES = "export_SQLAliases23.xml";
   private static final String ZIP_ENTRY_ALIAS_TREE = "export_SQLAliases23_treeStructure.xml";
   private static final String ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME = "export_driverIdentifierToName.props";

   private final AliasTransferDlg _dlg;
   private IToogleableAliasesList _aliasesList;

   public AliasTransferCtrl(IToogleableAliasesList aliasesList)
   {
      _aliasesList = aliasesList;
      _dlg = new AliasTransferDlg(Main.getApplication().getMainFrame());

      locateDialogBesidesAliases();

      AliasDndTreeHandler exportImportTreeHandler = new AliasDndTreeHandler(_dlg.treeExportedAliases);

      _dlg.btnExport.addActionListener(e -> onExport(exportImportTreeHandler));
      _dlg.btnImport.addActionListener(e -> onImport(exportImportTreeHandler));
      _dlg.btnUpdate.addActionListener(e -> onUpdate(exportImportTreeHandler));
      _dlg.btnUpdate.setEnabled(false);

      GUIUtils.enableCloseByEscape(_dlg);
      _dlg.setVisible(true);
   }

   private void onUpdate(AliasDndTreeHandler exportImportTreeHandler)
   {
      new AliasImportUpdateCtrl(_dlg, exportImportTreeHandler, _aliasesList);
   }

   private void onImport(AliasDndTreeHandler exportImportTreeHandler)
   {
      JFileChooser importFC = createFileChooser();

      importFC.setDialogTitle(s_stringMgr.getString("AliasTransferCtrl.import.file.dialog.title"));

      if (importFC.showOpenDialog(_dlg) != JFileChooser.APPROVE_OPTION)
      {
         return;
      }

      File importFile = importFC.getSelectedFile();
      Props.putString(PREF_LAST_EXPORT_FILE_DIR, importFile.getParent());

      try (ZipFile zipIn = new ZipFile(importFile);
           Java8CloseableFix dum = Main.getApplication().getGlobalSQLAliasVersioner().switchOff())
      {
         Enumeration<? extends ZipEntry> entries = zipIn.entries();

         XMLBeanReader sqlAliasesReader = null;
         XMLBeanReader treeFolderStateReader = null;
         Properties driverIdentifierToName = null;
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();

            if (ZIP_ENTRY_SQL_ALIASES.equals(entry.getName()))
            {
               sqlAliasesReader = new XMLBeanReader();
               sqlAliasesReader.load(zipIn.getInputStream(entry));
            }
            else if (ZIP_ENTRY_ALIAS_TREE.equals(entry.getName()))
            {
               treeFolderStateReader = new XMLBeanReader();
               treeFolderStateReader.load(zipIn.getInputStream(entry));
            }
            else if (ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME.equals(entry.getName()))
            {
               driverIdentifierToName = new Properties();
               try (InputStreamReader rdr = new InputStreamReader(zipIn.getInputStream(entry)))
               {
                  driverIdentifierToName.load(rdr);
               }
            }
            else
            {
               throw new IllegalStateException("Unexpected zip entry: " + entry.getName());
            }
         }

         if (null == sqlAliasesReader)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_SQL_ALIASES);
         }
         if (null == treeFolderStateReader)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_ALIAS_TREE);
         }
         if (null == driverIdentifierToName)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME);
         }

         List<SQLAlias> sqlAliases = sqlAliasesReader.getBeans();

         AssignDriversCtrl assignDriversCtrl = new AssignDriversCtrl(driverIdentifierToName, sqlAliases, _dlg);

         if (false == assignDriversCtrl.areAllDriversAssigned())
         {
            return;
         }

         assignDriversCtrl.updateDriverIdentifiersInAliases(sqlAliases, assignDriversCtrl);

         AliasFolderState aliasFolderState = (AliasFolderState) treeFolderStateReader.getBeans().get(0);

         exportImportTreeHandler.load(sqlAliases, aliasFolderState);

         _dlg.btnUpdate.setEnabled(true);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private void onExport(AliasDndTreeHandler exportImportTreeHandler)
   {
      try
      {
         if(exportImportTreeHandler.isEmpty())
         {
            JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasTransferCtrl.no.aliases.to.export"));

            return;
         }

         JFileChooser exportFC = createFileChooser();
         exportFC.setDialogTitle(s_stringMgr.getString("AliasTransferCtrl.export.file.dialog.title"));

         if (exportFC.showSaveDialog(_dlg) != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File exportFile = exportFC.getSelectedFile();

         Props.putString(PREF_LAST_EXPORT_FILE_DIR, exportFile.getParent());


         if(false == exportFile.getName().toLowerCase().endsWith(".zip"))
         {
            exportFile = new File(exportFile.getAbsolutePath() + ".zip");
         }

         List<SQLAlias> sqlAliasesToExport = exportImportTreeHandler.getSqlAliasList();
         XMLBeanWriter sqlAliasesWriter = new XMLBeanWriter();
         sqlAliasesWriter.addIteratorToRoot(sqlAliasesToExport.iterator());


         AliasFolderState state = exportImportTreeHandler.getAliasFolderState();
         XMLBeanWriter treeFolderStateWriter = new XMLBeanWriter(state);


         try (FileOutputStream fileOutputStream = new FileOutputStream(exportFile);
              ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream))
         {
            writeXMLBeanZipEntry(zipOut, sqlAliasesWriter, ZIP_ENTRY_SQL_ALIASES);
            writeXMLBeanZipEntry(zipOut, treeFolderStateWriter, ZIP_ENTRY_ALIAS_TREE);

            writeDriverIdentifierToNamePropZipEntry(sqlAliasesToExport, zipOut);

            zipOut.flush();
            fileOutputStream.flush();
         }

         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("AliasTransferCtrl.file.export.success.msg", exportFile.getAbsolutePath()));


         String[] selectionValues =
               {
                     s_stringMgr.getString("AliasTransferCtrl.export.completed.ok"),
                     s_stringMgr.getString("AliasTransferCtrl.export.completed.ok.show.in.file.manager"),
               };

         int selectIndex = JOptionPane.showOptionDialog(
               _dlg,
               s_stringMgr.getString("AliasTransferCtrl.file.export.success.dlg.msg", exportFile.getAbsolutePath()),
               s_stringMgr.getString("AliasTransferCtrl.file.export.success.dlg.title"),
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               null,
               selectionValues,
               selectionValues[0]);

         if (selectIndex == 1)
         {
            DesktopUtil.openInFileManager(exportFile);
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void writeDriverIdentifierToNamePropZipEntry(List<SQLAlias> sqlAliasesToExport, ZipOutputStream zipOut) throws IOException
   {
      Properties props = createDriverIdentifierToNameProps(sqlAliasesToExport);

      ZipEntry zipEntry = new ZipEntry(ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME);
      zipOut.putNextEntry(zipEntry);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      props.store(bos, "JBCD-Driver identifier to driver name");
      zipOut.write(bos.toByteArray());
      zipOut.flush();
      zipOut.closeEntry();
   }

   private Properties createDriverIdentifierToNameProps(List<SQLAlias> sqlAliasesToExport)
   {
      Properties ret = new Properties();

      AliasesAndDriversManager aliasesAndDriversManager = Main.getApplication().getAliasesAndDriversManager();

      for (SQLAlias a : sqlAliasesToExport)
      {
         ISQLDriver driver = aliasesAndDriversManager.getDriver(a.getDriverIdentifier());

         String driverName = "<<undefined>>";
         if (null != driver)
         {
            driverName = driver.getName();
         }

         ret.put(a.getDriverIdentifier().toString(), driverName);
      }

      return ret;
   }

   private JFileChooser createFileChooser()
   {
      JFileChooser exportFC = new JFileChooser(Props.getString(PREF_LAST_EXPORT_FILE_DIR, System.getProperty("user.home")));

      for (FileFilter chooseableFileFilter : exportFC.getChoosableFileFilters())
      {
         exportFC.removeChoosableFileFilter(chooseableFileFilter);
      }

      FileExtensionFilter zipFilter = new FileExtensionFilter("Export zip files", new String[]{".zip"});
      exportFC.addChoosableFileFilter(zipFilter);
      return exportFC;
   }

   private void writeXMLBeanZipEntry(ZipOutputStream zipOut, XMLBeanWriter xmlBeanWriter, String fileNameInZip) throws IOException
   {
      ZipEntry zipEntry;
      ByteArrayOutputStream bos;

      zipEntry = new ZipEntry(fileNameInZip);
      zipOut.putNextEntry(zipEntry);
      bos = new ByteArrayOutputStream();
      xmlBeanWriter.saveToOutputStream(bos);
      zipOut.write(bos.toByteArray());
      zipOut.flush();
      zipOut.closeEntry();
   }


   private void locateDialogBesidesAliases()
   {
      Point locOnScreen = GUIUtils.getScreenLocationFor(_aliasesList.getComponent());

      int x = locOnScreen.x + _aliasesList.getComponent().getWidth() + new JSplitPane().getDividerSize();

      Rectangle boundsOnScreen = new Rectangle(x, locOnScreen.y, Math.max(_aliasesList.getComponent().getWidth(), 400), _aliasesList.getComponent().getHeight());

      _dlg.setBounds(boundsOnScreen);
   }
}
