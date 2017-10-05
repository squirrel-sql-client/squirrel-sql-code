package org.squirrelsql.services;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasDecorator;
import org.squirrelsql.aliases.AliasProperties;
import org.squirrelsql.aliases.AliasPropertiesDecorator;
import org.squirrelsql.aliases.AliasTreeStructureNode;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.session.graph.GraphPersistence;
import org.squirrelsql.session.graph.GraphPersistenceWrapper;
import org.squirrelsql.session.schemainfo.schemacacheloading.SerializedCache;
import org.squirrelsql.session.sql.SQLHistoryEntry;
import org.squirrelsql.session.sql.bookmark.BookmarkPersistence;
import org.squirrelsql.settings.SQLFormatSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Dao
{

   private static final String FILE_NAME_DRIVERS = "drivers.json";
   private static final String FILE_NAME_ALIASES = "aliases.json";
   private static final String FILE_NAME_ALIAS_TREE = "aliasTree.json";
   private static final String FILE_NAME_SQL_HISTORY = "sqlHistory.json";
   private static final String FILE_NAME_BOOKMARKS = "bookmarks.json";
   private static final String FILE_NAME_SETTINGS = "settings.json";
   private static final String FILE_NAME_SQL_FORMAT_SETTINGS = "sqlFormatSettings.json";
   private static final String FILE_NAME_GRAPH_PERSISTENCE = "graphPersistence.json";

   private static final String FILE_NAME_PREFERENCES = "preferences.properties";

   private static final String SCHEMA_CACHE_DIR = "schemachaches";
   public static final String SCHEMA_CACHE_FILE_ENDING = ".ser";
   public static final String GRAPH_PERSISTENCE_FILE_PREFIX = "graphPersistence-";

   public static void writeDrivers(List<SQLDriver> sqlDrivers)
   {
      writeObject(sqlDrivers, FILE_NAME_DRIVERS);
   }

   public static void writeAliases(List<AliasDecorator> aliases, AliasTreeStructureNode treeStructureNode)
   {
      writeObject(CollectionUtil.transform(aliases, ad -> ad.getAlias()), FILE_NAME_ALIASES);
      writeObject(treeStructureNode, FILE_NAME_ALIAS_TREE);

      aliases.forEach(a -> writeAliasProperties( a.getAliasPropertiesDecorator().getAliasProperties() ));
   }

   private static void writeAliasProperties(AliasProperties aliasProperties)
   {
      if(null != aliasProperties)
      {
         writeObject(aliasProperties, getAliasPropertiesFileName(aliasProperties.getAliasId()));
      }
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

   public static List<AliasDecorator> loadAliases()
   {
      List<Alias> aliases = loadObjectArray(FILE_NAME_ALIASES, Alias.class);

      return CollectionUtil.transform(aliases, a -> new AliasDecorator(a));
   }

   /**
    * To respect current user changes this method should be used by AliasDecorator.getAliasPropertiesDecorator() only.
    *
    * @see AliasDecorator#getAliasPropertiesDecorator()
    */
   public static AliasPropertiesDecorator loadAliasProperties(String aliasId)
   {
      AliasProperties aliasProperties = loadObject(getAliasPropertiesFileName(aliasId), new AliasProperties());

      return new AliasPropertiesDecorator(aliasProperties);
   }

   public static boolean hasAliasProperties(String aliasId)
   {
      return new File(AppState.get().getUserDir(), getAliasPropertiesFileName(aliasId)).exists();
   }



   private static<T> T loadObject(String fileName, T defaultObject)
   {
      File file = new File(AppState.get().getUserDir(), fileName);

      return loadObject(file, defaultObject);
   }

   private static <T> T loadObject(File file, T defaultObject)
   {
      if(false == file.exists())
      {
         return defaultObject;
      }

      try (FileInputStream is = new FileInputStream(file);
           InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName());)
      {

         ObjectMapper mapper = new ObjectMapper();
         T ret = mapper.readValue(isr, SimpleType.construct(defaultObject.getClass()));
         return ret;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   private static<T> List<T> loadObjectArray(String fileName, Class<T> objectType)
   {
      File file = new File(AppState.get().getUserDir(), fileName);

      if(false == file.exists())
      {
         return new ArrayList<>();
      }

      try(FileInputStream is = new FileInputStream(file);
          InputStreamReader isr = new InputStreamReader(is, JsonEncoding.UTF8.getJavaName()))
      {
         ObjectMapper mapper = new ObjectMapper();
         List<T> drivers = mapper.readValue(isr, CollectionType.construct(List.class, SimpleType.construct(objectType)));
         return drivers;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static File writeObject(Object toWrite, String fileName)
   {
      File file = new File(AppState.get().getUserDir(), fileName);
      return writeObject(toWrite, file);
   }

   private static File writeObject(Object toWrite, File file)
   {
      try (FileOutputStream fos = new FileOutputStream(file))
      {
         ObjectMapper mapper = new ObjectMapper();
         ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();

         // This version of objectWriter.writeValue() ensures,
         // that objects are written in JsonEncoding.UTF8
         // and thus that there won't be encoding problems
         // that makes the loadObjects methods crash.
         objectWriter.writeValue(fos, toWrite);
         return file;
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

         Properties ret = new Properties();
         if (file.exists())
         {
             FileInputStream in = new FileInputStream(file);
             ret.load(in);
             in.close();
         }

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

   public static SerializedCache readSerializedSchemaCache(Alias alias)
   {
      File cache = getSchemaCacheFile(alias);

      if(false == cache.exists())
      {
         return null;
      }


      try(FileInputStream fis = new FileInputStream(cache);
          ObjectInputStream ois = new ObjectInputStream(fis))
      {
         return (SerializedCache) ois.readObject();
      }
      catch (Throwable e)
      {
         cache.delete();
         throw new RuntimeException(e);
      }
   }

   private static File getSchemaCacheFile(Alias alias)
   {
      File dir = new File(AppState.get().getUserDir(), SCHEMA_CACHE_DIR);
      dir.mkdirs();

      return new File(dir, alias.getId() + SCHEMA_CACHE_FILE_ENDING);
   }

   public static void writeSerializedSchemaCache(Alias alias, SerializedCache serializedCache)
   {
      File cache = getSchemaCacheFile(alias);

      try(FileOutputStream fos = new FileOutputStream(cache);
          ObjectOutputStream oos = new ObjectOutputStream(fos))
      {
         oos.writeObject(serializedCache);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static boolean deleteSerializedSchemaCache(Alias alias)
   {
      return getSchemaCacheFile(alias).delete();
   }

   public static File writeGraphPersistence(GraphPersistenceWrapper graphPersistenceWrapper, Alias alias)
   {
      File graphPersistenceFile = getGraphPersistenceFile(graphPersistenceWrapper, alias);

      GraphPersistence graphPersistence = graphPersistenceWrapper.getDelegate();

      return writeObject(graphPersistence, graphPersistenceFile);
   }

   private static File getGraphPersistenceFile(GraphPersistenceWrapper graphPersistenceWrapper, Alias alias)
   {
      File aliasDir = getAliasDir(alias);

      aliasDir.mkdirs();

      if(false == aliasDir.exists())
      {
         throw new IllegalStateException("Couldn't create directory: " + aliasDir.getPath());
      }

      return new File(aliasDir, GRAPH_PERSISTENCE_FILE_PREFIX + graphPersistenceWrapper.getDelegate().getId() + ".json");
   }

   private static File getAliasDir(Alias alias)
   {
      return new File(AppState.get().getUserDir(), "aliasDir-" + alias.getId());
   }

   public static List<GraphPersistenceWrapper> loadGraphPersistences(Alias alias)
   {
      File aliasDir = getAliasDir(alias);

      File[] files = aliasDir.listFiles(f -> f.getName().startsWith(GRAPH_PERSISTENCE_FILE_PREFIX));

      if(null == files)
      {
         return Collections.emptyList();
      }

      ArrayList<GraphPersistenceWrapper> ret = new ArrayList<>();
      for (File file : files)
      {
         GraphPersistenceWrapper wrapper = toGraphPersistenceWrapper(file);
         if (null != wrapper)
         {
            ret.add(wrapper);
         }
      }

      return ret;

   }

   private static GraphPersistenceWrapper toGraphPersistenceWrapper(File f)
   {
      try
      {
         return new GraphPersistenceWrapper(loadObject(f, new GraphPersistence()));
      }
      catch (Throwable e)
      {
         new MessageHandler(Dao.class, MessageHandlerDestination.MESSAGE_LOG_AND_PANEL).error(new I18n(Dao.class).t("dao.graph.failed.loading", f.getAbsolutePath()), e);
         return null;
      }
   }

   public static void deleteGraphPersistence(GraphPersistenceWrapper graphPersistenceWrapper, Alias alias)
   {
      File graphPersistenceFile = getGraphPersistenceFile(graphPersistenceWrapper, alias);

      if(false == graphPersistenceFile.delete())
      {
         throw new IllegalStateException("Could not delete file: " + graphPersistenceFile.getPath());
      }
   }
}
