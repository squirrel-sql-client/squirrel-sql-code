package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * A dialect compatible with the H2 database.
 * 
 * @author Thomas Mueller
 *
 */
public class H2Dialect extends Dialect {

    private String querySequenceString;
    public H2Dialect() {
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "binary");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.CHAR, "char($l)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, "longvarbinary");
        registerColumnType(Types.LONGVARCHAR, "longvarchar");
        registerColumnType(Types.REAL, "real");        
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.VARCHAR, "varchar($l)");
        registerColumnType(Types.VARBINARY, "binary($l)");
        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.CLOB, "clob");
        
    }
}