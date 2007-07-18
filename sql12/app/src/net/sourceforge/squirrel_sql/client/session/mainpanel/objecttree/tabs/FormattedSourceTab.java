package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;
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

import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This will provide source code formatting for object source to subclasses.  
 * The subclass only needs to call setupFormatter if code reformatting is 
 * desired and whether or not to compressWhitespace, which is on by default.
 * Without calling setupFormatter, word-wrapping on word boundaries is still 
 * performed and whitespace is compressed, if so configured.
 * 
 * @author manningr
 */
public abstract class FormattedSourceTab extends BaseSourceTab {

    /** Logger for this class. */
    private final static ILogger s_log = LoggerController
            .createLogger(FormattedSourceTab.class);

    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(FormattedSourceTab.class);
    
    /** does the work of formatting */
    private CodeReformator formatter = null;

    /** whether or not to compress whitespace */
    private boolean compressWhitespace = true;

    private CommentSpec[] commentSpecs =
        new CommentSpec[]
        {
            new CommentSpec("/*", "*/"),
            new CommentSpec("--", "\n")
        };
    
    
    static interface i18n {
        //i18n[FormatterSourceTab.noSourceAvailable=No object source code is 
        //available]
        String NO_SOURCE_AVAILABLE = 
            s_stringMgr.getString("FormatterSourceTab.noSourceAvailable");
    }
    
    public FormattedSourceTab(String hint) {
        super(hint);
        super.setSourcePanel(new FormattedSourcePanel());
    }

    /**
     * Sets up the formatter which formats the source after retrieving it from
     * the ResultSet. If this is not setup prior to loading, then the formatter
     * will not be used - only whitespace compressed if so enabled.
     * 
     * @param stmtSep
     *            the formatter needs to know what the statement separator is.
     * @param commentSpecs
     *            the types of comments that can be found in the source code.
     *            This can be null, and if so, the standard comment styles are 
     *            used (i.e. -- and c-style comments)
     */
    protected void setupFormatter(String stmtSep, CommentSpec[] commentSpecs) {
        if (commentSpecs != null) {
            this.commentSpecs = commentSpecs;
        }
        formatter = new CodeReformator(stmtSep, this.commentSpecs);
    }

    /**
     * Whether or not to convert multiple consecutive spaces into a single
     * space.
     * 
     * @param compressWhitespace
     */
    protected void setCompressWhitespace(boolean compressWhitespace) {
        this.compressWhitespace = compressWhitespace;
    }

    /**
     * The panel that displays the formatted source code.
     */
    private final class FormattedSourcePanel extends BaseSourcePanel {
        private static final long serialVersionUID = 1L;

        private JTextArea _ta;

        FormattedSourcePanel() {
            super(new BorderLayout());
            _ta = new JTextArea();
            _ta.setEditable(false);
            add(_ta, BorderLayout.CENTER);
        }

        public void load(ISession session, PreparedStatement stmt) {
            _ta.setText("");

            // always wrap on word boundaries
            _ta.setWrapStyleWord(true);

            ResultSet rs = null;
            try {
                rs = stmt.executeQuery();
                StringBuffer buf = new StringBuffer(4096);
                while (rs.next()) {
                    String line = rs.getString(1);
                    if (line == null) {
                        s_log.debug("load: Null object source line; skipping...");
                        continue;
                    }
                    if (compressWhitespace) {
                        buf.append(line.trim() + " ");
                    } else {
                        buf.append(line);
                    }
                }
                if (formatter != null && buf.length() != 0) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Object source code before formatting: "
                                + buf.toString());
                    }
                    _ta.setText(formatter.reformat(buf.toString()));
                } else {
                    if (buf.length() == 0) {
                        buf.append(i18n.NO_SOURCE_AVAILABLE);
                    }
                    _ta.setText(buf.toString());
                }
                _ta.setCaretPosition(0);

            } catch (Exception ex) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Unexpected exception while formatting " +
                                "object source code", ex);
                }
                session.showErrorMessage(ex);
            } finally {
                if (rs != null) try { rs.close(); } catch (Exception e) {}
                if (stmt != null) try { stmt.close(); } catch (Exception e) {}
            }
        }
    }

}
