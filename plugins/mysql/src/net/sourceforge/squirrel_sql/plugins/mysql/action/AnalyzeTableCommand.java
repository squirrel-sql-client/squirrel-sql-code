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
 * This command will run a &quot;ANALYZE TABLE&quot; over the
 * currently selected tables.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class AnalyzeTableCommand extends AbstractTableListCommand
{
	/**
	 * Ctor.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a?<TT>null</TT> <TT>ISession</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 */
	public AnalyzeTableCommand(ISession session, MysqlPlugin plugin)
	{
		super(session, plugin);
	}

	/**
	 * Retrieve the MySQL command to run.
	 *
	 * @return	the MySQL command to run.
	 */
	protected String getMySQLCommand()
	{
		return "analyze table";
	}
}
