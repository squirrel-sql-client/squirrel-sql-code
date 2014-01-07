package org.squirrelsql.session;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.services.Utils;

public class SQLTextAreaServices
{

   private final TextArea _sqlTextArea;

   public SQLTextAreaServices()
   {
      _sqlTextArea = new TextArea();

   }

   public Node getTextAreaComponent()
   {
      return _sqlTextArea;
   }

   public void setOnKeyTyped(EventHandler<KeyEvent> keyEventHandler)
   {
      _sqlTextArea.setOnKeyTyped(keyEventHandler);

   }

   public String getCurrentSql()
   {
      String sql = _sqlTextArea.getSelectedText();

      if(Utils.isEmptyString(sql))
      {
         int caretPosition = _sqlTextArea.getCaretPosition();
         String sqlTextAreaText = _sqlTextArea.getText();

         String sep = System.lineSeparator() + System.lineSeparator();

         int begin = caretPosition;
         while(0 < begin && false == sqlTextAreaText.substring(0, begin).endsWith(sep))
         {
            --begin;
         }

         int end = caretPosition;
         while(sqlTextAreaText.length() > end && false == sqlTextAreaText.substring(end).startsWith(sep))
         {
            ++end;
         }

         sql = sqlTextAreaText.substring(begin, end);
      }

      return sql;
   }

   public String getTokenAtCarret()
   {
      int caretPosition = _sqlTextArea.getCaretPosition();
      String sqlTextAreaText = _sqlTextArea.getText();


      int begin = caretPosition-1;
      while(0 < begin && false == Character.isWhitespace(sqlTextAreaText.charAt(begin)))
      {
         --begin;
      }
      String token = sqlTextAreaText.substring(Math.max(begin, 0), caretPosition).trim();

      return token;

   }
}
