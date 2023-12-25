package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.modifyaliases.ModifyMultipleAliasesCtrl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ModifyMultipleAliasesAction extends AliasAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ModifyMultipleAliasesAction.class);

   private IToogleableAliasesList _aliasesList;

   public ModifyMultipleAliasesAction(IToogleableAliasesList al)
   {
      super(Main.getApplication());
      _aliasesList = al;
   }

   public void actionPerformed(ActionEvent e)
   {
      if(false == _aliasesList.isViewAsTree())
      {
         int res = JOptionPane.showConfirmDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("ModifyMultipleAliasesAction.multiModify.needs.tree"));

         if(res != JOptionPane.YES_OPTION)
         {
            return;
         }

         ToggleTreeViewAction act = (ToggleTreeViewAction) Main.getApplication().getActionCollection().get(ToggleTreeViewAction.class);

         act.getToggleComponentHolder().doClick();

      }

      new ModifyMultipleAliasesCtrl();
   }
}