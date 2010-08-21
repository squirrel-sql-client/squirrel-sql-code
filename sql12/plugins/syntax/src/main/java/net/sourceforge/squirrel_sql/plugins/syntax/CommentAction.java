package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import java.awt.event.ActionEvent;


public class CommentAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;
   private ISQLEntryPanel _isqlEntryPanel;

   public CommentAction(IApplication app, SyntaxPluginResources rsrc)
      throws IllegalArgumentException
   {
      super(app, rsrc);
   }

   public CommentAction(IApplication app, SyntaxPluginResources rsrc, ISQLEntryPanel isqlEntryPanel)
   {
      this(app, rsrc);
      _isqlEntryPanel = isqlEntryPanel;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if(null != _isqlEntryPanel)
      {
         comment(_isqlEntryPanel);
      }
      else if (null != _panel)
      {
         comment(_panel.getSQLEntryPanel());
      }
   }

   private void comment(ISQLEntryPanel sqlEntryPanel)
   {
      int[] bounds = sqlEntryPanel.getBoundsOfSQLToBeExecuted();

      if (bounds[0] == bounds[1])
      {
         return;
      }

      int caretPosition = sqlEntryPanel.getCaretPosition();

      String textToComment = sqlEntryPanel.getText().substring(bounds[0], bounds[1]);

      String[] lines = textToComment.split("\n");

      StringBuffer commentedLines = new StringBuffer();

      String startOfLineComment = sqlEntryPanel.getSession().getProperties().getStartOfLineComment();

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

      sqlEntryPanel.setSelectionStart(bounds[0]);
      sqlEntryPanel.setSelectionEnd(bounds[1]);

      sqlEntryPanel.replaceSelection(commentedLines.toString());

      sqlEntryPanel.setCaretPosition(caretPosition);




   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
   }


}
