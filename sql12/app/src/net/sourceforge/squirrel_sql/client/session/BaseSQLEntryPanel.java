package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

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
public abstract class BaseSQLEntryPanel implements ISQLEntryPanel
{
	protected final static String LINE_SEPARATOR = "\n";

	protected final static String SQL_STMT_SEP = LINE_SEPARATOR + LINE_SEPARATOR;

   public static final IntegerIdentifierFactory ENTRY_PANEL_IDENTIFIER_FACTORY = new IntegerIdentifierFactory();
   private IIdentifier _entryPanelIdentifier;

   protected BaseSQLEntryPanel()
   {
      _entryPanelIdentifier = ENTRY_PANEL_IDENTIFIER_FACTORY.createIdentifier();
   }

   public IIdentifier getIdentifier()
   {
      return _entryPanelIdentifier;
   }

	public String getSQLToBeExecuted()
	{
		String sql = getSelectedText();
		if (sql == null || sql.trim().length() == 0)
		{
			sql = getText();

			int iStartIndex = 0;
			int iEndIndex = sql.length();

			int iCaretPos = getCaretPosition() - 1;
			if (iCaretPos < 0)
			{
				iCaretPos = 0;
			}

			int iIndex = sql.lastIndexOf(SQL_STMT_SEP, iCaretPos);
			if (iIndex > 0)
			{
				iStartIndex = iIndex;
			}
			iIndex = sql.indexOf(SQL_STMT_SEP, iCaretPos);
			if (iIndex > 0)
			{
				iEndIndex = iIndex;
			}

			sql = sql.substring(iStartIndex, iEndIndex).trim();
		}
		return sql != null ? sql : "";
	}
}
