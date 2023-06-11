package net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TableSelectionDiffUtil
{
   public static Path createLeftTempFile(String markDown)
   {
      return createTempFile(markDown, "SQuirreLSQL.tableSelectionDiff-left");
   }
   static Path createRightTempFile(String markDown)
   {
      return createTempFile(markDown, "SQuirreLSQL.tableSelectionDiff-right");
   }

   private static Path createTempFile(String markDown, String fileNamePrefix)
   {
      try
      {
         Path leftFile = Files.createTempFile(fileNamePrefix, ".markdown.txt");
         leftFile.toFile().deleteOnExit();
         return Files.write(leftFile, markDown.getBytes(StandardCharsets.UTF_8));
      }
      catch(IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
