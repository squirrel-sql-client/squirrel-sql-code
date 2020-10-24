package org.squirrelsql;

import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.services.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreDefinedDrivers
{
   private static final SQLDriver[] PREDEFINED_DRIVERS = new SQLDriver[]
   {
         new SQLDriver("PRE_DEF_0001", "Starschema BigQuery JDBC", "net.starschema.clouddb.jdbc.BQDriver", "jdbc:BQDriver:projectid(<urlencoded>)?withServiceAccount=false", "http://code.google.com/p/starschema-bigquery-jdbc/"),
         new SQLDriver("PRE_DEF_0002", "Pointbase Embedded", "com.pointbase.net.netJDBCDriver", "jdbc:pointbase:embedded:<dbname>", "http://www.datamirror.com/products/pointbase"),
         new SQLDriver("PRE_DEF_0003", "IBM DB2 App Driver", "COM.ibm.db2.jdbc.app.DB2Driver", "jdbc:db2:<dbname>", "http://www-306.ibm.com/software/data/db2"),
         new SQLDriver("PRE_DEF_0004", "SAPDB", "com.sap.dbtech.jdbc.DriverSapDB", "jdbc:sapdb:[//host/]dbname[?name=value[&name=value]*]", "http://www.sapdb.org/sap_db_jdbc.htm"),
         new SQLDriver("PRE_DEF_0005", "Pointbase Server", "com.pointbase.net.netJDBCDriver", "jdbc:pointbase:server://<server_name>/<dbname>", "http://www.datamirror.com/products/pointbase"),
         new SQLDriver("PRE_DEF_0006", "HSQLDB In-Memory", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:.", "http://www.hsqldb.org/"),
         new SQLDriver("PRE_DEF_0007", "InterSystems Cache", "com.intersys.jdbc.CacheDriver", "jdbc:Cache://<host>:<1972>/<database>", "http://www.intersystems.com/cache"),
         new SQLDriver("PRE_DEF_0008", "Firebird JayBird", "org.firebirdsql.jdbc.FBDriver", "jdbc:firebirdsql:[//host[:port]/]<database>", "http://www.firebirdsql.org"),
         new SQLDriver("PRE_DEF_0009", "MySQL Driver", "com.mysql.jdbc.Driver", "jdbc:mysql://<hostname>[,<failoverhost>][<:3306>]/<dbname>[?<param1>=<value1>][&<param2>=<value2>]", "http://dev.mysql.com"),
         new SQLDriver("PRE_DEF_0010", "jTDS Sybase", "net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sybase://<hostname>[:<4100>]/<dbname>[;<property>=<value>[;...]]", "http://jtds.sourceforge.net"),
         new SQLDriver("PRE_DEF_0011", "jTDS Microsoft SQL", "net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://<hostname>[:<1433>]/<dbname>[;<property>=<value>[;...]]", "http://jtds.sourceforge.net"),
         new SQLDriver("PRE_DEF_0012", "HXTT XML Embedded", "com.hxtt.sql.xml.XMLDriver", "jdbc:xml:///<databaseName>", "http://www.hxtt.com/xml.html"),
         new SQLDriver("PRE_DEF_0013", "HXTT XML Client", "com.hxtt.sql.xml.XMLDriver", "jdbc:xml://<server:port>/<databaseName>", "http://www.hxtt.com/xml.html"),
         new SQLDriver("PRE_DEF_0014", "HXTT Cobol Embedded", "com.hxtt.sql.cobol.CobolDriver", "jdbc:cobol:///<databaseName>", "http://www.hxtt.com/cobol.html"),
         new SQLDriver("PRE_DEF_0015", "HXTT Cobol Client", "com.hxtt.sql.cobol.CobolDriver", "jdbc:cobol://<server:port>/<databaseName>", "http://www.hxtt.com/cobol.html"),
         new SQLDriver("PRE_DEF_0016", "CUBRID JDBC Driver", "cubrid.jdbc.driver.CUBRIDDriver", "jdbc:cubrid:<hostname>:33000:<dbname>:<username>:<password>:", "http://www.cubrid.org"),
         new SQLDriver("PRE_DEF_0017", "JTOpen(AS/400)", "com.ibm.as400.access.AS400JDBCDriver", "jdbc:as400://<host_name>/<default-schema>;<properties>", "http://jt400.sourceforge.net"),
         new SQLDriver("PRE_DEF_0018", "NuoDB", "com.nuodb.jdbc.Driver", "jdbc:com.nuodb://<server>/test", "http://www.nuodb.com"),
         new SQLDriver("PRE_DEF_0019", "LucidDB", "org.luciddb.jdbc.LucidDbClientDriver", "jdbc:luciddb:http://<server>[:<8034>]", "http://www.luciddb.org"),
         new SQLDriver("PRE_DEF_0020", "Sunopsis XML", "com.sunopsis.jdbc.driver.xml.SnpsXmlDriver", "jdbc:snps:xml?f=<file-name>&s=<schema-name>", "http://www.sunopsis.com/corporate/us/products/jdbcforxml"),
         new SQLDriver("PRE_DEF_0021", "Microsoft MSSQL Server JDBC Driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://<server_name>:1433;databaseName=<db_name>", "http://msdn.microsoft.com/sql"),
         new SQLDriver("PRE_DEF_0022", "Mimer SQL", "com.mimer.jdbc.Driver", "jdbc:mimer:[//[<user>[:<password>]@]<server>[:<1360>]][/<dbname>][property-list]", "http://developer.mimer.com"),
         new SQLDriver("PRE_DEF_0023", "Axion", "org.axiondb.jdbc.AxionDriver", "jdbc:axiondb:<database-name>[:<database-directory>]", "http://axion.tigris.org"),
         new SQLDriver("PRE_DEF_0024", "HSQLDB Standalone", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:<databaseName>", "http://www.hsqldb.org"),
         new SQLDriver("PRE_DEF_0025", "HSQLDB Server", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://<server>[:<1476>]", "http://www.hsqldb.org"),
         new SQLDriver("PRE_DEF_0026", "Mckoi", "com.mckoi.JDBCDriver", "jdbc:mckoi://<host>[:9157][/<schema>]/", "http://www.mckoi.com/database"),
         new SQLDriver("PRE_DEF_0027", "ThinkSQL", "uk.co.thinksql.ThinkSQLDriver", "jdbc:thinksql://<server>:<9075>", "http://www.thinksql.com"),
         new SQLDriver("PRE_DEF_0028", "HSQLDB Web Server", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:http://<server>[:<1476>]", "http://www.hsqldb.org"),
         new SQLDriver("PRE_DEF_0029", "jTDS", "com.internetcds.jdbc.tds.Driver", "jdbc:jtds:sqlserver://<hostname>[:<4100>]/<dbname>[;<property>=<value>[;...]]", "http://jtds.sourceforge.net"),
         new SQLDriver("PRE_DEF_0030", "UnityJDBC MultiSource Virtualization", "unity.jdbc.UnityDriver", "jdbc:unity://virtual", "http://www.unityjdbc.com"),
         new SQLDriver("PRE_DEF_0031", "HXTT Text Client", "com.hxtt.sql.text.TextDriver", "jdbc:text://<server:port>/<databaseName>", "http://www.hxtt.com/text.html"),
         new SQLDriver("PRE_DEF_0032", "HXTT Text Embedded", "com.hxtt.sql.text.TextDriver", "jdbc:text:///<databaseName>", "http://www.hxtt.com/text.html"),
         new SQLDriver("PRE_DEF_0033", "HXTT CSV Embedded", "com.hxtt.sql.text.TextDriver", "jdbc:csv:///<databaseName>", "http://www.hxtt.com/text.html"),
         new SQLDriver("PRE_DEF_0034", "HXTT Access Client", "com.hxtt.sql.access.AccessDriver", "jdbc:access://<server:port>/<databaseName>", "http://www.hxtt.com/access.html"),
         new SQLDriver("PRE_DEF_0035", "HXTT Access Embedded", "com.hxtt.sql.access.AccessDriver", "jdbc:access:///<databaseName>", "http://www.hxtt.com/access.html"),
         new SQLDriver("PRE_DEF_0036", "HXTT Paradox Client", "com.hxtt.sql.paradox.ParadoxDriver", "jdbc:paradox://<server:port>/<databaseName>", "http://www.hxtt.com/paradox.html"),
         new SQLDriver("PRE_DEF_0037", "HXTT Paradox Embedded", "com.hxtt.sql.paradox.ParadoxDriver", "jdbc:paradox:///<databaseName>", "http://www.hxtt.com/paradox.html"),
         new SQLDriver("PRE_DEF_0038", "Informix", "com.informix.jdbc.IfxDriver", "jdbc:informix-sqli://<host_name>:<port_number>/<database_name>:INFORMIXSERVER=<server_name>", "http://www.informix.com"),
         new SQLDriver("PRE_DEF_0039", "HXTT Excel Embedded", "com.hxtt.sql.excel.ExcelDriver", "jdbc:excel:///<databaseName>", "http://www.hxtt.com/excel.html"),
         new SQLDriver("PRE_DEF_0040", "HXTT Excel Client", "com.hxtt.sql.excel.ExcelDriver", "jdbc:excel://<server:port>/<databaseName>", "http://www.hxtt.com/excel.html"),
         new SQLDriver("PRE_DEF_0041", "Apache Derby Embedded", "org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:<database>[;create=true]", "http://db.apache.org/derby"),
         new SQLDriver("PRE_DEF_0042", "Apache Derby Client", "org.apache.derby.jdbc.ClientDriver", "jdbc:derby://<server>[:<port>]/<databaseName>[;<URL attribute>=<value>]", "http://db.apache.org/derby"),
         new SQLDriver("PRE_DEF_0043", "IBM DB2 Net Driver", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://<server>:<6789>/<db-name>", "http://www-306.ibm.com/software/data/db2"),
         new SQLDriver("PRE_DEF_0044", "H2", "org.h2.Driver", "jdbc:h2://<server>:<9092>/<db-name>", "http://www.h2database.com"),
         new SQLDriver("PRE_DEF_0045", "H2 Embedded", "org.h2.Driver", "jdbc:h2://<db-name>", "http://www.h2database.com"),
         new SQLDriver("PRE_DEF_0046", "H2 In-Memory", "org.h2.Driver", "jdbc:h2:mem:", "http://www.h2database.com"),
         new SQLDriver("PRE_DEF_0047", "HXTT DBF Client", "com.hxtt.sql.dbf.DBFDriver", "jdbc:dbf://<server:port>/<databaseName>", "http://www.hxtt.com/dbf.html"),
         new SQLDriver("PRE_DEF_0048", "FrontBase", "jdbc.FrontBase.FBJDriver", "jdbc:FrontBase://<server>/<db-name>", "http://www.frontbase.com/cgi-bin/WebObjects/FrontBase"),
         new SQLDriver("PRE_DEF_0049", "HXTT CSV Client", "com.hxtt.sql.text.TextDriver", "jdbc:csv://<server:port>/<databaseName>", "http://www.hxtt.com/text.html"),
         new SQLDriver("PRE_DEF_0050", "HXTT DBF Embedded", "com.hxtt.sql.dbf.DBFDriver", "jdbc:dbf:///<databaseName>", "http://www.hxtt.com/dbf.html"),
         new SQLDriver("PRE_DEF_0051", "Oracle OCI Driver", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:oci8:@<database_name>", "http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.htm"),
         new SQLDriver("PRE_DEF_0052", "JDBC ODBC Bridge", "sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:<alias>", "http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/bridge.doc.html"),
         new SQLDriver("PRE_DEF_0053", "Oracle Thin Driver", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<server>[:<1521>]:<database_name>", "http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.htm"),
         new SQLDriver("PRE_DEF_0054", "InterClient", "interbase.interclient.Driver", "jdbc:interbase://<server>/<full_db_path>", "http://info.borland.com/devsupport/interbase/opensource"),
         new SQLDriver("PRE_DEF_0055", "MMMySQL Driver", "org.gjt.mm.mysql.Driver", "jdbc:mysql://<hostname>[<:3306>]/<dbname>", "http://dev.mysql.com"),
         new SQLDriver("PRE_DEF_0056", "InstantDB", "org.enhydra.instantdb.jdbc.idbDriver", "jdbc:idb:<pathname>", "http://www.instantdb.com"),
         new SQLDriver("PRE_DEF_0057", "Sybase Adaptive Server Anywhere", "com.sybase.jdbc2.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>?ServiceName=<DBNAME>", "http://www.sybase.com/products/middleware/jconnectforjdbc"),
         new SQLDriver("PRE_DEF_0058", "PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql:[<//host>[:<5432>/]]<database>", "http://jdbc.postgresql.org"),
         new SQLDriver("PRE_DEF_0059", "Sybase Adaptive Server Enterprise", "com.sybase.jdbc2.jdbc.SybDriver", "jdbc:sybase:Tds:<host>:<port>/<DBNAME>", "http://www.sybase.com/products/middleware/jconnectforjdbc"),
         new SQLDriver("PRE_DEF_0060", "HP Vertica", "com.vertica.jdbc.Driver", "jdbc:vertica://10.10.92.160:5433/VMart?user=uidbadmin123&password=uidbadmin123", "http://www.vertica.com"),
         new SQLDriver("PRE_DEF_0061", "InterSystems IRIS Data Platform", "com.intersystems.jdbc.IRISDriver", "jdbc:IRIS://<host>:<1972>/<database>", "https://github.com/intersystems-community/iris-driver-distribution"),
   };
   
   
   
   public static List<SQLDriver> get()
   {
      return new ArrayList<>(Arrays.asList(PREDEFINED_DRIVERS));
   }

   public static SQLDriver find(String id)
   {
      return CollectionUtil.filter(get(), d -> d.getId().equals(id)).get(0);
   }
}




