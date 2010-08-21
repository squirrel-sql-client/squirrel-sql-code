package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.Hashtable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class MappingRoot extends Object
{

   private static ILogger s_log = LoggerController.createLogger(MappingRoot.class);


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(MappingRoot.class);

   //i18n[MappingRoot.toString=Mapping]
   private String _toString = s_stringMgr.getString("MappingRoot.toString");

   private Hashtable<String, String> _properties = new Hashtable<String, String>();


   public String toString()
   {
      return _toString;
   }

   public Hashtable<String, String> getMappingProperties()
   {
      return _properties;
   }

   public void clear()
   {
      _properties = new Hashtable<String, String>();
   }

   public void init(HibernateConnection con, HibernateConfiguration cfg)
   {
      clear();

      //i18n[MappingRoot.cfgName=Configuration]
      _properties.put(s_stringMgr.getString("MappingRoot.cfgName"), cfg.getName());

      //i18n[MappingRoot.classpath=Classpath]
      _properties.put(s_stringMgr.getString("MappingRoot.classpath"), cfg.classpathAsString());


      try
      {
//         //i18n[MappingRoot.driverClassName=JDBC Driver class name (Hibernate)]
//         _properties.put(s_stringMgr.getString("MappingRoot.driverClassName"), con.getDriverClassName());

         Connection sqlCon = con.getSqlConnection();
         DatabaseMetaData md = sqlCon.getMetaData();

         //i18n[MappingRoot.url=JDBC Url (Hibernate)]
         _properties.put(s_stringMgr.getString("MappingRoot.url"), md.getURL());

         //i18n[MappingRoot.user=JDBC User (Hibernate)]
         _properties.put(s_stringMgr.getString("MappingRoot.user"), md.getUserName());

         //i18n[MappingRoot.driverName=JDBC driver name (Hibernate)]
         _properties.put(s_stringMgr.getString("MappingRoot.driverName"), md.getDriverName());

         //i18n[MappingRoot.driverVersion=JDBC driver version (Hibernate)]
         _properties.put(s_stringMgr.getString("MappingRoot.driverVersion"), md.getDriverVersion());
      }
      catch (Exception e)
      {
         //i18n[MappingRoot.connectionErr=Error getting SQL connection data from Hibernate:]
         s_log.error(s_stringMgr.getString("MappingRoot.connectionErr"), e);
      }
   }
}
