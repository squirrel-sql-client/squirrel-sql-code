package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Arrays;

public class BookMarksUtil
{
   public static boolean areOnlyUserBookmarksSelected(TreePath[] selectionPaths, DefaultMutableTreeNode nodeUserMarks)
   {
      return false == Arrays.stream(selectionPaths).anyMatch(bn -> ((DefaultMutableTreeNode)bn.getLastPathComponent()).getParent()  != nodeUserMarks);
   }
}
