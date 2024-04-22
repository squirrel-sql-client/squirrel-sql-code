package net.sourceforge.squirrel_sql.client.session.action.savedsession;

public class SaveSessionResult
{
   private final boolean sessionWasSaved;
   private final SQLEditorActivator sqlEditorActivator;

   public static SaveSessionResult ofUserCanceledSavingSession()
   {
      return new SaveSessionResult(false, null);
   }

   public static SaveSessionResult ofSessionWasSaved(SQLEditorActivator sqlEditorActivator)
   {
      return new SaveSessionResult(true, sqlEditorActivator);
   }


   private SaveSessionResult(boolean sessionWasSaved, SQLEditorActivator sqlEditorActivator)
   {
      this.sessionWasSaved = sessionWasSaved;
      this.sqlEditorActivator = sqlEditorActivator;
   }

   public boolean isSessionWasSaved()
   {
      return sessionWasSaved;
   }

   public boolean isUserCanceledSavingSession()
   {
      return false == sessionWasSaved;
   }

   public void activatePreviousSqlEditor()
   {
      sqlEditorActivator.activate();
   }
}
