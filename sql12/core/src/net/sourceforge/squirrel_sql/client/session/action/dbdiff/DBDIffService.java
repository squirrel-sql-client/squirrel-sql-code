package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldCore;

import java.awt.*;
import java.nio.file.Path;

public class DBDIffService
{
   public static void showDiff(Path leftFile, Path rightFile, String diffDialogTitle)
   {
      showDiff(leftFile, rightFile, diffDialogTitle, Main.getApplication().getMainFrame());
   }

   public static void showDiff(Path leftFile, Path rightFile, String diffDialogTitle, Window owningWindow)
   {
      showDiff(leftFile, rightFile, diffDialogTitle, owningWindow, null);
   }

   public static void showDiff(Path leftFile, Path rightFile, String diffDialogTitle, Window owningWindow, JMeldPanelHandlerSaveCallback saveCallback)
   {
      new JMeldCore().executeDiff(leftFile.toFile().getAbsolutePath(), rightFile.toFile().getAbsolutePath(), diffDialogTitle, saveCallback, owningWindow);
   }

}
