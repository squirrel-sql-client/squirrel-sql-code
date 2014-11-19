package org.squirrelsql;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.splash.Version;


public class AboutController
{
   public AboutController()
   {
      Stage dialog = new Stage();
      dialog.setTitle(new I18n(getClass()).t("about.title"));
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(AppState.get().getPrimaryStage());

      ImageView imageView = new Props(getClass()).getImageView("splash.jpg");

      BorderPane bp = new BorderPane();
      bp.setCenter(imageView);

      String text = Version.getVersion() + "\n" + Version.getCopyrightStatement();
      Label label = new Label(text);
      label.setStyle("-fx-text-alignment: center; -fx-background-color: #AEB0C5");
      bp.setStyle("-fx-background-color: #AEB0C5");
      label.setFocusTraversable(true);

      BorderPane.setAlignment(label, Pos.CENTER);
      bp.setBottom(label);


      dialog.setScene(new Scene(bp));

      GuiUtils.makeEscapeClosable(bp);

      bp.setPrefWidth(imageView.getFitWidth());
      bp.setPrefHeight(imageView.getFitHeight());

      Platform.runLater(() -> GuiUtils.centerWithinParent(dialog));

      dialog.showAndWait();

   }
}
