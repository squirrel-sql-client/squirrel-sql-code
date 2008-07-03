package net.sourceforge.squirrel_sql.plugins.graph;

public interface GraphDesktopListener
{
   void saveGraphRequested();
   void renameRequest(String newName);
   void removeRequest();
   void refreshAllTablesRequested();
   void scriptAllTablesRequested();

   void allTablesPkConstOrderRequested();

   void allTablesByNameOrderRequested();

   void allTablesDbOrderRequested();
}
