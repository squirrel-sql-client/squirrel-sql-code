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
    
    public final static String UPDATE_TEST =  
        "update test " +
        "set /*PARAM1*/ thing /*C*/ = 'default value' /*/PARAM1*/;";
}
