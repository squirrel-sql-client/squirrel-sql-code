package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003-2004 Maury Hammel
 * mjhammel@users.sourceforge.net
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
 * @author Maury Hammel
 *
 */
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * SQLFilterAction.java
 *
 * Created on March 22, 2003, 10:55 AM
 *
 * Adapted from SessionPropertiesAction.java by Colin Bell.
 *
 * TODO: CHange name to ContentsTabFilterAction
 */
public class SQLFilterAction extends SquirrelAction implements IObjectTreeAction
{
    private static final long serialVersionUID = 1L;

    transient private IObjectTreeAPI _tree;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLFilterAction.class);
    
	/** Creates a new instance of SQLFilterAction
	* @param app A reference to the SQuirreL application instance
	*/
	public SQLFilterAction(IApplication app)
	{
		super(app);
	}

	/** Sets the _session variable with a reference to the current SQuirrel session
	 * instance.
	 * @param session A reference to the current SQuirrel session instance.
	 */
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
		GUIUtils.processOnSwingEventThread(new Runnable() {
		    public void run() {
		        setEnabled(null != _tree);
		    }
		});
      
	}

	/**
	 * Invoked when an action occurs.
	 *
	 * @param	evt		The event that triggered this procedure.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (_tree != null)
		{
			// Ensure that the proper type of Object is selected in the Object Tree before allowing the SQL Filter to be activated.
			final IDatabaseObjectInfo selObjs[] =	_tree.getSelectedDatabaseObjects();
			final int objectTotal = selObjs.length;

			if ( (objectTotal == 1) &&
			        (
			                (selObjs[0].getDatabaseObjectType() == DatabaseObjectType.TABLE) ||
			                (selObjs[0].getDatabaseObjectType() == DatabaseObjectType.VIEW)
			        )
			)
			{
			    final IApplication app = getApplication();

			    final CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
			    cursorChg.show();
			    try
			    {
			        new SQLFilterCommand(_tree, selObjs[0]).execute();
			    }
			    finally
			    {
			        cursorChg.restore();
			    }
			}
			else
			{
                //i18n[SQLFilterAction.singleObjectMessage=You must have a 
                //single table or view selected to activate the SQL Filter]
                String msg = 
                    s_stringMgr.getString("SQLFilterAction.singleObjectMessage");
			    _tree.getSession().showMessage(msg);
			}
		}
	}
}
