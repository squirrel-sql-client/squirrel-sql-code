package net.sourceforge.squirrel_sql.plugins.oracle.tab;
/*
 * Copyright (C) 2007 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is only responsible for formatting source statements for Oracle
 * views.
 * 
 * @author manningr
 */
public abstract class OracleSourceTab extends BaseSourceTab {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OracleSourceTab.class);

    public static final int VIEW_TYPE = 0;
    public static final int STORED_PROC_TYPE = 1;
    public static final int TRIGGER_TYPE = 2;
    
    protected int sourceType = VIEW_TYPE;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(OracleSourceTab.class);

    private static CommentSpec[] commentSpecs =
          new CommentSpec[]
          {
              new CommentSpec("/*", "*/"),
              new CommentSpec("--", "\n")
          };
    
    private static CodeReformator formatter = 
        new CodeReformator(";", commentSpecs);
    
    public OracleSourceTab(String hint)
    {
        super(hint);
        super.setSourcePanel(new OracleSourcePanel());
    }

    private final class OracleSourcePanel extends BaseSourcePanel
    {
        private JTextArea _ta;

        OracleSourcePanel()
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
                while (rs.next())
                {
                    String line1 = rs.getString(1);
                    String line2 = rs.getString(2);
                    buf.append(line1.trim() + " ");
                    buf.append(line2.trim() + " ");
                }
                if (s_log.isDebugEnabled()) {
                    s_log.debug("View source before formatting: "+
                                buf.toString());
                }
                _ta.setText(formatter.reformat(buf.toString()));
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
