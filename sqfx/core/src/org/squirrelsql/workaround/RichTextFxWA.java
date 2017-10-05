package org.squirrelsql.workaround;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.AppState;
import org.squirrelsql.session.sql.CaretBounds;

import java.lang.reflect.Field;

public class RichTextFxWA
{

   private static enum LetterPos
   {
      RIGHT_LOWER, LEFT_UPPER
   }


   public static Region getVirtualFlowContent(CodeArea sqlTextArea)
   {
      // Use ScenicView to analyze this structure
      VirtualFlow virtualFlow = getVirtualFlow(sqlTextArea);
      Region virtualFlowContent = (Region) virtualFlow.getChildrenUnmodifiable().get(0);
      return virtualFlowContent;
   }

   private static VirtualFlow getVirtualFlow(CodeArea sqlTextArea)
   {
      // Use ScenicView to analyze this structure
      return (VirtualFlow) sqlTextArea.getChildrenUnmodifiable().get(0);
   }

   public static Bounds getBoundsForCaretBounds(VirtualizedScrollPane<CodeArea> virtualizedScrollPane, CaretBounds caretBounds)
   {
      String text = virtualizedScrollPane.getContent().getText();

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



      Point2D beg = getCoordinates(virtualizedScrollPane.getContent(), 0, begLine, LetterPos.LEFT_UPPER);
      Point2D end = getCoordinates(virtualizedScrollPane.getContent(), maxColumn, line, LetterPos.RIGHT_LOWER);

      ScrollBar verticalScrollbar = getScrollbar(virtualizedScrollPane, Orientation.VERTICAL);
      double yViewOffset;
      if (0 == verticalScrollbar.getMax())
      {
         yViewOffset = 0;
      }
      else
      {
         yViewOffset = (verticalScrollbar.getMax() - verticalScrollbar.getVisibleAmount()) * verticalScrollbar.getValue() / verticalScrollbar.getMax();
      }

      ScrollBar horizontalScrollbar = getScrollbar(virtualizedScrollPane, Orientation.HORIZONTAL);

      double xViewOffset;
      if (0 == horizontalScrollbar.getMax())
      {
         xViewOffset = 0;
      }
      else
      {
         xViewOffset = (horizontalScrollbar.getMax() - horizontalScrollbar.getVisibleAmount()) * horizontalScrollbar.getValue() / horizontalScrollbar.getMax();
      }

      return new BoundingBox(beg.getX() - xViewOffset, beg.getY() - yViewOffset, end.getX() - beg.getX(), end.getY() - beg.getY());
   }

   public static ScrollBar getScrollbar(VirtualizedScrollPane virtualizedScrollPane, Orientation orientation)
   {
      try
      {
         if(orientation == Orientation.HORIZONTAL)
         {
            Field hbar = VirtualizedScrollPane.class.getDeclaredField("hbar");
            hbar.setAccessible(true);
            return (ScrollBar) hbar.get(virtualizedScrollPane);
         }
         else
         {
            Field vbar = VirtualizedScrollPane.class.getDeclaredField("vbar");
            vbar.setAccessible(true);
            return (ScrollBar) vbar.get(virtualizedScrollPane);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static Point2D getCoordinates(CodeArea sqlTextArea, int col, int line, LetterPos letterPos)
   {
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(getFont(sqlTextArea));

      float lineHeight = fontMetrics.getLineHeight() + fontMetrics.getLeading() + (float)AppState.get().getSettingsManager().getSettings().getLineHeightOffset();

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

   public static Font getFont(CodeArea sqlTextArea)
   {
      // TODO Read from richtextfx-fat-0.7-M1.jar/org/fxmisc/richtext/util/code-area.css
      // TODO This style is loade in org.fxmisc.richtext.CodeArea
      return Font.font("monospace");
   }

}
