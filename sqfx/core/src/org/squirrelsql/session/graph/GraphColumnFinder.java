package org.squirrelsql.session.graph;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
import java.util.List;

public class GraphColumnFinder
{
   private Pane _desktopPane;

   public GraphColumnFinder(Pane desktopPane)
   {
      _desktopPane = desktopPane;
   }

   public GraphColumn findNonDbPkCol(GraphColumn fkCol, String fkId)
   {
      for (Node tableNode : _desktopPane.getChildren())
      {
         TableWindowCtrl tableCtrl = ((Window) tableNode).getCtrl();

         List<GraphColumn> graphColumns = tableCtrl.getGraphColumns();

         for (GraphColumn col : graphColumns)
         {
            if(fkCol.isMyNonDbPkCol(col, fkId))
            {
               return col;
            }
         }
      }

      return null;
   }

   public List<GraphColumn> getAllColumnsForTable(String qualifiedTableName)
   {
      for (Node tableNode : _desktopPane.getChildren())
      {
         TableWindowCtrl tableCtrl = ((Window) tableNode).getCtrl();

         // We can't use tableCtrl.getTableInfo().getQualifiedName() because this was generated using SQLUtil.generateQualifiedName() which may be different.
         String qualifiedName = SQLUtil.getQualifiedName(tableCtrl.getTableInfo().getCatalog(), tableCtrl.getTableInfo().getSchema(), tableCtrl.getTableInfo().getName());

         if (qualifiedName.equalsIgnoreCase(qualifiedTableName))
         {
            return new ArrayList<>(tableCtrl.getGraphColumns());
         }

      }

      return null;
   }

   public TableInfo getTable(String qualifiedTableName)
   {
      for (Node tableNode : _desktopPane.getChildren())
      {
         TableWindowCtrl tableCtrl = ((Window) tableNode).getCtrl();

         // We can't use tableCtrl.getTableInfo().getQualifiedName() because this was generated using SQLUtil.generateQualifiedName() which may be different.
         String qualifiedName = SQLUtil.getQualifiedName(tableCtrl.getTableInfo().getCatalog(), tableCtrl.getTableInfo().getSchema(), tableCtrl.getTableInfo().getName());

         if (qualifiedName.equalsIgnoreCase(qualifiedTableName))
         {
            return tableCtrl.getTableInfo();
         }
      }

      return null;
   }

}
