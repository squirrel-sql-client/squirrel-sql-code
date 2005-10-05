package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;

import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;


public class DuplicateLineAction extends SquirrelAction implements ISQLPanelAction
{
   private ISession _session;
   private ISQLPanelAPI _panel;

   public DuplicateLineAction(IApplication app, SyntaxPluginResources rsrc)
      throws IllegalArgumentException
   {
      super(app, rsrc);
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (null != _panel)
      {
         duplicateLineAction();
      }
   }

   private void duplicateLineAction()
   {
      try
      {
         JTextComponent txtComp = (JTextComponent) _panel.getSQLEntryPanel().getTextComponent();

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
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
   }


}
