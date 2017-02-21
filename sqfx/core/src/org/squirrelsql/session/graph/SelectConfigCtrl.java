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

public class SelectConfigCtrl
{
   private RowObjectTableLoader<SelectPositionRowObject> _tableLoader;

   private I18n _i18n = new I18n(getClass());

   private Button _btnUp = new Button(_i18n.t("SelectConfigCtrl.btnup"), new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));
   private Button _btnDown = new Button(_i18n.t("SelectConfigCtrl.btndown"), new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));

   private GraphPersistenceWrapper _graphPersistenceWrapper;
   private TableView _tableView = new TableView();

   public SelectConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper, QueryChannel queryChannel)
   {
      _graphPersistenceWrapper = graphPersistenceWrapper;

      queryChannel.addQueryChannelListener(() -> initRows());

      _tableLoader = new RowObjectTableLoader<>();
      ArrayList<ColumnHandle> columnHandles = _tableLoader.initColsByAnnotations(SelectPositionRowObject.class);
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
            if(col.getColumnConfigurationPersistence().getAggregateFunctionPersistence().isInSelect())
            {
               selCols.add(col);
            }
         }
      }

      selCols.sort((c1, c2) -> onCompare(c1, c2));




      for (int i = 0; i < selCols.size(); i++)
      {
         selCols.get(i).getColumnConfigurationPersistence().setSelectPosition(i);
         _tableLoader.addRowObject(new SelectPositionRowObject(selCols.get(i)));
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
         RowObjectHandle<SelectPositionRowObject> rowObjectHandle = _tableLoader.getRowObjectHandleForTableRow(_tableView.getItems().get(i));
         rowObjectHandle.getRowObject().setSelectPosition(i);
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
