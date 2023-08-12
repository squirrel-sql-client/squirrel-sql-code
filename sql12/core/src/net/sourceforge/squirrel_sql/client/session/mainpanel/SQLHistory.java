package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
/**
 * This class represents SQL history.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLHistory
{
	private List<SQLHistoryItem> _history = new ArrayList<>();
	private List<SqlHistoryListener> _sqlHistoryListeners = new ArrayList<>();

	public SQLHistory()
	{
	}

	public SQLHistoryItem[] getSQLHistoryItems()
	{
		SQLHistoryItem[] data = new SQLHistoryItem[_history.size()];
		return _history.toArray(data);
	}

	public void setSQLHistoryItems(SQLHistoryItem[] data)
	{
		_history.clear();

		Arrays.sort(data, Comparator.comparing(SQLHistoryItem::getLastUsageTime, Comparator.nullsLast(Comparator.reverseOrder())));

		_history.addAll(Arrays.asList(data));
	}


   public void addSQLHistoryItem(SQLHistoryItem sqlHistoryItem)
	{
		if (sqlHistoryItem == null)
		{
			throw new IllegalArgumentException("SQLHistoryItem == null");
		}

		// Make sure no duplicates are kept in history.
		while (_history.remove(sqlHistoryItem))
		{
			// Empty body.
		}

		_history.add(0, sqlHistoryItem);

		new ArrayList<>(_sqlHistoryListeners).forEach(l -> l.newSqlHistoryItem(sqlHistoryItem));
	}

	public void addSQLHistoryListener(SqlHistoryListener sqlHistoryListener)
	{
		_sqlHistoryListeners.add(sqlHistoryListener);
	}

	public void removeSQLHistoryListener(SqlHistoryListener sqlHistoryListener)
	{
		_sqlHistoryListeners.remove(sqlHistoryListener);
	}
}
