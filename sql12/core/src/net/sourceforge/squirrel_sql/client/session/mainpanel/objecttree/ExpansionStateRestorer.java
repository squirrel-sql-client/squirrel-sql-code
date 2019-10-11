package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExpansionStateRestorer
{
   /**
    * Restore the expansion state of the tree starting at the passed startNode.
    * The passed startNode is always expanded.
    *
    * @param objectTree
    * @param startNode  Node to restore expansion state from.
    * @param expandedPathNames
    * @return TreePaths to newly select.
    * @throws IllegalArgumentException Thrown if null ObjectTreeNode passed.
    */
   public static List<TreePath> restoreExpansionState(ObjectTree objectTree, ObjectTreeNode startNode, TreePath[] previouslySelectedTreePaths, Set<String> expandedPathNames)
   {
      List<TreePath> treePathsToNewlySelect = new ArrayList<>();
      _restoreExpansionState(objectTree, startNode, previouslySelectedTreePaths, treePathsToNewlySelect, expandedPathNames);
      return treePathsToNewlySelect;
   }

   private static void _restoreExpansionState(ObjectTree objectTree, ObjectTreeNode startNode, TreePath[] previouslySelectedTreePaths, List<TreePath> treePathsToNewlySelect, Set<String> expandedPathNames)
   {
      final TreePath nodePath = new TreePath(startNode.getPath());
      if (wasPreviouslySelected(previouslySelectedTreePaths, startNode, nodePath))
      {
         treePathsToNewlySelect.add(nodePath);
      }

      objectTree.expandPath(nodePath);


      // Go through each child of the parent and see if it was previously
      // expanded. If it was recursively call this method in order to expand
      // the child.
      Enumeration<TreeNode> childEnumeration = startNode.children();
      Iterator<TreeNode> it = new EnumerationIterator<>(childEnumeration);

      while (it.hasNext())
      {
         final ObjectTreeNode child = (ObjectTreeNode) it.next();
         final TreePath childPath = new TreePath(child.getPath());

         if (wasPreviouslySelected(previouslySelectedTreePaths, child, childPath))
         {
            treePathsToNewlySelect.add(childPath);
         }

         if (expandedPathNames.contains(childPath.toString()))
         {
            _restoreExpansionState(objectTree, child, previouslySelectedTreePaths, treePathsToNewlySelect, expandedPathNames);
         }
      }
   }

   /**
    * This is to handle the case where the user has enabled showRowCounts and
    * the table/view name as it appeared before is different only because the
    * number of rows has changed. For example, suppose a user deletes records
    * in a table "foo" with 100 rows then refreshes the tree.  The tree node
    * before the delete looks like foo(100) and after looks like foo(0).  We
    * want to strip off the (...) and test to see if the selected path "foo"
    * is the same before the delete as after.  This way, when the user refreshes
    * "foo(...)", then it is still selected after the refresh.
    */
   private static boolean wasPreviouslySelected(TreePath[] previouslySelectedTreePaths, ObjectTreeNode startNode, TreePath path)
   {
      for (TreePath previouslySelectedTreePath : previouslySelectedTreePaths)
      {
         if(matches(path, previouslySelectedTreePath, isTableOrView(startNode)))
         {
            return true;
         }
      }
      return false;
   }

   private static boolean isTableOrView(ObjectTreeNode startNode)
   {
       return startNode.getDatabaseObjectType() != DatabaseObjectType.TABLE || startNode.getDatabaseObjectType() != DatabaseObjectType.VIEW;
   }

   private static boolean matches(TreePath path1, TreePath path2, boolean tableOrView)
   {
      if(path1.getPath().length != path2.getPath().length)
      {
         return false;
      }

      for (int i = 0; i < path1.getPath().length; i++)
      {
         String node1Str = nodeObjToString(path1.getPath()[i]);
         String node2Str = nodeObjToString(path2.getPath()[i]);

         if(false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(node1Str, node2Str))
         {

            if(i < path1.getPath().length - 1)
            {
               return false;
            }

            if(false == tableOrView)
            {
               return false;
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            // This is to handle the case where the user has enabled SessionProperties._showRowCount
            if(-1 == node1Str.indexOf('(') || -1 == node2Str.indexOf('('))
            {
               return false;
            }

            String node1TillBracket = node1Str.substring(0, node1Str.lastIndexOf('('));
            String node2TillBracket = node2Str.substring(0, node2Str.lastIndexOf('('));
            if(false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(node1TillBracket, node2TillBracket))
            {
               return false;
            }
            //
            ///////////////////////////////////////////////////////////////////////////////////////////////////////

         }
      }

      return true;
   }

   private static String nodeObjToString(Object nodeObj)
   {
      if(null == nodeObj)
      {
         return StringUtilities.NULL_AS_STRING;
      }

      return nodeObj.toString();
   }
}
