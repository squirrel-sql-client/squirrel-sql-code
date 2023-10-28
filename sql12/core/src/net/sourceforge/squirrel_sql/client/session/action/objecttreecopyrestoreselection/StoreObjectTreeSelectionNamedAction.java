package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;

public class StoreObjectTreeSelectionNamedAction extends SquirrelAction implements IObjectTreeAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StoreObjectTreeSelectionNamedAction.class);

	private IObjectTreeAPI _tree;

	public StoreObjectTreeSelectionNamedAction()
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
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("StoreObjectTreeSelectionNamedAction.cannot.store.empty.selection"));
			return;
		}

		String generatedName = ObjectTreeSelectionUtil.generateSelectionName(objectTreeSelection);
		SaveObjectTreeSelectionAsDlg dlg = new SaveObjectTreeSelectionAsDlg(GUIUtils.getOwningFrame(_tree.getObjectTree()), generatedName);

		if(dlg.isOk())
		{
			Main.getApplication().getObjectTreeSelectionStoreManager().store(objectTreeSelection, dlg.getObjectTreeSelectionName());
		}
	}
}
