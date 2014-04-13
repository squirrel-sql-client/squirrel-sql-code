package org.squirrelsql;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.squirrelsql.services.Utils;

import java.sql.SQLException;

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

      _messages.heightProperty().addListener((observableValue, oldNumber, newNumber) -> _sp.setVvalue(newNumber.doubleValue()));
   }

   public void error(String s)
   {
      addMessage(s, STYLE_RED);
   }

   public void warning(String s)
   {
      addMessage(s, STYLE_YELLOW);
   }

   public void warning(String s, Throwable t)
   {
      addMessage(formatMessageWithStacktrace(s, t), STYLE_YELLOW);
   }


   public void info(String s)
   {
      addMessage(s, STYLE_GREEN);
   }

   private void addMessage(String s, String style)
   {
      if(Platform.isFxApplicationThread())
      {
         _addMessage(s, style);
      }
      else
      {
         Platform.runLater(() -> _addMessage(s, style));
      }

   }

   private void _addMessage(String s, String style)
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

      //https://forums.oracle.com/forums/thread.jspa?threadID=2317231
      Label label = new Label(s);
      label.setStyle(style);
      _messages.getChildren().add(label);
   }

   public void error(Throwable t)
   {
   }

   public Node getNode()
   {
      return _sp;
   }

   public void error(String s, Throwable t)
   {
      if (null == s && null == t)
      {
         return;
      }
      else if (null == s && null != t)
      {
         error(Utils.getStackString(t));
      }
      else if (null != s && null == t)
      {
         error(s);
      }
      else
      {
         error(formatMessageWithStacktrace(s, t));
      }

   }

   private String formatMessageWithStacktrace(String s, Throwable t)
   {
      String ret = s;

      if (null != t)
      {
         s += "\n" + Utils.getStackString(t);
      }

      return ret;
   }

   public String errorSQLNoStack(SQLException e)
   {
      SQLException buf = e;

      String errMsg = "";

      while(null != buf)
      {
         errMsg = getSQLNoStackMessage(buf) + errMsg;
         buf = buf.getNextException();
      }

      addMessage(errMsg, STYLE_RED);

      return errMsg;
   }

   private String getSQLNoStackMessage(SQLException e)
   {
      return e.getMessage() + "\nError code: " + e.getErrorCode() + "\nSQL state: " + e.getSQLState();
   }
}
