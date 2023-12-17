package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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
      SQLAlias selectedAlias = _aliasesList.getSelectedAlias(null);

      if(null == selectedAlias)
      {
         return;
      }
      new ModifyMultipleAliasesCtrl(selectedAlias);
   }
}