package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import java.util.ArrayList;
import java.util.List;

public class AliasCatalogLoadModelJsonBean
{
   private boolean _loadAllCatalogs = false;

   private List<String> _catalogsToLoad = new ArrayList<>();

   public boolean isLoadAllCatalogs()
   {
      return _loadAllCatalogs;
   }

   public void setLoadAllCatalogs(boolean loadAllCatalogs)
   {
      _loadAllCatalogs = loadAllCatalogs;
   }

   public List<String> getCatalogsToLoad()
   {
      return _catalogsToLoad;
   }

   public void setCatalogsToLoad(List<String> catalogsToLoad)
   {
      _catalogsToLoad = catalogsToLoad;
   }
}
