package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import junit.framework.Assert;

/**
 * A helper class for testing components that manipulate SQL.
 * 
 * @author mannignr
 */
public class SQLUtil {
    
    private static int genericSQLCount = 0;
    
    public static String getGenericSQLScript() {
        StringBuffer result = new StringBuffer();
        result.append(GenericSQL.CREATE_STUDENT);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_COURSES);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_PROFESSOR);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_TAKE);
        result.append("\n\n");
        result.append(GenericSQL.CREATE_TEACH);
        result.append("\n\n");
        result.append(GenericSQL.STUDENTS_NOT_TAKING_CS112);
        result.append("\n\n");
        // Don't forget to set this to the number of statements in result
        genericSQLCount = 6;
        return result.toString();
    }

    public static void checkQueryTokenizer(IQueryTokenizer qt, 
                                           int stmtCount) 
    {
        int count = 0;
        while (qt.hasQuery()) {
            count++;
            System.out.println(" query: "+qt.nextQuery());
        }
        Assert.assertEquals(stmtCount, count);                
    }    
    
    /**
     * @param genericSQLCount the genericSQLCount to set
     */
    public static void setGenericSQLCount(int genericSQLCount) {
        SQLUtil.genericSQLCount = genericSQLCount;
    }

    /**
     * @return the genericSQLCount
     */
    public static int getGenericSQLCount() {
        return genericSQLCount;
    }
    
    /**
     * Creates a temporary file with the specified SQL statements in it.
     * @param sqls
     * @param deleteOnExit
     * @return
     * @throws IOException
     */
    public static String createSQLFile(List<String> sqls, 
                                       boolean deleteOnExit) 
        throws IOException 
    {
        File f = File.createTempFile("test", ".sql");
        if (deleteOnExit) {
            f.deleteOnExit();
        }
        PrintWriter out = new PrintWriter(new FileWriter(f));
        for (String sql : sqls) {
            out.println(sql);
            out.println();            
        }
        out.close();
        String tmpFilename = f.getAbsolutePath();
        System.out.println("tmpFilename="+tmpFilename);
                
        return tmpFilename;
    }
    
}
