package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.SimpleType;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.*;
import java.util.ArrayList;

public class RecentFilesManager
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesManager.class);


   private RecentFilesJsonBean _recentFilesJsonBean = new RecentFilesJsonBean();

   public void fileTouched(String absolutePath, ISQLAliasExt alias)
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



   private AliasFileXmlBean findOrCreateAliasFile(ISQLAlias alias)
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

   private AliasFileXmlBean findAliasFile(ISQLAlias alias)
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

      try
      {
         FileOutputStream fos = new FileOutputStream(recentFilesBeanFile);

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         // This version of objectWriter.writeValue() ensures,
         // that objects are written in JsonEncoding.UTF8
         // and thus that there won't be encoding problems
         // that makes the loadObjects methods crash.
         objectWriter.writeValue(fos, _recentFilesJsonBean);

         fos.close();

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void initJSonBean(File recentFilesJsonBeanFile)
   {
      try
      {
         checkIfOlderVersionWarningIsNeeded();


         if(false == recentFilesJsonBeanFile.exists())
         {
            return;
         }


         FileInputStream is = new FileInputStream(recentFilesJsonBeanFile);
         InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName());


         ObjectMapper mapper = new ObjectMapper();
         _recentFilesJsonBean = mapper.readValue(isr, SimpleType.construct(RecentFilesJsonBean.class));


         isr.close();
         is.close();

      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void checkIfOlderVersionWarningIsNeeded()
   {
      File oldVerionFile = new ApplicationFiles().getRecentFilesXmlBeanFile_oldXmlVersion();
      if(oldVerionFile.exists())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("RecentFilesManager.old.version.warning", oldVerionFile.getPath()));
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

   public ArrayList<String> getRecentFilesForAlias(ISQLAlias selectedAlias)
   {
      return findOrCreateAliasFile(selectedAlias).getRecentFiles();
   }

   public ArrayList<String> getFavouriteFilesForAlias(ISQLAlias selectedAlias)
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

   public void adjustFavouriteAliasFiles(ISQLAlias alias, File selectedFile)
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

   public void setRecentFilesForAlias(ISQLAlias alias, ArrayList<String> files)
   {
      findOrCreateAliasFile(alias).setRecentFiles(files);
   }

   public void setFavouriteFilesForAlias(ISQLAlias alias, ArrayList<String> files)
   {
      findOrCreateAliasFile(alias).setFavouriteFiles(files);
   }
}
