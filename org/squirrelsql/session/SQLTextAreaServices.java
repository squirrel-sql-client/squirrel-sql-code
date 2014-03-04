package org.squirrelsql.session;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.workaround.CarretLocationOnScreenWA;
import org.squirrelsql.services.Utils;
import org.squirrelsql.workaround.FocusSqlTextAreaWA;

public class SQLTextAreaServices
{

   private final TextArea _sqlTextArea;

   public SQLTextAreaServices()
   {
      _sqlTextArea = new TextArea();

   }

   public TextArea getTextArea()
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
      return _getTokenAtCarretInfo().getToken();

   }

   private TokenAtCarretInfo _getTokenAtCarretInfo()
   {
      int caretPosition = _sqlTextArea.getCaretPosition();
      String sqlTextAreaText = _sqlTextArea.getText();


      int begin = caretPosition-1;
      while(0 < begin && false == Character.isWhitespace(sqlTextAreaText.charAt(begin)))
      {
         --begin;
      }

      if(0 < begin)
      {
         begin +=1; // keep the whitespace
      }

      begin = Math.max(begin, 0);
      String token = sqlTextAreaText.substring(begin, caretPosition).trim();

      return new TokenAtCarretInfo(token, begin, caretPosition);
   }

   public Point2D getCarretLocationOnScreen()
   {
      return CarretLocationOnScreenWA.getCarretLocationOnScreen(_sqlTextArea);
   }

   public void replaceTokenAtCarretBy(String selItem)
   {
      TokenAtCarretInfo tci = _getTokenAtCarretInfo();

      _sqlTextArea.replaceText(tci.getTokenBeginPos(), tci.getCaretPosition(), selItem);
   }

   public double getFontHight()
   {
      if( 0 > _sqlTextArea.getFont().getSize())
      {
         return 12;
      }
      else
      {
         return _sqlTextArea.getFont().getSize();
      }
   }

   public void requestFocus()
   {
      FocusSqlTextAreaWA.forceFocus(_sqlTextArea);
   }

   public double getStringWidth(String tokenAtCarret)
   {
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(_sqlTextArea.getFont());
      return fontMetrics.computeStringWidth(tokenAtCarret);
   }
}
