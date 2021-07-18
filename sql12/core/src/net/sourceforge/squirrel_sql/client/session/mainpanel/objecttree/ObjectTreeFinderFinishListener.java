package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;


import javax.swing.tree.TreePath;

@FunctionalInterface
public interface ObjectTreeFinderFinishListener
{
   void finderFinished(TreePath resultTreePath);
}
