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
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

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
      ISQLEntryPanel entryPanel = _api.getSQLEntryPanel();

      quoteSQL(entryPanel, false);
	}

   public static void quoteSQL(ISQLEntryPanel entryPanel, boolean sbAppend)
   {
      int[] bounds = entryPanel.getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToQuote = entryPanel.getSQLToBeExecuted();

      if (null == textToQuote)
      {
         return;
      }

      String quotedText = Utilities.quoteText(textToQuote, sbAppend);

      entryPanel.setSelectionStart(bounds[0]);
      entryPanel.setSelectionEnd(bounds[1]);
      entryPanel.replaceSelection(quotedText);
   }
}
