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

import net.sourceforge.squirrel_sql.client.db.AliasMaintSheetFactory;

/**
 * This <CODE>ICommand</CODE> allows the user to modify an existing
 * <TT>ISQLAlias</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ModifyAliasCommand implements ICommand {
	/** <TT>ISQLAlias</TT> to be modified. */
	private ISQLAlias _sqlAlias;

	/**
	 * Ctor.
	 *
	 * @param	sqlAlias	<TT>ISQLAlias</TT> to be modified.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public ModifyAliasCommand(ISQLAlias sqlAlias) {
		super();
		if (sqlAlias == null) {
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}

		_sqlAlias = sqlAlias;
	}

	/**
	 * Display a dialog allowing user to maintain the <TT>ISQLAlias</TT>.
	 */
	public void execute() {
		AliasMaintSheetFactory.getInstance().showModifySheet(_sqlAlias);
	}
}