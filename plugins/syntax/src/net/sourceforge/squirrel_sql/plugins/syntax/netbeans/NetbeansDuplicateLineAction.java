package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.BaseAction;

import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;


public class NetbeansDuplicateLineAction extends BaseAction
{

   public NetbeansDuplicateLineAction()
   {
      super(SQLKit.duplicateLineAction, CLEAR_STATUS_TEXT);
   }

   public void actionPerformed(ActionEvent evt, JTextComponent target)
   {
      try
      {
         if (target != null)
         {
            int docLen = target.getDocument().getLength();
            String text = target.getDocument().getText(0, target.getDocument().getLength());

            int caretPosition = target.getCaretPosition();

            int lineBeg = 0;
            for(int i=caretPosition-1; i > 0; --i)
            {
               if(text.charAt(i) == '\n')
               {
                  lineBeg = i;
                  break;
               }
            }

            int lineEnd = target.getDocument().getLength();
            for(int i=caretPosition; i < docLen ; ++i)
            {
               if(text.charAt(i) == '\n')
               {
                  lineEnd = i;
                  break;
               }
            }

            String line = text.substring(lineBeg, lineEnd);

            if(0 == lineBeg)
            {
               line += "\n";
            }


            target.getDocument().insertString(lineBeg, line, null);


         }
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }
}

