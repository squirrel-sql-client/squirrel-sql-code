package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.CodeArea;

public class DoubleClickHandler
{
   public DoubleClickHandler(CodeArea sqlTextArea)
   {
      // Filters(Capturing) before Handlers(Bubbling)
      // see http://docs.oracle.com/javafx/2/events/processing.htm
      sqlTextArea.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            onHandleDoubleClick(event, sqlTextArea);
         }
      });


   }

   private void onHandleDoubleClick(MouseEvent event, CodeArea sqlTextArea)
   {
      if (2 == event.getClickCount() && event.getButton() == MouseButton.PRIMARY)
      {
         selectWord(sqlTextArea);
         event.consume();
      }
   }

   private void selectWord(CodeArea sqlTextArea)
   {
      int wordBeginPos = getWordBeginPos(sqlTextArea);
      int wordEndPos = getWordEndPos(sqlTextArea);

      sqlTextArea.selectRange(wordBeginPos, wordEndPos);
   }

   private int getWordEndPos(CodeArea sqlTextArea)
   {
      String text = sqlTextArea.getText();
      int pos = sqlTextArea.getCaretPosition() + 1;

      if (pos > text.length())
      {
         return text.length();
      }


      for (; pos < text.length(); ++pos)
      {
         if (WordBoundaryCheck.isToStopAt(text.charAt(pos), text.charAt(pos - 1)))
         {
            break;
         }
      }

      return pos;
   }


   private int getWordBeginPos(CodeArea sqlTextArea)
   {
      String text = sqlTextArea.getText();

      int pos = sqlTextArea.getCaretPosition() - 1;

      if (pos < 0)
      {
         return 0;
      }


      for (; pos > 0; --pos)
      {
         if (WordBoundaryCheck.isToStopAt(text.charAt(pos - 1), text.charAt(pos)))
         {
            break;
         }
      }

      return pos;
   }
}
