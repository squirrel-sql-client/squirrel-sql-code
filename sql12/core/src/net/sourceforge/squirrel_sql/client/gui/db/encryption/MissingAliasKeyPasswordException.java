package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MissingAliasKeyPasswordException extends RuntimeException
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPasswordEncryptionDefineDlg.class);

   public MissingAliasKeyPasswordException()
   {
      super(s_stringMgr.getString("MissingKeyPasswordException.error.message"));
   }
}
