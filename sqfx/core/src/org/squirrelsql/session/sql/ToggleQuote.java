package org.squirrelsql.session.sql;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.squirrelsql.AppState;

import java.util.StringTokenizer;

public class ToggleQuote
{
   public ToggleQuote(SQLTextAreaServices sqlTextAreaServices, boolean sbAppend)
   {
      String[] splits = sqlTextAreaServices.getCurrentSql().split("\n");

      boolean quote = false;

      for (String split : splits)
      {
         if(false == isQuotedLine(split))
         {
            quote = true;
            break;
         }
      }


      String replacement;

      if (quote)
      {
         replacement = quoteText(sqlTextAreaServices.getCurrentSql(), sbAppend);

         if (AppState.get().getSettingsManager().getSettings().isCopyQuotedToClip())
         {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(replacement);
            clipboard.setContent(content);
         }
      }
      else
      {
         replacement = unquoteText(sqlTextAreaServices.getCurrentSql());
      }

      if (sqlTextAreaServices.hasSelection())
      {
         sqlTextAreaServices.replaceCurrentSql(replacement, true);
      }
      else
      {
         int caretPosition = sqlTextAreaServices.getTextArea().getCaretPosition();
         sqlTextAreaServices.replaceCurrentSql(replacement, false);
         sqlTextAreaServices.getTextArea().positionCaret(caretPosition);
      }
   }

   private boolean isQuotedLine(String line)
   {
      return line.matches(".*\\\"\\s*\\;.*") || line.matches(".*\\\"\\s*\\+.*") || line.matches(".*\\.\\s*append\\s*\\(\\s*\\\".*")  || line.matches(".*\\\"\\s*\\)\\s*\\;.*") || line.matches(".*\\\"\\s*\\.*");
   }

   private String quoteText(String textToQuote, boolean sbAppend)
   {
      if (null == textToQuote)
      {
         throw new IllegalArgumentException("textToQuote can not be null");
      }

      String[] lines = textToQuote.split("\n");

      StringBuffer ret = new StringBuffer();

      if (sbAppend)
      {
         ret.append("sb.append(\"").append(
               trimRight(lines[0].replaceAll("\"", "\\\\\"")));
      }
      else
      {
         ret.append("\"").append(
               trimRight(lines[0].replaceAll("\"", "\\\\\"")));
      }

      for (int i = 1; i < lines.length; ++i)
      {
         if (sbAppend)
         {
            ret.append(" \"); \nsb.append(\"").append(
                  trimRight(lines[i].replaceAll("\"", "\\\\\"")));
         }
         else
         {
            ret.append(" \" +\n\"").append(
                  trimRight(lines[i].replaceAll("\"", "\\\\\"")));
         }
      }

      if (sbAppend)
      {
         ret.append(" \");");
      }
      else
      {
         ret.append(" \";");
      }

      return ret.toString();
   }

   /**
    * textToUnquote is seen as a tokens separated by quotes. All tokens
    * that contain a new line character are left out.
    *
    * @param	textToUnquote	Text to be unquoted.
    *
    * @return	The unquoted text.
    */
   private String unquoteText(String textToUnquote)
   {
      // new line to the begining so that sb.append( will be removed
      // new line to the end so that a semi colon at the end will be removed.
      textToUnquote = "\n" + textToUnquote + "\n";

      StringTokenizer st = new StringTokenizer(textToUnquote, "\"");

      StringBuffer ret = new StringBuffer();
      while (st.hasMoreTokens())
      {
         String token = st.nextToken();
         String trimmedToken = token;
         if (0 != token.trim().length() && -1 == token.indexOf('\n'))
         {
            if (trimmedToken.endsWith("\\n"))
            {
               // Some people put new line characters in their SQL to have nice debug output.
               // Remove these new line characters too.
               trimmedToken =
                     trimmedToken.substring(0, trimmedToken.length() - 2);
            }

            if (trimmedToken.endsWith("\\"))
            {
               ret.append(
                     trimmedToken.substring(
                           0,
                           trimmedToken.length() - 1)).append(
                     "\"");
            }
            else
            {
               ret.append(trimmedToken).append("\n");
            }
         }
      }
      if (ret.toString().endsWith("\n"))
      {
         ret.setLength(ret.length() - 1);
      }
      return ret.toString();
   }

   static String trimRight(String toTrim)
   {
      if( 0 >= toTrim.length())
      {
         return toTrim;
      }

      int i;
      for(i=toTrim.length(); i > 0; --i)
      {
         if( !Character.isWhitespace(toTrim.charAt(i-1)) )
         {
            break;
         }
      }

      return toTrim.substring(0, i);
   }

}
