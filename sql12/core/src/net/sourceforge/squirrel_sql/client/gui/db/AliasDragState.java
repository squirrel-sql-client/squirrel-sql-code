package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.gui.TreeDndDropPositionData;

import javax.swing.JTree;

public class AliasDragState
{
   private final JTree _tree;

   private TreeDndDropPositionData _treeDndDropPositionData = TreeDndDropPositionData.empty();

   public AliasDragState(JTree tree)
   {
      _tree = tree;
   }

   public void updateDragPosition(TreeDndDropPositionData treeDndDropPositionData)
   {
      if(treeDndDropPositionData.same(_treeDndDropPositionData))
      {
         return;
      }

      _treeDndDropPositionData = treeDndDropPositionData;

      _tree.repaint();
   }

   public TreeDndDropPositionData getTreeDndDropPositionData()
   {
      return _treeDndDropPositionData;
   }
}
