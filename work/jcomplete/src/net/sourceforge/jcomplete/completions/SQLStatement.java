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
 * created 24.09.2002 12:27:12
 */
package net.sourceforge.jcomplete.completions;

import java.util.*;

import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.SQLSchema;

/**
 * a completion representing a full SQl statement. This object servers only
 * as a container for subelements, thus constituting a completion context.
 */
public class SQLStatement extends SQLCompletion implements SQLSchema
{
    private List children;
    protected SQLSchema sqlSchema;

    public SQLStatement(int start)
    {
        super(start);
    }

    /**
     * @param offset the position at which the completion should be inserted
     * @return the available completion
     */
    public SQLCompletion getCompletion(int offset)
    {
        if(super.getCompletion(offset) != null) {
            Iterator it = getChildren();
            while(it.hasNext()) {
                SQLCompletion c = ((SQLCompletion)it.next()).getCompletion(offset);
                if(c != null) return c;
            }
        }
        return null;
    }

    public void setSqlSchema(SQLSchema handler)
    {
        this.sqlSchema = handler;
    }

    public void addChild(SQLCompletion child)
    {
        if(children == null) children = new ArrayList();
        children.add(child);
    }

    public void addStatement(SQLStatement statement)
    {
        addChild(statement);
        statement.setSqlSchema(this);
    }

    public Iterator getChildren()
    {
        return children != null ? children.iterator() : Collections.EMPTY_LIST.iterator();
    }

    public void setEndPosition(int offset)
    {
        super.setEndPosition(offset);
        if(sqlSchema instanceof SQLStatement)
            ((SQLStatement)sqlSchema).setEndPosition(offset);
    }

    public boolean setTable(SQLTable table)
    {
        return setTable(table.catalog, table.schema, table.name, table.alias);
    }

    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        return sqlSchema.setTable(catalog, schema, name, alias);
    }

    public Table getTable(String catalog, String schema, String name)
    {
        return sqlSchema.getTable(catalog, schema, name);
    }

    public List getTables(String catalog, String schema, String name)
    {
        return sqlSchema.getTables(catalog, schema, name);
    }

    public Table getTableForAlias(String alias)
    {
        return sqlSchema.getTableForAlias(alias);
    }
}
