package org.squirrelsql.workaround;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.session.sql.CaretBounds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class RichTextFxWA
{
   private static enum LetterPos
   {
      RIGHT_LOWER, LEFT_UPPER
   }


   public static Region getContentRegion(CodeArea sqlTextArea)
   {
      Parent styledTextAreaView  = (Parent) sqlTextArea.getChildrenUnmodifiable().get(0);

      VirtualFlow virtualFlow = (VirtualFlow) styledTextAreaView.getChildrenUnmodifiable().get(0);


      Region virtualFlowContent = (Region) virtualFlow.getChildrenUnmodifiable().get(0);

      return virtualFlowContent;
   }

   public static Bounds getBoundsForCaretBounds(CodeArea sqlTextArea, CaretBounds caretBounds)
   {
      String text = sqlTextArea.getText();

      int line = 0;
      int begLine = 0;
      int column = 0;
      int maxColumn = 0;

      for(int i = 0; i < caretBounds.end; ++i)
      {
         if(i == caretBounds.begin)
         {
            begLine = line;
         }

         if(text.charAt(i) == '\n')
         {
            ++line;

            if(caretBounds.begin < i)
            {
               maxColumn = Math.max(column, maxColumn);
            }

            column = 0;
         }
         else
         {
            ++column;
         }
      }

      maxColumn = Math.max(column, maxColumn);


      Point2D beg = getCoordinates(sqlTextArea, 0, begLine, LetterPos.LEFT_UPPER);
      Point2D end = getCoordinates(sqlTextArea, maxColumn, line, LetterPos.RIGHT_LOWER);


      return new BoundingBox(beg.getX(), beg.getY(), end.getX() - beg.getX(), end.getY() - beg.getY());
   }

   private static Point2D getCoordinates(CodeArea sqlTextArea, int col, int line, LetterPos letterPos)
   {
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(sqlTextArea.getFont());

      float lineHeight = fontMetrics.getLineHeight() + fontMetrics.getLeading() + 2.3f;

      float y;

      if (LetterPos.LEFT_UPPER == letterPos)
      {
         y = lineHeight * (float)line;
      }
      else
      {
         y = lineHeight * (float)(line + 1);
      }

      float x;
      if (LetterPos.LEFT_UPPER == letterPos)
      {
         x = fontMetrics.computeStringWidth("a") * col;
      }
      else
      {
         x = fontMetrics.computeStringWidth("a") * col + 3;
      }

      Point2D p = new Point2D(x, y);

      return p;
   }
}
