package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.util.ArrayList;

public class DocumentMultiEditor
{
   private JTextArea _textArea;
   private MultiEdits _multiEdits;
   private boolean _updating = false;
   private ArrayList<DocEventExt> _executesToApplyToSecondaryEdits = new ArrayList<>();

   public DocumentMultiEditor(JTextArea textArea, MultiEdits multiEdits)
   {
      _textArea = textArea;
      _multiEdits = multiEdits;
   }

   public void executeInsert(DocumentEvent e)
   {
      try
      {
         String insertString = e.getDocument().getText(e.getOffset(), e.getLength());
         _executesToApplyToSecondaryEdits.add(new DocEventExt(e, insertString));
         SwingUtilities.invokeLater(() -> flush());
      }
      catch (BadLocationException ex)
      {
         throw Utilities.wrapRuntime(ex);
      }
   }

   private void _executeInsert(DocumentEvent e, String insertString)
   {
      try
      {
         logDocEvt(e);

         _updating = true;

         //final int relativeCaretPosition = multiEdits.relativeCaretPosition();

         int ancestorCount = 0;
         for (SingleEdit singleEdit : _multiEdits.allButInitial())
         {
            ++ancestorCount;
            singleEdit.adjustByAnchestorShift(ancestorCount * insertString.length());
            //final int thisEditsCaretPos = singleEdit.getStart() + relativeCaretPosition;
            final int editsInsertStart = singleEdit.getStart() + (e.getOffset() - _multiEdits.initial().getStart());

            //System.out.println("editsInsertStart = " + editsInsertStart);

            if(0 <= editsInsertStart && editsInsertStart < _textArea.getText().length())
            {
               _textArea.getDocument().insertString(editsInsertStart, insertString, null);
            }
         }
      }
      catch (BadLocationException ex)
      {
         throw Utilities.wrapRuntime(ex);
      }
      finally
      {
         _updating = false;
      }
   }

   public void executeRemove(DocumentEvent e)
   {
      _executesToApplyToSecondaryEdits.add(new DocEventExt(e));
      SwingUtilities.invokeLater(() -> flush());
   }

   private void _executeRemove(DocumentEvent e, int lenOfIntermediatelyDoneInsertsOnPrimaryEdit)
   {
      try
      {
         _updating = true;

         logDocEvt(e);

         int ancestorCount = 0;
         for (SingleEdit singleEdit : _multiEdits.allButInitial())
         {
            ++ancestorCount;
            singleEdit.adjustByAnchestorShift(- ancestorCount * e.getLength());
            final int editsRemoveStart = singleEdit.getStart() + (e.getOffset() - _multiEdits.initial().getStart()) + lenOfIntermediatelyDoneInsertsOnPrimaryEdit;

            //System.out.println("editsRemoveStart = " + editsRemoveStart);

            if(0 <= editsRemoveStart  && editsRemoveStart + e.getLength() < _textArea.getText().length())
            {
               _textArea.getDocument().remove(editsRemoveStart, e.getLength());
            }
         }
      }
      catch (BadLocationException ex)
      {
         throw Utilities.wrapRuntime(ex);
      }
      finally
      {
         _updating = false;
      }
   }

   private void logDocEvt(DocumentEvent e)
   {
//      try
//      {
//         System.out.println("Type=" + e.getType() + "; Offs=" + e.getOffset() + "; Len=" + e.getLength() +  " Str:" + e.getDocument().getText(e.getOffset(), e.getLength()));
//      }
//      catch (BadLocationException ex)
//      {
//         System.out.println("Type=" + e.getType() + "; Offs=" + e.getOffset() + "; Len=" + e.getLength() +  " Str: <FailedToRead>");
//         //throw Uti.toRte(ex);
//      }
   }

   private void flush()
   {
      if(_executesToApplyToSecondaryEdits.isEmpty())
      {
         return;
      }

      while (false == _executesToApplyToSecondaryEdits.isEmpty())
      {
         final DocEventExt docEventExt = _executesToApplyToSecondaryEdits.remove(0);

         if(docEventExt.getEvent().getType() == DocumentEvent.EventType.REMOVE)
         {
            //////////////////////////////////////////////////////////////////////////////
            // This variable plays a role when some text is selected and
            // then is immediately replaced by another character or clipboard content.
            // In this scenario _executesToApplyToSecondaryEdits
            // contains first a REMOVE and second an INSERT event.
            // When this flush is reached both events are already applied
            // to the primary edit of the original caret, see MultiEdits.initial().
            int lenOfIntermediatelyDoneInsertsOnPrimaryEdit = 0;
            //
            /////////////////////////////////////////////////////////////////////////////

            for (DocEventExt extEvt : _executesToApplyToSecondaryEdits)
            {
               if(extEvt.getEvent().getType() == DocumentEvent.EventType.INSERT)
               {
                  lenOfIntermediatelyDoneInsertsOnPrimaryEdit += extEvt.getInsertString().length();
               }
            }

            _executeRemove(docEventExt.getEvent(), lenOfIntermediatelyDoneInsertsOnPrimaryEdit);
         }
         else if(docEventExt.getEvent().getType() == DocumentEvent.EventType.INSERT)
         {
            _executeInsert(docEventExt.getEvent(), docEventExt.getInsertString());
         }
      }

      SwingUtilities.invokeLater(() -> _multiEdits.scrollToLastCaret());

   }


   public boolean isUpdating()
   {
      return _updating;
   }
}
