package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

public class AliasPasswordHandler
{
   public static String getPassword(ISQLAlias sqlAlias)
   {
      if (sqlAlias.isPasswordEncrypted())
      {
         return PasswordEncryption.decrypt(sqlAlias.getPassword());
      }
      else
      {
         return sqlAlias.getPassword();
      }
   }

   public static void setPassword(ISQLAlias alias, String unencryptedPassword) throws ValidationException
   {
      if (alias.isPasswordEncrypted())
      {
         alias.setPassword(PasswordEncryption.encrypt(unencryptedPassword));
      }
      else
      {
         alias.setPassword(unencryptedPassword);
      }
   }
}
