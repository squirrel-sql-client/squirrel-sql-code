package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Copyright (C) 2003 Jason Height
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteAllSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoNextResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoPreviousResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowNativeSQLAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

/* JASON: Class*/
public class SQLInternalFrame extends BaseSessionInternalFrame
								implements ISQLInternalFrame
{
	/** Application API. */
	private final IApplication _app;

	private SQLPanel _sqlPanel;
	/** Toolbar for window. */
	private SQLToolBar _toolBar;

	public SQLInternalFrame(ISession session)
	{
		super(session, session.getTitle(), true, true, true, true);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
		addInternalFrameListener(new SQLActionEnabler());
	}

	public SQLPanel getSQLPanel()
	{
		return _sqlPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sqlPanel.getSQLPanelAPI();
	}

	private void createGUI(ISession session)
	{
		setVisible(false);
		final IApplication app = session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		// This is to fix a problem with the JDK (up to version 1.3)
		// where focus events were not generated correctly. The sympton
		// is being unable to key into the text entry field unless you click
		// elsewhere after focus is gained by the internal frame.
		// See bug ID 4309079 on the JavaSoft bug parade (plus others).
		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameActivated(InternalFrameEvent evt)
			{
				Window window = SwingUtilities
						.windowForComponent(SQLInternalFrame.this.getSQLPanel());
				Component focusOwner = (window != null) ? window
						.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost = new FocusEvent(focusOwner,
							FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner,
							FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
		});

		_sqlPanel = new SQLPanel(getSession());
		_toolBar = new SQLToolBar(getSession(), _sqlPanel.getSQLPanelAPI());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_sqlPanel, BorderLayout.CENTER);
		setContentPane(contentPanel);
		validate();
	}

	/** The class representing the toolbar at the top of a sql internal frame*/
	private class SQLToolBar extends ToolBar
	{
		SQLToolBar(ISession session, ISQLPanelAPI panel)
		{
			super();
			createGUI(session, panel);
		}

		private void createGUI(ISession session, ISQLPanelAPI panel)
		{
			ActionCollection actions = session.getApplication()
					.getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(ExecuteSqlAction.class));
			add(actions.get(ExecuteAllSqlAction.class));
			addSeparator();
			add(actions.get(SQLFilterAction.class));
			actions.get(SQLFilterAction.class).setEnabled(true);
		}
	}

	private class SQLActionEnabler extends InternalFrameAdapter
	{
		public void internalFrameActivated(InternalFrameEvent evt)
		{
			final ActionCollection actions = _app.getActionCollection();
			actions.get(ExecuteSqlAction.class).setEnabled(true);
			actions.get(GotoNextResultsTabAction.class).setEnabled(true);
			actions.get(GotoPreviousResultsTabAction.class).setEnabled(true);
			actions.get(ShowNativeSQLAction.class).setEnabled(true);
			actions.get(SQLFilterAction.class).setEnabled(false);
		}

		public void internalFrameDeactivated(InternalFrameEvent evt)
		{
			final ActionCollection actions = getSession().getApplication()
					.getActionCollection();
			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(ExecuteAllSqlAction.class).setEnabled(false);
			actions.get(GotoNextResultsTabAction.class).setEnabled(false);
			actions.get(GotoPreviousResultsTabAction.class).setEnabled(false);
			actions.get(ShowNativeSQLAction.class).setEnabled(false);
			actions.get(SQLFilterAction.class).setEnabled(false);
		}

	}
}