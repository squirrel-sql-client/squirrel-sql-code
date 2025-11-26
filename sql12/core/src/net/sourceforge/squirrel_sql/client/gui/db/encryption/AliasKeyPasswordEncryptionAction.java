package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasKeyPasswordEncryptionAction extends SquirrelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasKeyPasswordEncryptionAction.class);

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(Main.getApplication().getAliasKeyPasswordManager().isUseKeyPassword())
      {
         AliasPasswordEncryptionLoginCtrl ctrl = new AliasPasswordEncryptionLoginCtrl();
         if(ctrl.isOk())
         {
            Main.getApplication().getAliasKeyPasswordManager().setKeyPassword(ctrl.getKeyPassword());
         }
         else if(ctrl.isRemoveKeyPassword())
         {
            Main.getApplication().getAliasKeyPasswordManager().removeKeyPassword(ctrl.getKeyPassword());
         }
         else if(ctrl.isChangeKeyPassword())
         {
            Main.getApplication().getAliasKeyPasswordManager().changeKeyPassword(ctrl.getKeyPassword(), ctrl.getNewKeyPassword());
         }
      }
      else
      {
         if(Main.getApplication().getAliasKeyPasswordManager().hasOpenAliasFrames())
         {
            JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("AliasKeyPasswordEncryptionAction.close.all.open.alias.dialogs"));
            return;
         }

         AliasPasswordEncryptionDefineCtrl ctrl = new AliasPasswordEncryptionDefineCtrl();
         if(ctrl.isOk())
         {
            Main.getApplication().getAliasKeyPasswordManager().initKeyPassword(ctrl.getKeyPassword());
         }
      }
   }
}
