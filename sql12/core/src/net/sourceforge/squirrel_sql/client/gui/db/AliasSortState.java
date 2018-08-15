package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class AliasSortState
{
   private final TreeModelListener _treeModelListener;
   private AliasFolderState _stateBeforeSort;
   private Direction _currentDirection;
   private JTree _tree;

   private enum Direction
   {
      ASC, DESC, ORIG;

      Direction next()
      {
         for (int i = 0; i < values().length; i++)
         {
            if(this == values()[i])
            {
               if (i < values().length - 1)
               {
                  return values()[++i];
               }
               else
               {
                  return values()[0];
               }
            }
         }

         throw new IllegalStateException("Can never happen");
      }
   }


   public AliasSortState(JTree tree)
   {
      _tree = tree;

      _treeModelListener = new TreeModelListener()
      {
         @Override
         public void treeNodesChanged(TreeModelEvent e)
         {
            _stateBeforeSort = null;
         }

         @Override
         public void treeNodesInserted(TreeModelEvent e)
         {
            _stateBeforeSort = null;
         }

         @Override
         public void treeNodesRemoved(TreeModelEvent e)
         {
            _stateBeforeSort = null;
         }

         @Override
         public void treeStructureChanged(TreeModelEvent e)
         {
            _stateBeforeSort = null;
         }
      };

      _tree.getModel().addTreeModelListener(_treeModelListener);
   }


   public void disableListener()
   {
      _tree.getModel().removeTreeModelListener(_treeModelListener);
   }

   public void enableListener()
   {
      _tree.getModel().addTreeModelListener(_treeModelListener);
   }


   public AliasFolderState sort(AliasFolderState state)
   {
      if(null == _stateBeforeSort)
      {
         _stateBeforeSort = Utilities.cloneObject(state);

         _currentDirection = Direction.ASC;
      }
      else
      {
         _currentDirection = _currentDirection.next();
      }

      if(_currentDirection == Direction.ASC)
      {
         state.sort(true);
      }
      else if(_currentDirection == Direction.DESC)
      {
         state.sort(false);
      }
      else
      {
         state = Utilities.cloneObject(_stateBeforeSort);
      }

      return state;
   }
}
