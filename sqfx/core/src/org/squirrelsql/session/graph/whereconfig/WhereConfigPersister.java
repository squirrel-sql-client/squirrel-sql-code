package org.squirrelsql.session.graph.whereconfig;

import javafx.scene.control.TreeItem;
import org.squirrelsql.session.graph.ColumnPersistence;
import org.squirrelsql.session.graph.ColumnPersistenceId;
import org.squirrelsql.session.graph.FilterPersistence;
import org.squirrelsql.session.graph.FilterPersistenceUtil;
import org.squirrelsql.session.graph.GraphPersistenceWrapper;
import org.squirrelsql.session.graph.GraphTablePersistence;

import java.util.ArrayList;

public class WhereConfigPersister
{
   public static void toPersistence(TreeItem<WhereConfigColTreeNode> root, GraphPersistenceWrapper graphPersistenceWrapper)
   {
      WhereConfigColPersistence whereConfigColPersistence = graphPersistenceWrapper.getDelegate().getWhereConfigColPersistence();

      _toPersistence(whereConfigColPersistence, root, getFilteredColumnPersistences(graphPersistenceWrapper));


   }

   private static void _toPersistence(WhereConfigColPersistence parentPersistence, TreeItem<WhereConfigColTreeNode> parentNode, ArrayList<ColumnPersistence> filteredColumnPersistences)
   {
      WhereConfigColTreeNode value = parentNode.getValue();

      if(value.isFolder())
      {
         if (false == value.isRoot())
         {
            parentPersistence.setWhereConfigEnumAsString(value.getWhereConfigColEnum().name());
            parentPersistence.setWhereConfigColEnumId(value.getId());
         }


         parentPersistence.getKids().clear();
         for (TreeItem<WhereConfigColTreeNode> node : parentNode.getChildren())
         {
            WhereConfigColPersistence buf = new WhereConfigColPersistence();

            parentPersistence.getKids().add(buf);

            _toPersistence(buf, node, filteredColumnPersistences);
         }
      }
      else if(value.isFilter() && containsId(filteredColumnPersistences, value.getId()))
      {
         parentPersistence.setFilterId(value.getId());
      }
   }

   private static boolean containsId(ArrayList<ColumnPersistence> filteredColumnPersistences, String id)
   {
      return filteredColumnPersistences.stream().filter(fcp -> ColumnPersistenceId.createId(fcp).equals(id)).findFirst().isPresent();
   }

   public static void toGui(TreeItem<WhereConfigColTreeNode> root, GraphPersistenceWrapper graphPersistenceWrapper)
   {
      root.getChildren().clear();


      WhereConfigColPersistence whereConfigColPersistence = graphPersistenceWrapper.getDelegate().getWhereConfigColPersistence();

      ArrayList<ColumnPersistence> filteredColumnPersistences = getFilteredColumnPersistences(graphPersistenceWrapper);


      _toGui(root, whereConfigColPersistence, filteredColumnPersistences);


      // The column persistences that weren't found in the whereConfigColPersistence tree are now added to the root node
      for (ColumnPersistence columnPersistence : filteredColumnPersistences)
      {
         TreeItem<WhereConfigColTreeNode> treeItem = new TreeItem<>(new WhereConfigColTreeNode(columnPersistence));
         root.getChildren().add(treeItem);
      }

   }

   private static void _toGui(TreeItem<WhereConfigColTreeNode> parentTreeNode, WhereConfigColPersistence parent, ArrayList<ColumnPersistence> filteredColumnPersistences)
   {
      for (WhereConfigColPersistence wccp : parent.getKids())
      {
         if(null == wccp.getWhereConfigEnumAsString() )
         {
            ArrayList<ColumnPersistence> toRemove = new ArrayList<>();
            for (ColumnPersistence fcp : filteredColumnPersistences)
            {
               if(   null != fcp.getColumnConfigurationPersistence().getFilterPersistence()
                  && ColumnPersistenceId.createId(fcp).equals(wccp.getFilterId()))
               {
                  TreeItem<WhereConfigColTreeNode> treeItem = new TreeItem<>(new WhereConfigColTreeNode(fcp));
                  parentTreeNode.getChildren().add(treeItem);
                  toRemove.add(fcp);
               }
            }

            filteredColumnPersistences.removeAll(toRemove);
         }
         else
         {
            TreeItem<WhereConfigColTreeNode> treeItem = new TreeItem<>(new WhereConfigColTreeNode(WhereConfigColEnum.valueOf(wccp.getWhereConfigEnumAsString())));
            parentTreeNode.getChildren().add(treeItem);
            _toGui(treeItem, wccp, filteredColumnPersistences);
         }
      }

      parentTreeNode.setExpanded(true);
   }

   private static ArrayList<ColumnPersistence> getFilteredColumnPersistences(GraphPersistenceWrapper graphPersistenceWrapper)
   {
      ArrayList<ColumnPersistence> columnPersistences = new ArrayList<>();

      for (GraphTablePersistence graphTablePersistence : graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         for (ColumnPersistence columnPersistence : graphTablePersistence.getColumnPersistences())
         {
            FilterPersistence filterPersistence = columnPersistence.getColumnConfigurationPersistence().getFilterPersistence();

            if (false == FilterPersistenceUtil.isEmpty(filterPersistence))
            {
               columnPersistences.add(columnPersistence);
            }

         }
      }
      return columnPersistences;
   }
}
