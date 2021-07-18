package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeFinderFinishListener;

import javax.swing.tree.TreePath;

public interface ObjectTreeFinderResultFuture
{
   ObjectTreeFinderResultFuture EMPTY_FINISHED_RESULT = new ObjectTreeFinderResultFuture() {
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
      public void addListenerOrdered(ObjectTreeFinderFinishListener listener)
      {
         listener.finderFinished(null);
      }
   };

   void executeTillFinishNow();

   TreePath getTreePath();

   void addListenerOrdered(ObjectTreeFinderFinishListener listener);
}
