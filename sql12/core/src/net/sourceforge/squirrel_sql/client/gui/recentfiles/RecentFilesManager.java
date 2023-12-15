package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;
import java.util.ArrayList;

public class RecentFilesManager
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesManager.class);


   private RecentFilesJsonBean _recentFilesJsonBean = new RecentFilesJsonBean();

   public void fileTouched(String absolutePath, SQLAlias alias)
   {
      ArrayList<String> recentFiles = _recentFilesJsonBean.getRecentFiles();
      adjustFileArray(absolutePath, recentFiles);


      ArrayList<String> recentAliasFiles = findOrCreateAliasFile(alias).getRecentFiles();
      adjustFileArray(absolutePath, recentAliasFiles);
   }

   private void adjustFileArray(String newAbsolutePath, ArrayList<String> fileArray)
   {
      fileArray.remove(newAbsolutePath);
      fileArray.add(0, newAbsolutePath);

      while (_recentFilesJsonBean.getMaxRecentFiles() < fileArray.size())
      {
         fileArray.remove(fileArray.size()-1);
      }
   }



   private AliasFileXmlBean findOrCreateAliasFile(SQLAlias alias)
   {
      AliasFileXmlBean ret = findAliasFile(alias);

      if (null == ret)
      {
         ret = new AliasFileXmlBean();
         ret.setAlisaIdentifierString(alias.getIdentifier().toString());
         _recentFilesJsonBean.getAliasFileXmlBeans().add(ret);
      }

      return ret;
   }

   private AliasFileXmlBean findAliasFile(SQLAlias alias)
   {
      AliasFileXmlBean ret = null;
      ArrayList<AliasFileXmlBean> aliasFileXmlBeans = _recentFilesJsonBean.getAliasFileXmlBeans();
      for (AliasFileXmlBean aliasFileXmlBean : aliasFileXmlBeans)
      {
         if(aliasFileXmlBean.getAlisaIdentifierString().equals(alias.getIdentifier().toString()))
         {
            ret = aliasFileXmlBean;
            break;
         }
      }
      return ret;
   }

   public void saveJsonBean(File recentFilesBeanFile)
   {
      JsonMarshalUtil.writeObjectToFile(recentFilesBeanFile, _recentFilesJsonBean);
   }

   public void initJSonBean(File recentFilesJsonBeanFile)
   {
      checkIfOlderVersionWarningIsNeeded();

      if(false == recentFilesJsonBeanFile.exists())
      {
         return;
      }

      _recentFilesJsonBean = JsonMarshalUtil.readObjectFromFileSave(recentFilesJsonBeanFile, RecentFilesJsonBean.class, new RecentFilesJsonBean());
   }

   private void checkIfOlderVersionWarningIsNeeded()
   {
      File oldVersionFile = new ApplicationFiles().getRecentFilesXmlBeanFile_oldXmlVersion();
      if(oldVersionFile.exists())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("RecentFilesManager.old.version.warning", oldVersionFile.getPath()));
      }
   }

   public ArrayList<String> getRecentFiles()
   {
      return _recentFilesJsonBean.getRecentFiles();
   }

   public ArrayList<String> getFavouriteFiles()
   {
      return _recentFilesJsonBean.getFavouriteFiles();
   }

   public ArrayList<String> getRecentFilesForAlias(SQLAlias selectedAlias)
   {
      return findOrCreateAliasFile(selectedAlias).getRecentFiles();
   }

   public ArrayList<String> getFavouriteFilesForAlias(SQLAlias selectedAlias)
   {
      return findOrCreateAliasFile(selectedAlias).getFavouriteFiles();
   }

   public int getMaxRecentFiles()
   {
      return _recentFilesJsonBean.getMaxRecentFiles();
   }

   public void setMaxRecentFiles(int n)
   {
      _recentFilesJsonBean.setMaxRecentFiles(n);
   }

   public void adjustFavouriteFiles(File selectedFile)
   {
      adjustFileArray(selectedFile.getAbsolutePath(), _recentFilesJsonBean.getFavouriteFiles());
   }

   public void adjustFavouriteAliasFiles(SQLAlias alias, File selectedFile)
   {
      adjustFileArray(selectedFile.getAbsolutePath(), findOrCreateAliasFile(alias).getFavouriteFiles());
   }

   public void setRecentFiles(ArrayList<String> files)
   {
      _recentFilesJsonBean.setRecentFiles(files);
   }

   public void setFavouriteFiles(ArrayList<String> files)
   {
      _recentFilesJsonBean.setFavouriteFiles(files);
   }

   public void setRecentFilesForAlias(SQLAlias alias, ArrayList<String> files)
   {
      findOrCreateAliasFile(alias).setRecentFiles(files);
   }

   public void setFavouriteFilesForAlias(SQLAlias alias, ArrayList<String> files)
   {
      findOrCreateAliasFile(alias).setFavouriteFiles(files);
   }

   public void setOpenAtStartupFile(SQLAlias alias, String openAtStartupFile)
   {
      AliasFileXmlBean aliasFile = findOrCreateAliasFile(alias);

      if (null != openAtStartupFile)
      {
         boolean found = false;
         for (String favouriteFile : aliasFile.getFavouriteFiles())
         {
            if(openAtStartupFile.equals(favouriteFile))
            {
               found = true;
               break;
            }
         }

         if(false == found)
         {
            throw new IllegalStateException("The startup file \"" +  openAtStartupFile + " \"is not one of the Alias favourite files.");
         }
      }

      aliasFile.setOpenAtStartupFile(openAtStartupFile);
   }

   public String getOpenAtStartupFileForAlias(SQLAlias alias)
   {
      AliasFileXmlBean aliasFile = findAliasFile(alias);

      if (null == aliasFile)
      {
         return null;
      }

      return aliasFile.getOpenAtStartupFile();
   }

   public void removeFromRecentFiles(File toRemove)
   {
      _recentFilesJsonBean.getRecentFiles().removeIf(f -> f.equals(toRemove.getAbsolutePath()));
      _recentFilesJsonBean.getAliasFileXmlBeans().forEach(af -> af.getRecentFiles().removeIf(f -> f.equals(toRemove.getAbsolutePath())) );
   }
}
