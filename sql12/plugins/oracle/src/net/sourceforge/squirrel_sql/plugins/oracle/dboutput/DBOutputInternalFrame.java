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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;


import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
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

        private Resources _resources;

        public DBOutputInternalFrame(ISession session, Resources resources)
        {
                super(session, session.getTitle(), true, true, true, true);
                _app = session.getApplication();
                _resources = resources;
                _sessionId = session.getIdentifier();
                setVisible(false);
                createGUI(session);
        }

        public DBOutputPanel getDBOutputPanel()
        {
                return _dbOutputPanel;
        }

        private void createGUI(ISession session)
        {
                setVisible(false);

                addInternalFrameListener(new InternalFrameAdapter() {
                  public void internalFrameClosing(InternalFrameEvent e) {
                    //Turn off auto refresh when we are shutting down.
                    _dbOutputPanel.setAutoRefresh(false);
                  }
                });


                Icon icon = _resources.getIcon(getClass(), "frameIcon"); //i18n
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
                        add(new GetDBOutputAction(app, _resources, _dbOutputPanel));
                        add(new ClearDBOutputAction(app, _resources, _dbOutputPanel));


                        //Create checkbox for enabling auto refresh
                        final JCheckBox autoRefresh = new JCheckBox("Enable auto refresh", false);
                        autoRefresh.addActionListener(new ActionListener() {
                          public void actionPerformed(ActionEvent e) {
                            _dbOutputPanel.setAutoRefresh(autoRefresh.isSelected());
                          }
                        });
                        add(autoRefresh);

                        //Create spinner for update period
                        final SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 60, 5);
                        final JSpinner refreshRate = new JSpinner(model);
                        refreshRate.addChangeListener(new ChangeListener() {
                          public void stateChanged(ChangeEvent e) {
                            _dbOutputPanel.setAutoRefreshPeriod(model.getNumber().intValue());
                          }
                        });
                        add(refreshRate);
                        add(new JLabel("(seconds)"));
                }
        }
}
