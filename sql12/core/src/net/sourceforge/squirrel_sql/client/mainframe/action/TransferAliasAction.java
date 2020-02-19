package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.aliastransfer.AliasTransferCtrl;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class TransferAliasAction extends AliasAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TransferAliasAction.class);

   private IToogleableAliasesList _aliasesList;

   public TransferAliasAction(IToogleableAliasesList al)
   {
      super(Main.getApplication());
      _aliasesList = al;
   }

   public void actionPerformed(ActionEvent e)
   {
      //_aliasesList.getAliasTreeInterface().expandAll();

      if(false == _aliasesList.isViewAsTree())
      {
         int res = JOptionPane.showConfirmDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("TransferAliasAction.transfer.needs.tree"));

         if(res != JOptionPane.YES_OPTION)
         {
            return;
         }

         ToggleTreeViewAction act = (ToggleTreeViewAction) Main.getApplication().getActionCollection().get(ToggleTreeViewAction.class);

         act.getToggleComponentHolder().doClick();

      }

      new AliasTransferCtrl(_aliasesList);

   }
}