package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import java.util.ArrayList;

public class AliasFileXmlBean
{
   private ArrayList<String> _recentFiles = new ArrayList<String>();
   private ArrayList<String> _favouriteFiles = new ArrayList<String>();
   private String _alisaIdentifierString;
   private String _openAtStartupFile;

   public ArrayList<String> getRecentFiles()
   {
      return _recentFiles;
   }

   public void setRecentFiles(ArrayList<String> recentFiles)
   {
      _recentFiles = recentFiles;
   }

   public String getAlisaIdentifierString()
   {
      return _alisaIdentifierString;
   }

   public void setAlisaIdentifierString(String alisaIdentifierString)
   {
      _alisaIdentifierString = alisaIdentifierString;
   }

   public ArrayList<String> getFavouriteFiles()
   {
      return _favouriteFiles;
   }

   public void setFavouriteFiles(ArrayList<String> favouriteFiles)
   {
      _favouriteFiles = favouriteFiles;
   }

   public void setOpenAtStartupFile(String openAtStartupFile)
   {
      _openAtStartupFile = openAtStartupFile;
   }

   public String getOpenAtStartupFile()
   {
      return _openAtStartupFile;
   }
}
