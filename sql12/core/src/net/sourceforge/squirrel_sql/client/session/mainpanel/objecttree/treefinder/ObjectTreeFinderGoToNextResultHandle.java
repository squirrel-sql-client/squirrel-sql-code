package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.HashSet;
import java.util.Set;

public class ObjectTreeFinderGoToNextResultHandle
{
   public static final ObjectTreeFinderGoToNextResultHandle DONT_GO_TO_NEXT_RESULT_HANDLE = new ObjectTreeFinderGoToNextResultHandle()
         {
            @Override
            public void addPreviousResult(TreePath resultTreePath) {}

            @Override
            public void reachedEmptyResult() {}

            @Override
            public boolean isAPreviousResult(TreeNode[] path) { return false;}

            @Override
            public boolean hasPreviousResults() {return false;}
         };


   private Set<TreePath> previousResults = new HashSet<>();
   private String _currentSearchString;
   private Boolean _currentFilteredState;

   public void addPreviousResult(TreePath resultTreePath)
   {
      previousResults.add(resultTreePath);
   }

   public boolean isAPreviousResult(TreeNode[] path)
   {
      return previousResults.contains(new TreePath(path));
   }

   /**
    * @return true when one or both of the two parameters changed.
    */
   public boolean setCurrentSearchState(String searchString, boolean filteredState)
   {
      if(   false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(searchString, _currentSearchString)
         || false == Utilities.equalsRespectNull(_currentFilteredState, filteredState) )
      {
         _currentSearchString = searchString;
         _currentFilteredState = filteredState;
         previousResults.clear();
         return true;
      }

      return false;
   }

   public void reachedEmptyResult()
   {
      previousResults.clear();
   }

   public boolean hasPreviousResults()
   {
      return 0 < previousResults.size();
   }
}
