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
package net.sourceforge.jcomplete.test;

import java.io.File;
import java.io.IOException;

import net.sourceforge.jcomplete.parser.Scanner;
import net.sourceforge.jcomplete.parser.Parser;
import net.sourceforge.jcomplete.parser.ErrorStream;

class MyErrorStream extends ErrorStream
{

    protected void SemErr(int n, int line, int col)
    {
        String s;
        super.SemErr(n, line, col);
    }
}

/**
 * test driver to invoke the SquirrelSQL parser
 */
public class Main
{

    public static void main(String args[]) throws IOException
    {
        if (args.length < 1) {
            System.out.println("-- source file needed as argument");
            System.exit(-1);
        }
        Scanner scanner = new Scanner(new File(args[0]), new MyErrorStream());
        Parser p = new Parser(scanner);
        p.parse();
    }
}
