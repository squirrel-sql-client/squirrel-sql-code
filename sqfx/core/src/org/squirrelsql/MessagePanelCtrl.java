package org.squirrelsql;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.workaround.StringWidthWA;
import org.squirrelsql.services.rightmousemenuhandler.RightMouseMenuHandler;


import java.sql.SQLException;

public class MessagePanelCtrl
{
   private static final String STYLE_RED = "-fx-border-color: red; -fx-border-width: 3;";
   private static final String STYLE_YELLOW = "-fx-border-color: yellow; -fx-border-width: 3;";
   private static final String STYLE_GREEN = "-fx-border-color: green; -fx-border-width: 3;";
   private static final String STYLE_LIGHT_RED = "-fx-border-color: indianred; -fx-border-width: 3";

   private I18n _i18n = new I18n(getClass());


   private VBox _messages;
   private final ScrollPane _sp;

   public MessagePanelCtrl()
   {
      _messages = new VBox();
      _messages.setFillWidth(true);

      _sp = new ScrollPane();
      _sp.setContent(_messages);

      _messages.heightProperty().addListener((observableValue, oldNumber, newNumber) -> _sp.setVvalue(newNumber.doubleValue()));

      RightMouseMenuHandler messagePanelRightMouseMenuHandler = new RightMouseMenuHandler(_sp);
      messagePanelRightMouseMenuHandler.addMenu(getClearMenuText(), () -> clearMessagePanel());
   }

   private void clearMessagePanel()
   {
      _messages.getChildren().clear();
   }

   private String getClearMenuText()
   {
      return new I18n(getClass()).t("msg.clear.messagepanel");
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
         TextArea label = (TextArea) _messages.getChildren().get(size - 1);

         if(STYLE_RED.equals(label.getStyle()))
         {
            label.setStyle(STYLE_LIGHT_RED);
         }
      }

      //https://forums.oracle.com/forums/thread.jspa?threadID=2317231
      TextArea label = new TextArea(s);
      label.setStyle(style);
      label.setEditable(false);
      //Font font = Font.font("Courier", FontWeight.NORMAL, 14);
      //label.setFont(font);
      TextBounds textBounds = getTextBounds(s, label.getFont());
      label.setPrefRowCount(textBounds.getRows());
      label.setMinWidth(textBounds.getTextWidth());
      label.setMaxWidth(textBounds.getTextWidth());

      MenuItem mnuClear = new MenuItem(getClearMenuText());
      mnuClear.setOnAction(event -> clearMessagePanel());
      GuiUtils.addContextMenuItemToStandardTextAreaMenu(label, mnuClear);

      _messages.getChildren().add(label);
   }

   private TextBounds getTextBounds(String s, Font font)
   {
      int rows;
      double textWidth = 0 ;

      String[] splits = s.trim().split("\n");


      rows = splits.length;
      for (String split : splits)
      {
         textWidth = Math.max(textWidth, StringWidthWA.computeTextWidth(font, split));
      }

      return new TextBounds(textWidth + 35, rows);
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
