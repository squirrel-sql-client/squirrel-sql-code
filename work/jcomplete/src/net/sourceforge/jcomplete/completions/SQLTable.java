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
 * created by cse, 27.09.2002 20:15:39
 */
package net.sourceforge.jcomplete.completions;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;

/**
 * this class represents a table completion, as it appears within the FROM
 * clause.<br>
 * <em>Note: do not confuse with SQLSchema.Table</em>
 */
public class SQLTable extends SQLCompletion
{
    public String catalog;
    public String schema;
    public String name;
    public String alias;

    private SQLStatement statement;

    public SQLTable(SQLStatement statement, int start)
    {
        super(start);
        this.statement = statement;
    }

    public SQLStatement getStatement()
    {
        return statement;
    }

    public SQLSchema.Table[] getCompletions()
    {
        List tables = getStatement().getTables(catalog, schema, name);
        Collections.sort(tables);
        return (SQLSchema.Table[])tables.toArray(new SQLSchema.Table[tables.size()]);
    }

    /**
     * tables are safe to repeat, as they only appear in the from clause
     * @return <em>true</em>
     */
    public boolean isRepeatable()
    {
        return true;
    }
}
