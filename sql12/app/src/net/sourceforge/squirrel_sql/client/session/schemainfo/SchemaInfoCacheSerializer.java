package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.io.*;
import java.util.Hashtable;

public class SchemaInfoCacheSerializer
{
   private static final ILogger s_log = LoggerController.createLogger(SchemaInfoCacheSerializer.class);
   private static Hashtable _storingSessionIDs = new Hashtable();
   private static boolean _waitingForStoring = false;


   public static SchemaInfoCache load(ISession session)
   {
      SchemaInfoCache ret = privateLoad(session);
      ret.setSession(session);

      return ret;
   }

   private static SchemaInfoCache privateLoad(ISession session)
   {
      File schemaCacheFile = getSchemaCacheFile(session);

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
            protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
            {
               return SchemaInfoCache.class.getClassLoader().loadClass(desc.getName());
            }
         };
         SchemaInfoCache ret = (SchemaInfoCache) ois.readObject();
         ois.close();
         fis.close();

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

         schemaInfoCache.prepareSerialization();

         FileOutputStream fos = new FileOutputStream(getSchemaCacheFile(session));
         ObjectOutputStream oOut = new ObjectOutputStream(fos);
         oOut.writeObject(schemaInfoCache);
         oOut.close();
         fos.close();

      }
      catch (Exception e)
      {
         s_log.error("Failed to write Schema cache.", e);
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


   private static File getSchemaCacheFile(ISession session)
   {
      String uniquePrefix = session.getAlias().getIdentifier().toString();

      uniquePrefix = uniquePrefix.replace(':', '_').replace(File.separatorChar, '-');

      String path = new ApplicationFiles().getUserSettingsDirectory().getPath() +
                    File.separator + "schemacaches" + File.separator + uniquePrefix + "_schemacache.ser";

      File ret = new File(path);
      ret.getParentFile().mkdirs();

      return ret;

   }

}
