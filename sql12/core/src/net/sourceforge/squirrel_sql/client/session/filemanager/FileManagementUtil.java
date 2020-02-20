package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileManagementUtil
{
   public static StringBuffer readFile(File file) throws IOException
   {
      return readFile(file, null);
   }

   public static StringBuffer readFile(File file, Integer maxLen) throws IOException
   {
      StringBuffer sb = new StringBuffer();

      try(FileInputStream fis = new FileInputStream(file);
          BufferedInputStream bis = new BufferedInputStream(fis);)
      {
         byte[] bytes = new byte[2048];
         int iRead = bis.read(bytes);
         while (iRead != -1)
         {
            sb.append(new String(bytes, 0, iRead));

            if(null != maxLen && sb.length() > maxLen)
            {
               break;
            }

            iRead = bis.read(bytes);
         }
      }
      return sb;
   }

   public static String readFileAsString(File file)
   {
      return readFileAsString(file, null);
   }


   public static String readFileAsString(File file, Integer maxLen)
   {
      try
      {
         return StringUtilities.removeCarriageReturn(readFile(file, maxLen).toString());
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
