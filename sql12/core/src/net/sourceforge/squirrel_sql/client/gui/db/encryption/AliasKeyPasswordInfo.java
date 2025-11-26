package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasKeyPasswordInfo
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasKeyPasswordInfo.class);

   public static SmallTabButton getSmallInfoButton()
   {
      return new SmallToolTipInfoButton(s_stringMgr.getString("AliasKeyPasswordInfo.small.info.button")).getButton();
   }

   public static SmallTabButton getSmallInfoButtonInfoForAliasWidget()
   {
      String info;
      if(Main.getApplication().getAliasKeyPasswordManager().isUseKeyPassword())
      {
         info = s_stringMgr.getString("AliasKeyPasswordInfo.key.password.used");
      }
      else
      {
         info = s_stringMgr.getString("AliasKeyPasswordInfo.key.password.not.used");
      }

      return new SmallToolTipInfoButton(info).getButton();
   }
}
