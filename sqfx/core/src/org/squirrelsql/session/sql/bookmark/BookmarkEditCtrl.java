package org.squirrelsql.session.sql.bookmark;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.ActionHandle;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.StandardActionConfiguration;

public class BookmarkEditCtrl
{
   private final Stage _dialog;
   private final TreeTableView<BookmarkWrapper> _treeTableView;
   private final BookmarkEditView _view;

   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());
   private final Region _region;


   public BookmarkEditCtrl(SessionTabContext sessionTabContext)
   {
      FxmlHelper<BookmarkEditView> fxmlHelper = new FxmlHelper<>(BookmarkEditView.class);


      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("bookmark.edit.title"));
      _dialog.initModality(Modality.WINDOW_MODAL);
      _dialog.initOwner(AppState.get().getPrimaryStage());

      _region = fxmlHelper.getRegion();
      _view = fxmlHelper.getView();

      _view.lblNote.setText(_i18n.t("bookmarkedit.note", StandardActionConfiguration.EXEC_BOOKMARK.getActionConfiguration().getKeyCodeCombination()));

      SplitPane splitPane = new SplitPane();
      splitPane.setOrientation(Orientation.VERTICAL);

      splitPane.getItems().add(_region);
      _treeTableView = new TreeTableView<>();
      splitPane.getItems().add(_treeTableView);


      _dialog.setScene(new Scene(splitPane));

      GuiUtils.makeEscapeClosable(splitPane);

      new StageDimensionSaver("bookmarkedit", _dialog, _pref, splitPane.getPrefWidth(), splitPane.getPrefHeight(), _dialog.getOwner());

      loadData();

      _view.btnNew.setOnAction(event -> _treeTableView.getSelectionModel().clearSelection());
      _view.btnSave.setOnAction(event -> onSave(sessionTabContext));
      _view.btnDelete.setOnAction(event -> onDelete(sessionTabContext));

      _dialog.showAndWait();
   }

   private void onDelete(SessionTabContext sessionTabContext)
   {
      BookmarkSaveHelper.delete(_dialog, _view, _treeTableView);
      fireChanged(sessionTabContext);
   }


   private void onSave(SessionTabContext sessionTabContext)
   {
      boolean saved = BookmarkSaveHelper.save(_dialog, _view, _treeTableView);
      if(saved)
      {
         fireChanged(sessionTabContext);
      }
   }

   private void fireChanged(SessionTabContext sessionTabContext)
   {
      sessionTabContext.bookmarksChangedProperty().set(!sessionTabContext.bookmarksChangedProperty().get());
   }


   private void loadData()
   {
      // http://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-table-view.htm

      BookmarkWrapper root = BookmarkWrapper.createWrapperTree();

      TreeItem<BookmarkWrapper> treeRoot = new TreeItem<>(root);


      for (BookmarkWrapper folder : root.getKids())
      {
         ImageView folderImg = new Props(BookmarkEditCtrl.class).getImageView(GlobalIconNames.FOLDER);
         TreeItem<BookmarkWrapper> treeFolder = new TreeItem<>(folder, folderImg);

         treeRoot.getChildren().add(treeFolder);

         for (BookmarkWrapper wrapper : folder.getKids())
         {
            treeFolder.getChildren().add(new TreeItem<>(wrapper));
         }

         if (folder.getBookmarkWrapperType() == BookmarkWrapperType.USER_BOOKMARKS_NODE)
         {
            treeFolder.setExpanded(true);
         }
      }


      TreeTableColumn<BookmarkWrapper,String> selShortCutColumn = new TreeTableColumn<>(_i18n.t("bookmarkedit.selShortcut.column"));
      selShortCutColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<BookmarkWrapper, String> p) -> new ReadOnlyStringWrapper(p.getValue().getValue().getSelShortcut()));


      TreeTableColumn<BookmarkWrapper, String> descriptionColumn = new TreeTableColumn<>(_i18n.t("bookmarkedit.description.column"));
      descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<BookmarkWrapper, String> param) ->new ReadOnlyStringWrapper(param.getValue().getValue().getDescription()));

      TreeTableColumn<BookmarkWrapper, String> sqlColumn = new TreeTableColumn<>(_i18n.t("bookmarkedit.sql.column"));
      sqlColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<BookmarkWrapper, String> param) ->new ReadOnlyStringWrapper(param.getValue().getValue().getSql()));

      TreeTableColumn<BookmarkWrapper, Boolean> useAsBookmarkColumn = new TreeTableColumn<>(_i18n.t("bookmarkedit.sql.useAsBookmark.column"));
      useAsBookmarkColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<BookmarkWrapper, Boolean> param) ->new ReadOnlyBooleanWrapper(param.getValue().getValue().isUseAsBookmark()));

      TreeTableColumn<BookmarkWrapper, Boolean> useAsAbbreviation = new TreeTableColumn<>(_i18n.t("bookmarkedit.sql.useAsAbbreviation.column"));
      useAsAbbreviation.setCellValueFactory((TreeTableColumn.CellDataFeatures<BookmarkWrapper, Boolean> param) ->new ReadOnlyBooleanWrapper(param.getValue().getValue().isUseAsAbbreviation()));


      _treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTreeSelectionChanged(newValue));


      _treeTableView.setRoot(treeRoot);
      _treeTableView.getColumns().addAll(selShortCutColumn, descriptionColumn, sqlColumn, useAsBookmarkColumn, useAsAbbreviation);
      _treeTableView.setShowRoot(false);


   }

   private void onTreeSelectionChanged(TreeItem<BookmarkWrapper> newValue)
   {
      _view.txtKey.setText("");
      _view.txtDescription.setText("");
      _view.txtSQL.setText("");

      _view.txtKey.setEditable(true);
      _view.txtDescription.setEditable(true);
      _view.txtSQL.setEditable(true);

      _view.chkBookmark.setSelected(false);
      _view.chkAbbreviation.setSelected(false);

      _view.btnNew.setDisable(false);
      _view.btnDelete.setDisable(false);

      disableAllButNewButton(false);



      if (null == newValue)
      {
         return;
      }

      BookmarkWrapper selBookmark = newValue.getValue();

      if(   selBookmark.getBookmarkWrapperType() == BookmarkWrapperType.SQUIRREL_BOOKMARKS_NODE
         || selBookmark.getBookmarkWrapperType() == BookmarkWrapperType.USER_BOOKMARKS_NODE)
      {
         disableAllButNewButton(true);
         return;
      }


      _view.txtKey.setText(selBookmark.getSelShortcut());
      _view.txtDescription.setText(selBookmark.getDescription());
      _view.txtSQL.setText(selBookmark.getSql());

      _view.chkBookmark.setSelected(selBookmark.isUseAsBookmark());
      _view.chkAbbreviation.setSelected(selBookmark.isUseAsAbbreviation());



      TreeItem<BookmarkWrapper> parent = newValue.getParent();
      if(null != parent && parent.getValue().getBookmarkWrapperType() == BookmarkWrapperType.SQUIRREL_BOOKMARKS_NODE)
      {
         _view.txtKey.setEditable(false);
         _view.txtDescription.setEditable(false);
         _view.txtSQL.setEditable(false);
         _view.btnNew.setDisable(true);
         _view.btnDelete.setDisable(true);
      }
   }

   private void disableAllButNewButton(boolean b)
   {
      _view.txtKey.setDisable(b);
      _view.txtDescription.setDisable(b);
      _view.txtSQL.setDisable(b);
      _view.chkBookmark.setDisable(b);
      _view.chkAbbreviation.setDisable(b);
      _view.btnSave.setDisable(b);
      //_view.btnNew.setDisable(b);
      _view.btnDelete.setDisable(b);
   }

   public static void registerListener(SessionTabContext sessionTabContext)
   {
      ActionHandle hEditBookmark = new ActionManager().getActionHandle(StandardActionConfiguration.EDIT_BOOKMARK, sessionTabContext);
      hEditBookmark.setOnAction(() -> new BookmarkEditCtrl(sessionTabContext));
   }
}
