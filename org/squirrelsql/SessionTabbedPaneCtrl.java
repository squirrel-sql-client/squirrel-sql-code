package org.squirrelsql;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class SessionTabbedPaneCtrl
{
   public Node getNode()
   {
      BorderPane bp = new BorderPane();
      bp.setCenter(new TextArea("Session"));


      EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>()
      {
         @Override
         public void handle(ActionEvent actionEvent)
         {
            onAction(actionEvent);
         }
      };

      HBox hBox = new HBox();

      Button error = new Button("Error");
      error.setOnAction(eventHandler);
      hBox.getChildren().add(error);

      Button warning = new Button("Warning");
      warning.setOnAction(eventHandler);
      hBox.getChildren().add(warning);

      Button message = new Button("Message");
      message.setOnAction(eventHandler);
      hBox.getChildren().add(message);

      bp.setBottom(hBox);


      return bp;
   }

   private void onAction(ActionEvent actionEvent)
   {
      Button source = (Button) actionEvent.getSource();
      if("Error".equals(source.getText()))
      {
         throw new IllegalStateException("Test exception");
      }
      else if("Warning".equals(source.getText()))
      {
         AppState.get().getStatusBarCtrl().warning("Test warning");
      }
      else if("Message".equals(source.getText()))
      {
         AppState.get().getStatusBarCtrl().info("Test info");
      }
   }
}
