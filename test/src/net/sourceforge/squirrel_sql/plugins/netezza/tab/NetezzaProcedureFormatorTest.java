/*
 * Copyright (C) 2009 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.netezza.tab;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NetezzaProcedureFormatorTest
{
	private NetezzaProcedureFormator classUnderTest = null;
	
	private static final String SQL = 
		"CREATE OR REPLACE PROCEDURE num() RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC/n DECLARE/n	" +
		"n NUMERIC;BEGIN/n	n := 2147483647;RAISE NOTICE 'n is %', n;n := 2147483647 + 1;" +
		"RAISE NOTICE 'n is %', n;n := 2147483647::numeric + 1;RAISE NOTICE 'n is %', n;" +
		"n := 2147483647::bigint + 1;RAISE NOTICE 'n is %', n;n := 2147483647;n := n + 1;" +
		"RAISE NOTICE 'n is %', n;END;END_PROC;";
	
	private static final String SQL2 = 
		  "create or replace procedure EXEC_NZPLSQL_BLOCK(TEXT) returns BOOLEAN LANGUAGE NZPLSQL AS " +
		  "BEGIN_PROC/nDECLARE lRet BOOLEAN;DECLARE sid INTEGER;DECLARE nm varchar;DECLARE cr varchar;BEGIN  " +
		  "/nsid := current_sid;nm := 'any_block' || sid || '()';cr = 'CREATE OR REPLACE PROCEDURE ' || " +
		  "nm ||  /n' RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC '  /n|| $1 || ' END_PROC';EXECUTE IMMEDIATE " +
		  "cr;EXECUTE IMMEDIATE 'SELECT ' || nm;EXECUTE IMMEDIATE 'DROP PROCEDURE ' || nm;RETURN TRUE;END;" +
		  "END_PROC;";		
	
	@Before
	public void setup() {
		classUnderTest = new NetezzaProcedureFormator(";");
	}
	
	@After
	public void teardown() {
		classUnderTest = null;
	}
	
	/**
	 * This test should produce output that looks like this:
	 * 
		CREATE OR REPLACE PROCEDURE num() RETURNS BOOL LANGUAGE NZPLSQL AS
		BEGIN_PROC
		DECLARE
			n NUMERIC;
		BEGIN
			n := 2147483647; 
			RAISE NOTICE 'n is %', n; 
			n := 2147483647 + 1; 
			RAISE NOTICE 'n is %', n; 
			n := 2147483647::numeric + 1; 
			RAISE NOTICE 'n is %', n; 
			n := 2147483647::bigint + 1; 
			RAISE NOTICE 'n is %', n; 
			n := 2147483647; 
			n := n + 1; 
			RAISE NOTICE 'n is %', n; 
		END;
		END_PROC;
	 *  
	 */
	@Test
	public void testformat() {
		String newSql = classUnderTest.reformat(SQL);
		//System.err.println("formatted query: \n"+newSql);
		String[] parts = newSql.split("\n");
		assertEquals(18, parts.length);
	}
	/**
	 * This test should produce output that looks like this:
	 * 
		CREATE OR REPLACE PROCEDURE exec_nzplsql_block(text) RETURNS BOOLEAN
		LANGUAGE NZPLSQL AS
		BEGIN_PROC
			DECLARE lRet BOOLEAN;
			DECLARE sid INTEGER;
			DECLARE nm varchar;
			DECLARE cr varchar;
		BEGIN
			sid := current_sid;
			nm := 'any_block' || sid || '()';
			cr = 'CREATE OR REPLACE PROCEDURE ' || nm || ' RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC ' || $1 || ' END_PROC';
			EXECUTE IMMEDIATE cr;
			EXECUTE IMMEDIATE 'SELECT ' || nm;
			EXECUTE IMMEDIATE 'DROP PROCEDURE ' || nm;
			RETURN TRUE;
		END;
		END_PROC; 
	 */
	@Test
	public void testformat2() {
		String newSql = classUnderTest.reformat(SQL2);
		//System.err.println("formatted query: \n"+newSql);
		String[] parts = newSql.split("\n");
		assertEquals(20, parts.length);
	}

	
}
