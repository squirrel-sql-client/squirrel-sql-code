package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

public class HighlightUtil
{
   public static Object highlightRange(JTextArea textArea, int startPos, int endPos)
   {
      try
      {
         if(endPos <= startPos)
         {
            return null;
         }

         final DefaultHighlighter highlighter = (DefaultHighlighter) textArea.getHighlighter();
         highlighter.setDrawsLayeredHighlights(false);
         final DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(textArea.getSelectionColor());

         final Object highlightTag = highlighter.addHighlight(startPos, endPos, defaultHighlightPainter);
         return highlightTag;
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static void removeHighlight(JTextArea textArea, Object highLightTag)
   {
      if(null == highLightTag)
      {
         return;
      }

      final DefaultHighlighter highlighter = (DefaultHighlighter) textArea.getHighlighter();
      highlighter.removeHighlight(highLightTag);
   }
}
