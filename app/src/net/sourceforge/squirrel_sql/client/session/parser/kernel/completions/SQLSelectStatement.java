/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 26.09.2002 15:14:45
 *
 * @version $Id: SQLSelectStatement.java,v 1.2 2007-08-20 00:16:17 manningr Exp $
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserLogger;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;


/**
 * an SQL select statement
 */
public class SQLSelectStatement extends SQLStatement
{
    private static final int FA_START = 0;
    private static final int FA_END = 1;

    private static final int FA_GROUPBY = 0;
    private static final int FA_HAVING = 1;
    private static final int FA_ORDERBY = 2;

    private Map<String, Table> aliasMap = new HashMap<String, Table>();
    private int selectListStart, selectListEnd, fromStart, fromEnd;

    private int[][] fieldAreas = new int[3][2]; //FA_xxx

	 private Vector<SQLSelectStatementListener> listeners = 
	     new Vector<SQLSelectStatementListener>();

    public SQLSelectStatement(int start)
    {
        super(start);
    }

    public void setSelectListStart(int start)
    {
        selectListStart = start;
        selectListEnd = NO_LIMIT;
        setEndPosition(selectListEnd);
    }

    public void setSelectListEnd(int end)
    {
        selectListEnd = end;
        setEndPosition(end);
    }

    public void setFromStart(int fromStart)
    {
        this.fromStart = fromStart;
        this.fromEnd = NO_LIMIT;
        setEndPosition(fromEnd);
    }

    public void setFromEnd(int fromEnd)
    {
        this.fromEnd = fromEnd;
        setEndPosition(fromEnd);
    }

    public void setGroupByStart(int start)
    {
        setFieldAreaStart(FA_GROUPBY, start);
    }

    public void setGroupByEnd(int whereEnd)
    {
        setFieldAreEnd(FA_GROUPBY, whereEnd);
    }

    public void setHavingStart(int start)
    {
        setFieldAreaStart(FA_HAVING, start);
    }

    public void setHavingEnd(int whereEnd)
    {
        setFieldAreEnd(FA_HAVING, whereEnd);
    }

    public void setOrderByStart(int start)
    {
        setFieldAreaStart(FA_ORDERBY, start);
    }

    public void setOrderByEnd(int whereEnd)
    {
        setFieldAreEnd(FA_ORDERBY, whereEnd);
    }

    private void setFieldAreaStart(int fa, int start)
    {
        fieldAreas[fa][FA_START] = start;
        fieldAreas[fa][FA_END] = NO_LIMIT;
        setEndPosition(NO_LIMIT);
    }

    private void setFieldAreEnd(int fa, int end)
    {
        fieldAreas[fa][FA_END] = end;
        setEndPosition(end);
    }

    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        ParserLogger.log("setTable: "+alias+"."+name);
        Table table = sqlSchema.getTable(catalog, schema, name);
        if(table == null) return false;
        if(alias != null)
		  {
            aliasMap.put(alias, table.clone(alias));
			   fireAliasDefined(name, alias);
		  }
        return true;
    }

    public List<Table> getTables(String catalog, String schema, String name)
    {
        if(aliasMap.size() == 0)
            return sqlSchema.getTables(catalog, schema, name);
        else {
            List<Table> tables = sqlSchema.getTables(catalog, schema, name);
            tables.addAll(aliasMap.values());
            return tables;
        }
    }

    public SQLSchema.Table getTableForAlias(String alias)
    {
        SQLSchema.Table table = aliasMap.get(alias);
        return table != null ? table : sqlSchema.getTableForAlias(alias);
    }

    public Completion getCompletion(int offset)
    {
        Completion comp = super.getCompletion(offset);
        if(comp != null) return comp;

        if(offset >= selectListStart && offset <= selectListEnd)
            return new SQLColumn(this, offset, offset);
        else if(offset >= fromStart && offset <= fromEnd)
            return new SQLTable(this, offset, offset);
        else {
            for(int i=0; i<fieldAreas.length; i++) {
                if(offset >= fieldAreas[i][FA_START] && offset <= fieldAreas[i][FA_END]) {
                    SQLColumn col = new SQLColumn(this, offset, offset);
                    col.setRepeatable(false);
                    return col;
                }
            }
        }
        return null;
    }

	 public void addListener(SQLSelectStatementListener l)
	 {
        listeners.add(l);
	 }

 	 public void removeListener(SQLSelectStatementListener l)
    {
	    listeners.remove(l);
	 }

	 private void fireAliasDefined(String tableName, String aliasName)
	 {
    	 SQLSelectStatementListener[] clone = 
    	     listeners.toArray(new SQLSelectStatementListener[listeners.size()]);

		 for (int i = 0; i < clone.length; i++)
		 {
			 clone[i].aliasDefined(tableName, aliasName);
		 }
	 }

}
