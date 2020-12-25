package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
}
