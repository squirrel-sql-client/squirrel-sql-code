package net.sourceforge.squirrel_sql.plugins.sqlbookmark.exportimport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.sqlbookmark.BookMarksUtil;
import net.sourceforge.squirrel_sql.plugins.sqlbookmark.Bookmark;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkExportImport
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(BookmarkExportImport.class);

   private final static ILogger s_log = LoggerController.createLogger(BookmarkExportImport.class);

   private static final String PREF_LAST_BOOKMARK_EXPORT_IMPORT_FILE_DIR = "BookmarkImportExport.last.bookmark.export.import.file.dir";

   public static void exportBookMarks(DefaultMutableTreeNode nodeUserMarks, JTree treBookmarks)
   {
      try
      {
         TreePath[] selectionPaths = treBookmarks.getSelectionPaths();

         if(   null == selectionPaths
            || 0 == selectionPaths.length
            || false == BookMarksUtil.areOnlyUserBookmarksSelected(selectionPaths, nodeUserMarks))
         {

            JOptionPane.showMessageDialog(treBookmarks, s_stringMgr.getString("BookmarkImportExport.userbookmarks.only"));

            return;
         }

         JFileChooser exportFC = createFileChooser();
         exportFC.setDialogTitle(s_stringMgr.getString("BookmarkImportExport.export.file.dialog.title"));

         if (exportFC.showSaveDialog(treBookmarks) != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File exportFile = exportFC.getSelectedFile();

         Props.putString(PREF_LAST_BOOKMARK_EXPORT_IMPORT_FILE_DIR, exportFile.getParent());

         if(false == exportFile.getName().toLowerCase().endsWith(".xml"))
         {
            exportFile = new File(exportFile.getAbsolutePath() + ".xml");;
         }


         List<Bookmark> selectedBookmarks =
               Arrays.stream(selectionPaths).map(p -> (Bookmark) ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject()).collect(Collectors.toList());

         XMLBeanWriter xmlBeanWriter = new XMLBeanWriter();
         xmlBeanWriter.addIteratorToRoot(selectedBookmarks.iterator());

         xmlBeanWriter.save(exportFile);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static void importBookMarks(DefaultMutableTreeNode nodeUserMarks, JTree treBookmarks)
   {

      try
      {
         JFileChooser importFC = createFileChooser();
         importFC.setDialogTitle(s_stringMgr.getString("BookmarkImportExport.import.file.dialog.title"));

         if (importFC.showOpenDialog(treBookmarks) != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File importFile = importFC.getSelectedFile();

         Props.putString(PREF_LAST_BOOKMARK_EXPORT_IMPORT_FILE_DIR, importFile.getParent());

         List<Bookmark> importBookmarks = readBookmarks(importFile, treBookmarks);

         if(null == importBookmarks)
         {
            return;
         }


         ArrayList<Bookmark> conflictingImportBookmarks = new ArrayList<>();
         BookmarkImportConflictOption conflictOption = null;

         for (int i = 0; i < nodeUserMarks.getChildCount(); i++)
         {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeUserMarks.getChildAt(i);
            Bookmark bookmark = (Bookmark) node.getUserObject();

            for (Bookmark importBookmark : importBookmarks)
            {
               if(StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(bookmark.getName(), importBookmark.getName()))
               {
                  if (null == conflictOption)
                  {
                     conflictOption = new ImportDuplicateNameCtrl(GUIUtils.getOwningWindow(treBookmarks)).getChosenOption();
                     if(null == conflictOption)
                     {
                        return;
                     }
                  }
                  conflictingImportBookmarks.add(importBookmark);
               }
            }
         }


         List<Bookmark>  bookmarksToAdd = applyConflictOption(nodeUserMarks, importBookmarks, conflictingImportBookmarks, conflictOption);
         addBookmarks(nodeUserMarks, treBookmarks, bookmarksToAdd);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static List<Bookmark> applyConflictOption(DefaultMutableTreeNode nodeUserMarks, List<Bookmark> importBookmarks, ArrayList<Bookmark> conflictingImportBookmarks, BookmarkImportConflictOption conflictOption)
   {
      if(null == conflictOption)
      {
         messageImport(importBookmarks, false);
         return importBookmarks;
      }


      ArrayList<Bookmark> ret;

      switch (conflictOption)
      {
         case COPY:
            ret = new ArrayList<>(importBookmarks);
            for (Bookmark conflictingImportBookmark : conflictingImportBookmarks)
            {
               for (int i = 1; ; i++)
               {
                  final String copyName = conflictingImportBookmark.getName() + "_" + i;
                  if(isNameUnique(copyName, nodeUserMarks, importBookmarks))
                  {
                     conflictingImportBookmark.setName(copyName);
                     break;
                  }
               }
            }

            messageImport(ret, false);
            break;

         case IGNORE:
            ret = new ArrayList<>(importBookmarks);
            ret.removeAll(conflictingImportBookmarks);
            messageImport(ret, false);
            break;

         case UPDATE:
            ret = new ArrayList<>(importBookmarks);
            ret.removeAll(conflictingImportBookmarks);

            for (Bookmark conflictingImportBookmark : conflictingImportBookmarks)
            {
               DefaultMutableTreeNode node = findNodeByBookmarkName(nodeUserMarks, conflictingImportBookmark.getName());
               Bookmark bookmarkToUpdate = (Bookmark) node.getUserObject();
               bookmarkToUpdate.setDescription(conflictingImportBookmark.getDescription());
               bookmarkToUpdate.setSql(conflictingImportBookmark.getSql());
            }
            messageImport(ret, false);
            messageImport(conflictingImportBookmarks, true);
            break;

         default:
            throw new IllegalStateException("Unknown option " + conflictOption);
      }

      return ret;
   }

   private static void messageImport(List<Bookmark> importBookmarks, boolean updatedExistingBookmarks)
   {
      String importedBookmarkNames = importBookmarks.stream().map(b -> "\"" + b.getName() + "\"").collect(Collectors.joining(", "));

      if(StringUtilities.isEmpty(importedBookmarkNames, true))
      {
         importedBookmarkNames = s_stringMgr.getString("BookmarkImportExport.none");
      }

      if (updatedExistingBookmarks)
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("BookmarkImportExport.update.bookmarks.list", importedBookmarkNames));
      }
      else
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("BookmarkImportExport.imported.bookmarks.list", importedBookmarkNames));
      }
   }

   private static boolean isNameUnique(String nameToCheck, DefaultMutableTreeNode nodeUserMarks, List<Bookmark> importBookmarks)
   {
      for (int i = 0; i < nodeUserMarks.getChildCount(); i++)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeUserMarks.getChildAt(i);
         Bookmark bookmark = (Bookmark) node.getUserObject();
         if(StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(bookmark.getName(), nameToCheck))
         {
            return false;
         }
      }

      for (Bookmark importBookmark : importBookmarks)
      {
         if(StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(importBookmark.getName(), nameToCheck))
         {
            return false;
         }
      }

      return true;
   }

   private static DefaultMutableTreeNode findNodeByBookmarkName(DefaultMutableTreeNode nodeUserMarks, String bookmarkName)
   {
      for (int i = 0; i < nodeUserMarks.getChildCount(); i++)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeUserMarks.getChildAt(i);
         Bookmark bookmark = (Bookmark) node.getUserObject();
         if(StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(bookmark.getName(), bookmarkName))
         {
            return node;
         }
      }

      throw new IllegalStateException("Didn't find Bookmark named " + bookmarkName);
   }

   private static void addBookmarks(DefaultMutableTreeNode nodeUserMarks, JTree treBookmarks, List<Bookmark> bookmarksToAdd)
   {
      ArrayList<DefaultMutableTreeNode> nodesToSelect = new ArrayList<>();

      if(null != treBookmarks.getSelectionPath() && nodeUserMarks.isNodeChild((DefaultMutableTreeNode) treBookmarks.getSelectionPath().getLastPathComponent()))
      {
         DefaultMutableTreeNode sibling = (DefaultMutableTreeNode) treBookmarks.getSelectionPath().getLastPathComponent();
         int insertIndex = nodeUserMarks.getIndex(sibling) + 1;

         for (Bookmark importBookmark : bookmarksToAdd)
         {
            final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(importBookmark);
            nodeUserMarks.insert(newChild, insertIndex);
            insertIndex = nodeUserMarks.getIndex(newChild) + 1;
            nodesToSelect.add(newChild);
         }
      }
      else
      {
         for (Bookmark importBookmark : bookmarksToAdd)
         {
            final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(importBookmark);
            nodeUserMarks.add(newChild);
            nodesToSelect.add(newChild);
         }
      }

      ((DefaultTreeModel)treBookmarks.getModel()).nodeStructureChanged(nodeUserMarks);


      List<TreePath> pathsToSelect = nodesToSelect.stream().map(n -> new TreePath(n.getPath())).collect(Collectors.toList());

      treBookmarks.setSelectionPaths(pathsToSelect.toArray(new TreePath[0]));
   }

   private static List<Bookmark> readBookmarks(File importFile, JTree treBookmarks)
   {
      try
      {
         XMLBeanReader xmlBeanReader = new XMLBeanReader();

         xmlBeanReader.load(importFile, Bookmark.class.getClassLoader());

         final List<Bookmark> ret = xmlBeanReader.getBeans();

         if(0 == ret.size())
         {
            JOptionPane.showMessageDialog(treBookmarks, s_stringMgr.getString("BookmarkImportExport.bookmark.import.empty.dlg"));
            return null;
         }

         return ret;
      }
      catch (Exception e)
      {
         s_log.error(s_stringMgr.getString("BookmarkImportExport.bookmark.import.error"), e);
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("BookmarkImportExport.bookmark.import.error"), e);

         JOptionPane.showMessageDialog(
               treBookmarks,
               s_stringMgr.getString("BookmarkImportExport.bookmark.import.error.dlg"),
               s_stringMgr.getString("BookmarkImportExport.bookmark.import.error.dlg.title"),
               JOptionPane.ERROR_MESSAGE);

         return null;
      }
   }


   private static JFileChooser createFileChooser()
   {
      JFileChooser exportFC = new JFileChooser(Props.getString(PREF_LAST_BOOKMARK_EXPORT_IMPORT_FILE_DIR, System.getProperty("user.home")));

      for (FileFilter chooseableFileFilter : exportFC.getChoosableFileFilters())
      {
         exportFC.removeChoosableFileFilter(chooseableFileFilter);
      }

      FileExtensionFilter xmlFilter = new FileExtensionFilter("Bookmark XML files", new String[]{".xml"});
      exportFC.addChoosableFileFilter(xmlFilter);
      return exportFC;
   }

}
