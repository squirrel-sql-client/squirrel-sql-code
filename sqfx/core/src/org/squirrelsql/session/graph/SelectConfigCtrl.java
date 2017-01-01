package org.squirrelsql.session.graph;

import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.ArrayList;

public class SelectConfigCtrl
{
   private final RowObjectTableLoader<SelectPositionRowObject> _tableLoader;

   public SelectConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper)
   {

      ArrayList<ColumnPersistence> selCols = new ArrayList<>();

      for (GraphTablePersistence table : graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         for (ColumnPersistence col : table.getColumnPersistences())
         {
            if(col.getColumnConfigurationPersistence().getAggregateFunctionPersistence().isInSelect())
            {
               selCols.add(col);
            }
         }
      }

      selCols.sort((c1, c2) -> onCompare(c1, c2));


      _tableLoader = new RowObjectTableLoader<>();

      ArrayList<ColumnHandle> columnHandles = _tableLoader.initColsByAnnotations(SelectPositionRowObject.class);

      columnHandles.forEach(ch -> ch.getTableColumn().setSortable(false));

      for (int i = 0; i < selCols.size(); i++)
      {
         selCols.get(i).getColumnConfigurationPersistence().setSelectPosition(i);
         _tableLoader.addRowObject(new SelectPositionRowObject(selCols.get(i)));
      }
   }

   private int onCompare(ColumnPersistence c1, ColumnPersistence c2)
   {
      int selPos1 = c1.getColumnConfigurationPersistence().getSelectPosition();
      int selPos2 = c2.getColumnConfigurationPersistence().getSelectPosition();

      if(selPos1 == -1)
      {
         return 1;
      }

      if(selPos2 == -1)
      {
         return -1;
      }


      return new Integer(selPos1).compareTo(selPos2);
   }

   public Pane getPane()
   {
      TableView ret = new TableView();

      _tableLoader.load(ret);

      return new BorderPane(ret);
   }
}
