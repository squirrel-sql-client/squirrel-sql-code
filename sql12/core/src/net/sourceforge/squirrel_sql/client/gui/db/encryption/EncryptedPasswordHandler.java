package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class EncryptedPasswordHandler
{
   private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(EncryptedPasswordHandler.class);


   public static void apply(SQLAlias alias, String password, boolean checkBoxSavePasswordEncryptedSelected) throws ValidationException
   {
      if(false == checkBoxSavePasswordEncryptedSelected)
      {
         alias.setEncryptPassword(false);
         AliasPasswordHandler.setPassword(alias, password);
      }
      else
      {
         boolean formerEncryptedPasswordValue = alias.isEncryptPassword();
         String formerAliasPassword = alias.getPassword();
         try
         {
            alias.setEncryptPassword(true);

            // This raises a MissingKeyPasswordException if the correct key-password wasn't entered.
            AliasPasswordHandler.setPassword(alias, password);
         }
         catch(Exception e)
         {
            // Revert SQLAlias._encryptPassword and SQLAlias._password to their previous values.
            alias.setEncryptPassword(formerEncryptedPasswordValue);
            alias.setPassword(formerAliasPassword);
            Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("EncryptedPasswordHandler.changes.to.password.and.encryptPassword.flag.not.applied", e));

            throw Utilities.wrapRuntime(e);
         }
      }
   }
}
