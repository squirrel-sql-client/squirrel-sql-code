/*
 * net.sourceforge.jcomplete.completions.SQLModifyingStatement
 * 
 * created by cse, 11.10.2002 17:14:06
 *
 * Copyright (c) 2002 DynaBEAN Consulting, all rights reserved
 */
package net.sourceforge.jcomplete.completions;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.jcomplete.SQLSchema;
import net.sourceforge.jcomplete.SQLCompletion;
import net.sourceforge.jcomplete.Completion;

/**
 * an SQL statement class representing modyfing statements, i.e.
 * INSERT, UPDATE and DELETE. The important common trait of these statements
 * is that they alow for only one target table to be specified
 */
public class SQLModifyingStatement extends SQLStatement
{
    private SQLTable m_table;
    private int updateListStart = NO_POSITION;
    private int updateListEnd = NO_POSITION;

    public SQLModifyingStatement(int start)
    {
        super(start);
    }

    public List getTables(String catalog, String schema, String name)
    {
        if(name != null || m_table == null) {
            return super.getTables(catalog, schema, name);
        }
        else {
            List tables = super.getTables(m_table.catalog, m_table.schema, m_table.name);
            List result = new ArrayList();
            Iterator it = tables.iterator();
            while(it.hasNext()) {
                Table table = (Table)it.next();
                if(table.matches(catalog, schema, name))
                    result.add(table);
            }
            return result;
        }
    }

    public void addTable(SQLTable table)
    {
        super.addTable(table);
        m_table = table;
    }

    public SQLSchema.Table getTable()
    {
        return getTable(m_table.catalog, m_table.schema, m_table.name);
    }

    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        return super.setTable(catalog, schema, name, alias);
    }

    public void setUpdateListStart(int position)
    {
        System.out.println("updateListStart: "+position);
        updateListStart = position;
        updateListEnd = NO_LIMIT;
    }

    public void setUpdateListEnd(int position)
    {
        updateListEnd = position;
    }

    public Completion getCompletion(int position)
    {
        Completion c = super.getCompletion(position);
        if(c == null) {
            if(position >= updateListStart && position <= updateListEnd) {
                SQLColumn col = new SQLColumn(this, position);
                col.setRepeatable(false);
                return col;
            }
        }
        return c;
    }
}
