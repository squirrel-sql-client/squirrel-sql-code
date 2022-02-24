package net.sourceforge.squirrel_sql.client.session;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

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
            if(e instanceof AbstractDocument.DefaultDocumentEvent && ((AbstractDocument.DefaultDocumentEvent) e).isInProgress())
            {
               return;
            }

            _lastEditLocation = e.getOffset();
            //System.out.println("LastEditLocationHandler.changedUpdate " + _lastEditLocation);
         }

         public void insertUpdate(DocumentEvent e)
         {
            if(e instanceof AbstractDocument.DefaultDocumentEvent && ((AbstractDocument.DefaultDocumentEvent) e).isInProgress())
            {
               return;
            }

            _lastEditLocation = e.getOffset();
            //System.out.println("LastEditLocationHandler.insertUpdate " + _lastEditLocation);
         }

         public void removeUpdate(DocumentEvent e)
         {
            if(e instanceof AbstractDocument.DefaultDocumentEvent && ((AbstractDocument.DefaultDocumentEvent) e).isInProgress())
            {
               return;
            }

            _lastEditLocation = e.getOffset();
            //System.out.println("LastEditLocationHandler.removeUpdate " + _lastEditLocation);
         }
      });
   }

   public void goToLastEditLocation()
   {
      _textComponent.setCaretPosition(_lastEditLocation);
   }
}
