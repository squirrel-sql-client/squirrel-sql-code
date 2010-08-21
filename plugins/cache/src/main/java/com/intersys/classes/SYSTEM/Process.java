package com.intersys.classes.SYSTEM;

import com.intersys.classes.Persistent;
import com.intersys.classes.RegisteredObject;
import com.intersys.objects.Database;
import com.intersys.objects.CacheException;
import com.intersys.objects.Id;
import com.intersys.cache.CacheObject;
import com.intersys.cache.SysDatabase;
import com.intersys.cache.Dataholder;


public class Process extends Persistent
{
   private static String CACHE_CLASS_NAME = "%SYSTEM.Process";

   /**
    * NB: DO NOT USE IN APPLICATION!
    * Use <code>Person._open</code> instead.
    * <p/>
    * Used to construct a Java object, corresponding to existing object
    * in Cache database.
    *
    * @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    * @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
    */
   public Process(CacheObject ref) throws CacheException
   {
      super(ref);
   }

   /**
    * Creates a new instance of object "<CacheClassName>" in Cache
    * database and corresponding object of class
    * <code>Person</code>.
    *
    * @param _db <code>Database</code> object used for connection with
    *            Cache database.
    * @throws CacheException in case of error.
    * @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    * @see #_open(com.intersys.objects.Database, com.intersys.objects.Id)
    */
   public Process(Database db) throws CacheException
   {
      super(((SysDatabase) db).newCacheObject(CACHE_CLASS_NAME));
   }

   /**
    * Runs method <code> %OpenId </code> in Cache to open an object
    * from Cache database and creates corresponding object of class
    * <code>Person</code>.
    *
    * @param _db <code>Database</code> object used for connection with
    *            Cache database.
    * @param id  ID as specified in Cache represented as
    *            <code>Id</code>.
    * @return <code> RegisteredObject </code>, corresponding to opened
    *         object. This object may be of <code>Person</code> or of
    *         any of its subclasses. Cast to <code>Person</code> is
    *         guaranteed to pass without <code>ClassCastException</code> exception.
    * @throws CacheException in case of error.
    * @see java.lang.ClassCastException
    * @see #_open(com.intersys.objects.Database, com.intersys.objects.Oid)
    * @see #Person
    */
   public static RegisteredObject _open(Database db, Id id) throws CacheException
   {
      CacheObject cobj = (((SysDatabase) db).openCacheObject(CACHE_CLASS_NAME, id.toString()));
      return (RegisteredObject) (cobj.newJavaInstance());
   }

   public Dataholder terminate() throws CacheException
   {
      Dataholder[] args = new Dataholder[0];
      Dataholder res=mInternal.runInstanceMethod("Terminate",args,Database.RET_PRIM);
      return res;

   }
}
