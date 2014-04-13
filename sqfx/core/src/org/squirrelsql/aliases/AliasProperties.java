package org.squirrelsql.aliases;

import org.squirrelsql.services.AliasPropertiesSpecifiedLoading;

import java.util.ArrayList;

public class AliasProperties
{
   private ArrayList<AliasPropertiesSpecifiedLoading> _specifiedLoadings = new ArrayList<>();
   private String _aliasId;
   private boolean _loadAllCacheNon = true;
   private boolean _loadAndCacheAll;
   private boolean _hideEmptySchemasInObjectTree = true;


   public AliasProperties()
   {
   }

   public AliasProperties(ArrayList<AliasPropertiesSpecifiedLoading> specifiedLoadings, String aliasId, boolean loadAllCacheNon, boolean loadAndCacheAll, boolean hideEmptySchemasInObjectTree)
   {
      _specifiedLoadings = specifiedLoadings;
      _aliasId = aliasId;
      _loadAllCacheNon = loadAllCacheNon;
      _loadAndCacheAll = loadAndCacheAll;
      _hideEmptySchemasInObjectTree = hideEmptySchemasInObjectTree;
   }


   public ArrayList<AliasPropertiesSpecifiedLoading> getSpecifiedLoadings()
   {
      return _specifiedLoadings;
   }

   public void setSpecifiedLoadings(ArrayList<AliasPropertiesSpecifiedLoading> specifiedLoadings)
   {
      _specifiedLoadings = specifiedLoadings;
   }

   public String getAliasId()
   {
      return _aliasId;
   }

   public void setAliasId(String aliasId)
   {
      _aliasId = aliasId;
   }

   public boolean isLoadAllCacheNon()
   {
      return _loadAllCacheNon;
   }

   public void setLoadAllCacheNon(boolean loadAllCacheNon)
   {
      _loadAllCacheNon = loadAllCacheNon;
   }

   public boolean isLoadAndCacheAll()
   {
      return _loadAndCacheAll;
   }

   public void setLoadAndCacheAll(boolean loadAndCacheAll)
   {
      _loadAndCacheAll = loadAndCacheAll;
   }

   public boolean isHideEmptySchemasInObjectTree()
   {
      return _hideEmptySchemasInObjectTree;
   }

   public void setHideEmptySchemasInObjectTree(boolean hideEmptySchemasInObjectTree)
   {
      _hideEmptySchemasInObjectTree = hideEmptySchemasInObjectTree;
   }
}
