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
 *
 * @version $Id: SQLStatement.java,v 1.2 2007-08-07 01:02:15 manningr Exp $
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;


import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;

import java.util.*;


/**
 * a completion representing a full SQl statement. This object servers only
 * as a container for subelements, thus constituting a completion context.
 */
public class SQLStatement extends SQLCompletion implements SQLSchema, SQLStatementContext
{
    private SortedSet<Completion> children;
    protected SQLSchema sqlSchema;
    private static final List<Completion> EMPTY_LIST = 
        new ArrayList<Completion>();

    public SQLStatement(int start)
    {
        super(start);
    }

    /**
     * @param position the position at which the completion should be inserted
     * @return the available completion
     */
    public Completion getCompletion(int position)
    {
        if(isEnclosed(position)) {
            Iterator<Completion> it = getChildren();
            while(it.hasNext()) {
                Completion c = it.next().getCompletion(position);
                if(c != null) return c;
            }
        }
        return null;
    }

    public void setSqlSchema(SQLSchema schema)
    {
        if(schema == this) throw new RuntimeException("internal error: recursive schema");
        this.sqlSchema = schema;
    }

    protected void addChild(Completion child)
    {
        if(children == null) children = new TreeSet(new ChildComparator());
        children.add(child);
    }

    public void addContext(SQLStatementContext context)
    {
        context.setSqlSchema(this);
        addChild(context);
    }

    public void setEndPosition(int offset)
    {
        super.setEndPosition(offset);
        if(sqlSchema instanceof SQLStatement)
            ((SQLStatement)sqlSchema).setEndPosition(offset);
    }

    public void addTable(SQLTable table)
    {
        addChild(table);
    }

    public boolean setTable(SQLTable table)
    {
        return setTable(table.catalog, table.schema, table.name, table.alias);
    }

    /**
     * take note of table usage, validating it against the underlying schema.
     * @param catalog catalog name (otional)
     * @param schema schema name (optional)
     * @param name table name (required)
     * @param alias alias (unused)
     * @return true if the table is valid
     */
    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        return sqlSchema.getTable(catalog, schema, name) != null;
    }

    public Table getTable(String catalog, String schema, String name)
    {
        return sqlSchema.getTable(catalog, schema, name);
    }

    public List<Table> getTables(String catalog, String schema, String name)
    {
        return sqlSchema.getTables(catalog, schema, name);
    }

    public Table getTableForAlias(String alias)
    {
        return sqlSchema.getTableForAlias(alias);
    }

    public void addColumn(SQLColumn column)
    {
        addChild(column);
    }

    public SQLStatement getStatement()
    {
        return this;
    }

    protected Iterator<Completion> getChildren()
    {
        return children != null ? children.iterator() : EMPTY_LIST.iterator();
    }

    /**
     * @return the unique table assigned to this statement, or <em>null</em> if this statement
     * can hold multiple tables, or no table was assigned. By default, <em>null</em>.
     */
    public Table getTable()
    {
        return null;
    }
}
