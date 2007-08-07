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
 * created by cse, 10.10.2002 14:29:20
 *
 * @version $Id: SQLWhere.java,v 1.2 2007-08-07 01:02:15 manningr Exp $
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * a completion which represents the WHERE clause inside SELECT, UPDATE or DLETE
 * statements
 */
public class SQLWhere extends SQLCompletion implements SQLStatementContext
{
    private SQLStatement statement;
    private List<Completion> children = new ArrayList<Completion>();

    public SQLWhere(SQLStatement statement, int startPosition)
    {
        super(startPosition);
        this.statement = statement;
        setEndPosition(NO_LIMIT);
    }

    public void setEndPosition(int position)
    {
        statement.setEndPosition(position);
        super.setEndPosition(position);
    }

    public Completion getCompletion(int position)
    {
        if(super.getCompletion(position) != null) {
            Iterator<Completion> it = children.iterator();
            while(it.hasNext()) {
                Completion comp = it.next();
                if((comp = comp.getCompletion(position)) != null)
                    return comp;
            }
            SQLColumn col = new SQLColumn(this, position);
            col.setRepeatable(false);
            return col;
        }
        return null;
    }

    public SQLStatement getStatement()
    {
        return statement;
    }

    public void setSqlSchema(SQLSchema schema)
    {
        //schema should be identical to the statement. Ignore
    }

    public void addContext(SQLStatementContext context)
    {
        context.setSqlSchema(statement);
        children.add(context);
    }

    public void addColumn(SQLColumn column)
    {
        children.add(column);
    }
}
