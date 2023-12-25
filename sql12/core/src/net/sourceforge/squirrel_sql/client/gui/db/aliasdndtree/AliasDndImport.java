package net.sourceforge.squirrel_sql.client.gui.db.aliasdndtree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class AliasDndImport
{
   public static final String ALIAS_DND_IMPORT_PROPERTY_NAME = "aliasDnd->AliasDnd_IMPORT";

   private ArrayList<DefaultMutableTreeNode> nodesToImport = new ArrayList<>();

   public AliasDndImport(TreePath[] pathsToImport)
   {
      for (TreePath selectionPath : pathsToImport)
      {
         nodesToImport.add((DefaultMutableTreeNode) selectionPath.getLastPathComponent());
      }
   }

   public ArrayList<DefaultMutableTreeNode> getNodesToImport()
   {
      return nodesToImport;
   }

}
