package net.sourceforge.squirrel_sql.client.gui.db;

public interface IToogleableAliasesList extends IAliasesList
{
   void setViewAsTree(boolean selected);

   IAliasTreeInterface getAliasTreeInterface();
}
