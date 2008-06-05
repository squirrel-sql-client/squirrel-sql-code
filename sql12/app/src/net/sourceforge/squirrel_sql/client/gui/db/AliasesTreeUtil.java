package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.tree.DefaultMutableTreeNode;

public class AliasesTreeUtil
{
   static DefaultMutableTreeNode createFolderNode(final String folderName)
   {
      DefaultMutableTreeNode newFolder  = new DefaultMutableTreeNode(folderName)
      {
         public boolean isLeaf()
         {
            return false;
         }
      };
      return newFolder;
   }
}
