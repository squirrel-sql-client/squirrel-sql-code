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
public interface OracleSQL {

    public final static String SELECT_DUAL = 
        "select 'X' from dual;";        
    
    public final static String SELECT_DUAL_2 = 
        "select 1/100 from dual;";        
    
    public final static String CREATE_STORED_PROC = 
        " create procedure fooproc (Person_name IN varchar2) \n" +
        "AS \n" +
        "BEGIN \n" +
        "    insert into testdate \n" +
        "            (mydate) \n" +
        "    values \n" +
        "            (sysdate); \n" +
        "END; \n" +
        " \n" +
        "/ \n\n;";       
    
    public final static String CREATE_OR_REPLACE_STORED_PROC = 
        "create or replace procedure fooproc (Person_name IN varchar2) \n" +
        "AS \n" +
        "BEGIN \n" +
        "    insert into testdate \n" +
        "            (mydate) \n" +
        "    values \n" +
        "            (sysdate); \n" +
        "END; \n" +
        " \n" +
        "/ \n\n;";       
    
    public final static String ANON_PROC_EXEC = 
        "declare \n" +
        "v_foo number(10); \n" +
        "v_bar number(10); \n" +
        "begin \n" +
        "   for usr in \n" +
        "       (select * from foo_bar where user like 'TST%') \n" +
        "       loop \n" +
        "       begin \n" +
        "            update STUDENT set AGE = 13 where SNO = 100000; \n" +
        "       end; \n" +
        "   end loop; \n" +
        "end; \n" +
        "/ \n\n";        
    
    public final static String ANON_PROC_EXEC_2 = 
        "begin " +
        "   for usr in " +
        "       (select mychar from test where mychar like 'TST%') " +
        "       loop " +
        "       begin " +
        "            update test set mychar = 'foo'; " +
        "       end; " +
        "   end loop; " +
        "end; \n" +
        "/ \n\n";        
    
    public final static String CREATE_OR_REPLACE_PACKAGE_SQL = 
   	 "CREATE OR REPLACE PACKAGE tmk IS \n" +
   	 "  PROCEDURE test; \n" +
   	 "END tmk; \n" +
   	 "/ \n";
    
    public final static String CREATE_OR_REPLACE_PACKAGE_BODY_SQL =
   	 "CREATE OR REPLACE PACKAGE BODY tmk IS \n" +
   	 "  PROCEDURE test IS \n" +
   	 "    rec_tmp atmk%ROWTYPE; \n" +
   	 "  BEGIN \n" +
   	 "    SELECT * INTO rec_tmp FROM atmk WHERE ROWNUM=1; \n" +
   	 "    Dbms_Output.Put_Line(rec_tmp.table_name || ',' || " +
   	 "    rec_tmp.tablespace_name || ',' || rec_tmp.cluster_name || '.'); " +
   	 "  END test; \n" +
   	 "END tmk; \n" +
   	 "/ \n";
   	 
    public final static String UPDATE_TEST =  
        "update test " +
        "set /*PARAM1*/ thing /*C*/ = 'default value' /*/PARAM1*/;";
    
    public final static String STUDENTS_NOT_TAKING_CS112 = 
        "select s.sno, s.sname, s.age " +
        "from student s, take t " +
        "where s.SNO = t.SNO (+) " +
        "group by s.sno, s.SNAME, s.AGE " +
        "having max(case when t.cno = 'CS112' " +
        "                then 1 else 0 end) = 0; ";        
    
    public final static String NO_SEP_SLASH_SQL = 
        "/*==============================================================*/\n" +
        "/* Database name:  FOOB                                         */\n" +
        "/* DBMS name:      ORACLE Version 8i2 (8.1.6)                   */\n" +
        "/* Created on:     4/1/2005 2:51:03 PM                          */\n" +
        "/*==============================================================*/\n" +
        "\n" +
        "\n" +
        "\n" +
        "\n" +
        "/*==============================================================*/\n" +
        "/* Table : FOOB_CAB                                             */\n" +
        "/*==============================================================*/\n" +
        "\n" +
        "\n" +
        "create table FOOB_CAB  (\n" +
        "   CABID                INTEGER                          not null,\n" +
        "   Name                 VARCHAR2(100)                    not null,\n" +
        "   Description          CLOB,\n" +
        "   constraint PK_FOOB_CAB primary key (ALCID)\n" +
        ")\n" +
        "/\n" +
        "\n" +
        "\n" +
        "/*==============================================================*/\n" +
        "/* Table : FOOB_Add12EmakeTypo                                  */\n" +
        "/*==============================================================*/\n" +
        "\n" +
        "\n" +
        "create table FOOB_Add12EmakeTypo  (\n" +
        "   Add12EmakeTypo       VARCHAR2(35)                     not null,\n" +
        "   Description          VARCHAR2(255),\n" +
        "   constraint PK_FOOB_Add12EmakeTypo primary key (Add12EmakeTypo)\n" +
        ")\n" +
        "/\n";    
    
    public final static String CREATE_FUNCTION_SQL = 
        "create or replace function airport_city(iata_code in char) " +
        "return varchar2 " +
        "is " +
        "    city_name varchar2(50); " +
        "begin " +
        "    select city " +
        "    into city_name " +
        "    from iata_airport_codes " +
        "    where code = iata_code " +
        "    return (city_name); " +
        "end; " +
        "/ ";
}
