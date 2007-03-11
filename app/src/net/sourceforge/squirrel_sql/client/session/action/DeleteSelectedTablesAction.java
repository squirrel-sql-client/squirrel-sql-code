package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * @version 	$Id: DeleteSelectedTablesAction.java,v 1.3 2007-03-11 03:01:29 manningr Exp $
 * @author		Rob Manning
 */
public class DeleteSelectedTablesAction extends SquirrelAction
										implements IObjectTreeAction
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DeleteSelectedTablesAction.class);   
    
	/** Title for confirmation dialog. */
	private static  String TITLE = 
        s_stringMgr.getString("DeleteSelectedTablesAction.title");

	/** Message for confirmation dialog. */
	private static String MSG = 
        s_stringMgr.getString("DeleteSelectedTablesAction.message");

    /** API for the current tree. */
	private IObjectTreeAPI _tree;

	/**
	 * @param	app	Application API.
	 */
	public DeleteSelectedTablesAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the current object tree API.
	 *
	 * @param	tree	Current ObjectTree
	 */
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (_tree != null)
		{
			List<ITableInfo> tables = _tree.getSelectedTables();
			if (tables.size() > 0)
			{
				if (Dialogs.showYesNo(getApplication().getMainFrame(), MSG, TITLE))
				{
                    DeleteTablesCommand command = 
                        new DeleteTablesCommand(_tree, tables);
                    command.execute();
				}
			}
		}
	}
}
