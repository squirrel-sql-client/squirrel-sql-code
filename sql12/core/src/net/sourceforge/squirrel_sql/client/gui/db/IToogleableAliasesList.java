package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.List;

public interface IToogleableAliasesList extends IAliasesList
{
   void setViewAsTree(boolean selected);

   boolean isViewAsTree();

   IAliasTreeInterface getAliasTreeInterface();

   void colorSelected();

   List<SQLAlias> updateAliasesByImport(List<SQLAlias> sqlAliasList, boolean selected);
}
