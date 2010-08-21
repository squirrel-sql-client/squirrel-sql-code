/*
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.SQLStatementContext
 * 
 * created by cse, 10.10.2002 16:49:35
 *
 * Copyright (c) 2002 DynaBEAN Consulting, all rights reserved
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;


/**
 * a context which gives access to the nearest statement
 */
public interface SQLStatementContext extends Completion
{
    SQLStatement getStatement();
    void setSqlSchema(SQLSchema schema);
    void addContext(SQLStatementContext context);
    void addColumn(SQLColumn column);
}
