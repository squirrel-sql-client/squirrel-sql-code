package net.sourceforge.squirrel_sql.client.gui.db;

public interface IToogleableAliasesList extends IAliasesList
{
   void setViewAsTree(boolean selected);

   boolean isViewAsTree();

   IAliasTreeInterface getAliasTreeInterface();

   void colorSelected();
}
