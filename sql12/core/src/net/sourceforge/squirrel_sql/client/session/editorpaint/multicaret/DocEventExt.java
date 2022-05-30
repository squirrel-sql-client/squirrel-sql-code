package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import javax.swing.event.DocumentEvent;

public class DocEventExt
{
   private final DocumentEvent _e;
   private final String _insertString;

   public DocEventExt(DocumentEvent e)
   {
      this(e, null);
   }

   public DocEventExt(DocumentEvent e, String insertString)
   {
      _e = e;
      _insertString = insertString;
   }

   public DocumentEvent getEvent()
   {
      return _e;
   }

   public String getInsertString()
   {
      return _insertString;
   }
}
