package net.sourceforge.squirrel_sql.fw.sql.querytokenizer;

import net.sourceforge.squirrel_sql.fw.sql.commentandliteral.SQLCommentAndLiteralHandler;
import org.apache.commons.lang3.StringUtils;

public class ChangeStatementSeparatorSupport
{
   private static final String SET_TERMINATOR_COMMAND = "#SET TERMINATOR ";
   private final String _script;
   private final String _lineCommentBegin;
   private String _terminatorCommandInclLineCommentPrefix;

   private final boolean _scriptContainsTerminatorActive;

   public ChangeStatementSeparatorSupport(QueryTokenizePurpose queryTokenizePurpose, String script, String lineCommentBegin)
   {
      _script = script;
      _lineCommentBegin = lineCommentBegin;
      _terminatorCommandInclLineCommentPrefix = _lineCommentBegin + SET_TERMINATOR_COMMAND;

      _scriptContainsTerminatorActive =
               queryTokenizePurpose == QueryTokenizePurpose.STATEMENT_EXECUTION
            && StringUtils.containsIgnoreCase(script, _terminatorCommandInclLineCommentPrefix);
   }

   /**
    * Check for a line that contains '--#SET TERMINATOR x' to change the current new statement separator
    * The line may start by spaces or tabs. Other characters are not allowed.
    */
   public String findSetTerminatorInstruction(final int searchStartPos, SQLCommentAndLiteralHandler commentAndLiteralHandler)
   {
      if(false == isActive())
      {
         return null;
      }

      if(commentAndLiteralHandler.isInLiteral() || commentAndLiteralHandler.isInMultiLineComment())
      {
         return null;
      }

      if(false == StringUtils.startsWithIgnoreCase(_script.substring(searchStartPos), _terminatorCommandInclLineCommentPrefix))
      {
         return null;
      }

      if(isCommandPrecededBySpacesOrTabsOnly(_script, searchStartPos))
      {
         return null;
      }

      // Only when the comment starts on a new line
      int newLinePos = _script.indexOf('\n', searchStartPos);
      if(newLinePos > searchStartPos + _terminatorCommandInclLineCommentPrefix.length())
      {
         String terminator = _script.substring(searchStartPos + _terminatorCommandInclLineCommentPrefix.length(), newLinePos).trim();
         if(!terminator.isEmpty())
         {
            return terminator;
         }
      }


      return null;
   }

   private static boolean isCommandPrecededBySpacesOrTabsOnly(String script, int searchStartPos)
   {
      // Check if the comment is only preceded by spaces or tabs
      int j = searchStartPos;
      while(j-- > 0)
      {
         char c = script.charAt(j);
         if(c == '\n')
         {
            // Found the start of the line, break the loop
            break;
         }
         else if(c != ' ' && c != '\t')
         {
            // Found non-whitespace character
            return true;
         }
      }
      return false;
   }

   public boolean isActive()
   {
      return _scriptContainsTerminatorActive;
   }
}
