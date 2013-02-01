package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import java.util.ArrayList;

public class AliasFileXmlBean
{
   private ArrayList<String> _recentFiles = new ArrayList<String>();
   private String _alisaIdentifierString;

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
}
