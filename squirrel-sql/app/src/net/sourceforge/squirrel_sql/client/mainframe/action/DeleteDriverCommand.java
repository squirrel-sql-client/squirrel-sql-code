package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.text.MessageFormat;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.db.DataCache;

/**
 * This <CODE>ICommand</CODE> allows the user to delete an existing
 * <TT>ISQLDriver</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DeleteDriverCommand implements ICommand
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String MSG_CONFIRM =
			"Are you sure to want to delete the driver \"{0}\"?";
		String USED =
			"The driver \"{0}\" is used by one or more aliases and cannot be deleted.";
	}

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
	public DeleteDriverCommand(
		IApplication app,
		Frame frame,
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
		Object[] args = { _sqlDriver.getName()};
		final DataCache cache = _app.getDataCache();
		Iterator it = cache.getAliasesForDriver(_sqlDriver);
		if (it.hasNext())
		{
			String msg = MessageFormat.format(i18n.USED, args);
			Dialogs.showOk(_frame, msg);
		}
		else
		{
			String msg = MessageFormat.format(i18n.MSG_CONFIRM, args);
			if (Dialogs.showYesNo(_frame, msg))
			{
				cache.removeDriver(_sqlDriver);
			}
		}
	}
}
