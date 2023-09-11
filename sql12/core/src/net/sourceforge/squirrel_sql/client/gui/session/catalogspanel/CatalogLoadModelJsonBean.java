package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import java.util.TreeMap;

public class CatalogLoadModelJsonBean
{
   private TreeMap<String, AliasCatalogLoadModelJsonBean> _aliasIdToAliasCatalogLoadModelBeans = new TreeMap<>();

   public TreeMap<String, AliasCatalogLoadModelJsonBean> getAliasIdToAliasCatalogLoadModelBeans()
   {
      return _aliasIdToAliasCatalogLoadModelBeans;
   }

   public void setAliasIdToAliasCatalogLoadModelBeans(TreeMap<String, AliasCatalogLoadModelJsonBean> aliasIdToAliasCatalogLoadModelBeans)
   {
      _aliasIdToAliasCatalogLoadModelBeans = aliasIdToAliasCatalogLoadModelBeans;
   }
}
