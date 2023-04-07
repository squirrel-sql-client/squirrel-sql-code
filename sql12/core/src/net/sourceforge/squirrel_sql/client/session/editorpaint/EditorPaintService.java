package net.sourceforge.squirrel_sql.client.session.editorpaint;

public interface EditorPaintService
{
   EditorPaintService EMPTY = new EditorPaintService() {
      @Override
      public void setPauseInsertPairedCharacters(boolean b) {}
   };

   void setPauseInsertPairedCharacters(boolean b);
}
