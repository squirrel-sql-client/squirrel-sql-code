package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;

public class ApplyObjectTreeSelectionFromClipAction extends SquirrelAction implements IObjectTreeAction
{
	private static final ILogger s_log = LoggerController.createLogger(ApplyObjectTreeSelectionFromClipAction.class);
	private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(ApplyObjectTreeSelectionFromClipAction.class);


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

		try
		{
			ObjectTreeSelectionUtil.applySelection(_tree, jsonString);
		}
		catch (Exception e)
		{
			String msg = s_stringMgr.getString("ApplyObjectTreeSelectionFromClipAction.failed");
			Main.getApplication().getMessageHandler().showWarningMessage(msg);
			s_log.warn(msg, e);
		}
	}
}
