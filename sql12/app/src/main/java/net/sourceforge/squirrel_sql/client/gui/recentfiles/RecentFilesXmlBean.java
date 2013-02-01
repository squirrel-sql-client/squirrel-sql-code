package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "AliasFileXmlBean")
public class RecentFilesXmlBean
{
   private ArrayList<String> _recentFiles = new ArrayList<String>();
   private ArrayList<AliasFileXmlBean> _aliasFileXmlBeans = new ArrayList<AliasFileXmlBean>();
   private ArrayList<String> _favouriteFiles = new ArrayList<String>();

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
}
