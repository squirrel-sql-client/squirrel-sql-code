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
 * created by cse, 13.09.2002 23:01:20
 */
package net.sourceforge.jcomplete;

import java.util.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.jcomplete.completions.SQLStatement;
import net.sourceforge.jcomplete.Completion;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.util.ParserThread;

/**
 * a completion handler which drives the SQL parser and offers SQL
 * schema related services.
 */
public class SQLCompletionHandler implements CompletionHandler, SQLSchema
{
    public  List statements;

    private Map tables = new HashMap();
    private TextProvider textProvider;
    private ParserThread parserThread;
    private DatabaseMetaData dbData;

    public SQLCompletionHandler(
          CompletionHandler.ErrorListener errorListener,
          DatabaseMetaData metaData)
    {
        this.dbData = metaData;
        loadSchema(false);
        this.parserThread = new ParserThread(this, errorListener);
    }

    public SQLCompletionHandler(
          CompletionHandler.ErrorListener errorListener,
          Map tableMap)
    {
        this.tables = tableMap;
        this.parserThread = new ParserThread(this, errorListener);
    }

    public Completion getCompletion(int offset)
    {
        Iterator it = statements.iterator();
        while(it.hasNext()) {
            SQLStatement stmt = (SQLStatement)it.next();
            Completion result = stmt.getCompletion(offset);
            if(result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Terminate the parsing thread
     */
    public void end()
    {
        parserThread.reset(null);
    }

    public void begin(TextProvider textProvider, int length)
    {
        this.textProvider = textProvider;
        parserThread.start(textProvider.getChars(0));
    }

    public void textInserted(int offset, int length)
    {
        int endPos = offset + length - 1;
        if(textProvider.atEnd(endPos)) {
            parserThread.accept(textProvider.getChars(offset));
        }
        else {
            parserThread.reset(textProvider.getChars(0));
        }
    }

    public void textRemoved(int offset, int length)
    {
        parserThread.reset(textProvider.getChars(0));
    }

    public List getTables(String catalog, String schema, String name)
    {
        Iterator it = tables.values().iterator();
        List matching = new ArrayList();
        while(it.hasNext()) {
            SQLSchema.Table next = (SQLSchema.Table)it.next();
            if(next.matches(catalog, schema, name))
                matching.add(next);
        }
        return matching;
    }

    public SQLSchema.Table getTableForAlias(String alias)
    {
        return null;
    }

    public SQLSchema.Table getTable(String catalog, String schema, String name)
    {
        return (SQLSchema.Table)tables.get(SQLSchema.Table.createCompositeName(catalog, schema, name));
    }

    private void loadSchema(boolean all)
    {
        if(all)
            loadTablesAndColumns();
        else
            loadTables();
    }

    private void loadTablesAndColumns()
    {
        ResultSet rs = null;
        try {
            SQLSchema.Table tdesc = null;
            List cols = new ArrayList();
            rs = dbData.getColumns(null, null, null, null);
            while(rs.next())
            {
                String catalog = rs.getString(1);
                String schema = rs.getString(2);
                String table = rs.getString(3);
                String column = rs.getString(4);

                if(tdesc == null || !tdesc.equals(catalog, schema, table)) {
                    if(tdesc != null) tdesc.setColumns(cols);
                    cols.clear();
                    tdesc = new SQLSchema.Table(catalog, schema, table);
                    tables.put(tdesc.getCompositeName(), tdesc);
                }
                cols.add(column);
            }
            if(tdesc != null) tdesc.setColumns(cols);
        }
        catch(SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        finally {
            if(rs != null) try {rs.close();} catch(SQLException e){}
        }
    }
    private void loadTables()
    {
        ResultSet rs = null;
        try {
            SQLSchema.Table tdesc = null;
            rs = dbData.getTables(null, null, null, null);
            while(rs.next())
            {
                String catalog = rs.getString(1);
                String schema = rs.getString(2);
                String table = rs.getString(3);

                tdesc = new SQLSchema.Table(catalog, schema, table, dbData);
                tables.put(tdesc.getCompositeName(), tdesc);
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        finally {
            if(rs != null) try {rs.close();} catch(SQLException e){}
        }
    }
}
