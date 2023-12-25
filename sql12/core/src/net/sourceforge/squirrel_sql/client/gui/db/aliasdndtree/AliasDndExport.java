package net.sourceforge.squirrel_sql.client.gui.db.aliasdndtree;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class AliasDndExport
{
   public static final String ALIAS_DND_EXPORT_PROPERTY_NAME = "aliasDnd->AliasDnd_EXPORT";
   ;

   private List<TreePath> _treeSelectionPaths;

   private SQLAlias _listSelectedAlias;

   public AliasDndExport(TreePath[] treeSelectionPaths)
   {
      // Do not move to initialization to field
      _treeSelectionPaths = new ArrayList<>();

      // Clear descendants
      for (TreePath tp1 : treeSelectionPaths)
      {

         boolean isDescendant = false;
         for (TreePath tp2 : treeSelectionPaths)
         {
            if(tp2 != tp1 && tp2.isDescendant(tp1))
            {
               isDescendant = true;
               break;
            }
         }

         if(false == isDescendant)
         {
            _treeSelectionPaths.add(tp1);
         }
      }
   }

   public AliasDndExport(SQLAlias listSelectedAlias)
   {
      _listSelectedAlias = listSelectedAlias;
   }


   public List<TreePath> getTreeSelectionPaths()
   {
      return _treeSelectionPaths;
   }

   public SQLAlias getListSelectedAlias()
   {
      return _listSelectedAlias;
   }
}
