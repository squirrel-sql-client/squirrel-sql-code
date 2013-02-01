package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RecentFilesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);

   private RecentFilesDialog _dialog;

   public RecentFilesController(RecentFilesManager recentFilesManager, Frame parent, ISQLAlias selectedAlias)
   {
      init(recentFilesManager, parent, selectedAlias, false);
   }


   public RecentFilesController(ISQLPanelAPI panel)
   {
      Frame parent = GUIUtils.getOwningFrame(panel.getSQLEntryPanel().getTextComponent());
      RecentFilesManager recentFilesManager = panel.getSession().getApplication().getRecentFilesManager();

      init(recentFilesManager, parent, panel.getSession().getAlias(), true);
   }


   private void init(RecentFilesManager recentFilesManager, Frame parent, ISQLAlias selectedAlias, boolean showAppendOption)
   {
      _dialog = new RecentFilesDialog(parent, showAppendOption);

      _dialog.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _dialog.dispose();
         }
      });

      DefaultMutableTreeNode root = new DefaultMutableTreeNode();

      DefaultMutableTreeNode recentFiles = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.global"));
      root.add(recentFiles);
      addFileKidsToNode(recentFiles, recentFilesManager.getRecentFiles());

      DefaultMutableTreeNode favouriteFiles = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.global"));
      root.add(favouriteFiles);
      addFileKidsToNode(favouriteFiles, recentFilesManager.getFavouriteFiles());

      DefaultMutableTreeNode recentFilesForAlias = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.alias", selectedAlias.getName()));
      root.add(recentFilesForAlias);
      addFileKidsToNode(recentFilesForAlias, recentFilesManager.getRecentFilesForAlias(selectedAlias));

      DefaultMutableTreeNode favouriteFilesForAlias = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.alias", selectedAlias.getName()));
      root.add(favouriteFilesForAlias);
      addFileKidsToNode(favouriteFilesForAlias, recentFilesManager.getFavouriteFilesForAlias(selectedAlias));

      _dialog.treFiles.setModel(new DefaultTreeModel(root));
      _dialog.treFiles.setRootVisible(false);

      _dialog.setVisible(true);
   }

   private void addFileKidsToNode(DefaultMutableTreeNode parentNode, ArrayList<String> filePaths)
   {
      for (String filePath : filePaths)
      {
         parentNode.add(new DefaultMutableTreeNode(filePath));
      }
   }
}
