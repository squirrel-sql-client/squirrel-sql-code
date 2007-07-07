package net.sourceforge.squirrel_sql.plugins.oracle.prefs;

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
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;

/**
 * A bean class to store preferences for the Oracle plugin.
 */
public class OraclePreferenceBean implements Cloneable, 
                                             Serializable,
                                             IQueryTokenizerPreferenceBean 
{

    static final long serialVersionUID = 5818886723165356478L;

    static final String UNSUPPORTED = "Unsupported";

    /** Client Name. */
    private String _clientName;

    /** Client version. */
    private String _clientVersion;

    private String statementSeparator = ";";

    private String procedureSeparator = "/";

    private String lineComment = "--";

    private boolean removeMultiLineComments = false;

    private boolean installCustomQueryTokenizer = true;

    public OraclePreferenceBean() {
        super();
    }

    /**
     * Return a copy of this object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError(ex.getMessage()); // Impossible.
        }
    }

    /**
     * Retrieve the client to use. This is only used if <TT>useAnonymousClient</TT>
     * is false.
     * 
     * @return Client name.
     */
    public String getClientName() {
        return _clientName;
    }

    /**
     * Set the client name.
     * 
     * @param value
     *            Client name
     */
    public void setClientName(String value) {
        _clientName = value;
    }

    /**
     * Retrieve the client version to use. This is only used if <TT>useAnonymousLogon</TT>
     * is false.
     * 
     * @return Client version.
     */
    public String getClientVersion() {
        return _clientVersion;
    }

    /**
     * Set the client version.
     * 
     * @param value
     *            Client version
     */
    public void setClientVersion(String value) {
        _clientVersion = value;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#setStatementSeparator(java.lang.String)
     */
    public void setStatementSeparator(String statementSeparator) {
        this.statementSeparator = statementSeparator;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#getStatementSeparator()
     */
    public String getStatementSeparator() {
        return statementSeparator;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#setProcedureSeparator(java.lang.String)
     */
    public void setProcedureSeparator(String procedureSeparator) {
        this.procedureSeparator = procedureSeparator;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#getProcedureSeparator()
     */
    public String getProcedureSeparator() {
        return procedureSeparator;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#setLineComment(java.lang.String)
     */
    public void setLineComment(String lineComment) {
        this.lineComment = lineComment;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#getLineComment()
     */
    public String getLineComment() {
        return lineComment;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#setRemoveMultiLineComments(boolean)
     */
    public void setRemoveMultiLineComments(boolean removeMultiLineComments) {
        this.removeMultiLineComments = removeMultiLineComments;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#isRemoveMultiLineComments()
     */
    public boolean isRemoveMultiLineComments() {
        return removeMultiLineComments;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#setInstallCustomQueryTokenizer(boolean)
     */
    public void setInstallCustomQueryTokenizer(
            boolean installCustomQueryTokenizer) {
        this.installCustomQueryTokenizer = installCustomQueryTokenizer;
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.oracle.prefs.IQueryTokenizerPreferenceBean#isInstallCustomQueryTokenizer()
     */
    public boolean isInstallCustomQueryTokenizer() {
        return installCustomQueryTokenizer;
    }

}
