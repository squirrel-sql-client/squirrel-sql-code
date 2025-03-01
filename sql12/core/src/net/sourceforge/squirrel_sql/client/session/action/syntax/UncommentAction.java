package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;

import java.awt.event.ActionEvent;


public class UncommentAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;
   private ISQLEntryPanel _isqlEntryPanel;

   public UncommentAction()
      throws IllegalArgumentException
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public UncommentAction(ISQLEntryPanel isqlEntryPanel)
   {
      this();
      _isqlEntryPanel = isqlEntryPanel;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if(null != _isqlEntryPanel)
      {
         uncomment(_isqlEntryPanel);
      }
      else if (null != _panel)
      {
         uncomment(_panel.getSQLEntryPanel());
      }
   }

   private void uncomment(ISQLEntryPanel sqlEntryPanel)
   {
      int[] bounds = sqlEntryPanel.getBoundsOfSQLToBeExecuted();

      if (bounds[0] == bounds[1])
      {
         return;
      }

      int caretPosition = sqlEntryPanel.getCaretPosition();

      String textToComment = sqlEntryPanel.getText().substring(bounds[0], bounds[1]);

      String[] lines = textToComment.split("\n");

      StringBuffer uncommentedLines = new StringBuffer();

      String startOfLineComment = sqlEntryPanel.getSession().getProperties().getStartOfLineComment();

      for (int i = 0; i < lines.length; i++)
      {
         if(bounds[0] + uncommentedLines.length() < caretPosition)
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

      sqlEntryPanel.setSelectionStart(bounds[0]);
      sqlEntryPanel.setSelectionEnd(bounds[1]);

      sqlEntryPanel.replaceSelection(uncommentedLines.toString());

      sqlEntryPanel.setCaretPosition(caretPosition);




   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
   }


}
