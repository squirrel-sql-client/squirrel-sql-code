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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ResultFrame;

/**
 * This <CODE>ICommand</CODE> returns the specifed SQL results
 * frame back where it was torn off from.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ReturnResultTabCommand implements ICommand {
	/** Parent frame. */
	private final Frame _frame;

	/** Current session. */
	private final ISession _session;

	/** Frame to be returned. */
	private ResultFrame _resultFrame;

	/**
	 * Ctor.
	 *
	 * @param	frame   Parent Frame.
	 * @param	session Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or
	 * 			<TT>ResultFrame</TT> passed.
	 */
	public ReturnResultTabCommand(Frame frame, ISession session,
									ResultFrame resultFrame)
			throws IllegalArgumentException {
		super();
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (resultFrame == null) {
			throw new IllegalArgumentException("Null ResultFrame passed");
		}

		_frame = frame;
		_session = session;
		_resultFrame = resultFrame;
	}

	public void execute() {
		_resultFrame.returnToTabbedPane();
	}
}
