package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionEvent;

public class ApplyStoredObjectTreeSelectionAction extends SquirrelAction implements IObjectTreeAction
{
	private IObjectTreeAPI _tree;

	public ApplyStoredObjectTreeSelectionAction()
	{
		super(Main.getApplication());
	}

	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
	}

	public void actionPerformed(ActionEvent evt)
	{
		StoredObjectTreeSelectionCtrl ctrl = new StoredObjectTreeSelectionCtrl(GUIUtils.getOwningWindow(_tree.getObjectTree()));

		if(null != ctrl.getObjectTreeSelection())
		{
			ObjectTreeSelectionUtil.applySelection(_tree, ctrl.getObjectTreeSelection());
		}
	}
}
