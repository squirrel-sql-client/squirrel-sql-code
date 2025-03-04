package net.sourceforge.squirrel_sql.client.session.action.syntax;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import org.apache.commons.lang3.StringUtils;

public class SyntaxManager
{
   private static final ILogger s_log = LoggerController.createLogger(SyntaxManager.class);
   private static final String OLD_SYNTAX_PLUGIN_PACKAGE = "net.sourceforge.squirrel_sql.plugins.syntax.";
   private static final String NEW_SYNTAX_PACKAGE = "net.sourceforge.squirrel_sql.client.session.action.syntax.";

   private SyntaxPreferences _syntaxPreferences;
   private AutoCorrectData _autoCorrectData;

   public void loadPreferences()
   {
      loadSyntaxPreferences();
      loadAutoCorrectData();
   }

   private void loadSyntaxPreferences()
   {
      _syntaxPreferences = new SyntaxPreferences();
      try
      {
         File prefsFile = new ApplicationFiles().getSyntaxPreferencesFile();

         if(false == prefsFile.exists())
         {
            try
            {
               File oldPrefsDir = new File(new ApplicationFiles().getPluginsUserSettingsDirectory(), "syntax");
               if(oldPrefsDir.exists())
               {
                  File olfPrefsFile = new File(oldPrefsDir, "prefs.xml");
                  if(olfPrefsFile.exists())
                  {
                     String oldPrefsString = Files.readString(olfPrefsFile.toPath(), StandardCharsets.UTF_8);

                     String newPrefsString = StringUtils.replace(oldPrefsString,
                                                                 OLD_SYNTAX_PLUGIN_PACKAGE,
                                                                 NEW_SYNTAX_PACKAGE);
                     Files.write(prefsFile.toPath(), newPrefsString.getBytes(StandardCharsets.UTF_8));
                     Files.delete(olfPrefsFile.toPath());
                  }
               }
            }
            catch(Exception e)
            {
               s_log.error("Failed to copy old Syntax-PLugin preferences file", e);
            }
         }

         if(prefsFile.exists())
         {
            final XMLBeanReader doc = new XMLBeanReader();
            doc.load(prefsFile, getClass().getClassLoader());
            Iterator<?> it = doc.iterator();
            if(it.hasNext())
            {
               _syntaxPreferences = (SyntaxPreferences) it.next();
            }
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to read syntax preferences file", e);
      }
   }

   private void loadAutoCorrectData()
   {
      _autoCorrectData = createDefaultAutoCorrectData();
      try
      {
         File autoCorrectdDataFile = new ApplicationFiles().getAutocorrectDataFile();

         if(false == autoCorrectdDataFile.exists())
         {
            try
            {
               File oldAutoCorrectDir = new File(new ApplicationFiles().getPluginsUserSettingsDirectory(), "syntax");
               if(oldAutoCorrectDir.exists())
               {
                  File oldfAutoCorrectFile = new File(oldAutoCorrectDir, "autocorrectdata.xml");
                  if(oldfAutoCorrectFile.exists())
                  {
                     String oldPrefsString = Files.readString(oldfAutoCorrectFile.toPath(), StandardCharsets.UTF_8);

                     String newPrefsString = StringUtils.replace(oldPrefsString,
                                                                 OLD_SYNTAX_PLUGIN_PACKAGE,
                                                                 NEW_SYNTAX_PACKAGE);
                     Files.write(autoCorrectdDataFile.toPath(), newPrefsString.getBytes(StandardCharsets.UTF_8));
                     Files.delete(oldfAutoCorrectFile.toPath());
                  }
               }
            }
            catch(Exception e)
            {
               s_log.error("Failed to copy old AutoCorrectData file from old Syntax-Plugin directory", e);
            }
         }

         if(autoCorrectdDataFile.exists())
         {
            final XMLBeanReader doc = new XMLBeanReader();
            doc.load(autoCorrectdDataFile, getClass().getClassLoader());
            Iterator<?> it = doc.iterator();
            if(it.hasNext())
            {
               _autoCorrectData = (AutoCorrectData)it.next();
            }
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to read AutoCorrectData file", e);
      }
   }

   private AutoCorrectData createDefaultAutoCorrectData()
   {
      Hashtable<String, String> ret = new Hashtable<String, String>();
      ret.put("SLECT", "SELECT");
      ret.put("FORM", "FROM");
      ret.put("WERE", "WHERE");
      ret.put("SF", "SELECT * FROM");
      ret.put("OB", "ORDER BY");

      return new AutoCorrectData(ret, true);

   }

   public void saveAutoCorrectData(Hashtable<String, String> newAutoCorrects, boolean enableAutoCorrects)
   {
      try
      {
         _autoCorrectData = new AutoCorrectData(newAutoCorrects, enableAutoCorrects);
         XMLBeanWriter bw = new XMLBeanWriter(_autoCorrectData);
         bw.save(new ApplicationFiles().getAutocorrectDataFile());
      }
      catch(Exception e)
      {
         s_log.error("Failed to write AutoCorrectData file", e);
      }
   }

   public AutoCorrectData getAutoCorrectData()
   {
      return _autoCorrectData;
   }

   public SyntaxPreferences getSyntaxPreferences()
   {
      return _syntaxPreferences;
   }

   public void savePreferences()
   {
      try
      {
         final XMLBeanWriter wtr = new XMLBeanWriter(_syntaxPreferences);
         wtr.save(new ApplicationFiles().getSyntaxPreferencesFile());
      }
      catch(Exception e)
      {
         final String msg = "Failed to write syntax preferences file: " + new ApplicationFiles().getSyntaxPreferencesFile();
         s_log.error(msg, e);
      }
   }
}
