package net.sourceforge.squirrel_sql.client.session.action;
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultFrame;
import net.sourceforge.squirrel_sql.client.session.mainpanel.*;


public class ReturnResultTabAction extends SquirrelAction {
	/** Current session. */
	private final ISession _session;

	/** Frame to be returned. */
	private ResultFrame _resultFrame;

	/**
	 * Ctor.
	 * 
	 * @param	app			Application API.
	 * @param	session		Current session
	 * @param	resultFrame	Results frame to be returned.
	 */
	public ReturnResultTabAction(IApplication app, ISession session,
									ResultFrame resultFrame)
			throws IllegalArgumentException {
		super(app);
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (resultFrame == null) {
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_session = session;
		_resultFrame = resultFrame;
	}

	public void actionPerformed(ActionEvent evt) {
		new ReturnResultTabCommand(this.getParentFrame(evt), _session, _resultFrame).execute();
	}

}
