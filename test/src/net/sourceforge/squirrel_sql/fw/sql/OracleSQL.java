package net.sourceforge.squirrel_sql.fw.sql;

public interface OracleSQL {

    public final static String SELECT_DUAL = 
        "select 'X' from dual;";        
    
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
        "/ \n";       
    
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
        "/ \n";       
    
    public final static String CREATE_OR_REPLACE_STORED_PROC2 = 
        "CREATE OR REPLACE PROCEDURE RUNPROCESS ( " +
        "   processname IN  VARCHAR2 DEFAULT NULL, " +
        "   servername  IN  VARCHAR2 DEFAULT NULL, " +
        "   run_        OUT NUMBER) IS " +
        "BEGIN \n" +
        " \n" +
        "  DECLARE \n" +
        "    server$     VARCHAR2(255); \n" +
        "    enabled$    CHAR; " +
        "    run$        CHAR; " +
        "    lastrun$    DATE; " +
        "    cycle$      NUMBER; " +
        "    runcompare$ DATE; " +
        "    starthour$  NUMBER; " +
        "    endhour$    NUMBER; " +
        "    nowhour$    NUMBER; \n" +
        " \n" +
        "  BEGIN \n" +
        "    BEGIN \n" +
        "      SELECT Server, FEnabled, LastRunTime, CycleTime, StartHour, EndHour " +
        "      INTO server$, enabled$, lastrun$, cycle$, starthour$, endhour$ " +
        "      FROM JF_ScheduledProcess " +
        "      WHERE Process = processname; " +
        "    EXCEPTION " +
        "      WHEN NO_DATA_FOUND THEN " +
        "          enabled$ := '0'; " +
        "    END; \n" +
        " \n" +
        "    IF (enabled$ = '0') THEN \n" +
        "      run$ := '0'; " +
        "    ELSE " +
        "      nowhour$ := TO_CHAR(SYSDATE, 'HH24'); " +
        "      IF ((((starthour$ > nowhour$) or (endhour$ < nowhour$)) and (starthour$ < endhour$)) or " +
        "          ((starthour$ > nowhour$) and (endhour$ < nowhour$))) THEN " +
        "        run$ := '0'; " +
        "      ELSE " +
        "        IF (server$ = servername) THEN " +
        "          runcompare$ := lastrun$ + (cycle$/1440); " +
        "          IF (runcompare$ < SYSDATE) THEN " +
        "            UPDATE JF_ScheduledProcess SET LastRunTime = SYSDATE " +
        "            WHERE Process = processname; " +
        "            run$ := '1'; " +
        "          ELSE " +
        "            run$ := '0'; " +
        "          END IF; " +
        "        ELSE " +
        "          runcompare$ := lastrun$ + ((2 * cycle$)/1440); " +
        "          IF (runcompare$ < SYSDATE) THEN " +
        "            UPDATE JF_ScheduledProcess SET LastRunTime = SYSDATE, " +
        "                                           Server = servername " +
        "            WHERE Process = processname; " +
        "            run$ := '1'; " +
        "          ELSE " +
        "            run$ := '0'; " +
        "          END IF; " +
        "        END IF; " +
        "      END IF; " +
        "    END IF; " +
        " " +
        "    run_ := run$; " +
        "  END; \n" +
        "END RUNPROCESS; \n" +
        " \n" +
        "/ \n";        
}
