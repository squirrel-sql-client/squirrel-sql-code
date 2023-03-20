package net.sourceforge.squirrel_sql.plugins.dbdiff;

import java.io.IOException;
import java.nio.file.Path;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.ExternalToolSideBySideDiffPresentation;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.JMeldDiffPresentation;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferenceBean;

public class DBDIffExternalService
{

   private DBDiffPreferenceBean _preferences;

   public DBDIffExternalService(DBDiffPreferenceBean preferences)
   {
      _preferences = preferences;
   }

   public void showDiff(Path leftFile, Path rightFile, String diffDialogTitle)
   {
      try
      {
         if (_preferences.isUseExternalGraphicalDiffTool())
         {
            new ExternalToolSideBySideDiffPresentation().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath());
         }
         else
         {
            new JMeldDiffPresentation().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath(), diffDialogTitle);
         }
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }

   }

}
