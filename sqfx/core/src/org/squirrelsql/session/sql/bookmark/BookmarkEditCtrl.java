package org.squirrelsql.session.sql.bookmark;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.ActionHandle;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.SqFxActionListener;
import org.squirrelsql.session.action.StandardActionConfiguration;

public class BookmarkEditCtrl
{
   private final Stage _dialog;

   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());


   public BookmarkEditCtrl()
   {
      FxmlHelper<BookmarkEditView> fxmlHelper = new FxmlHelper<>(BookmarkEditView.class);


      _dialog = new Stage();
      _dialog.setTitle(_i18n.t("bookmark.edit.title"));
      _dialog.initModality(Modality.WINDOW_MODAL);
      _dialog.initOwner(AppState.get().getPrimaryStage());
      Region region = fxmlHelper.getRegion();

      SplitPane splitPane = new SplitPane();
      splitPane.setOrientation(Orientation.VERTICAL);

      splitPane.getItems().add(region);
      splitPane.getItems().add(new TableView<>());


      _dialog.setScene(new Scene(splitPane));

      GuiUtils.makeEscapeClosable(splitPane);

      new StageDimensionSaver("bookmarkedit", _dialog, _pref, splitPane.getPrefWidth(), splitPane.getPrefHeight(), _dialog.getOwner());

      _dialog.showAndWait();




   }

   public static void registerListener(SessionTabContext sessionTabContext)
   {
      ActionHandle hEditBookmark = new ActionManager().getActionHandle(StandardActionConfiguration.EDIT_BOOKMARK, sessionTabContext);
      hEditBookmark.setOnAction(BookmarkEditCtrl::new);
   }
}
