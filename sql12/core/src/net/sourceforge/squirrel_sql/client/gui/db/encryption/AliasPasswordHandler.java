package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class AliasPasswordHandler
{
   public static String getPassword(SQLAlias sqlAlias)
   {
      if(sqlAlias.isEncryptPassword() && false == StringUtils.isBlank(sqlAlias.getPassword()))
      {
         return PasswordEncryption.decrypt(sqlAlias.getPassword());
      }
      else
      {
         return sqlAlias.getPassword();
      }
   }

   public static void setPassword(SQLAlias alias, String unencryptedPassword) throws ValidationException
   {
      if (alias.isEncryptPassword() && false == StringUtils.isBlank(alias.getPassword()))
      {
         alias.setPassword(PasswordEncryption.encrypt(unencryptedPassword));
      }
      else
      {
         alias.setPassword(unencryptedPassword);
      }
   }
}
