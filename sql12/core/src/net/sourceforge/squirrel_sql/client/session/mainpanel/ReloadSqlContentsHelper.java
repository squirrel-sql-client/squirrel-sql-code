package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.*;

class ReloadSqlContentsHelper
{
   static String getLastSqlContent(ISQLAliasExt alias)
   {
      try
      {

         File sqlContentsFile = getContentsFile(alias);

         if(false == sqlContentsFile.exists())
         {
            return null;
         }


         FileReader fr = new FileReader(sqlContentsFile);
         BufferedReader br = new BufferedReader(fr);

         String line = br.readLine();

         StringWriter sw = new StringWriter();

         while(null != line)
         {
            sw.append(line).append('\n');
            line = br.readLine();
         }

         br.close();
         fr.close();


         return sw.toString();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static File getContentsFile(ISQLAliasExt alias)
   {
      File userDir = new ApplicationFiles().getUserSettingsDirectory();

      File sqlContentsDir = new File(userDir, "sqlcontents");

      sqlContentsDir.mkdirs();

      return new File(sqlContentsDir, "sqlcontents_" + StringUtilities.javaNormalize(alias.getIdentifier().toString(), false) + ".sql");
   }

   static void writeLastSqlContent(ISQLAliasExt alias, String entireSQLScript)
   {
      try
      {
         File contentsFile = getContentsFile(alias);

         PrintWriter pw = new PrintWriter(contentsFile);

         pw.print(entireSQLScript);

         pw.flush();
         pw.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   static void tryDeleteContentsFile(ISQLAliasExt alias)
   {
      getContentsFile(alias).delete();
   }
}
