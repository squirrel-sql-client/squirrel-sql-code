package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeFinderFinishListener;

import javax.swing.tree.TreePath;

public interface ObjectTreeSearchResultFuture
{
   ObjectTreeSearchResultFuture EMPTY_FINISHED_RESULT = new ObjectTreeSearchResultFuture() {
      @Override
      public void executeTillFinishNow()
      {
      }

      @Override
      public TreePath getTreePath()
      {
         return null;
      }

      @Override
      public void addFinishedListenerOrdered(ObjectTreeFinderFinishListener listener)
      {
         listener.finderFinished(null);
      }
   };

   void executeTillFinishNow();

   TreePath getTreePath();

   void addFinishedListenerOrdered(ObjectTreeFinderFinishListener listener);
}
