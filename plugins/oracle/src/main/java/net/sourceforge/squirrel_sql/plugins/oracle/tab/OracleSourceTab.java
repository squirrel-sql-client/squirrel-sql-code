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
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class is only responsible for formatting source statements for Oracle
 * views.
 * 
 * @author manningr
 */
public abstract class OracleSourceTab extends BaseSourceTab {

    public static final int VIEW_TYPE = 0;
    public static final int STORED_PROC_TYPE = 1;
    public static final int TRIGGER_TYPE = 2;
    public static final int TABLE_TYPE = 3;
    
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
        private static final long serialVersionUID = 7855991042669454322L;

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
            ResultSet rs = null;
            try
            {
                rs = stmt.executeQuery();
                StringBuffer buf = new StringBuffer(4096);
                while (rs.next())
                {
                    String line1 = rs.getString(1);
                    String line2 = rs.getString(2);
                    buf.append(line1.trim() + " ");
                    buf.append(line2.trim() + " ");
                }
                String source = "";
                if (buf.length() == 0 && sourceType == TABLE_TYPE) {
                    ISQLDatabaseMetaData md = session.getMetaData();
                    // TODO: Need to define a better approach to getting dialects.
                    // That is, we don't really want to ever prompt the user in this
                    // case.  It's always Oracle.  Yet, we may have a new OracleDialect
                    // at some point.
                    HibernateDialect dialect = DialectFactory.getDialect("Oracle");
                    
                    // TODO: How to let the user customize this??
                    CreateScriptPreferences prefs = new CreateScriptPreferences();
                    
                    ITableInfo[] tabs = new ITableInfo[] { (ITableInfo)getDatabaseObjectInfo() };
                    List<ITableInfo> tables = Arrays.asList(tabs);
                    // Handle table source
                    List<String> sqls = dialect.getCreateTableSQL(tables, md, prefs, false);
                    String sep = session.getQueryTokenizer().getSQLStatementSeparator();
                    for (String sql : sqls) {
                        buf.append(sql);
                        buf.append(sep);
                        buf.append("\n");
                    }
                    source = buf.toString();
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("View source before formatting: "+
                                    buf.toString());
                    }
                    source = formatter.reformat(buf.toString());
                }
                _ta.setText(source);
                _ta.setCaretPosition(0);
            }
            catch (SQLException ex)
            {
                s_log.error("Unexpected exception: "+ex.getMessage(), ex);
                session.showErrorMessage(ex);
            } finally {
            	SQLUtilities.closeResultSet(rs);
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
