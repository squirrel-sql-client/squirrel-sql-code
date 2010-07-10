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
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.db.AliasMaintSheetFactory;

/**
 * This <CODE>ICommand</CODE> allows the user to copy an existing
 * <TT>ISQLAlias</TT> to a new one and then maintain the new one.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyAliasCommand implements ICommand {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(CopyAliasCommand.class);

	/** <TT>ISQLAlias</TT> to be copied. */
	private final ISQLAlias _sqlAlias;

	/**
	 * Ctor.
	 *
	 * @param	sqlAlias	<TT>ISQLAlias</TT> to be copied.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public CopyAliasCommand(ISQLAlias sqlAlias) {
		super();
		if (sqlAlias == null) {
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}

		_sqlAlias = sqlAlias;
	}

	public void execute() {
		AliasMaintSheetFactory.getInstance().showCopySheet(_sqlAlias);
	}
}
