package net.sourceforge.squirrel_sql.plugins.syntax;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class TabKeyHandler
{
   private JTextArea _textComponent;
   private final int _tabLength;

   public TabKeyHandler(final JTextArea textComponent, SyntaxPlugin syntaxPlugin)
   {
      _textComponent = textComponent;

      _tabLength = syntaxPlugin.getSyntaxPreferences().getTabLength();

      _textComponent.setTabSize(_tabLength);

      if(false == syntaxPlugin.getSyntaxPreferences().isReplaceTabsBySpaces())
      {
         return;
      }


      _textComponent.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }

         public void insertUpdate(DocumentEvent e)
         {
            onCorrectTab(e);
         }

         public void removeUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }
      });

   }

   private void onCorrectTab(final DocumentEvent e)
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
                  correctTab(e);
               }
            });
         }
      }
      catch (BadLocationException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void correctTab(DocumentEvent e)
   {
      int offset = e.getOffset();
      _textComponent.replaceRange(createStringOfBlanks(_tabLength), offset, offset + 1);
   }

   public static String createStringOfBlanks(int n)
   {
      return String.format("%1$-" + n + "s", " ");
   }

}
