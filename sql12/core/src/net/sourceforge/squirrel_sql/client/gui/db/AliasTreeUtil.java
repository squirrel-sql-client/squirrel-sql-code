package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AliasTreeUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasTreeUtil.class);


   public static void throwUnknownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      throw createUnknownUserObjectException(aliasTreeNode);
   }

   public static IllegalStateException createUnknownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      return new IllegalStateException("Unknown Alias tree object typ: " + (null == aliasTreeNode.getUserObject() ? "null" : aliasTreeNode.getUserObject().getClass().getName()));
   }


   public static void expandRecursively(TreePath treePath, JTree tree)
   {
      if(null == treePath || false == ((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject() instanceof AliasFolder)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("JTreeAliasesListImpl.noFolderSelected"));
         return;
      }

      recurseChildNodes(treePath, p -> tree.expandPath(p));
   }

   public static void collapseRecursively(TreePath treePath, JTree tree)
   {
      if(null == treePath || false == ((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject() instanceof AliasFolder)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("JTreeAliasesListImpl.noFolderSelected"));
         return;
      }

      recurseChildNodes(treePath, p -> tree.collapsePath(p.getParentPath()));
   }

   private static void recurseChildNodes(TreePath treePath, Consumer<TreePath> treePathConsumer)
   {
      treePathConsumer.accept(treePath);

      DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) treePath.getLastPathComponent();

      for (int i = 0; i < dtm.getChildCount(); i++)
      {
         final TreeNode[] buf = ((DefaultMutableTreeNode) dtm.getChildAt(i)).getPath();
         recurseChildNodes(new TreePath(buf), treePathConsumer);
      }
   }

   public static List<AliasFolder> getAllAliasFolders(JTree tree)
   {
      final DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

      ArrayList<AliasFolder> ret = new ArrayList<>();

      recurseChildNodes(new TreePath(root.getPath()), p -> fillFolder(p, ret));

      return ret;
   }

   private static void fillFolder(TreePath treePath, ArrayList<AliasFolder> toFill)
   {
      if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject() instanceof AliasFolder)
      {
         toFill.add((AliasFolder) ((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject());
      }
   }

   public static DefaultMutableTreeNode findAliasNode(SQLAlias sqlAlias, DefaultMutableTreeNode tn)
   {
      return _findNode(sqlAlias, tn);
   }


   public static DefaultMutableTreeNode findAliasFolderNode(AliasFolder aliasFolder, DefaultMutableTreeNode tn)
   {
      return _findNode(aliasFolder, tn);
   }

   private static DefaultMutableTreeNode _findNode(Object aliasOrAliasFolder, DefaultMutableTreeNode tn)
   {
      if(aliasOrAliasFolder.equals(tn.getUserObject()))
      {
         return tn;
      }

      for (int i = 0; i < tn.getChildCount(); i++)
      {
         DefaultMutableTreeNode ret = _findNode(aliasOrAliasFolder, (DefaultMutableTreeNode) tn.getChildAt(i));
         if(null != ret)
         {
            return ret;
         }
      }

      return null;
   }

}
