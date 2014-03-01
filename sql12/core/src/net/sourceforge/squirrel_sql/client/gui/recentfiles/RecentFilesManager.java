package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class RecentFilesManager
{
   private RecentFilesXmlBean _recentFilesXmlBean = new RecentFilesXmlBean();

   public void fileTouched(String absolutePath, ISQLAliasExt alias)
   {
      ArrayList<String> recentFiles = _recentFilesXmlBean.getRecentFiles();
      adjustFileArray(absolutePath, recentFiles);


      ArrayList<String> recentAliasFiles = findOrCreateAliasFile(alias).getRecentFiles();
      adjustFileArray(absolutePath, recentAliasFiles);
   }

   private void adjustFileArray(String newAbsolutePath, ArrayList<String> fileArray)
   {
      fileArray.remove(newAbsolutePath);
      fileArray.add(0, newAbsolutePath);

      while (_recentFilesXmlBean.getMaxRecentFiles() < fileArray.size())
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
         _recentFilesXmlBean.getAliasFileXmlBeans().add(ret);
      }

      return ret;
   }

   private AliasFileXmlBean findAliasFile(ISQLAlias alias)
   {
      AliasFileXmlBean ret = null;
      ArrayList<AliasFileXmlBean> aliasFileXmlBeans = _recentFilesXmlBean.getAliasFileXmlBeans();
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

   public void saveXmlBean(File recentFilesBeanFile)
   {
      try
      {
         Marshaller marshaller = JAXBContext.newInstance(RecentFilesXmlBean.class).createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         FileOutputStream fos = new FileOutputStream(recentFilesBeanFile);
         marshaller.marshal(_recentFilesXmlBean, fos);

         fos.flush();
         fos.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void initXmlBean(File recentFilesXmlBeanFile)
   {
      if(false == recentFilesXmlBeanFile.exists())
      {
         return;
      }

      try
      {
         Unmarshaller um = JAXBContext.newInstance(RecentFilesXmlBean.class).createUnmarshaller();
         _recentFilesXmlBean = (RecentFilesXmlBean) um.unmarshal(new FileReader(recentFilesXmlBeanFile));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public ArrayList<String> getRecentFiles()
   {
      return _recentFilesXmlBean.getRecentFiles();
   }

   public ArrayList<String> getFavouriteFiles()
   {
      return _recentFilesXmlBean.getFavouriteFiles();
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
      return _recentFilesXmlBean.getMaxRecentFiles();
   }

   public void setMaxRecentFiles(int n)
   {
      _recentFilesXmlBean.setMaxRecentFiles(n);
   }

   public void adjustFavouriteFiles(File selectedFile)
   {
      adjustFileArray(selectedFile.getAbsolutePath(), _recentFilesXmlBean.getFavouriteFiles());
   }

   public void adjustFavouriteAliasFiles(ISQLAlias alias, File selectedFile)
   {
      adjustFileArray(selectedFile.getAbsolutePath(), findOrCreateAliasFile(alias).getFavouriteFiles());
   }

   public void setRecentFiles(ArrayList<String> files)
   {
      _recentFilesXmlBean.setRecentFiles(files);
   }

   public void setFavouriteFiles(ArrayList<String> files)
   {
      _recentFilesXmlBean.setFavouriteFiles(files);
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
