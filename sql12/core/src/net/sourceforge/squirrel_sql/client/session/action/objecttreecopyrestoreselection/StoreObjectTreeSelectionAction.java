package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;

public class StoreObjectTreeSelectionAction extends SquirrelAction implements IObjectTreeAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StoreObjectTreeSelectionAction.class);

	private IObjectTreeAPI _tree;

	public StoreObjectTreeSelectionAction()
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
		ObjectTreeSelection objectTreeSelection = ObjectTreeSelectionUtil.selectionToObjectTreeSelection(_tree);

		if(objectTreeSelection.getObjectTreePathSelections().isEmpty())
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("StoreObjectTreeSelectionAction.cannot.store.empty.selection" ));
			return;
		}

		String name = ObjectTreeSelectionUtil.generateSelectionName(objectTreeSelection);
		Main.getApplication().getObjectTreeSelectionStoreManager().store(objectTreeSelection, name);
	}
}
