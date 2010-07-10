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
 * @version $Id: SQLSelectStatement.java,v 1.7 2002-10-13 18:09:13 csell Exp $
 */
package net.sourceforge.jcomplete.completions;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;
import net.sourceforge.jcomplete.Completion;

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

    private Map aliasMap = new HashMap();
    private int selectListStart, selectListEnd, fromStart, fromEnd;

    private int[][] fieldAreas = new int[3][2]; //FA_xxx

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
        System.out.println("setTable: "+alias+"."+name);
        Table table = sqlSchema.getTable(catalog, schema, name);
        if(table == null) return false;
        if(alias != null)
            aliasMap.put(alias, table.clone(alias));
        return true;
    }

    public List getTables(String catalog, String schema, String name)
    {
        if(aliasMap.size() == 0)
            return sqlSchema.getTables(catalog, schema, name);
        else {
            List tables = sqlSchema.getTables(catalog, schema, name);
            tables.addAll(aliasMap.values());
            return tables;
        }
    }

    public SQLSchema.Table getTableForAlias(String alias)
    {
        SQLSchema.Table table = (SQLSchema.Table)aliasMap.get(alias);
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
}
