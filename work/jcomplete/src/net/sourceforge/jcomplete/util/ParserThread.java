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
 * created by cse, 07.10.2002 11:57:54
 */
package net.sourceforge.jcomplete.util;

import java.text.CharacterIterator;

import net.sourceforge.jcomplete.SQLCompletionHandler;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.parser.Scanner;
import net.sourceforge.jcomplete.parser.Parser;
import net.sourceforge.jcomplete.parser.ErrorStream;

/**
 * a thread subclass which drives the SQL parser. The thread reads from a buffer
 * which always blocks until data is made available. It can thus be run in the
 * background, parsing the input from the text UI as it arrives.
 *
 * <em>Unfortunately, it depends on the generated parser/scanner and therefore
 * cannot be generalized, unless the generated classes are made to implement public
 * interfaces</em>
 */
public class ParserThread extends Thread
{
    public static final String PARSER_THREAD_NM = "SQLParserThread";

    private IncrementalBuffer buffer;
    private SQLCompletionHandler handler;
    private Errors errors;

    public ParserThread(SQLCompletionHandler handler, CompletionHandler.ErrorListener errorListener)
    {
        super(PARSER_THREAD_NM);
        this.handler = handler;
        this.errors = new Errors(errorListener);
    }

    public void start(CharacterIterator chars)
    {
        buffer = new IncrementalBuffer(chars);
        start();
    }

    public void run()
    {
        while(buffer != null) {
            errors.reset();
            Scanner scanner = new Scanner(buffer, errors);

            Parser parser = new Parser(scanner);
            parser.rootSchema = handler;
            handler.statements = parser.statements;
            System.out.println("begin parse");
            parser.parse();
            System.out.println("end parse");
        }
    }

    /**
     * reset the parser, starting a new parse on the characters given by the iterator
     * @param chars the characters to start parsing from
     */
    public void reset(CharacterIterator chars)
    {
        IncrementalBuffer oldBuffer = this.buffer;
        this.buffer = new IncrementalBuffer(chars);
        this.buffer.append = true;
        oldBuffer.eof();
    }

    /**
     * accept the next character sequence to be parsed
     * @param chars
     */
    public void accept(CharacterIterator chars)
    {
        buffer.waitChars();     //wait for pending chars to be processed
        buffer.accept(chars);   //post new characters
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
        private boolean atEnd, append;

        IncrementalBuffer(CharacterIterator chars)
        {
            this.atEnd = false;
            this.chars = chars;
            this.current = chars != null ? chars.first() : CharacterIterator.DONE;
        }

        /**
         * read the next character. This method is invoked from the parser thread
         * @return the next available character
         */
        protected synchronized char read()
        {
            if(atEnd) {
                return eof;
            }
            else {
                if(current == CharacterIterator.DONE) {
                    if(append) {
                        append = false;
                        return ' ';
                    }
                    if(chars != null) {
                        synchronized(chars) {
                            //System.out.println(">chars.notify");
                            chars.notify(); //tell the UI that we're done
                            //System.out.println("chars.notify>");
                        }
                    }
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
                    //System.out.print(prev);
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

        /**
         * Post a character sequence to be read. Notify the parser thread accordingly. Invoking
         * this method should always be followed by a call to {@link #waitChars} to ensure that
         * the character sequence is not overwritten before it has been fully processed.
         * @param chars the chracters to be read
         */
        synchronized void accept(CharacterIterator chars)
        {
            this.chars = chars;
            this.current = chars != null ? chars.first() : CharacterIterator.DONE;
            notify();
        }

        /**
         * block the current thread until all characters from the current iterator have
         * been processed
         */
        void waitChars()
        {
            if(chars != null && current != CharacterIterator.DONE) {
                synchronized(chars) {
                    try {
                        //System.out.println(">chars.wait");
                        chars.wait();
                        //System.out.println("chars.wait>");
                    }
                    catch (InterruptedException e) {}
                }
            }
        }

        int getBeginIndex()
        {
            return chars != null ? chars.getBeginIndex() : 0;
        }
    }

    /**
     * error stream which simply saves the error codes and line info
     * circularily in an array of fixed size, and notifies a listener
     * if requested
     */
    private static class Errors extends ErrorStream
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
                    case 10:
                        StoreError(n, line, col, "undefined table"); break;
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
}
