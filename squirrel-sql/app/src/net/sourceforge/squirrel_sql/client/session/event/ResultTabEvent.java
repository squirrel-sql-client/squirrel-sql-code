package net.sourceforge.squirrel_sql.client.session.event;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.*;


public class ResultTabEvent {
	private ISession _session;
	private ResultTab _tab;

	public ResultTabEvent(ISession session, ResultTab tab)
			throws IllegalArgumentException {
		super();
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (tab == null) {
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		_session = session;
		_tab = tab;
	}

	public ISession getSession() {
		return _session;
	}

	public ResultTab getResultTab() {
		return _tab;
	}
}

