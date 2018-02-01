package org.squirrelsql.session.sql;

import org.squirrelsql.services.SQLUtil;

public class ToggleCommentLinesCommand
{
   public ToggleCommentLinesCommand(SQLTextAreaServices sqlTextAreaServices)
   {
      String[] splits = sqlTextAreaServices.getCurrentSql().split("\n");

      boolean comment = false;

      for (String split : splits)
      {
         if(false == split.startsWith("--"))
         {
            comment = true;
            break;
         }
      }


      if (comment)
      {
         comment(sqlTextAreaServices);
      }
      else
      {
         uncomment(sqlTextAreaServices);
      }
   }

   private void uncomment(SQLTextAreaServices sqlTextAreaServices)
   {
      CaretBounds caretBounds = sqlTextAreaServices.getCurrentSqlCaretBounds();

      int caretPosition = sqlTextAreaServices.getTextArea().getCaretPosition();

      String textToComment = sqlTextAreaServices.getCurrentSql();

      String[] lines = textToComment.split("\n");

      StringBuffer uncommentedLines = new StringBuffer();

      String startOfLineComment = SQLUtil.LINE_COMMENT_BEGIN;

      for (int i = 0; i < lines.length; i++)
      {
         if(caretBounds.begin + uncommentedLines.length() < caretPosition)
         {
            if(lines[i].startsWith(startOfLineComment))
            {
               caretPosition -= startOfLineComment.length();
            }
         }

         if(lines[i].startsWith(startOfLineComment))
         {
            uncommentedLines.append(lines[i].substring(startOfLineComment.length()));
         }
         else
         {
            uncommentedLines.append(lines[i]);
         }

         if(i < lines.length - 1 || textToComment.endsWith("\n"))
         {
            uncommentedLines.append("\n");
         }

      }

      replaceText(sqlTextAreaServices, caretPosition, uncommentedLines.toString());

   }

   private void replaceText(SQLTextAreaServices sqlTextAreaServices, int caretPosition, String replacement)
   {

      if (sqlTextAreaServices.hasSelection())
      {
         sqlTextAreaServices.replaceCurrentSql(replacement, true);
      }
      else
      {
         sqlTextAreaServices.replaceCurrentSql(replacement, false);
         sqlTextAreaServices.getTextArea().moveTo(caretPosition);
      }
   }

   private void comment(SQLTextAreaServices sqlTextAreaServices)
   {
      CaretBounds caretBounds = sqlTextAreaServices.getCurrentSqlCaretBounds();

      if (caretBounds.begin == caretBounds.end)
      {
         return;
      }

      int caretPosition = sqlTextAreaServices.getTextArea().getCaretPosition();

      String textToComment = sqlTextAreaServices.getCurrentSql();

      String[] lines = textToComment.split("\n");

      StringBuffer commentedLines = new StringBuffer();

      String startOfLineComment = SQLUtil.LINE_COMMENT_BEGIN;

      for (int i = 0; i < lines.length; i++)
      {
         if(caretBounds.begin + commentedLines.length() <= caretPosition)
         {
            caretPosition += startOfLineComment.length();
         }

         commentedLines.append(startOfLineComment).append(lines[i]);
         if(i < lines.length - 1 || textToComment.endsWith("\n"))
         {
            commentedLines.append("\n");
         }

      }

      replaceText(sqlTextAreaServices, caretPosition, commentedLines.toString());
   }
}
