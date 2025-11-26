package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

public interface AliasesBackupCallback
{
   void setStatus(String status);
   void cleanUp();
}
