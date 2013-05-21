package org.squirrelsql;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MessagePanelCtrl
{
   private VBox _messages;
   private final ScrollPane _sp;

   public MessagePanelCtrl()
   {
      _messages = new VBox();
      _sp = new ScrollPane();
      _sp.setContent(_messages);
   }

   public void error(String s)
   {
      addMessage(s);
   }

   public void warning(String s)
   {
      addMessage(s);
   }

   public void info(String s)
   {
      addMessage(s);
   }

   private void addMessage(String s)
   {
      Label label = new Label(s);

      _messages.getChildren().add(label);

      Runnable runnable = new Runnable()
      {
         public void run()
         {
            _sp.setVvalue(1);
         }
      };

      Platform.runLater(runnable);
   }

   public void error(Throwable t)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);

      pw.flush();
      sw.flush();

      addMessage(sw.toString());

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
