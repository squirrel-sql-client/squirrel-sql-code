package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
/**
 * Base functionality for Squirrels internal frames that are attached directly
 * to a session.
 *
 * @author  <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class BaseSessionSheet extends BaseSheet
{
	private ISession _session;

	/**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable JInternalFrame with no title.
	 */
	public BaseSessionSheet(ISession session)
	{
		super();
		setupSheet(session);
	}

	/**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable JInternalFrame with the specified title.
	 *
	 * @param	title	Title for internal frame.
	 */
	public BaseSessionSheet(ISession session, String title)
	{
		super(title);
		setupSheet(session);
	}

	/**
	 * Creates a non-closable, non-maximizable, non-iconifiable
	 * JInternalFrame with the specified title and with
	 * resizability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 */
	public BaseSessionSheet(ISession session, String title, boolean resizable)
	{
		super(title, resizable);
		setupSheet(session);
	}

	/**
	 * Creates a non-maximizable, non-iconifiable JInternalFrame
	 * with the specified title and with resizability and closability
	 * specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 */
	public BaseSessionSheet(ISession session, String title, boolean resizable,
		boolean closable)
	{
		super(title, resizable, closable);
		setupSheet(session);
	}

	/**
	 * Creates a non-iconifiable JInternalFrame with the specified title
	 * and with resizability, closability, and maximizability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 * @param	maximizable	<TT>true</TT> if frame can be maximized.
	 */
	public BaseSessionSheet(ISession session, String title, boolean resizable,
			boolean closable, boolean maximizable)
	{
		super(title, resizable, closable, maximizable);
		setupSheet(session);
	}

	/**
	 * Creates a JInternalFrame with the specified title and with
	 * resizability, closability, maximizability and
	 * iconifability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 * @param	maximizable	<TT>true</TT> if frame can be maximized.
	 * @param	iconifiable	<TT>true</TT> if frame can be iconified.
	 */
	public BaseSessionSheet(ISession session, String title, boolean resizable,
			boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
		setupSheet(session);
	}

	private final void setupSheet(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		_session.getApplication().getSessionWindowManager().registerSessionSheet(this);
		addInternalFrameListener(new SheetActivationListener());
	}

	public ISession getSession()
	{
		return _session;
	}

	/**
	 * Sets the session behind this sheet to the active session when the
	 * frame is activated
	 */
	private class SheetActivationListener extends InternalFrameAdapter
	{
		public void internalFrameActivated(InternalFrameEvent e)
		{
			_session.getApplication().getSessionManager().setActiveSession(_session);
		}
	}
}