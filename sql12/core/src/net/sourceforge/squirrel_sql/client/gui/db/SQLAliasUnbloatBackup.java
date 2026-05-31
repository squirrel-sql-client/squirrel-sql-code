package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


/**
 * Other code spots concerned with unbloating the SQLAliases file are:
 * {@link SQLDriverPropertyCollection#toSpecifiedOnly()}
 * {@link SQLAliasBeanInfo#getPropertyDescriptors()}
 * {@link SQLAlias#getDriverPropertiesUnbloated()}
 */
public class SQLAliasUnbloatBackup
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLAliasUnbloatBackup.class);
   public static final ILogger s_log = LoggerController.createLogger(SQLAliasUnbloatBackup.class);

   public static void createAliasesBackupBeforeUnbloat()
   {
      try
      {
         Path source = new ApplicationFiles().getDatabaseAliasesFile().toPath();
         Path target = new ApplicationFiles().getDatabaseAliasesBeforeUnbloatBackupFile().toPath();
         if(false == target.toFile().exists())
         {
            if(doUnspecifiedDriverPropertiesExist())
            {
               Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
               String msg = s_stringMgr.getString("SQLAliasUnbloatBackup.backup.before.unbloat.info", target);
               s_log.info(msg);
            }
         }
      }
      catch(IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static boolean doUnspecifiedDriverPropertiesExist()
   {
      boolean unspecifiedDriverPropertiesExist = false;
      for(SQLAlias sqlAlias : Main.getApplication().getAliasesAndDriversManager().getAliasList())
      {
         for(SQLDriverProperty driverProperty : sqlAlias.getDriverPropertiesClone().getDriverProperties())
         {
            if(false == driverProperty.isSpecified())
            {
               unspecifiedDriverPropertiesExist = true;
               break;
            }
         }

         if(unspecifiedDriverPropertiesExist)
         {
            break;
         }
      }
      return unspecifiedDriverPropertiesExist;
   }
}
