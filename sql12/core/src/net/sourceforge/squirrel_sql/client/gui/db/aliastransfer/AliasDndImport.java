package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class AliasDndImport
{
   public static final String IMPORT_PROPERTY_NAME = "aliasExportImport->AliasDnd_IMPORT";

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

   public List<SQLAlias> getSQLAliasesToImport()
   {
      List<SQLAlias> ret = new ArrayList<>();

      for (DefaultMutableTreeNode node : nodesToImport)
      {
         gatherAliases(node, ret);
      }

      return ret;
   }

   private void gatherAliases(DefaultMutableTreeNode node, List<SQLAlias> ret)
   {
      if(node.getUserObject() instanceof SQLAlias)
      {
         ret.add((SQLAlias) node.getUserObject());
         return;
      }

      for (int i = 0; i < node.getChildCount(); i++)
      {
         gatherAliases((DefaultMutableTreeNode) node.getChildAt(i), ret);
      }
   }
}
