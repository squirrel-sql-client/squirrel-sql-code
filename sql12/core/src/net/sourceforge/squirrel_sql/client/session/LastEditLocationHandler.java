package net.sourceforge.squirrel_sql.client.session;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LastEditLocationHandler
{
   private JTextArea _textComponent;
   private int _lastEditLocation;

   public LastEditLocationHandler(JTextArea textComponent)
   {
      _textComponent = textComponent;
      _textComponent.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            _lastEditLocation = e.getOffset();
         }

         public void insertUpdate(DocumentEvent e)
         {
            _lastEditLocation = e.getOffset();
         }

         public void removeUpdate(DocumentEvent e)
         {
            _lastEditLocation = e.getOffset();
         }
      });
   }

   public void goToLastEditLocation()
   {
      _textComponent.setCaretPosition(_lastEditLocation);
   }
}
