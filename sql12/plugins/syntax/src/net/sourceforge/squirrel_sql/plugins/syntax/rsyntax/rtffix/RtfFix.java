package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.rtffix;

import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.HtmlUtil;
import org.fife.ui.rsyntaxtextarea.RtfGenerator;
import org.fife.ui.rsyntaxtextarea.Token;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.nio.charset.StandardCharsets;

public class RtfFix
{
   /**
    * Copied from RSyntax only to fix a line in {@link SquirrelRtfToText#endControlWord()}
    */
   public static void copyAsStyledText(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      int selStart = squirrelRSyntaxTextArea.getSelectionStart();
      int selEnd = squirrelRSyntaxTextArea.getSelectionEnd();
      if (selStart != selEnd)
      {
         String html = HtmlUtil.getTextAsHtml(squirrelRSyntaxTextArea, selStart, selEnd);
         byte[] rtfBytes = getTextAsRtf(selStart, selEnd, squirrelRSyntaxTextArea);
         SquirrelStyledTextTransferable contents = new SquirrelStyledTextTransferable(html, rtfBytes);
         Clipboard cb = squirrelRSyntaxTextArea.getToolkit().getSystemClipboard();

         try
         {
            cb.setContents(contents, (ClipboardOwner) null);
         }
         catch (IllegalStateException var8)
         {
            UIManager.getLookAndFeel().provideErrorFeedback((Component) null);
         }

      }
   }

   private static byte[] getTextAsRtf(int start, int end, SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      RtfGenerator gen = new RtfGenerator(squirrelRSyntaxTextArea.getBackground());
      Token tokenList = squirrelRSyntaxTextArea.getTokenListFor(start, end);

      for (Token t = tokenList; t != null; t = t.getNextToken())
      {
         if (t.isPaintable())
         {
            if (t.length() == 1 && t.charAt(0) == '\n')
            {
               gen.appendNewline();
            }
            else
            {
               Font font = squirrelRSyntaxTextArea.getFontForTokenType(t.getType());
               Color bg = squirrelRSyntaxTextArea.getBackgroundForToken(t);
               boolean underline = squirrelRSyntaxTextArea.getUnderlineForToken(t);
               if (t.isWhitespace())
               {
                  gen.appendToDocNoFG(t.getLexeme(), font, bg, underline);
               }
               else
               {
                  Color fg = squirrelRSyntaxTextArea.getForegroundForToken(t);
                  gen.appendToDoc(t.getLexeme(), font, fg, bg, underline);
               }
            }
         }
      }

      return gen.getRtf().getBytes(StandardCharsets.UTF_8);
   }

}
