package org.squirrelsql;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

public class SessionTabbedPaneCtrl
{
   private MessageHandler _mhPanel = new MessageHandler(this.getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private MessageHandler _mhLog = new MessageHandler(this.getClass(), MessageHandlerDestination.MESSAGE_LOG);


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
         _mhLog.error("Test error");
         _mhPanel.error("Test error");
         IllegalStateException illegalStateException = new IllegalStateException("Test exception");
         _mhPanel.error(illegalStateException);
         throw illegalStateException;
      }
      else if("Warning".equals(source.getText()))
      {
         _mhLog.warning("Test warning");
         _mhPanel.warning("Test warning");
      }
      else if("Message".equals(source.getText()))
      {
         _mhLog.info("Test info");
         _mhPanel.info("Test info");
      }
   }
}
