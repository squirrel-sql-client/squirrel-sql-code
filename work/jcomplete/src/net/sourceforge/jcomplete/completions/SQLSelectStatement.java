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
 */
package net.sourceforge.jcomplete.completions;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;

/**
 * an SQL select statement
 */
public class SQLSelectStatement extends SQLStatement
{
    private Map aliasMap = new HashMap();
    private int selectListStart, selectListEnd, fromStart, fromEnd, whereStart, whereEnd;

    public SQLSelectStatement(int start)
    {
        super(start);
    }

    public void setSelectListStart(int start)
    {
        System.out.println("entering selectlist: "+start);
        selectListStart = start;
        selectListEnd = 99999;
        setEndPosition(selectListEnd);
    }

    public void setSelectListEnd(int end)
    {
        System.out.println("leaving selectlist: "+end);
        selectListEnd = end;
        setEndPosition(end);
    }

    public void setFromStart(int fromStart)
    {
        System.out.println("entering from: "+fromStart);
        this.fromStart = fromStart;
        this.fromEnd = 99999;
        setEndPosition(fromEnd);
    }

    public void setFromEnd(int fromEnd)
    {
        System.out.println("leaving from: "+fromEnd);
        this.fromEnd = fromEnd;
        setEndPosition(fromEnd);
    }

    public void setWhereStart(int whereStart)
    {
        System.out.println("entering where: "+whereStart);
        this.whereStart = whereStart;
        this.whereEnd = 99999;
        setEndPosition(whereEnd);
    }

    public void setWhereEnd(int whereEnd)
    {
        System.out.println("leaving where: "+whereEnd);
        this.whereEnd = whereEnd;
        setEndPosition(whereEnd);
    }

    public boolean setTable(String catalog, String schema, String name, String alias)
    {
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

    public SQLCompletion getCompletion(int offset)
    {
        SQLCompletion comp = super.getCompletion(offset);
        if(comp != null) return comp;

        if(offset >= selectListStart && offset <= selectListEnd)
            return new SQLColumn(this, offset);
        else if(offset >= fromStart && offset <= fromEnd)
            return new SQLTable(this, offset);
        else if(offset >= whereStart && offset <= whereEnd) {
            SQLColumn col = new SQLColumn(this, offset);
            col.setRepeatable(false);
            return col;
        }
        return null;
    }
}
