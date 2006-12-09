package net.sourceforge.squirrel_sql.plugins.informix.tab;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * 
 * @author manningr
 *
 */
public abstract class InformixSourceTab extends BaseSourceTab {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InformixSourceTab.class);

    public static final int VIEW_TYPE = 0;
    public static final int STORED_PROC_TYPE = 1;
    public static final int TRIGGER_TYPE = 2;
    
    protected int sourceType = VIEW_TYPE;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(InformixSourceTab.class);

    public InformixSourceTab(String hint)
    {
        super(hint);
        super.setSourcePanel(new InformixSourcePanel());
    }

    private final class InformixSourcePanel extends BaseSourcePanel
    {
        private JTextArea _ta;

        InformixSourcePanel()
        {
            super(new BorderLayout());
            createUserInterface();
        }

        public void load(ISession session, PreparedStatement stmt)
        {
            _ta.setText("");
            _ta.setWrapStyleWord(true);
            try
            {
                ResultSet rs = stmt.executeQuery();
                StringBuffer buf = new StringBuffer(4096);
                int lastProcId = -1;
                while (rs.next())
                {
                    if (sourceType == STORED_PROC_TYPE) {
                        int tmpProcId = rs.getInt(1);
                        String tmpProcData = rs.getString(2);
                        if (lastProcId != tmpProcId) {
                            // First time through, skip the double spacing 
                            if (lastProcId != -1) {
                                // double space since this is a new version of 
                                // the stored procedure (overloading name with
                                // different parameters)
                                buf.append("\n\n");
                            }
                            lastProcId = tmpProcId;
                        }
                        buf.append(tmpProcData);
                    } 
                    if (sourceType == TRIGGER_TYPE) {
                        String data = rs.getString(1);
                        buf.append(data.trim() + " ");
                    }
                    if (sourceType == VIEW_TYPE) {
                        String line = rs.getString(1);
                        buf.append(line.trim() + " ");                        
                    }
                }
                _ta.setText(GUIUtils.getWrappedLine(buf.toString(), 80));
                _ta.setCaretPosition(0);
            }
            catch (SQLException ex)
            {
                session.getMessageHandler().showErrorMessage(ex);
            }

        }

        private void createUserInterface()
        {
            _ta = new JTextArea();
            _ta.setEditable(false);
            add(_ta, BorderLayout.CENTER);
        }
    }

}
