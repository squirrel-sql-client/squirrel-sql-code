package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;

import java.awt.event.ActionEvent;

public class ToggleTreeViewAction extends SquirrelAction implements IToggleAction
{

   private ToggleComponentHolder _toogleComponentHolder;
   private IToogleableAliasesList _aliasesList;

   public ToggleTreeViewAction(IApplication app, IToogleableAliasesList aliasesList)
   {
      super(app);
      _aliasesList = aliasesList;

      _toogleComponentHolder = new ToggleComponentHolder();
   }


   public ToggleComponentHolder getToggleComponentHolder()
   {
      return _toogleComponentHolder;
   }


   public void actionPerformed(ActionEvent evt)
   {
      _aliasesList.setViewAsTree(_toogleComponentHolder.isSelected());   
      getApplication().getWindowManager().getAliasesListInternalFrame().enableDisableActions();
   }
}