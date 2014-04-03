package org.squirrelsql.services;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class ProgressibleStage
{

   private final StackPane stackPane;
   private Stage stage;

   public ProgressibleStage(Stage stage, boolean cancelable)
   {
      this.stage = stage;
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setMaxSize(50, 50);
      progressIndicator.setVisible(false);

      ProgressRegion veil = new ProgressRegion(cancelable);
      veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
      veil.setVisible(false);

      stackPane = new StackPane();
      stackPane.getChildren().addAll(veil, progressIndicator);

      stage.setScene(new Scene(stackPane));
   }

   public void setSceneRoot(Node root)
   {
//      root.setStyle("-fx-border-color: blue;");
//      stackPane.setStyle("-fx-border-color: red;");


      AnchorPane.setBottomAnchor(root, 0d);
      AnchorPane.setTopAnchor(root, 0d);
      AnchorPane.setLeftAnchor(root, 0d);
      AnchorPane.setRightAnchor(root, 0d);

      AnchorPane ap = new AnchorPane();
      ap.getChildren().add(root);
      stackPane.getChildren().add(0, ap);
   }


   public Stage getStage()
   {
      return stage;
   }
}
