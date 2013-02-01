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
   private static final int MAX_FILES = 5;

   private RecentFilesXmlBean _recentFilesXmlBean;

   public void fileTouched(String absolutePath, ISQLAliasExt alias)
   {
      ArrayList<String> recentFiles = _recentFilesXmlBean.getRecentFiles();
      adjustRecentFiles(absolutePath, recentFiles);


      ArrayList<String> recentAliasFiles = findOrCreateAliasFile(alias).getRecentFiles();
      adjustRecentFiles(absolutePath, recentAliasFiles);
   }

   private void adjustRecentFiles(String newAbsolutePath, ArrayList<String> recentFiles)
   {
      recentFiles.remove(newAbsolutePath);
      recentFiles.add(0, newAbsolutePath);

      while (MAX_FILES < recentFiles.size())
      {
         recentFiles.remove(recentFiles.size()-1);
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
         _recentFilesXmlBean = new RecentFilesXmlBean();
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
}
