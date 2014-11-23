package org.squirrelsql.session.sql;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.Session;
import org.squirrelsql.table.RowObjectTableLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLHistorySearchCtrl
{
   private final Stage _dialog;
   private final SQLHistorySearchView _view;
   private RowObjectTableLoader<SQLHistoryEntry> _currentLoader;
   private SQLTextAreaServices _sqlTextAreaServices;

   private final TableView _tblHistory = new TableView();
   private final TextArea _txtSqlPreview = new TextArea();

   private SplitPositionSaver _splitPositionSaver = new SplitPositionSaver(getClass(), "history.split");
   private final RowObjectTableLoader<SQLHistoryEntry> _originalTableLoader;
   private boolean _dontReactToChkFiltered =false;

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


      _view.cboFilterType.setItems(FXCollections.observableList(Arrays.asList(SqlHistoryFilterType.values())));
      _view.cboFilterType.getSelectionModel().selectFirst();

      _view.btnApply.setOnAction(e -> onApply());
      _view.chkFiltered.setOnAction(e -> onChkFiltered());


      _view.split.getItems().add(_tblHistory);
      _view.split.getItems().add(_txtSqlPreview);

      _originalTableLoader = new RowObjectTableLoader<>();
      _originalTableLoader.initColsByAnnotations(SQLHistoryEntry.class);
      _originalTableLoader.addRowObjects(items);
      _currentLoader = _originalTableLoader.cloneLoader();
      _currentLoader.load(_tblHistory);

      _tblHistory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTableSelectionChanged());

      _tblHistory.setOnMouseClicked(e -> onTblHistoryClicked(e));


      _txtSqlPreview.setEditable(false);

      _dialog.setOnCloseRequest(e -> close());


      _view.txtFilter.requestFocus();

      _splitPositionSaver.apply(_view.split);
      _dialog.showAndWait();
   }

   private void onTblHistoryClicked(MouseEvent e)
   {
      if(Utils.isDoubleClick(e))
      {
         for (SQLHistoryEntry sqlHistoryEntry : getSelectedRowObjects())
         {
            _sqlTextAreaServices.appendToEditor("\n" + sqlHistoryEntry.getSql());
         }
      }
   }

   private void onTableSelectionChanged()
   {
      List<SQLHistoryEntry> selectedRows = getSelectedRowObjects();

      if(0 < selectedRows.size())
      {
         _txtSqlPreview.setText(selectedRows.get(0).getSql());
      }
   }

   private List<SQLHistoryEntry> getSelectedRowObjects()
   {
      ObservableList<Integer> selectedIndices = _tblHistory.getSelectionModel().getSelectedIndices();
      return _currentLoader.getRowObjectsForIndices(selectedIndices);
   }

   private void onApply()
   {
      List<SQLHistoryEntry> toFilter = _originalTableLoader.getRowObjects();

      ArrayList<SQLHistoryEntry> toRemove = new ArrayList<>();
      for (SQLHistoryEntry sqlHistoryEntry : toFilter)
      {
         if(false == matchesFilter(sqlHistoryEntry))
         {
            toRemove.add(sqlHistoryEntry);
         }
      }


      boolean filtered;
      if(0 < toRemove.size())
      {
         toFilter.removeAll(toRemove);

         _currentLoader = _originalTableLoader.cloneLoaderFor(toFilter);

         _tblHistory.getItems().clear();
         _currentLoader.load(_tblHistory);
         filtered = true;
      }
      else
      {
         _currentLoader = _originalTableLoader.cloneLoader();
         _currentLoader.load(_tblHistory);
         filtered = false;
      }

      try
      {
         _dontReactToChkFiltered = true;
         _view.chkFiltered.setSelected(filtered);
      }
      finally
      {
         _dontReactToChkFiltered = false;
      }
   }

   private boolean matchesFilter(SQLHistoryEntry sqlHistoryEntry)
   {
      String filter = _view.txtFilter.getText();

      if(null == filter || 0 == filter.length())
      {
         return true;
      }

      String ucfilter;

      SqlHistoryFilterType sel = _view.cboFilterType.getSelectionModel().getSelectedItem();
      switch (sel)
      {
         case CONTAINS:
            ucfilter = filter.toUpperCase();
            return -1 < sqlHistoryEntry.getSql().toUpperCase().indexOf(ucfilter);
         case STARTS_WITH:
            ucfilter = filter.toUpperCase();
            return sqlHistoryEntry.getSql().toUpperCase().startsWith(ucfilter);
         case ENDS_WITH:
            ucfilter = filter.toUpperCase();
            return sqlHistoryEntry.getSql().toUpperCase().endsWith(ucfilter);
         case REG_EXP:
            return sqlHistoryEntry.getSql().matches(filter);
      }

      throw new IllegalArgumentException("How can I ever get here?????");
   }

   private void onChkFiltered()
   {
      if(_dontReactToChkFiltered)
      {
         return;
      }


      if(_view.chkFiltered.isSelected())
      {
         onApply();
      }
      else
      {
         _tblHistory.getItems().clear();
         _currentLoader = _originalTableLoader.cloneLoader();
         _currentLoader.load(_tblHistory);
      }
   }


   private void close()
   {
      _splitPositionSaver.save(_view.split);
   }
}
