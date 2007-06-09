package net.sourceforge.squirrel_sql.plugins.mysql.action;
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

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
/**
 * This <TT>Action</TT> will run a &quot;CHECK TABLE&quot; over the
 * currently selected tables.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CheckTableAction extends SquirrelAction implements ISessionAction
{
	/** Different check types that can be run. */
	interface ICheckTypes
	{
		int QUICK = 0;
		int FAST = 1;
		int MEDIUM = 2;
		int EXTENDED = 3;
		int CHANGED = 4;
	}

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/** Type of check to run on the tables @see ICheckTypes. */
	private int _checkType;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	rsrc		Plugins resources.
	 * @param	plugin		This plugin.
	 * @param	checktype	The type of table check to be done.
	 *						@see ICheckTypes.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a?<TT>null</TT> <TT>IApplication</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if an invalid <TT>checktype</TT> passed.
	 */
	private CheckTableAction(IApplication app, Resources rsrc,
							MysqlPlugin plugin, int checkType)
	{
		super(app, rsrc);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Resources == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (checkType < ICheckTypes.QUICK || checkType > ICheckTypes.CHANGED)
		{
			throw new IllegalArgumentException("Invalid checkType of " + checkType);
		}

		_plugin = plugin;
		_checkType = checkType;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new CheckTableCommand(_session, _plugin, _checkType).execute();
			}
			catch (Throwable th)
			{
				_session.showErrorMessage(th);
			}
		}
	}

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}

	public static final class ChangedCheckTableAction extends CheckTableAction
	{
		public ChangedCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.CHANGED);
		}
	}

	public static final class ExtendedCheckTableAction extends CheckTableAction
	{
		public ExtendedCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.EXTENDED);
		}
	}

	public static final class FastCheckTableAction extends CheckTableAction
	{
		public FastCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.FAST);
		}
	}

	public static final class MediumCheckTableAction extends CheckTableAction
	{
		public MediumCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.MEDIUM);
		}
	}

	public static final class QuickCheckTableAction extends CheckTableAction
	{
		public QuickCheckTableAction(IApplication app, Resources rsrc,
										MysqlPlugin plugin)
		{
			super(app, rsrc, plugin, ICheckTypes.QUICK);
		}
	}
}

