package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;

public class CopyObjectTreeSelectionToClipAction  extends SquirrelAction implements IObjectTreeAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyObjectTreeSelectionToClipAction.class);


	private IObjectTreeAPI _tree;

	public CopyObjectTreeSelectionToClipAction()
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
		if(0 == _tree.getSelectedNodes().length)
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CopyObjectTreeSelectionToClipAction.cannot.store.empty.selection"));
			return;
		}

		String jsonString = ObjectTreeSelectionUtil.selectionToJsonString(_tree);
		ClipboardUtil.copyToClip(jsonString);
	}
}
