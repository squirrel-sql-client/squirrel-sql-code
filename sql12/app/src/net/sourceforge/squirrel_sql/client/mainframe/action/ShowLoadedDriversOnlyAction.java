package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This <CODE>Action</CODE> will show/hide drivers in the Drivers List
 * that cannot be loaded.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ShowLoadedDriversOnlyAction extends SquirrelAction
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowLoadedDriversOnlyAction.class);

	/**
	 * Ctor.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public ShowLoadedDriversOnlyAction(IApplication app)
	{
		super(app);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	/**
	 * Perform this action.
	 *
	 * @param	evt	 The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ShowLoadedDriversOnlyCommand(getApplication()).execute();
		}
		catch (Exception ex)
		{
			getApplication().showErrorDialog(s_stringMgr.getString("ShowLoadedDriversOnlyAction.error"), ex);
		}
	}
}
