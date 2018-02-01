package org.squirrelsql.workaround;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.session.sql.CaretBounds;

import java.lang.reflect.Field;
import java.util.Optional;

public class RichTextFxWA
{
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

   public static Bounds getBoundsForCaretBounds(CaretBounds caretBounds, CodeArea codeArea)
   {
      Optional<Bounds> screenBounds = codeArea.getCharacterBoundsOnScreen(caretBounds.begin, caretBounds.end);

      if(false == screenBounds.isPresent())
      {
         return null;
      }


      return codeArea.screenToLocal(screenBounds.get());

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

   public static Font getFont(CodeArea sqlTextArea)
   {
      // TODO Read from richtextfx-fat-0.7-M1.jar/org/fxmisc/richtext/util/code-area.css
      // TODO This style is loade in org.fxmisc.richtext.CodeArea
      return Font.font("monospace");
   }

}
