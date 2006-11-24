package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2006 Gerd Wagner 
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
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

      if(appFiles.getDatabaseAliasesFile().exists())
      {
         return null;
      }


      if(false == appFiles.getDatabaseAliasesFile_before_version_2_3().exists())
      {
         return null;
      }


      try
      {
         FileReader fr = new FileReader(appFiles.getDatabaseAliasesFile_before_version_2_3());
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

         return null;
      }
      catch (Exception e)
      {
         return "Conversion of Aliases file failed: Could not write new Aliases file named \n" +
            appFiles.getDatabaseAliasesFile().getPath() + "\n" +
            "You can not start this new version of SQuirreL using your existing Aliases.\n" +
            "You may either continue to use your former SQuirreL version or remove file\n" +
            appFiles.getDatabaseAliasesFile_before_version_2_3().getPath() + "\n" +
            "for your first start of this SQuirreL version. SQuirreL will then try to create an empty Alias file named\n" +
             appFiles.getDatabaseAliasesFile().getPath() + "\n" +
            "Please contact us about this problem. Send a mail to squirrel-sql-users@lists.sourceforge.net.";
      }
   }
}
