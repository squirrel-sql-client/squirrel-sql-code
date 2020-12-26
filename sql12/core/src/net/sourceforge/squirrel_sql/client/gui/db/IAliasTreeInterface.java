package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.List;

public interface IAliasTreeInterface
{
   void createNewFolder();

   void cutSelected();

   void pasteSelected();

   void copyToPasteSelected();

   void collapseAll();

   void expandAll();

   void collapseSelected();

   void expandSelected();

   List<AliasFolder> getAllAliasFolders();

}
