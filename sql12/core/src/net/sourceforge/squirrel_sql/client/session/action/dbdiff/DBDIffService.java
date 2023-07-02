package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldCore;

import java.nio.file.Path;

public class DBDIffService
{
   public static void showDiff(Path leftFile, Path rightFile, String diffDialogTitle)
   {
      new JMeldCore().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath(), diffDialogTitle);
   }

   public static void showEditableDiff(Path leftFile, Path rightFile, String diffDialogTitle, JMeldPanelHandlerSaveCallback saveCallback)
   {
   }


}
