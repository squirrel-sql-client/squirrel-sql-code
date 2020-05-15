package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CLISqlFileHandler
{
   public static String handleOptionalSqlFile(String sql, boolean throwIoException)
   {
      Path path = null;
      try
      {
         path = Paths.get(sql);
      }
      catch (Exception e)
      {
      }

      if(null != path && Files.isRegularFile(path))
      {
         try
         {
            sql = new String(Files.readAllBytes(path));
         }
         catch (IOException e)
         {
            final String errMsg = "ERROR: Failed to read file " + path.getFileName() + ": " + e.getMessage();
            if (throwIoException)
            {
               throw new RuntimeException(errMsg, e);
            }
            else
            {
               System.err.println(errMsg);
               e.printStackTrace();
            }
         }
      }
      return sql;
   }
}
