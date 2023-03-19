package net.sourceforge.squirrel_sql.plugins.dbdiff;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.ExternalToolSideBySideDiffPresentation;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.JMeldDiffPresentation;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferenceBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DBDIffExternalService
{

   private DBDiffPreferenceBean _preferences;

   public DBDIffExternalService(DBDiffPreferenceBean preferences)
   {
      _preferences = preferences;
   }

   public void showDiff(String leftMarkdown, String rightMarkdown)
   {
      try
      {
         Path leftFile = Files.createTempFile("DBDIffExternalService-left-File", ".markdown.txt");
         Files.write(leftFile, leftMarkdown.getBytes(StandardCharsets.UTF_8));

         Path rightFile = Files.createTempFile("DBDIffExternalService-right-File", ".markdown.txt");
         Files.write(rightFile, rightMarkdown.getBytes(StandardCharsets.UTF_8));

         if (_preferences.isUseExternalGraphicalDiffTool())
         {
            new ExternalToolSideBySideDiffPresentation().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath());
         }
         else
         {
   //         if (_preferences.isUseTabularDiffPresenation())
   //         {
   //            result = new TabularDiffPresentation();
   //         }
   //         else
            {
               new JMeldDiffPresentation().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath());
            }
         }
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }

   }

}
