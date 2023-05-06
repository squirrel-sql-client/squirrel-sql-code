package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

public interface RevisionListControllerChannel
{
   void replaceEditorContent(String newEditorContent);

   void replaceChangeTrackBase(String newChangeTrackBase);

   String getEditorContent();
}
