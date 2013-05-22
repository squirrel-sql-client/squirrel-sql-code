package org.squirrelsql;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MessagePanelCtrl
{
   private static final String STYLE_RED = "-fx-border-color: red; -fx-border-width: 3;";
   private static final String STYLE_YELLOW = "-fx-border-color: yellow; -fx-border-width: 3;";
   private static final String STYLE_GREEN = "-fx-border-color: green; -fx-border-width: 3;";
   private static final String STYLE_LIGHT_RED = "-fx-border-color: indianred; -fx-border-width: 3";


   private VBox _messages;
   private final ScrollPane _sp;

   public MessagePanelCtrl()
   {
      _messages = new VBox();
      _messages.setFillWidth(true);

      _sp = new ScrollPane();
      _sp.setContent(_messages);

      _messages.heightProperty().addListener(new ChangeListener<Number>()
      {
         @Override
         public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber)
         {
            _sp.setVvalue(newNumber.doubleValue());
         }
      });
   }

   public void error(String s)
   {
      addMessage(s, STYLE_RED);
   }

   public void warning(String s)
   {
      addMessage(s, STYLE_YELLOW);
   }

   public void info(String s)
   {
      addMessage(s, STYLE_GREEN);
   }

   private void addMessage(String s, String style)
   {
      int size = _messages.getChildren().size();
      if(0 < size)
      {
         Label label = (Label) _messages.getChildren().get(size - 1);

         if(STYLE_RED.equals(label.getStyle()))
         {
            label.setStyle(STYLE_LIGHT_RED);
         }
      }

      Label label = new Label(s);
      label.setStyle(style);
      _messages.getChildren().add(label);
   }

   public void error(Throwable t)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);

      pw.flush();
      sw.flush();

      error(sw.toString());

      try
      {
         pw.close();
         sw.close();
      }
      catch (IOException e)
      {

      }

   }

   public Node getNode()
   {
      return _sp;
   }
}
