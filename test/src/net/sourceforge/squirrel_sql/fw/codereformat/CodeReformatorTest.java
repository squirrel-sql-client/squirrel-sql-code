package net.sourceforge.squirrel_sql.fw.codereformat;
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
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.ANSI_SQL_92_PROCEDURE;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.ANSI_SQL_92_PROCEDURE_READABLE;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_COURSES;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_PROFESSOR;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_STUDENT;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_TAKE;
import static net.sourceforge.squirrel_sql.fw.sql.GenericSQL.CREATE_TEACH;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.ANON_PROC_EXEC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_FUNCTION_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_OR_REPLACE_STORED_PROC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.CREATE_STORED_PROC;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.NO_SEP_SLASH_SQL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SELECT_DUAL;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.SELECT_DUAL_2;
import static net.sourceforge.squirrel_sql.fw.sql.OracleSQL.UPDATE_TEST;
import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.DB2SQL;
import net.sourceforge.squirrel_sql.fw.sql.GenericSQL;
import net.sourceforge.squirrel_sql.fw.sql.OracleSQL;

public class CodeReformatorTest extends BaseSQuirreLTestCase {

    private static CommentSpec[] COMMENT_SPECS =
        new CommentSpec[]
        {
            new CommentSpec("/*", "*/"),
            new CommentSpec("--", "\n")
        };    
        
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Just try to run as much SQL as possible through the reformator to check
     * that we don't ever throw an IllegalArgumentException - meaning that the 
     * code reformator failed to produce equivalent SQL.
     */
    public void testReformat() {
        CodeReformator c = new CodeReformator(";", COMMENT_SPECS);
        // We know this fails - Bug# 1700093
        c.reformat(DB2SQL.insertSubSelectSQL);

        // Generic SQL
        c.reformat(CREATE_STUDENT);
        c.reformat(CREATE_COURSES);
        c.reformat(CREATE_PROFESSOR);
        c.reformat(CREATE_TAKE);
        c.reformat(CREATE_TEACH);
        c.reformat(GenericSQL.STUDENTS_NOT_TAKING_CS112);
        c.reformat(ANSI_SQL_92_PROCEDURE);
        c.reformat(ANSI_SQL_92_PROCEDURE_READABLE);
        c.reformat(SELECT_DUAL);
        
        // Oracle SQL
        c.reformat(SELECT_DUAL);
        c.reformat(SELECT_DUAL_2);
        c.reformat(CREATE_STORED_PROC);
        c.reformat(CREATE_OR_REPLACE_STORED_PROC);
        c.reformat(ANON_PROC_EXEC);
        c.reformat(UPDATE_TEST);
        c.reformat(OracleSQL.STUDENTS_NOT_TAKING_CS112);
        c.reformat(NO_SEP_SLASH_SQL);
        c.reformat(CREATE_FUNCTION_SQL);
       
    }

}
