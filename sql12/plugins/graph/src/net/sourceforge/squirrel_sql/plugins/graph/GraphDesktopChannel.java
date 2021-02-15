package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.awt.Point;
import java.util.List;

public interface GraphDesktopChannel
{
   void saveGraphRequested();
   void renameRequest(String newName);
   void removeRequest();
   void refreshAllTablesRequested();
   void scriptAllTablesRequested();

   void allTablesPkConstOrderRequested();

   void allTablesByNameOrderRequested();

   void allTablesDbOrderRequested();

   void allTablesFilteredSelectedOrderRequested();

   void showQualifiedTableNamesRequested();

   void tablesDropped(List<ITableInfo> tis, Point dropPoint);

   void toggleWindowTab();



   boolean isLink();

   void saveLinkAsLocalCopy();

   void saveLinkedGraph();

   void removeLink();

   void showLinkDetails();

   void copyGraph();

   String getGraphName();
}
