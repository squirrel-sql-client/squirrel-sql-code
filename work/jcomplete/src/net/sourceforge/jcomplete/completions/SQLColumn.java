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
 * created by cse, 24.09.2002 16:00:59
 */
package net.sourceforge.jcomplete.completions;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;

/**
 * a completion suggesting column names
 */
public class SQLColumn extends SQLCompletion
{
    public static final String TABLE = "TABLE";
    public static final String[] PROPERTIES = {TABLE};

    private SQLSchema.Table table;
    private String column;
    private String alias;

    private boolean isRepeatable = true;
    private SQLStatement statement;

    public SQLColumn(SQLStatement statement,  int start)
    {
        super(start);
        this.statement = statement;
    }

    public SQLColumn(SQLStatement statement, int start, int end)
    {
        super(start);
        this.statement = statement;
        setEndPosition(end);
    }

    public SQLColumn(SQLStatement statement)
    {
        super();
        this.statement = statement;
    }

    public void setAlias(String alias, int pos)
    {
        this.alias = alias;
        this.table = statement.getTableForAlias(alias);
        setEndPosition(pos+1);
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setColumn(String name, int pos)
    {
        column = name;
        setEndPosition(pos);
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public String getColumn()
    {
        return column;
    }

    public SQLSchema.Table getTable()
    {
        return table;
    }

    public boolean hasTable()
    {
        return table != null;
    }

    public boolean hasAlias()
    {
        return alias != null;
    }

    public SQLStatement getStatement()
    {
        return statement;
    }

    public String getText()
    {
        String text;
        if(alias != null)
            text = alias+"."+column;
        else if(table != null)
            text = table+"."+column;
        else
            text = column;

        if(hasInsertPosition()) {
            int oldDataPos = endPosition - startPosition;
            return text.substring(oldDataPos, text.length());
        } else {
            return text;
        }
    }

    public String[] getCompletions()
    {
        //try to handle alias as alias
        SQLSchema.Table table = getStatement().getTableForAlias(alias);

        //it could also be a table name
        if(table == null)
            table = getStatement().getTable(null, null, alias);

        if(table != null)
              return table.getColumns(column);
        else
            return EMPTY_RESULT;
    }

    public void setRepeatable(boolean repeatable)
    {
        isRepeatable = repeatable;
    }

    public boolean isRepeatable()
    {
        return isRepeatable;
    }
}
