package net.sourceforge.squirrel_sql.plugins.syntax;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class TabKeyHandler
{
   int _tabLength = 12;

   public TabKeyHandler(final JTextArea textComponent)
   {
      textComponent.setTabSize(_tabLength);

      textComponent.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }

         public void insertUpdate(DocumentEvent e)
         {
            onCorrectTab(e, textComponent);
         }

         public void removeUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }
      });

   }

   private void onCorrectTab(final DocumentEvent e, final JTextArea textComponent)
   {
      try
      {
         if(1 != e.getLength())
         {
            return;
         }

         final String insertChar = e.getDocument().getText(e.getOffset(), 1);

         if ('\t' == insertChar.charAt(0))
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  correctTab(e, textComponent);
               }
            });
         }
      }
      catch (BadLocationException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void correctTab(DocumentEvent e, JTextArea textComponent)
   {
      int offset = e.getOffset();
      textComponent.replaceRange(createStringOfBlanks(_tabLength), offset, offset + 1);
   }

   public static String createStringOfBlanks(int n)
   {
      return String.format("%1$-" + n + "s", " ");
   }

}
