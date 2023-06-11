package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.ExternalToolSideBySideDiffPresentation;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldDiffPresentation;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.IOException;
import java.nio.file.Path;

public class DBDIffService
{
   public static void showDiff(Path leftFile, Path rightFile, String diffDialogTitle)
   {
      try
      {
         if (Main.getApplication().getDBDiffState().getDBDiffPreferenceBean().isUseExternalGraphicalDiffTool())
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
