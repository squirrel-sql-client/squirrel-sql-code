package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import java.util.Hashtable;
import java.io.File;
import java.io.FileNotFoundException;

public class AutoCorrectProviderImpl
{
   private File _pluginUserSettingsFolder;
   private AutoCorrectData _autoCorrectData;
   private Hashtable _emptyHashtable = new Hashtable();

   public static final String AUTO_CORRECT_DATA_FILE_NAME = "autocorrectdata.xml";

   AutoCorrectProviderImpl(File pluginUserSettingsFolder)
   {
      _pluginUserSettingsFolder = pluginUserSettingsFolder;
   }

   public Hashtable getAutoCorrects()
   {
      if(_autoCorrectData.isEnableAutoCorrects())
      {
         return _autoCorrectData.getAutoCorrectsHash();
      }
      else
      {
         return _emptyHashtable;
      }
   }

   public AutoCorrectData getAutoCorrectData()
   {
      try
      {
         if(null == _autoCorrectData)
         {
            XMLBeanReader br = new XMLBeanReader();

            File path = new File(_pluginUserSettingsFolder.getPath() + File.separator + AUTO_CORRECT_DATA_FILE_NAME);

            if(path.exists())
            {
               br.load(_pluginUserSettingsFolder.getPath() + File.separator + AUTO_CORRECT_DATA_FILE_NAME, this.getClass().getClassLoader());
               _autoCorrectData = (AutoCorrectData) br.iterator().next();
            }
            else
            {
               _autoCorrectData = getDefaultAutoCorrectData();
            }
         }

         return _autoCorrectData;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   private AutoCorrectData getDefaultAutoCorrectData()
   {
      Hashtable ret = new Hashtable();
      ret.put("SLECT", "SELECT");
      ret.put("FORM", "FROM");
      ret.put("WERE", "WHERE");

      return new AutoCorrectData(ret, true);

   }

   public void setAutoCorrects(Hashtable newAutoCorrects, boolean enableAutoCorrects)
   {
      try
      {
         _autoCorrectData = new AutoCorrectData(newAutoCorrects, enableAutoCorrects);
         XMLBeanWriter bw = new XMLBeanWriter(_autoCorrectData);
         bw.save(_pluginUserSettingsFolder.getPath() + File.separator + AUTO_CORRECT_DATA_FILE_NAME);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
