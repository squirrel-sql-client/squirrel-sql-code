package org.squirrelsql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;

import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasProperties;
import org.squirrelsql.aliases.AliasPropertiesDecorator;
import org.squirrelsql.aliases.AliasTreeStructureNode;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.session.sql.SQLHistoryEntry;
import org.squirrelsql.session.sql.bookmark.BookmarkPersistence;
import org.squirrelsql.settings.SQLFormatSettings;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Dao
{

   public static final String FILE_NAME_DRIVERS = "drivers.json";
   public static final String FILE_NAME_ALIASES = "aliases.json";
   public static final String FILE_NAME_ALIAS_TREE = "aliasTree.json";
   public static final String FILE_NAME_SQL_HISTORY = "sqlHistory.json";
   public static final String FILE_NAME_BOOKMARKS = "bookmarks.json";
   public static final String FILE_NAME_SETTINGS = "settings.json";
   public static final String FILE_NAME_SQL_FORMAT_SETTINGS = "sqlFormatSettings.json";

   public static final String FILE_NAME_PREFERENCES = "preferences.properties";

   public static void writeDrivers(List<SQLDriver> sqlDrivers)
   {
      writeObject(sqlDrivers, FILE_NAME_DRIVERS);
   }

   public static void writeAliases(List<Alias> aliases, AliasTreeStructureNode treeStructureNode)
   {
      writeObject(aliases, FILE_NAME_ALIASES);
      writeObject(treeStructureNode, FILE_NAME_ALIAS_TREE);
   }

   public static void writeAliasProperties(AliasProperties aliasProperties)
   {
      writeObject(aliasProperties, getAliasPropertiesFileName(aliasProperties.getAliasId()));
   }

   private static String getAliasPropertiesFileName(String aliasId)
   {
      return "aliasProperties" + "_" + aliasId + ".json";
   }

   public static List<SQLDriver> loadSquirrelDrivers()
   {
      return loadObjectArray(FILE_NAME_DRIVERS, SQLDriver.class);
   }


   public static AliasTreeStructureNode loadAliasTree()
   {
      return loadObject(FILE_NAME_ALIAS_TREE, new AliasTreeStructureNode());
   }

   public static List<Alias> loadAliases()
   {
      return loadObjectArray(FILE_NAME_ALIASES, Alias.class);
   }

   public static AliasPropertiesDecorator loadAliasProperties(String aliasId)
   {
      AliasProperties aliasProperties = loadObject(getAliasPropertiesFileName(aliasId), new AliasProperties());

      return new AliasPropertiesDecorator(aliasProperties);
   }


   private static<T> T loadObject(String fileName, T defaultObject)
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), fileName);

         if(false == file.exists())
         {
            return defaultObject;
         }

         ObjectMapper mapper = new ObjectMapper();
         T ret = mapper.readValue(file, SimpleType.construct(defaultObject.getClass()));

         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   private static<T> List<T> loadObjectArray(String fileName, Class<T> objectType)
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), fileName);

         if(false == file.exists())
         {
            return new ArrayList<>();
         }

         ObjectMapper mapper = new ObjectMapper();
         List<T> drivers = mapper.readValue(file, CollectionType.construct(List.class, SimpleType.construct(objectType)));

         return drivers;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static void writeObject(Object toWrite, String fileName)
   {
      try
      {

         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         File file = new File(AppState.get().getUserDir(), fileName);
         FileWriter fileWriter = new FileWriter(file);
         objectWriter.writeValue(fileWriter, toWrite);
         fileWriter.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void writeSqlHistory(List<SQLHistoryEntry> sqlHistoryEntries)
   {
      writeObject(sqlHistoryEntries, FILE_NAME_SQL_HISTORY);
   }

   public static List<SQLHistoryEntry> loadSqlHistory()
   {
      return loadObjectArray(FILE_NAME_SQL_HISTORY, SQLHistoryEntry.class);
   }

   public static BookmarkPersistence loadBookmarkPersistence()
   {
      return loadObject(FILE_NAME_BOOKMARKS, new BookmarkPersistence());
   }

   public static void writeBookmarkPersistence(BookmarkPersistence bookmarkPersistence)
   {
      writeObject(bookmarkPersistence, FILE_NAME_BOOKMARKS);
   }

   public static void log(String logType, String s, Throwable t)
   {
      try
      {
         long logTime = System.currentTimeMillis();

         String filePrefix = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss--SSS").format(new Date(logTime));

         File file = new File(AppState.get().getLogDir(), createFileName(filePrefix, logType));

         for (int i = 2; file.exists() ; i++)
         {
            file = new File(createFileName(filePrefix, logType + "__" + i));
         }

         PrintWriter pw = new PrintWriter(file);

         pw.println("LOG TIME: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss : SSS").format(new Date(logTime)));
         pw.println("LOG TYPE: " + logType);
         pw.println();

         if(null != s)
         {
            pw.println(s);
         }
         if(null != t)
         {
            t.printStackTrace(pw);
         }

         //pw.flush(); is done by pw.checkError()

         if(pw.checkError())
         {
            System.err.println("ERRORS OCCURRED WHEN WRITING LOG FILE " + file.getName());
         }

         pw.close();

         cleanUpLogs();
      }
      catch (Throwable e)
      {
         System.out.println("FAILED TO WRITE LOG FILE");
         e.printStackTrace();
      }
      finally
      {
         if(null != s)
         {
            System.err.println(s);
         }
         if(null != t)
         {
            t.printStackTrace(System.err);
         }
      }
   }

   private static void cleanUpLogs()
   {
      try
      {
         File logDir = AppState.get().getLogDir();

         File[] files = logDir.listFiles();

         if(110 < files.length)
         {
            Arrays.sort(files);

            for (int i = 0; i < 10; i++)
            {
               files[i].delete();
            }
         }
      }
      catch (Throwable e)
      {
         System.out.println("FAILED TO CLEAN LOG DIRECTORY");
         e.printStackTrace();
      }
   }

   private static String createFileName(String filePrefix, String fileNamePostfix)
   {
      return filePrefix + "_" + fileNamePostfix + ".log";
   }

   public static File[] getLogFiles()
   {
      File[] files = AppState.get().getLogDir().listFiles();
      Arrays.sort(files, (f1,f2) -> -f1.compareTo(f2));
      return files;
   }

   public static File getLogDir()
   {
      return AppState.get().getLogDir();
   }

   public static Properties loadPreferences()
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), FILE_NAME_PREFERENCES);
         FileInputStream in = new FileInputStream(file);

         Properties ret = new Properties();
         if (file.exists())
         {
            ret.load(in);
         }

         in.close();
         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void writePreferences(Properties preferences)
   {
      try
      {
         File file = new File(AppState.get().getUserDir(), FILE_NAME_PREFERENCES);
         FileWriter fileWriter = new FileWriter(file);
         preferences.store(fileWriter, "SQuirreL SQL FX preferences");
         fileWriter.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static void writeSettings(Settings settings)
   {
      writeObject(settings, FILE_NAME_SETTINGS);
   }

   public static Settings loadSettings()
   {
      return loadObject(FILE_NAME_SETTINGS, new Settings());
   }


   public static SQLFormatSettings loadSQLFormatSeetings()
   {
      return loadObject(FILE_NAME_SQL_FORMAT_SETTINGS, new SQLFormatSettings());
   }

   public static  void writerSQLFormatSeetings(SQLFormatSettings sqlFormatSettings)
   {
      writeObject(sqlFormatSettings, FILE_NAME_SQL_FORMAT_SETTINGS);
   }
}
