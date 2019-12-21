package net.sourceforge.squirrel_sql.client.session.filemanager;

@FunctionalInterface
public interface FileHandlerListener
{
   void fileChanged(String entireSqlScript);
}
