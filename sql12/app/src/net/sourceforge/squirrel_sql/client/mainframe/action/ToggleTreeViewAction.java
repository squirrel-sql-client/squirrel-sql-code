package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;

public class ToggleTreeViewAction extends SquirrelAction implements IToggleAction
{

	private static final long serialVersionUID = 2767325027149049773L;
	
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
		IApplication application = getApplication();
		if (application != null)
		{
			WindowManager windowManager = application.getWindowManager();
			if (windowManager != null)
			{
				AliasesListInternalFrame aliasesListInternalFrame = windowManager.getAliasesListInternalFrame();
				if (aliasesListInternalFrame != null)
				{
					aliasesListInternalFrame.enableDisableActions();
				}
			}
		}
   }
}