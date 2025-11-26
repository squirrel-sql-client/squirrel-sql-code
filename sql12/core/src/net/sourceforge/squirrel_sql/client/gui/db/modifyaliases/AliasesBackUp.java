package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class AliasesBackUp
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasesBackUp.class);
   private static final ILogger s_log = LoggerController.createLogger(AliasesBackUp.class);


   public static File backupAliases(AliasesBackupCallback aliasesBackupCallback)
   {
      return backupAliases(aliasesBackupCallback, "");
   }

   public static File backupAliases(AliasesBackupCallback aliasesBackupCallback, String logPrefix)
   {
      try
      {
         aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.prepare.aliases.backup.begin.save.existing"));
         File aliasesFileToBackUp = Main.getApplication().saveAliases();
         aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.prepare.aliases.backup.finished.save.existing"));

         aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.preparing.aliases.backup.file"));
         String datePostfix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("__yyyy-MM-dd__HH-mm-ss"));
         File databaseAliasesBackupDir = new ApplicationFiles().getDatabaseAliasesBackupDir();
         String aliasesBackupFileName = ApplicationFiles.ALIASES_FILE_NAME + datePostfix + "." + ApplicationFiles.ALIASES_FILE_NAME_EXTENSION;
         databaseAliasesBackupDir.mkdirs();

         File backupFile = new File(databaseAliasesBackupDir, aliasesBackupFileName);
         aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.begin.write.alias.backup.file", backupFile.getAbsolutePath()));
         Files.copy(aliasesFileToBackUp.toPath(), backupFile.toPath());
         s_log.info(logPrefix + "Backuped Aliases file " + new ApplicationFiles().getDatabaseAliasesFile().getAbsolutePath() + " to " + backupFile.getAbsolutePath());
         aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.finished.write.alias.backup.file", backupFile.getAbsolutePath()));

         File[] files = databaseAliasesBackupDir.listFiles((dir, name) -> StringUtils.startsWithIgnoreCase(name, ApplicationFiles.ALIASES_FILE_NAME));

         if(null != files && 30 < files.length)
         {
            aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.begin.cleaning.backups"));

            int delCount = 0;
            for(File file : files)
            {
               if(false == file.isDirectory()
                     && file.lastModified() < System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
               {
                  Files.delete(file.toPath());
                  ++delCount;
               }
            }
            aliasesBackupCallback.setStatus(s_stringMgr.getString("ModifyMultipleAliasesCtrl.finished.cleaning.backups", delCount));
         }
         return backupFile;
      }
      catch(Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
      finally
      {
         aliasesBackupCallback.cleanUp();
      }
   }
}
