package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class TableSelectionDiffUtil
{
   static Path createLeftTempFile(String markDown)
   {
      return createTempFile(markDown, "DiffTableCellSelection-left");
   }
   static Path createRightTempFile(String markDown)
   {
      return createTempFile(markDown, "DiffTableCellSelection-right");
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
