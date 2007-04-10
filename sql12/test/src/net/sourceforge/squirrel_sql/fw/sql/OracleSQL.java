package net.sourceforge.squirrel_sql.fw.sql;

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
}
