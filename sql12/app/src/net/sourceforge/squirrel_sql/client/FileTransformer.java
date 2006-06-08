package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

import java.util.prefs.Preferences;
import java.io.*;


/**
 * When new SQuirreL versions need transform config files
 * these should be done here.
 */
public class FileTransformer
{
   /**
    *
    * @return Error message. Will cause SQuirreL to QUIT!!!
    */
   public static String transform(ApplicationFiles appFiles)
   {
      return convertAliases_2_2_to_2_3(appFiles);
   }

   private static String convertAliases_2_2_to_2_3(ApplicationFiles appFiles)
   {
      String prefKey = "SQUirreLSQL_FileTransformer_aliases_2_2_to_2_3";
      if(Preferences.userRoot().get(prefKey, "").equals(appFiles.getDatabaseAliasesFile().getPath()))
      {
         return null;
      }

      if(false == appFiles.getDatabaseAliasesFile().exists())
      {
         return null;
      }

      File backupFile = new File(appFiles.getDatabaseAliasesFile().getPath() + "_2_2_to_2_3_conversion_backup");
      if(false == appFiles.getDatabaseAliasesFile().renameTo(backupFile))
      {
         return "Conversion of Aliases file failed: Could not backup old file,\n" +
            "You can not start this new version of SQuirreL using your existing Aliases.\n" +
            "You may either continue to use your former version or remove file\n"
            + appFiles.getDatabaseAliasesFile().getPath() + "\n\n" +
            "Please contact us about this problem. Send a mail to squirrel-sql-users@lists.sourceforge.net.";
      }

      try
      {
         FileReader fr = new FileReader(backupFile);
         BufferedReader br = new BufferedReader(fr);

         FileWriter fw = new FileWriter(appFiles.getDatabaseAliasesFile());
         BufferedWriter bw = new BufferedWriter(fw);


         String oldClassName = "net.sourceforge.squirrel_sql.fw.sql.SQLAlias";
         String newClassName = SQLAlias.class.getName();

         String line = br.readLine();
         while(null != line)
         {
            int ix = line.indexOf(oldClassName);
            if(-1 != ix)
            {
               line = line.substring(0,ix) + newClassName + line.substring(ix + oldClassName.length(), line.length());
            }

            bw.write(line + "\n");
            line = br.readLine();
         }

         bw.flush();
         fw.flush();
         bw.close();
         fw.close();

         br.close();
         fr.close();

         Preferences.userRoot().put(prefKey, appFiles.getDatabaseAliasesFile().getPath());
         return null;
      }
      catch (Exception e)
      {
         backupFile.renameTo(appFiles.getDatabaseAliasesFile());

         return "Conversion of Aliases file failed: Could not write new Aliases file,\n" +
            "You can not start this new version of SQuirreL using your existing Aliases.\n" +
            "You may either continue to use your former version or remove file\n"
            + appFiles.getDatabaseAliasesFile().getPath() + "\n\n" +
            "Please contact us about this problem. Send a mail to squirrel-sql-users@lists.sourceforge.net.";
      }
   }
}
