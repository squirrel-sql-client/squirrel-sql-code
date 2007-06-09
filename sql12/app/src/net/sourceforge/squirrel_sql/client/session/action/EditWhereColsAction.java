package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003-2004 Maury Hammel
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * EditWhereColsAction.java
 *
 * Adapted from SQLFilterAction.java by Maury Hammel.
 */
public class EditWhereColsAction extends SquirrelAction
								implements IObjectTreeAction
{
	/** The object tree which this Action applies. */
	private IObjectTreeAPI _tree;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(EditWhereColsAction.class);

    
	/**
	 * Ctor.
	 *
	 * @param	app		A reference to the SQuirreL application instance
	 */
	public EditWhereColsAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the Object Tree that we are working with.
	 *
	 * @param	tree	Object tree that we want to work with.
	 */
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
      
	}

	/**
	 * Perform this action.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		final IApplication app = getApplication();
		if (_tree != null)
		{
			// Ensure that the proper type of Object is selected in the Object
			// Tree.
			IDatabaseObjectInfo selectedObjects[] =	_tree.getSelectedDatabaseObjects();
			int objectTotal = selectedObjects.length;

			if ((objectTotal == 1)
				&& (selectedObjects[0].getDatabaseObjectType()
					== DatabaseObjectType.TABLE))
			{
				CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
				cursorChg.show();
				try
				{
					new EditWhereColsCommand(app, _tree, selectedObjects[0]).execute();
				}
				finally
				{
					cursorChg.restore();
				}
			}
			else
			{
                //i18n[EditWhereColsAction.singleObjectMessage=You must have a 
                //single table selected to limit the colums used in the Edit 
                //WHERE clause]
                String msg = 
                    s_stringMgr.getString("EditWhereColsAction.singleObjectMessage");
				_tree.getSession().showMessage(msg);
			}
		}
	}
}
