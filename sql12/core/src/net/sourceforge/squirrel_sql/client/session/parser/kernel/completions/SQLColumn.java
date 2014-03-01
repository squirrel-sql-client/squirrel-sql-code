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
 * @version $Id: SQLColumn.java,v 1.1 2004-04-04 10:36:30 colbell Exp $
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserLogger;

/**
 * a completion suggesting column names
 */
public class SQLColumn extends SQLCompletion
{
    private String name;
    private String qualifier;

    private boolean isRepeatable = true;
    private SQLStatementContext parent;
    private int namePos = NO_POSITION;

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

    public void setQualifier(String alias, int pos)
    {
        this.qualifier = alias;
        this.namePos = pos+alias.length()+1;
        setEndPosition(namePos);
        ParserLogger.log("setAlias: s="+startPosition+" e="+endPosition);
    }

    public void setQualifier(String alias)
    {
        this.qualifier = alias;
    }

    public String getQualifier()
    {
        return qualifier;
    }

    public void setColumn(String name, int pos)
    {
        this.name = name;
        this.namePos = pos;
        setEndPosition(pos+name.length()-1);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /**
     * check whether a table assignment exists at the given cursor position. A table assignment
     * exists in the following cases:<br>
     * <ul><li>if the parent statement has only one table</li>
     * <li>if the cursor is placed immediately after the qualifier separator. It does not
     * exist if the column is not qualified (by alias or table name), or if the cursor is placed
     * inside the qualifier.</li>
     * @param position the caret position
     * @return whether a table assignment exists
     */
    public boolean hasTable(int position)
    {
        return
              (qualifier == null &&  parent.getStatement().getTable() != null) ||
              (qualifier != null &&
              position >= namePos && position <= endPosition &&
              parent.getStatement().getTableForAlias(qualifier) != null);
    }

    public SQLStatement getStatement()
    {
        return parent.getStatement();
    }

    public String getText()
    {
        String text = qualifier != null ? qualifier+"."+name : name;

        if(hasTextPosition()) {
            int oldDataPos = endPosition - startPosition;
            return text.substring(oldDataPos, text.length());
        } else {
            return text;
        }
    }

    /**
     * @return true if the name is set
     */
    protected boolean isConcrete()
    {
        return name != null;
    }

    public String getText(int position)
    {
        return getText(position, name);
    }

    public String getText(int position, String option)
    {
        if(position == endPosition) {
            return option;
        }
        else if(mustReplace(position) || isOther(position)) {
            return qualifier != null ? qualifier+"."+option : option;
        }
        else {
            String text = qualifier != null ? qualifier+"."+option : option;
            int oldDataPos = endPosition - position;
            return text.substring(oldDataPos, text.length());
        }
    }

    // check if this completion request is outside the original definition point
    private boolean isOther(int position)
    {
        return endPosition == NO_LIMIT || position < startPosition || position > endPosition;
    }

    public String[] getCompletions(int position)
    {
        SQLSchema.Table table = null;

        if(qualifier != null) {
            // try as an alias
            table = getStatement().getTableForAlias(qualifier);

            // could also be a table name
            if(table == null) table = getStatement().getTable(null, null, qualifier);
        }
        else
            // see if its a one-table statement
            table = getStatement().getTable();

        // now match the columns
        if(table != null) {
            String col = null;
            if(name != null && position > namePos) {
                col = position <= endPosition ? name.substring(0, position-namePos) : name;
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
        return name != null && position >= startPosition && position <= endPosition;
    }
}
