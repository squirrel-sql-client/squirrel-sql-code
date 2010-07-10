/*
 * net.sourceforge.jcomplete.completions.SQLStatementContext
 * 
 * created by cse, 10.10.2002 16:49:35
 *
 * Copyright (c) 2002 DynaBEAN Consulting, all rights reserved
 */
package net.sourceforge.jcomplete.completions;

import net.sourceforge.jcomplete.Completion;
import net.sourceforge.jcomplete.SQLSchema;

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
