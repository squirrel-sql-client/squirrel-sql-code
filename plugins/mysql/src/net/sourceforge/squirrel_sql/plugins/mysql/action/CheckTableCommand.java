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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
/**
 * This command will run a &quot;CHECK TABLE&quot; over the
 * currently selected tables.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class CheckTableCommand extends AbstractTableListCommand
{
	/** Type of check to run on the tables @see ICheckTypes. */
	private int _checkType;

	/**
	 * Ctor.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a?<TT>null</TT> <TT>ISession</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 */
	public CheckTableCommand(ISession session, MysqlPlugin plugin, int checkType)
	{
		super(session, plugin);
		_checkType = checkType;
	}

	/**
	 * Retrieve the MySQL command to run.
	 *
	 * @return	the MySQL command to run.
	 */
	protected String getMySQLCommand()
	{
		return "check table";
	}

	/**
	 * Add the "type" of the check to the command.
	 *
	 * @return	The SQL command to execute.
	 */
	protected String checkSQL(String sql)
	{
		final StringBuffer buf = new StringBuffer(sql);
		buf.append(' ')
			.append(getResolvedCheckType());
		return buf.toString();
	}

	private String getResolvedCheckType()
	{
		switch (_checkType)
		{
			case CheckTableAction.ICheckTypes.QUICK:
				return "Quick";
			case CheckTableAction.ICheckTypes.FAST:
				return "Fast";
			case CheckTableAction.ICheckTypes.MEDIUM:
				return "Medium";
			case CheckTableAction.ICheckTypes.EXTENDED:
				return "Extended";
			case CheckTableAction.ICheckTypes.CHANGED:
				return "Changed";
			default:
				throw new IllegalStateException("Invalid check type of " + _checkType);
		}
	}
}
