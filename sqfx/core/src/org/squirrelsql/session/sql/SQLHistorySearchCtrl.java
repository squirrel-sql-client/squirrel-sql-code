package org.squirrelsql.session.sql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.Session;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.Arrays;

public class SQLHistorySearchCtrl
{
   private final Stage _dialog;
   private final SQLHistorySearchView _view;
   private SQLTextAreaServices _sqlTextAreaServices;

   private final TableView<Object> _tblHistory = new TableView<>();
   private final TextArea _txtSqlPreview = new TextArea();

   private SplitPositionSaver _splitPositionSaver = new SplitPositionSaver(getClass(), "history.split");

   public SQLHistorySearchCtrl(SQLTextAreaServices sqlTextAreaServices, Session session, ObservableList<SQLHistoryEntry> items)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      FxmlHelper<SQLHistorySearchView> fxmlHelper = new FxmlHelper<>(SQLHistorySearchView.class);
      _view = fxmlHelper.getView();

      _dialog = new Stage();
      _dialog.setTitle(new I18n(getClass()).t("SQLHistorySearchCtrl.title", session.getMainTabContext().getSessionTabTitle()));
      _dialog.initModality(Modality.WINDOW_MODAL);
      _dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();
      _dialog.setScene(new Scene(region));

      GuiUtils.makeEscapeClosable(region);

      new StageDimensionSaver("sqlhistorysearch", _dialog, new Pref(getClass()), region.getPrefWidth(), region.getPrefHeight(), _dialog.getOwner());


      _view.cboSearchType.setItems(FXCollections.observableList(Arrays.asList(SqlHistorySearchType.values())));
      _view.cboSearchType.getSelectionModel().selectFirst();

      _view.btnApply.setOnAction(e -> onApply());


      _view.split.getItems().add(_tblHistory);
      _view.split.getItems().add(_txtSqlPreview);
      _splitPositionSaver.apply(_view.split);

      RowObjectTableLoader<SQLHistoryEntry> tableLoader = new RowObjectTableLoader<>();
      tableLoader.initColsByAnnotations(SQLHistoryEntry.class);
      tableLoader.addRowObjects(items);
      tableLoader.load(_tblHistory);


      _dialog.setOnCloseRequest(e -> close());


      _dialog.showAndWait();
   }

   private void onApply()
   {
      if(null == _view.txtSearchText.getText() || 0 == _view.txtSearchText.getText().length())
      {
         return;
      }

      _view.chkFiltered.setSelected(true);



   }

   private void close()
   {
      _splitPositionSaver.save(_view.split);
   }
}
