package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;

import java.awt.event.ActionEvent;

public class ApplyObjectTreeSelectionFromClipAction extends SquirrelAction implements IObjectTreeAction
{
	private IObjectTreeAPI _tree;

	public ApplyObjectTreeSelectionFromClipAction()
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
		String jsonString = ClipboardUtil.getClipboardAsString();

		ObjectTreeSelectionUtil.applySelection(_tree, jsonString);

	}
}
