package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class RecentFilesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);

   private RecentFilesDialog _dialog;
   private IApplication _app;
   private DefaultMutableTreeNode _recentFilesNode;
   private DefaultMutableTreeNode _favouriteFilesNode;
   private DefaultMutableTreeNode _recentFilesForAliasNode;
   private DefaultMutableTreeNode _favouriteFilesForAliasNode;

   public RecentFilesController(IApplication app, ISQLAlias selectedAlias)
   {
      init(app, app.getMainFrame() , selectedAlias, false);
   }


   public RecentFilesController(ISQLPanelAPI panel)
   {
      Frame parent = GUIUtils.getOwningFrame(panel.getSQLEntryPanel().getTextComponent());
      init(panel.getSession().getApplication(), parent, panel.getSession().getAlias(), true);
   }


   private void init(IApplication app, final Frame parent, final ISQLAlias selectedAlias, boolean showAppendOption)
   {
      _app = app;
      _dialog = new RecentFilesDialog(parent, showAppendOption);

      _dialog.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _dialog.dispose();
         }
      });

      initAndLoadTree(selectedAlias);

      _dialog.txtNumberRecentFiles.setInt(_app.getRecentFilesManager().getMaxRecentFiles());
      _dialog.txtNumberRecentFiles.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }
      });


      _dialog.btnFavourites.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddToFavourites(parent, null);
         }
      });

      _dialog.btnAliasFavourites.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddToFavourites(parent, selectedAlias);
         }
      });

      _dialog.setVisible(true);
   }

   private void onAddToFavourites(Frame parent, ISQLAlias alias)
   {
      JFileChooser fc = new JFileChooser(_app.getSquirrelPreferences().getFilePreviousDir());
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      int returnVal = fc.showOpenDialog(parent);
      if (returnVal != JFileChooser.APPROVE_OPTION)
      {
         return;
      }


      DefaultMutableTreeNode nodeToAddTo;
      ArrayList<String> listToAddTo;

      if (null == alias)
      {
         _app.getRecentFilesManager().adjustFavouriteFiles(fc.getSelectedFile());
         nodeToAddTo = _favouriteFilesNode;
         listToAddTo = _app.getRecentFilesManager().getFavouriteFiles();
      }
      else
      {
         _app.getRecentFilesManager().adjustFavouriteAliasFiles(alias, fc.getSelectedFile());
         nodeToAddTo = _favouriteFilesForAliasNode;
         listToAddTo = _app.getRecentFilesManager().getFavouriteFilesForAlias(alias);
      }

      nodeToAddTo.removeAllChildren();
      addFileKidsToNode(nodeToAddTo, listToAddTo);

      DefaultTreeModel dtm = (DefaultTreeModel) _dialog.treFiles.getModel();
      dtm.nodeStructureChanged(nodeToAddTo);
      _dialog.treFiles.expandPath(new TreePath(nodeToAddTo.getPath()));

      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nodeToAddTo.getFirstChild();
      _dialog.treFiles.scrollPathToVisible(new TreePath(firstChild.getPath()));

   }

   private void updateRecentFilesCount()
   {
      _app.getRecentFilesManager().setMaxRecentFiles(_dialog.txtNumberRecentFiles.getInt());
   }

   private void initAndLoadTree(ISQLAlias selectedAlias)
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode();

      _recentFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.global"));
      root.add(_recentFilesNode);
      addFileKidsToNode(_recentFilesNode, _app.getRecentFilesManager().getRecentFiles());

      _favouriteFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.global"));
      root.add(_favouriteFilesNode);
      addFileKidsToNode(_favouriteFilesNode, _app.getRecentFilesManager().getFavouriteFiles());

      _recentFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.alias", selectedAlias.getName()));
      root.add(_recentFilesForAliasNode);
      addFileKidsToNode(_recentFilesForAliasNode, _app.getRecentFilesManager().getRecentFilesForAlias(selectedAlias));

      _favouriteFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.alias", selectedAlias.getName()));
      root.add(_favouriteFilesForAliasNode);
      addFileKidsToNode(_favouriteFilesForAliasNode, _app.getRecentFilesManager().getFavouriteFilesForAlias(selectedAlias));

      _dialog.treFiles.setModel(new DefaultTreeModel(root));
      _dialog.treFiles.setRootVisible(false);

      _dialog.treFiles.setCellRenderer(new RecentFilesTreeCellRenderer(_app));
   }

   private void addFileKidsToNode(DefaultMutableTreeNode parentNode, ArrayList<String> filePaths)
   {
      for (String filePath : filePaths)
      {
         DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File(filePath));
         parentNode.add(node);
      }
   }
}
