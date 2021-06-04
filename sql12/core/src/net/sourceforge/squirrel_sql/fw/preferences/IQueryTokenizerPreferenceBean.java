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
package net.sourceforge.squirrel_sql.fw.preferences;

public interface IQueryTokenizerPreferenceBean
{

    void setStatementSeparator(String statementSeparator);

    String getStatementSeparator();

    void setProcedureSeparator(String procedureSeparator);

    String getProcedureSeparator();

    void setLineComment(String lineComment);

    String getLineComment();

    void setRemoveMultiLineComments(boolean removeMultiLineComments);

    boolean isRemoveMultiLineComments();

    boolean isRemoveLineComments();

    void setRemoveLineComments(boolean removeLineComments);

    void setInstallCustomQueryTokenizer(boolean installCustomQueryTokenizer);

    boolean isInstallCustomQueryTokenizer();

    /**
     * Retrieve the client to use. This is only used if <TT>useAnonymousClient</TT>
     * is false.
     */
    String getClientName();
    
    /**
     * Set the client name.
     */
    void setClientName(String value);

    /**
     * Retrieve the client version to use. This is only used if <TT>useAnonymousLogon</TT>
     * is false.
     */
    String getClientVersion();
    
    /**
     * Set the client version.
     */
    void setClientVersion(String value);
    
    
}