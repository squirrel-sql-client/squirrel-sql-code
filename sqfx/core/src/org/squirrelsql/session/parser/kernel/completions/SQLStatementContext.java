/*
 * net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.SQLStatementContext
 * 
 * created by cse, 10.10.2002 16:49:35
 *
 * Copyright (c) 2002 DynaBEAN Consulting, all rights reserved
 */
package org.squirrelsql.session.parser.kernel.completions;

import org.squirrelsql.session.parser.kernel.Completion;
import org.squirrelsql.session.parser.kernel.SQLSchema;


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
