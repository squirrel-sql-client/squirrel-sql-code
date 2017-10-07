package org.squirrelsql.session.graph;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.RowObjectHandle;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.ArrayList;

public class OrderByConfigCtrl
{
   private RowObjectTableLoader<OrderByPositionRowObject> _tableLoader;

   private I18n _i18n = new I18n(getClass());

   private Button _btnUp = new Button(_i18n.t("OrderByConfigCtrl.btnup"), new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));
   private Button _btnDown = new Button(_i18n.t("OrderByConfigCtrl.btndown"), new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));

   private GraphPersistenceWrapper _graphPersistenceWrapper;
   private TableView _tableView = new TableView();

   public OrderByConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper, QueryChannel queryChannel)
   {
      _graphPersistenceWrapper = graphPersistenceWrapper;

      queryChannel.addQueryChannelListener(() -> initRows());

      _tableLoader = new RowObjectTableLoader<>();
      ArrayList<ColumnHandle> columnHandles = _tableLoader.initColsByAnnotations(OrderByPositionRowObject.class);
      columnHandles.forEach(ch -> ch.getTableColumn().setSortable(false));

      initRows();

      _btnUp.setOnAction(e -> onUp());
      _btnDown.setOnAction(e -> onDown());
   }

   private void initRows()
   {
      _tableLoader.clearRows();

      ArrayList<ColumnPersistence> selCols = new ArrayList<>();

      for (GraphTablePersistence table : _graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         for (ColumnPersistence col : table.getColumnPersistences())
         {
            String orderByString = col.getColumnConfigurationPersistence().getOrderByPersistence().getOrderBy();

            if( OrderBy.NONE != OrderBy.valueOf(orderByString))
            {
               selCols.add(col);
            }
         }
      }

      selCols.sort((c1, c2) -> onCompare(c1, c2));


      for (int i = 0; i < selCols.size(); i++)
      {
         selCols.get(i).getColumnConfigurationPersistence().setOrderByPosition(i);
         _tableLoader.addRowObject(new OrderByPositionRowObject(selCols.get(i)));
      }

      _tableView.refresh();
   }

   private void onDown()
   {
      _tableLoader.moveSelectedRowsDown();
      initColumnIndexes();
   }


   private void onUp()
   {
      _tableLoader.moveSelectedRowsUp();
      initColumnIndexes();
   }

   private void initColumnIndexes()
   {
      for (int i = 0; i < _tableView.getItems().size(); i++)
      {
         RowObjectHandle<OrderByPositionRowObject> rowObjectHandle = _tableLoader.getRowObjectHandleForTableRow(_tableView.getItems().get(i));
         rowObjectHandle.getRowObject().setOrderByPosition(i);
      }
   }


   private int onCompare(ColumnPersistence c1, ColumnPersistence c2)
   {
      int orderByPos1 = c1.getColumnConfigurationPersistence().getOrderByPosition();
      int orderByPos2 = c2.getColumnConfigurationPersistence().getOrderByPosition();

      if(orderByPos1 == -1)
      {
         return 1;
      }

      if(orderByPos2 == -1)
      {
         return -1;
      }


      return new Integer(orderByPos1).compareTo(orderByPos2);
   }

   public Pane getPane()
   {
      _tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      _tableLoader.load(_tableView);

      HBox hBox = new HBox();

      HBox.setMargin(_btnUp, new Insets(5,10,5, 5));
      HBox.setMargin(_btnDown, new Insets(5,0,5, 0));

      hBox.getChildren().addAll(_btnUp, _btnDown);

      BorderPane borderPane = new BorderPane(_tableView);

      borderPane.setBottom(hBox);

      return borderPane;
   }
}
