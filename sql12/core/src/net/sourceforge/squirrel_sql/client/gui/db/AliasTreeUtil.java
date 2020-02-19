package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.tree.DefaultMutableTreeNode;

public class AliasTreeUtil
{
   public static void throwUnknownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      throw createUnknownUserObjectException(aliasTreeNode);
   }

   public static IllegalStateException createUnknownUserObjectException(DefaultMutableTreeNode aliasTreeNode)
   {
      return new IllegalStateException("Unknown Alias tree object typ: " + (null == aliasTreeNode.getUserObject() ? "null" : aliasTreeNode.getUserObject().getClass().getName()));
   }


}
