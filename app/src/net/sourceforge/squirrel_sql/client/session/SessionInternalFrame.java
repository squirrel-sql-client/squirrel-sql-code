package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import net.sourceforge.squirrel_sql.client.IApplication;

public class SessionInternalFrame extends BaseSessionSheet
					implements ISQLInternalFrame, IObjectTreeInternalFrame
{
	/** Application API. */
	private final IApplication _app;

	/** ID of the session for this window. */
	private IIdentifier _sessionId;

	private SessionSheet _sessionPanel;

	// JASON: Put back in
//	private MySessionListener _sessionLis;

	SessionInternalFrame(ISession session)
	{
		super(session, session.getTitle(), true, true, true, true);
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
		setVisible(false);
		createGUI(session);

// JASON: Put back in
//		_sessionLis = new MySessionListener();
//		session.addSessionListener(_sessionLis);
	}

	public SessionSheet getSessionPanel()
	{
		return _sessionPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sessionPanel.getSQLPaneAPI();
	}

	public IObjectTreeAPI getObjectTreeAPI()
	{
		return _sessionPanel.getObjectTreePanel();
	}

	public void setSelected(boolean selected)
			throws PropertyVetoException
	{
		super.setSelected(selected);

		// Without this when using alt left/right to move
		// between sessions the focus is left in the SQL
		// entry area of the previous session.
		if (selected)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					_sessionPanel.getSQLEntryPanel().requestFocus();
				}
			});
		}
	}

	private void createGUI(ISession session)
	{
		setVisible(false);
		setDefaultCloseOperation(SessionInternalFrame.DO_NOTHING_ON_CLOSE);
		final IApplication app = session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		addInternalFrameListener(new InternalFrameAdapter()
		{
			// This is to fix a problem with the JDK (up to version 1.3)
			// where focus events were not generated correctly. The sympton
			// is being unable to key into the text entry field unless you click
			// elsewhere after focus is gained by the internal frame.
			// See bug ID 4309079 on the JavaSoft bug parade (plus others).
			public void internalFrameActivated(InternalFrameEvent evt)
			{
				Window window = SwingUtilities.windowForComponent(
										SessionInternalFrame.this._sessionPanel.getSQLPanel());
				Component focusOwner = (window != null)
											? window.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
			public void internalFrameClosing(InternalFrameEvent evt)
			{
				final ISession session = getSession();
				if (session != null)
				{
					_app.getSessionManager().closeSession(session);
				}
			}
		});

		_sessionPanel = new SessionSheet(session);
		setContentPane(_sessionPanel);
		validate();
	}

	// JASON: Put back in
//	private class MySessionListener extends SessionAdapter
//	{
//		public void sessionClosed(SessionEvent evt)
//		{
//			evt.getSession().removeSessionListener(_sessionLis);
//			_sessionLis = null;
//		}
//
//		public void sessionTitleChanged(SessionEvent evt)
//		{
//			setTitle(evt.getSession().getTitle());
//		}
//	}
}
