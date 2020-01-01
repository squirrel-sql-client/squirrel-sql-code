package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

public class TextPaneUtil
{
   public static void insert(JTextPane txtFormerText, String msg, SimpleAttributeSet attributes)
   {
      try
      {
         txtFormerText.getStyledDocument().insertString(txtFormerText.getDocument().getLength(), msg, attributes);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
