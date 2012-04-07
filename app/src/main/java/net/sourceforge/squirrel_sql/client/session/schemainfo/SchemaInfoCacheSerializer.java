package net.sourceforge.squirrel_sql.client.session.schemainfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.Hashtable;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SchemaInfoCacheSerializer
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaInfoCacheSerializer.class);


   private static final ILogger s_log = LoggerController.createLogger(SchemaInfoCacheSerializer.class);
   private static Hashtable<IIdentifier, IIdentifier> _storingSessionIDs = 
       new Hashtable<IIdentifier, IIdentifier>();


   public static SchemaInfoCache load(ISession session)
   {
      SchemaInfoCache ret = privateLoad(session);
      ret.setSession(session);

      return ret;
   }

   private static SchemaInfoCache privateLoad(ISession session)
   {
      File schemaCacheFile = getSchemaCacheFile(session.getAlias());

      if(false == session.getAlias().getSchemaProperties().getExpectsSomeCachedData())
      {
         // Current Alias Schema properties dont want cache.
         // so we don't cache.

         try
         {
            if(schemaCacheFile.exists() && false == schemaCacheFile.delete())
            {
               s_log.error("Failed to delete Schema cache file " + schemaCacheFile.getPath());
            }
         }
         catch (Exception e)
         {
            s_log.error("Could not delete Schema cache file " + schemaCacheFile.getPath(), e);
         }

         return new SchemaInfoCache();
      }

      if(false == schemaCacheFile.exists())
      {
         return new SchemaInfoCache();
      }

      try
      {
         FileInputStream fis = new FileInputStream(schemaCacheFile);
         ObjectInputStream ois = new ObjectInputStream(fis)
         {
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
            {
               ClassLoader loader = SchemaInfoCache.class.getClassLoader();
               return Class.forName(desc.getName(), false, loader);
            }
         };
         SchemaInfoCache ret = (SchemaInfoCache) ois.readObject();
         ois.close();
         fis.close();

         ret.replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM();

         return ret;
      }
      catch (Exception e)
      {
         s_log.error("Failed to load Schema cache. Note: this can happen when the SQuirreL version changed", e);
         return new SchemaInfoCache();
      }
   }

   public static void store(final ISession session, final SchemaInfoCache schemaInfoCache)
   {

      _storingSessionIDs.put(session.getIdentifier(), session.getIdentifier());
      session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            privateStore(schemaInfoCache, session);
         }
      });

   }

   private static void privateStore(SchemaInfoCache schemaInfoCache, ISession session)
   {
      try
      {
         if(false == session.getAlias().getSchemaProperties().getExpectsSomeCachedData())
         {
            return;
         }

         IMessageHandler msgHandler = session.getApplication().getMessageHandler();
         File schemaCacheFile = getSchemaCacheFile(session.getAlias());


         String params[] = {session.getAlias().getName(),  schemaCacheFile.getPath()};
         // i18n[SchemaInfoCacheSerializer.beginStore=Starting to write schema cache for Alias {0}. file: {1}]
         msgHandler.showMessage(s_stringMgr.getString("SchemaInfoCacheSerializer.beginStore", params));


         schemaInfoCache.prepareSerialization();

         FileOutputStream fos = new FileOutputStream(schemaCacheFile);
         ObjectOutputStream oOut = new ObjectOutputStream(fos);
         oOut.writeObject(schemaInfoCache);
         oOut.close();
         fos.close();

         // i18n[SchemaInfoCacheSerializer.endStore=Finished writing schema cache for Alias{0}. file: {1}]
         msgHandler.showMessage(s_stringMgr.getString("SchemaInfoCacheSerializer.endStore", params));

      }
      catch (Exception e)
      {
         s_log.error("Failed to write Schema cache file ", e);
      }
      finally
      {
         synchronized (SchemaInfoCacheSerializer.class)
         {
            _storingSessionIDs.remove(session.getIdentifier());
            if(0 == _storingSessionIDs.size())
            {
               SchemaInfoCacheSerializer.class.notifyAll();
            }
         }

      }
   }

   public static void waitTillStoringIsDone()
   {
      try
      {
         synchronized (SchemaInfoCacheSerializer.class)
         {
            if(0 < _storingSessionIDs.size())
            {
               SchemaInfoCacheSerializer.class.wait(30000);
            }
         }
      }
      catch (InterruptedException e)
      {
         s_log.error("Error waiting for SchemaInfoCacheSerializer to finish storing", e);
      }
   }


   public static File getSchemaCacheFile(ISQLAliasExt alias)
   {
      String uniquePrefix = alias.getIdentifier().toString();

      uniquePrefix = uniquePrefix.replace(':', '_').replace(File.separatorChar, '-');

      String path = new ApplicationFiles().getUserSettingsDirectory().getPath() +
                    File.separator + "schemacaches" + File.separator + uniquePrefix + "_schemacache.ser";

      File ret = new File(path);
      ret.getParentFile().mkdirs();

      return ret;

   }

   public static void aliasRemoved(SQLAlias alias)
   {
      File schemaCacheFile = getSchemaCacheFile(alias);
      if(schemaCacheFile.exists())
      {
         schemaCacheFile.delete();
      }
   }

   public static void deleteCacheFile(IApplication app, ISQLAliasExt alias, boolean verbose)
   {
      File schemaCacheFile = SchemaInfoCacheSerializer.getSchemaCacheFile(alias);

      String aliasName = null == alias.getName() || 0 == alias.getName().trim().length() ? "<unnamed>" : alias.getName();

      if (schemaCacheFile.exists())
      {
         if (schemaCacheFile.delete())
         {
            // i18n[SchemaPropertiesController.cacheDeleted=Deleted {0}]
            app.getMessageHandler().showMessage(s_stringMgr.getString("SchemaInfoCacheSerializer.cacheDeleted", schemaCacheFile.getPath()));
         }
         else
         {
            // i18n[SchemaPropertiesController.cacheDeleteFailed=Could not delete {0}]
            app.getMessageHandler().showWarningMessage(s_stringMgr.getString("SchemaInfoCacheSerializer.cacheDeleteFailed", schemaCacheFile.getPath()));
         }

      }
      else if(verbose)
      {
         // i18n[SchemaPropertiesController.cacheToDelNotExists=Cache file for Alias "{0}" does not exist. No file was deleted]
         app.getMessageHandler().showWarningMessage(s_stringMgr.getString("SchemaInfoCacheSerializer.cacheToDelNotExists", aliasName));
      }
   }

}
