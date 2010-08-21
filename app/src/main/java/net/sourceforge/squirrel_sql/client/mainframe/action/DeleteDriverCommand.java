package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.Frame;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This <CODE>ICommand</CODE> allows the user to delete an existing
 * <TT>ISQLDriver</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DeleteDriverCommand implements ICommand
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DeleteDriverCommand.class);

	/** Application API. */
	private final IApplication _app;

	/** Owner of the maintenance dialog. */
	private Frame _frame;

	/** <TT>ISQLDriver</TT> to be deleted. */
	private ISQLDriver _sqlDriver;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	frame		Owning <TT>Frame</TT>.
	 * @param	sqlDriver	<ISQLDriver</TT> to be deleted.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> or
	 *			<TT>IApplication</TT> passed.
	 */
	public DeleteDriverCommand(IApplication app, Frame frame,
								ISQLDriver sqlDriver)
	{
		super();
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("Null ISQLDriver passed");
		}
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;
		_frame = frame;
		_sqlDriver = sqlDriver;
	}

	/**
	 * Delete the current <TT>ISQLDriver</TT> after confirmation.
	 */
	public void execute()
	{
		final Object[] args = {_sqlDriver.getName()};
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
		Iterator<ISQLAlias> it = cache.getAliasesForDriver(_sqlDriver);
		if (it.hasNext())
		{
            StringBuffer aliasList = new StringBuffer();
            while (it.hasNext()) {
                net.sourceforge.squirrel_sql.client.gui.db.SQLAlias alias = (SQLAlias)it.next();
                aliasList.append("\n");
                aliasList.append(alias.getName());
            }
            final Object[] args2 = { _sqlDriver.getName(), aliasList };
            String msg = 
                s_stringMgr.getString("DeleteDriverCommand.used", args2);
			Dialogs.showOk(_frame, msg);
		}
		else
		{
			if (Dialogs.showYesNo(_frame, s_stringMgr.getString("DeleteDriverCommand.comfirm", args)))
			{
				cache.removeDriver(_sqlDriver);
			}
		}
	}
}
