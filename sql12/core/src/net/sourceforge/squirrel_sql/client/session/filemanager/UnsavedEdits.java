package net.sourceforge.squirrel_sql.client.session.filemanager;

public class UnsavedEdits
{
   private boolean _unsavedEdits;
   private boolean _unsavedBufferEdits;

   public void setUnsavedEdits(boolean unsavedEdits)
   {
      _unsavedEdits = unsavedEdits;
   }

   public boolean isUnsavedEdits()
   {
      return _unsavedEdits || _unsavedBufferEdits;
   }

   public void setUnsavedBufferEdits(boolean unsavedBufferEdits)
   {
      _unsavedBufferEdits = unsavedBufferEdits;
   }

   public boolean isUnsavedBufferEdits()
   {
      return false == _unsavedEdits && _unsavedBufferEdits;
   }
}
