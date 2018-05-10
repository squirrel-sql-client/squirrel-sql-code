package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;

public class AliasTreeUtil
{
   public static void throwUnknownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      throw createUnkonownUserObjectException(aliasTreeNode);
   }

   public static IllegalStateException createUnkonownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      return new IllegalStateException("Unknown Alias tree object typ: " + (null == aliasTreeNode.getUserObject() ? "null" : aliasTreeNode.getUserObject().getClass().getName()));
   }
}
