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
 *
 * @version $Id: SQLColumn.java,v 1.5 2002-10-10 22:33:49 csell Exp $
 */
package net.sourceforge.jcomplete.completions;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;

/**
 * a completion suggesting column names
 */
public class SQLColumn extends SQLCompletion
{
    private String column;
    private String alias;

    private boolean isRepeatable = true;
    private SQLStatementContext parent;
    private int afterSeparatorPos = NO_POSITION;

    public SQLColumn(SQLStatementContext parent,  int start)
    {
        super(start);
        this.parent = parent;
    }

    public SQLColumn(SQLStatementContext parent, int start, int end)
    {
        super(start);
        this.parent = parent;
        setEndPosition(end);
    }

    public SQLColumn(SQLStatementContext parent)
    {
        super();
        this.parent = parent;
    }

    public void setAlias(String alias, int pos)
    {
        this.alias = alias;
        this.afterSeparatorPos = pos+alias.length()+1;
        setEndPosition(afterSeparatorPos);
        System.out.println("setAlias: s="+startPosition+" e="+endPosition);
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
        setEndPosition(pos+name.length()-1);
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
        return alias != null ? parent.getStatement().getTableForAlias(alias) : null;
    }

    public boolean hasTable(int position)
    {
        return position >= afterSeparatorPos && position <= endPosition && getTable() != null;
    }

    public boolean hasAlias()
    {
        return alias != null;
    }

    public SQLStatement getStatement()
    {
        return parent.getStatement();
    }

    public String getText()
    {
        String text = alias != null ? alias+"."+column : column;

        if(hasTextPosition()) {
            int oldDataPos = endPosition - startPosition;
            return text.substring(oldDataPos, text.length());
        } else {
            return text;
        }
    }

    public String getText(int position)
    {
        return getText(position, column);
    }

    public String getText(int position, String option)
    {
        if(position == endPosition) {
            return option;
        }
        else if(mustReplace(position) || isOther(position)) {
            return alias != null ? alias+"."+option : option;
        }
        else {
            String text = alias != null ? alias+"."+option : option;
            int oldDataPos = endPosition - position;
            return text.substring(oldDataPos, text.length());
        }
    }

    private boolean isOther(int position)
    {
        return position < startPosition || position > endPosition;
    }

    public String[] getCompletions(int position)
    {
        //try to treat alias as alias
        SQLSchema.Table table = getStatement().getTableForAlias(alias);

        //it could also be a table name
        if(table == null)
            table = getStatement().getTable(null, null, alias);

        if(table != null) {
            String col = null;
            if(position > afterSeparatorPos && column != null) {
                col = position <= endPosition ? column.substring(0, position-afterSeparatorPos) : column;
            }
            String[] result = table.getColumns(col);
            return (col != null && result.length == 1 && result[0].length() == col.length()) ?
                EMPTY_RESULT : result;  //no need to return if completion is identical
        }
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

    public boolean mustReplace(int position)
    {
        return column != null && position >= startPosition && position <= endPosition;
    }
}
