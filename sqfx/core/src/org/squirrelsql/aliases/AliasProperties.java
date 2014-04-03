package org.squirrelsql.aliases;

import java.util.ArrayList;

public class AliasProperties
{
   private ArrayList<ArrayList> _specifiedLoading = new ArrayList<>();
   private String _aliasId;
   private boolean _loadAllCacheNon = true;
   private boolean _loadAndCacheAll;

   public AliasProperties(ArrayList<ArrayList> specifiedLoading, String aliasId, boolean loadAllCacheNon, boolean loadAndCacheAll)
   {
      _specifiedLoading = specifiedLoading;
      _aliasId = aliasId;
      _loadAllCacheNon = loadAllCacheNon;
      _loadAndCacheAll = loadAndCacheAll;
   }

   public AliasProperties()
   {
   }


   public ArrayList<ArrayList> getSpecifiedLoading()
   {
      return _specifiedLoading;
   }

   public void setSpecifiedLoading(ArrayList<ArrayList> specifiedLoading)
   {
      _specifiedLoading = specifiedLoading;
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
}
