package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import java.util.ArrayList;

public class RecentFilesJsonBean
{
   private ArrayList<String> _recentFiles = new ArrayList<String>();
   private ArrayList<AliasFileXmlBean> _aliasFileXmlBeans = new ArrayList<AliasFileXmlBean>();
   private ArrayList<String> _favouriteFiles = new ArrayList<String>();
   private int _maxRecentFiles = 5;

   public ArrayList<String> getRecentFiles()
   {
      return _recentFiles;
   }

   public void setRecentFiles(ArrayList<String> recentFiles)
   {
      _recentFiles = recentFiles;
   }

   public ArrayList<AliasFileXmlBean> getAliasFileXmlBeans()
   {
      return _aliasFileXmlBeans;
   }

   public void setAliasFileXmlBeans(ArrayList<AliasFileXmlBean> aliasFileXmlBeans)
   {
      _aliasFileXmlBeans = aliasFileXmlBeans;
   }

   public ArrayList<String> getFavouriteFiles()
   {
      return _favouriteFiles;
   }

   public void setFavouriteFiles(ArrayList<String> favouriteFiles)
   {
      _favouriteFiles = favouriteFiles;
   }


   public int getMaxRecentFiles()
   {
      return _maxRecentFiles;
   }

   public void setMaxRecentFiles(int maxRecentFiles)
   {
      _maxRecentFiles = maxRecentFiles;
   }
}
