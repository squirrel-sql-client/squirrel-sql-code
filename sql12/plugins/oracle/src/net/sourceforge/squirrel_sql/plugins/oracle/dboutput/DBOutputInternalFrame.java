package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;
/*
 * Copyright (C) 2004 Jason Height
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
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;

public class DBOutputInternalFrame extends BaseSessionInternalFrame
{
        /** Application API. */
        private final IApplication _app;

        /** ID of the session for this window. */
        private IIdentifier _sessionId;

        private DBOutputPanel _dbOutputPanel;
        /** Toolbar for window. */
        private DBOutputToolBar _toolBar;

        public DBOutputInternalFrame(ISession session)
        {
                super(session, session.getTitle(), true, true, true, true);
                _app = session.getApplication();
                _sessionId = session.getIdentifier();
                setVisible(false);
                createGUI(session);
                addInternalFrameListener(new DBOutputActionEnabler());
        }

        public DBOutputPanel getDBOutputPanel()
        {
                return _dbOutputPanel;
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

                _dbOutputPanel = new DBOutputPanel(getSession());
                _toolBar = new DBOutputToolBar(getSession());
                JPanel contentPanel = new JPanel(new BorderLayout());
                contentPanel.add(_toolBar, BorderLayout.NORTH);
                contentPanel.add(_dbOutputPanel, BorderLayout.CENTER);
                setContentPane(contentPanel);
                validate();
        }

        /** The class representing the toolbar at the top of a dboutput internal frame*/
        private class DBOutputToolBar extends ToolBar
        {
                DBOutputToolBar(ISession session)
                {
                        super();
                        createGUI(session);
                }

                private void createGUI(ISession session)
                {
                  IApplication app = session.getApplication();
                        setUseRolloverButtons(true);
                        setFloatable(false);
                        add(new GetDBOutputAction(app, _dbOutputPanel));
                        /*
                        ActionCollection actions = .getActionCollection();
                        
                        add(actions.get(GetDBOutputAction.class));
                        
                        add(actions.get(ExecuteAllSqlAction.class));
                        addSeparator();
                        add(actions.get(SQLFilterAction.class));
                        actions.get(SQLFilterAction.class).setEnabled(true);*/
                }
        }

        private class DBOutputActionEnabler extends InternalFrameAdapter {
          public void internalFrameActivated(InternalFrameEvent evt) {
            /*final ActionCollection actions = _app.getActionCollection();
            actions.get(ExecuteSqlAction.class).setEnabled(true);
            actions.get(GotoNextResultsTabAction.class).setEnabled(true);
            actions.get(GotoPreviousResultsTabAction.class).setEnabled(true);
            actions.get(ShowNativeSQLAction.class).setEnabled(true);
            actions.get(SQLFilterAction.class).setEnabled(false);*/
          }

          public void internalFrameDeactivated(InternalFrameEvent evt) {
            /*final ActionCollection actions = getSession().getApplication().getActionCollection();
            actions.get(ExecuteSqlAction.class).setEnabled(false);
            actions.get(ExecuteAllSqlAction.class).setEnabled(false);
            actions.get(GotoNextResultsTabAction.class).setEnabled(false);
            actions.get(GotoPreviousResultsTabAction.class).setEnabled(false);
            actions.get(ShowNativeSQLAction.class).setEnabled(false);
            actions.get(SQLFilterAction.class).setEnabled(false);*/
          }

        }
}
