package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2004 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
/**
 * This <CODE>Action</CODE> will set the default catalog for the session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SetDefaultCatalogAction extends SquirrelAction
										implements IObjectTreeAction
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SetDefaultCatalogAction.class);

	private IObjectTreeAPI _tree;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public SetDefaultCatalogAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the current object tree.
	 *
	 * @param	tree	The current object tree.
	 */
	public void setObjectTree(IObjectTreeAPI tree)
	{
		_tree = tree;
      setEnabled(null != _tree);
	}

	/**
	 * Perform this action. Use the <TT>ShowNativeSQLCommand</TT>.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		IDatabaseObjectInfo[] catalogs = _tree.getSelectedDatabaseObjects();
		if (catalogs.length == 1)
		{
			String catalog = catalogs[0].getSimpleName();
			try
			{
				new SetDefaultCatalogCommand(_tree.getSession(), catalog).execute();
			}
			catch (Throwable th)
			{
				_tree.getSession().showErrorMessage(th);
				s_log.error("Error occured setting session catalog to " + catalog, th);
			}
		}
		else
		{
			_tree.getSession().getApplication().showErrorDialog("Must select a single catalog");
		}

//		IApplication app = getApplication();
//		CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
//		cursorChg.show();
//		try
//		{
//			new ShowNativeSQLCommand(_session).execute();
//		}
//		finally
//		{
//			cursorChg.restore();
//		}
	}
}
