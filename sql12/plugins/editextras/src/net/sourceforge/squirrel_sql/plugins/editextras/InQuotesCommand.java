package net.sourceforge.squirrel_sql.plugins.editextras;
/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
/**
 * This command will &quot;quote&quot; an SQL string.
 *
 * @author  Gerd Wagner
 */
class InQuotesCommand implements ICommand
{
	private final ISQLPanelAPI _api;

	InQuotesCommand(ISQLPanelAPI api)
	{
		super();
		_api = api;
	}

	public void execute() throws BaseException
	{
		String textToQuote = _api.getSelectedSQLScript();
		boolean isSelection = true;
		if (null == textToQuote)
		{
			textToQuote = _api.getEntireSQLScript();
			isSelection = false;
		}
		if (null == textToQuote)
		{
			return;
		}

		String quotedText = Utilities.quoteText(textToQuote, false);

		if (isSelection)
		{
			_api.replaceSelectedSQLScript(quotedText, true);
		}
		else
		{
			_api.setEntireSQLScript(quotedText);
		}
	}
}
