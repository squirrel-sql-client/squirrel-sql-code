/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * Contains information about an SQL statement that was executed.
 */
public class StatementEvent {
    
    /** the type that indicates a create table statement */
    public static final int CREATE_TABLE_TYPE = 0;

    /** the type that indicates a create index statement */
    public static final int CREATE_INDEX_TYPE = 1;
    
    public static final int CREATE_FOREIGN_KEY_TYPE = 2;
    
    public static final int INSERT_RECORD_TYPE = 3;
    
    /** the statement */
    private String statement = null;
    
    /** any bind variable values.  only used when type = INSERT_RECORD_TYPE */
    private String[] bindValues = null;
    
    /** the type of the statement */
    private int statementType = -1;
    
    /**
     * 
     * @param aStatement
     * @param type
     */
    public StatementEvent(String aStatement, int type) {
        statement = aStatement;
        statementType = type;
    }

    /**
     * @param statement The statement to set.
     */
    public void setStatement(String statement) {
        this.statement = statement;
    }

    /**
     * @return Returns the statement.
     */
    public String getStatement() {
        return statement;
    }

    /**
     * @param statementType The statementType to set.
     */
    public void setStatementType(int statementType) {
        this.statementType = statementType;
    }

    /**
     * @return Returns the statementType.
     */
    public int getStatementType() {
        return statementType;
    }

    /**
     * @param bindValues The bindValues to set.
     */
    public void setBindValues(String[] bindValues) {
        this.bindValues = bindValues;
    }

    /**
     * @return Returns the bindValues.
     */
    public String[] getBindValues() {
        return bindValues;
    }
    
    @Override
    public String toString() {
   	 StringBuilder result = new StringBuilder();
   	 result.append("Statement (");
   	 result.append(statement);
   	 result.append(")");
   	 if (bindValues != null) {
   		 result.append(", bind values = \n");
   		 int count = 1;
   		 for (String value : bindValues) {
   			 result.append("(");
   			 result.append("length=");
   			 result.append(value.length());
   			 result.append(") ");
   			 result.append(":");
   			 result.append(count++);
   			 result.append("=");
   			 result.append(value);
   			 result.append("\n");
   		 }
   	 }
   	 return result.toString();
    }
}
