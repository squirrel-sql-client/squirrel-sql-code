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

import java.text.CharacterIterator;
import java.util.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.jcomplete.parser.Scanner;
import net.sourceforge.jcomplete.parser.Parser;
import net.sourceforge.jcomplete.parser.ErrorStream;
import net.sourceforge.jcomplete.completions.SQLStatement;
import net.sourceforge.jcomplete.Completion;
import net.sourceforge.jcomplete.CompletionHandler;

/**
 * a completion handler which drives the SQL parser and offers SQL
 * schema related services.
 */
public class SQLCompletionHandler implements CompletionHandler, SQLSchema
{
    public static final String PARSER_THREAD_NM = "SQLParserThread";

    private Map aliasMap = new HashMap();
    private Map tables = new HashMap();

    private List statements;
    private Errors errors;
    private IncrementalBuffer scannerBuffer;
    private ParserThread parserThread;
    private DatabaseMetaData dbData;

    public SQLCompletionHandler(CompletionHandler.ErrorListener errorListener, DatabaseMetaData metaData)
    {
        dbData = metaData;
        loadMetaData();
        errors = new Errors(errorListener);
    }

    public SQLCompletionHandler(CompletionHandler.ErrorListener errorListener, Map tableMap)
    {
        tables = tableMap;
        errors = new Errors(errorListener);
    }

    /**
     * @return INCR_FWD, because the parser only supports forward incrementation
     */
    public int getIncrementType()
    {
        return INCR_FWD;
    }

    public Completion getCompletion(int offset)
    {
        Iterator it = statements.iterator();
        while(it.hasNext()) {
            SQLStatement stmt = (SQLStatement)it.next();
            Completion result = stmt.getCompletion(offset);
            if(result != null) return result;
        }
        return null;
    }

    /**
     * Begin parsing the underlying text. This will spawn a separate parsing thread,
     * which must be terminated by calling the {@link #end} method.
     * @param chars initial characters, or <em>null</em>
     */
    public void begin(CharacterIterator chars)
    {
        scannerBuffer = new IncrementalBuffer(chars);
        parserThread = new ParserThread(scannerBuffer);
        parserThread.start();
    }

    /**
     * Terminate the parsing thread
     */
    public void end()
    {
        parserThread.reset(null);
    }

    public void invalidate(CharacterIterator iterator, boolean forward)
    {
        //backward changes: revert parser
        if(forward == false) {
            scannerBuffer = new IncrementalBuffer(iterator);
            parserThread.reset(scannerBuffer);
        }
        else
            scannerBuffer.notify(iterator);
    }

    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        SQLSchema.Table table = (SQLSchema.Table)tables.get(SQLSchema.Table.createCompositeName(catalog, schema, name));
        if(table == null) return false;
        if(alias != null)
            aliasMap.put(alias, table.clone(alias));
        return true;
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
        return (SQLSchema.Table)aliasMap.get(alias);
    }

    public SQLSchema.Table getTable(String catalog, String schema, String name)
    {
        return (SQLSchema.Table)tables.get(SQLSchema.Table.createCompositeName(catalog, schema, name));
    }

    private void loadMetaData()
    {
        ResultSet rs = null;
        try {
            SQLSchema.Table tdesc = null;
            rs = dbData.getColumns(null, null, null, null);
            while(rs.next())
            {
                String catalog = rs.getString(1);
                String schema = rs.getString(2);
                String table = rs.getString(3);
                String column = rs.getString(4);

                if(tdesc == null || !tdesc.equals(catalog, schema, table)) {
                    tdesc = new SQLSchema.Table(catalog, schema, table);
                    tables.put(tdesc.getCompositeName(), tdesc);
                }
                tdesc.addColumn(column);
            }
        }
        catch(SQLException e) {}
        finally {
            try {rs.close();} catch(SQLException e){}
        }
    }

    private class ParserThread extends Thread
    {
        private IncrementalBuffer buffer;

        public ParserThread(IncrementalBuffer buffer)
        {
            super(PARSER_THREAD_NM);
            this.buffer = buffer;
        }

        public void run()
        {
            while(buffer != null) {
                System.out.println("begin parse");

                SQLCompletionHandler.this.errors.reset();
                Scanner scanner = new Scanner(buffer, SQLCompletionHandler.this.errors);

                Parser parser = new Parser(scanner);
                parser.rootSchema = SQLCompletionHandler.this;
                SQLCompletionHandler.this.statements = parser.statements;
                parser.parse();

                System.out.println("end parse");
            }
        }

        public void reset(IncrementalBuffer buffer)
        {
            IncrementalBuffer oldBuffer = this.buffer;
            this.buffer = buffer;
            oldBuffer.eof();
        }
    }

    /**
     * error stream which simply saves the error codes and line info
     * circularily in an array of fixed size
     */
    private class Errors extends ErrorStream
    {
        private int [][] errorStore;
        private int count;
        private CompletionHandler.ErrorListener listener;

        public Errors(CompletionHandler.ErrorListener listener)
        {
            this.listener = listener;
            errorStore = new int [5][3];
        }

        protected void ParsErr(int n, int line, int col)
        {
            errorStore[count][0] = n;
            errorStore[count][1] = line;
            errorStore[count][2] = col;
            count = (count + 1) % 5;
            if(listener != null)
                super.ParsErr(n, line, col);
        }

        protected void SemErr(int n, int line, int col)
        {
            errorStore[count][0] = n;
            errorStore[count][1] = line;
            errorStore[count][2] = col;
            count = (count + 1) % 5;
            if(listener != null) {
                switch (n) {
                    case 0:
                        StoreError(n, line, col, "EOF expected"); break;
                    default:
                        super.SemErr(n, line, col);
                }
            }
        }

        protected void StoreError(int n, int line, int col, String s)
        {
            if(listener != null)
                listener.errorDetected(s, line, col);
        }

        public void reset()
        {
            errorStore = new int [5][3];
        }
    }

    /**
     * This is a Scanner.Buffer implementation which blocks until character data is
     * available. The {@link #read} method is invoked from the background parsing thread.
     * The parsing thread can be terimated by calling the {@link #eof} method on this object
     */
    private static class IncrementalBuffer extends Scanner.Buffer
    {
        private CharacterIterator chars;
        private char current;
        private boolean atEnd;

        IncrementalBuffer(CharacterIterator chars)
        {
            this.atEnd = false;
            this.chars = chars;
            this.current = chars != null ? chars.first() : CharacterIterator.DONE;
        }

        protected synchronized char read()
        {
            if(atEnd) {
                return eof;
            }
            else {
                if(current == CharacterIterator.DONE) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                    }
                }
                if(atEnd) {
                    current = eof;
                    return eof;
                }
                else {
                    char prev = current;
                    current = chars.next();
                    return prev;
                }
            }
        }

        synchronized void eof()
        {
            atEnd = true;
            notify();
        }

        synchronized void notify(CharacterIterator iterator)
        {
            this.chars = iterator;
            this.current = chars != null ? chars.first() : CharacterIterator.DONE;
            notify();
        }

        int getBeginIndex()
        {
            return chars != null ? chars.getBeginIndex() : 0;
        }
    }
}
