package net.sourceforge.squirrel_sql.plugins.db2.tab;
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
 * 
 * @author manningr
 *
 */
public abstract class DB2SourceTab extends BaseSourceTab {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DB2SourceTab.class);

    public static final int VIEW_TYPE = 0;
    public static final int STORED_PROC_TYPE = 1;
    public static final int TRIGGER_TYPE = 2;
    
    protected int sourceType = VIEW_TYPE;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(DB2SourceTab.class);

    private static CommentSpec[] commentSpecs =
          new CommentSpec[]
          {
              new CommentSpec("/*", "*/"),
              new CommentSpec("--", "\n")
          };
    
    private static CodeReformator formatter = 
        new CodeReformator(";", commentSpecs);
    
    static interface i18n {
        //i18n[DB2SourceTab.cLanguageProcMsg=This is a C-language routine. The 
        //source code is unavailable.]
        String C_LANGUAGE_PROC_MSG = 
            s_stringMgr.getString("DB2SourceTab.cLanguageProcMsg"); 
    }
    
    public DB2SourceTab(String hint)
    {
        super(hint);
        super.setSourcePanel(new InformixSourcePanel());
    }

    private final class InformixSourcePanel extends BaseSourcePanel
    {
        private static final long serialVersionUID = 1L;

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
                while (rs.next())
                {
                    if (sourceType == STORED_PROC_TYPE) {
                        String tmpProcData = rs.getString(1);
                        String tmpProcLanguage = rs.getString(2);
                        if (tmpProcData == null || "".equals(tmpProcData)) {
                            if (tmpProcLanguage != null 
                                    && tmpProcLanguage.trim().equalsIgnoreCase("C")) 
                            {
                                tmpProcData = i18n.C_LANGUAGE_PROC_MSG;
                            }
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
                // Stored Procedures can have comments embedded in them, so 
                // don't line-wrap them.
                if (sourceType == VIEW_TYPE) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("View source before formatting: "+
                                    buf.toString());
                    }
                    _ta.setText(formatter.reformat(buf.toString()));
                } else {
                    _ta.setText(buf.toString());
                }
                _ta.setCaretPosition(0);
            }
            catch (SQLException ex)
            {
                session.showErrorMessage(ex);
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
