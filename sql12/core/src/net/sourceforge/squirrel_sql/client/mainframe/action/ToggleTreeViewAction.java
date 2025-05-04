package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;
import net.sourceforge.squirrel_sql.fw.props.Props;

import java.awt.event.ActionEvent;

public class ToggleTreeViewAction extends SquirrelAction implements IToggleAction
{
   public static final String PREF_KEY_VIEW_ALIASES_AS_TREE = "Squirrel.viewAliasesAsTree";
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
      Props.putBoolean(ToggleTreeViewAction.PREF_KEY_VIEW_ALIASES_AS_TREE, _toogleComponentHolder.isSelected());
      _aliasesList.setViewAsTree(_toogleComponentHolder.isSelected());

      //if (Main.getApplication() != null)
		//{
			WindowManager windowManager = Main.getApplication().getWindowManager();
			if (windowManager != null)
			{
				AliasesListInternalFrame aliasesListInternalFrame = windowManager.getAliasesListInternalFrame();
				if (aliasesListInternalFrame != null)
				{
					aliasesListInternalFrame.enableDisableActions();
				}
			}
		//}
   }
}