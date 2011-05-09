package net.sourceforge.squirrel_sql.fw.gui;


import javax.swing.tree.DefaultMutableTreeNode;

public interface TreeDnDHandlerCallback
{
   public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode);

   void dndExecuted();
}
