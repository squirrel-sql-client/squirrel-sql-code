package org.squirrelsql.services;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ProgressibleStage
{

   private final StackPane stackPane;
   private Stage stage;

   public ProgressibleStage(Stage stage)
   {
      this.stage = stage;
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setMaxSize(50, 50);
      progressIndicator.setVisible(false);

      ProgressRegion veil = new ProgressRegion();
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
      stackPane.getChildren().add(0, root);
   }


   public Stage getStage()
   {
      return stage;
   }
}
