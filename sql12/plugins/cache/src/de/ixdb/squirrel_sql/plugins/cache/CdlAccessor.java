package de.ixdb.squirrel_sql.plugins.cache;

import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.sql.Statement;
import java.sql.Connection;

import com.intersys.cache.Dataholder;
import com.intersys.cache.CacheObject;
import com.intersys.objects.Database;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheException;
import com.intersys.objects.CacheReader;
import com.intersys.classes.CharacterStream;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

public class CdlAccessor
{
   static org.w3c.dom.Document getDocument(InputStream istream)
   {
      try
      {
         InputSource is = new InputSource(istream);

         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setValidating(false);
         dbf.setIgnoringElementContentWhitespace(true);

         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         return db.parse(is);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   static String getDefinition(String list, Connection con)
   {
      try
      {
         Dataholder[] argv = new Dataholder[1];
         argv[0] = Dataholder.create(list);

         Database conn =  CacheDatabase.getDatabase(con);

         Dataholder res;

         try
         {
            res = conn.runClassMethod("CM.methgetClassDefinition", "getClassDefinition", argv, Database.RET_OBJECT);
         }
         catch (CacheException e)
         {
            Statement stat = con.createStatement();
            stat.executeUpdate
            (

               "CREATE METHOD CM.getClassDefinition(IN className %String)\n" +
                  "RETURNS Integer\n" +
                  "PROCEDURE\n" +
                  "LANGUAGE COS\n" +
                  "{\n" +
                  "   new id,oref,oFile,file\n" +
                  "   if $F($ZV,\"Linux\") \n" +
                  "      set file = \"$HOME/CacheTemp\"_$job_\".xml\"\n" +
                  "   else \n" +
                  "      set file = \"c:\\\\temp\\\\$CacheTemp\"_$job_\".xml\"   \n" +
                  "   do $SYSTEM.OBJ.ExportCDL(className ,file, \"-d\")\n" +
                  "   set oFile = ##class(%FileBinaryStream).%New()\n" +
                  "   do oFile.LinkToFile(file)\n" +
                  "\n" +
                  "   set outStream = ##class(%GlobalCharacterStream).%New()\n" +
                  "   do outStream.CopyFrom(oFile)\n" +
                  "   quit outStream\n" +
                  "}"
            );
            stat.close();

            res = conn.runClassMethod("CM.methgetClassDefinition", "getClassDefinition", argv, Database.RET_OBJECT);
         }


         CacheObject cobj = res.getCacheObject();
         CharacterStream characterStream = (CharacterStream) (cobj.newJavaInstance());

         CacheReader reader = characterStream.getReader();

         StringBuffer sb = new StringBuffer();
         sb.append("");


         char[] buf = new char[50];
         int count = reader.read(buf);
         while(true)
         {

            for (int i = 0; i < count; i++)
            {
               sb.append(buf[i]);
            }

            if(count < 50)
            {
               break;
            }

            count = reader.read(buf);
         }
         return sb.toString();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   static org.w3c.dom.Document getDefinitionAsXmlDoc(String list, Connection connection)
   {
      String cdl = getDefinition(list, connection);

      ByteArrayInputStream bis = new ByteArrayInputStream(cdl.getBytes());

      return getDocument(bis);
   }


   static String getSearchStringForCdlAccess(IDatabaseObjectInfo[] dbObjs)
   {
      String ret = null;

      for (int i = 0; i < dbObjs.length; i++)
      {
         String prefix = "User.";

         if(false == "SQLUser".equalsIgnoreCase(dbObjs[i].getSchemaName()))
         {
            prefix = dbObjs[i].getSchemaName() + ".";
         }



         if (dbObjs[i] instanceof IProcedureInfo)
         {
            IProcedureInfo pi = (IProcedureInfo) dbObjs[i];

            if (null == ret)
            {
               ret = prefix + "func" + pi.getSimpleName();
               ret += "," + prefix + "meth" + pi.getSimpleName();

            }
            else
            {
               ret += "," + prefix + "func" + pi.getSimpleName();
               ret += "," + prefix + "meth" + pi.getSimpleName();
            }
         }
         else
         {
            if (null == ret)
            {
               ret = prefix + dbObjs[i].getSimpleName();
            }
            else
            {
               ret += "," + prefix + dbObjs[i].getSimpleName();
            }
         }

      }
      return ret;
   }

}
