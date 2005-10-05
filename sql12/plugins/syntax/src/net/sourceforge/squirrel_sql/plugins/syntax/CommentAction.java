package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import java.awt.event.ActionEvent;


public class CommentAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public CommentAction(IApplication app, SyntaxPluginResources rsrc)
      throws IllegalArgumentException
   {
      super(app, rsrc);
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (null != _panel)
      {
         comment();
      }
   }

   private void comment()
   {
      int[] bounds = _panel.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

      if (bounds[0] == bounds[1])
      {
         return;
      }

      int caretPosition = _panel.getSQLEntryPanel().getCaretPosition();

      String textToComment = _panel.getSQLEntryPanel().getText().substring(bounds[0], bounds[1]);

      String[] lines = textToComment.split("\n");

      StringBuffer commentedLines = new StringBuffer();

      String startOfLineComment = _panel.getSession().getProperties().getStartOfLineComment();

      for (int i = 0; i < lines.length; i++)
      {
         if(bounds[0] + commentedLines.length() <= caretPosition)
         {
            caretPosition += startOfLineComment.length();
         }

         commentedLines.append(startOfLineComment).append(lines[i]);
         if(i < lines.length - 1 || textToComment.endsWith("\n"))
         {
            commentedLines.append("\n");
         }

      }

      _panel.getSQLEntryPanel().setSelectionStart(bounds[0]);
      _panel.getSQLEntryPanel().setSelectionEnd(bounds[1]);

      _panel.getSQLEntryPanel().replaceSelection(commentedLines.toString());

      _panel.getSQLEntryPanel().setCaretPosition(caretPosition);




   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
   }


}
