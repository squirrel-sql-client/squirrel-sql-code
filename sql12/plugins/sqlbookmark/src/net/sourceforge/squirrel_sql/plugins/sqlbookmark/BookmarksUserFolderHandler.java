package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.dnd.DropTargetDropEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandler;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandlerCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class BookmarksUserFolderHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(BookmarksUserFolderHandler.class);

   private final JTree _treBookmarks;
   private final DefaultMutableTreeNode _nodeUserMarks;
   private final DefaultMutableTreeNode _nodeSquirrelMarks;

   public BookmarksUserFolderHandler(JTree treBookmarks, DefaultMutableTreeNode nodeUserMarks, DefaultMutableTreeNode nodeSquirrelMarks)
   {
      _treBookmarks = treBookmarks;
      _nodeUserMarks = nodeUserMarks;
      _nodeSquirrelMarks = nodeSquirrelMarks;
      new TreeDnDHandler(_treBookmarks, new TreeDnDHandlerCallback()
      {
         @Override
         public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode)
         {
            return _nodeUserMarks == selNode || selNode.getUserObject() instanceof UserBookmarkFolder;
         }

         @Override
         public ArrayList<DefaultMutableTreeNode> getPasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath)
         {
            throw new UnsupportedOperationException("Should not have been called.");
         }

         @Override
         public boolean allowDND(DefaultMutableTreeNode targetNode, ArrayList<DefaultMutableTreeNode> draggedNodes)
         {
            return onAllowDND(targetNode, draggedNodes);
         }
      });

   }

   private boolean onAllowDND(DefaultMutableTreeNode targetNode, ArrayList<DefaultMutableTreeNode> draggedNodes)
   {
      if(false == Stream.of(targetNode.getPath()).anyMatch(n -> n == _nodeUserMarks))
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("BookmarksUserFolderHandler.can.not.move.to.squirrel.bookmarks"));
         return false;
      }

      for(DefaultMutableTreeNode node : draggedNodes)
      {
         if(false == Stream.of(node.getPath()).anyMatch(n -> n == _nodeUserMarks))
         {
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("BookmarksUserFolderHandler.only.user.bookmarks.can.be.moved"));
            return false;
         }
      }

      return true;
   }

   public void addFolder()
   {
      TreePath selectionPath = _treBookmarks.getSelectionPath();
      if(null != selectionPath && _nodeUserMarks != selectionPath.getPathComponent(1))
      {
         JOptionPane.showMessageDialog(_treBookmarks, s_stringMgr.getString("BookmarksUserFolderHandler.msg.can.add.folder.to.user.bookmarks.only"));
         return;
      }

      DefaultMutableTreeNode lastPathComponent = _nodeUserMarks;
      if(null != selectionPath)
      {
         lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
      }

      DefaultMutableTreeNode parent;
      if(lastPathComponent.getUserObject() instanceof Bookmark)
      {
         parent = (DefaultMutableTreeNode) lastPathComponent.getParent();
      }
      else if(lastPathComponent == _nodeUserMarks || lastPathComponent.getUserObject() instanceof UserBookmarkFolder)
      {
         parent = lastPathComponent;
      }
      else
      {
         throw new IllegalStateException("Invalid parent node: " + lastPathComponent);
      }

      String editedFolderName = letUserEditFolderName(parent, null);
      if(editedFolderName == null)
      {
         return;
      }

      DefaultMutableTreeNode newUserFolder = GUIUtils.createFolderNode(new UserBookmarkFolder(editedFolderName));
      int siblingIndex = parent.getIndex(lastPathComponent);
      if(-1 < siblingIndex)
      {
         parent.insert(newUserFolder, siblingIndex+1);
      }
      else
      {
         parent.add(newUserFolder);
      }
      ((DefaultTreeModel)_treBookmarks.getModel()).nodeStructureChanged(parent);
      BookmarkTreeUtil.selectNode(_treBookmarks, newUserFolder);
   }

   public void editFolder(DefaultMutableTreeNode selectedFolderNode)
   {
      String currentFolderName = ((UserBookmarkFolder) selectedFolderNode.getUserObject()).getFolderName();

      String editedFolderName = letUserEditFolderName((DefaultMutableTreeNode) selectedFolderNode.getParent(), currentFolderName);
      if(editedFolderName == null)
      {
         return;
      }

      ((UserBookmarkFolder)selectedFolderNode.getUserObject()).setFolderName(editedFolderName);

      ((DefaultTreeModel)_treBookmarks.getModel()).nodeChanged(selectedFolderNode);
   }

   /**
    * If necessary may be replaced by a folder edit dialog.
    */
   private String letUserEditFolderName(DefaultMutableTreeNode parentOfFolder, String folderNameToEdit)
   {
      byte[] bytes =
            JOptionPane.showInputDialog(_treBookmarks, s_stringMgr.getString("BookmarksUserFolderHandler.enter.folder.name"), folderNameToEdit).getBytes(StandardCharsets.UTF_8);

      String editedFolderName = new String(bytes);

      if(StringUtilities.isEmpty(editedFolderName, true))
      {
         JOptionPane.showMessageDialog(_treBookmarks, s_stringMgr.getString("BookmarksUserFolderHandler.folder.name.required"));
         return null;
      }

      List<DefaultMutableTreeNode> siblingFolders = BookmarkTreeUtil.getAllChildFoldersShallow(parentOfFolder);
      for(DefaultMutableTreeNode siblingFolder : siblingFolders)
      {
         String siblingFolderName = ((UserBookmarkFolder) siblingFolder.getUserObject()).getFolderName();
         if( StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(siblingFolderName, editedFolderName) )
         {
            JOptionPane.showMessageDialog(_treBookmarks, s_stringMgr.getString("BookmarksUserFolderHandler.sibling.folder.name.exists"));
            return null;
         }
      }
      return editedFolderName;
   }


   public void addBookMarkNode(Bookmark bookmark)
   {
      DefaultMutableTreeNode parent = _nodeUserMarks;

      if(   null != _treBookmarks.getSelectionPath()
         && _treBookmarks.getSelectionPath().getLastPathComponent() instanceof  DefaultMutableTreeNode selNode
         && selNode.getUserObject() instanceof UserBookmarkFolder
      )
      {
         parent = selNode;
      }

      DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(bookmark);
      parent.add(newChild);

      ((DefaultTreeModel)_treBookmarks.getModel()).nodesWereInserted(parent, new int[]{parent.getIndex(newChild)});

      BookmarkTreeUtil.selectNode(_treBookmarks, newChild);
   }
}
