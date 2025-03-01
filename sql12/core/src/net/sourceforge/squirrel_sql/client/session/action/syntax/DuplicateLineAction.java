package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;


public class DuplicateLineAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;
   private ISQLEntryPanel _isqlEntryPanel;

   public DuplicateLineAction()
      throws IllegalArgumentException
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   public DuplicateLineAction(ISQLEntryPanel isqlEntryPanel)
   {
      this();
      _isqlEntryPanel = isqlEntryPanel;
   }

   public void actionPerformed(ActionEvent evt)
   {

      if(null != _isqlEntryPanel)
      {
         duplicateLineAction(_isqlEntryPanel);
      }
      else if (null != _panel)
      {
         duplicateLineAction(_panel.getSQLEntryPanel());
      }
   }

   private void duplicateLineAction(ISQLEntryPanel sqlEntryPanel)
   {
      try
      {
         String selectedText = sqlEntryPanel.getSelectedText();

         if(null != selectedText && 0 < selectedText.length())
         {
            JTextComponent txtComp = sqlEntryPanel.getTextComponent();
            int selectionEnd = sqlEntryPanel.getSelectionEnd();

            txtComp.getDocument().insertString(selectionEnd, selectedText, null);
            sqlEntryPanel.setSelectionStart(selectionEnd);
            sqlEntryPanel.setSelectionEnd(selectionEnd + selectedText.length());
         }
         else
         {
            duplicateCurrentLine(sqlEntryPanel);
         }

      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void duplicateCurrentLine(ISQLEntryPanel sqlEntryPanel) throws BadLocationException
   {
      JTextComponent txtComp = sqlEntryPanel.getTextComponent();

      int docLen = txtComp.getDocument().getLength();
      String text = txtComp.getDocument().getText(0, txtComp.getDocument().getLength());

      int caretPosition = txtComp.getCaretPosition();

      int lineBeg = 0;
      for (int i = caretPosition - 1; i > 0; --i)
      {
         if (text.charAt(i) == '\n')
         {
            lineBeg = i;
            break;
         }
      }

      int lineEnd = txtComp.getDocument().getLength();
      for (int i = caretPosition; i < docLen; ++i)
      {
         if (text.charAt(i) == '\n')
         {
            lineEnd = i;
            break;
         }
      }

      String line = text.substring(lineBeg, lineEnd);

      if (0 == lineBeg)
      {
         line += "\n";
      }


      txtComp.getDocument().insertString(lineBeg, line, null);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
   }


}
