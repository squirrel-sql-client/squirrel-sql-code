package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * A SQL dialect for TimesTen 5.1.
 *
 * Known limitations:
 * joined-subclass support because of no CASE support in TimesTen
 * No support for subqueries that includes aggregation
 *  - size() in HQL not supported
 *  - user queries that does subqueries with aggregation
 * No CLOB/BLOB support
 * No cascade delete support.
 * No Calendar support
 * No support for updating primary keys.
 *
 * @author Sherry Listgarten and Max Andersen
 */
public class TimesTenDialect extends Dialect {

	public TimesTenDialect() {
		super();
		registerColumnType( Types.BIT, "TINYINT" );
		registerColumnType( Types.BIGINT, "BIGINT" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.TINYINT, "TINYINT" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.CHAR, "CHAR(1)" );
		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.DOUBLE, "DOUBLE" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
		// TimesTen has no BLOB/CLOB support, but these types may be suitable
		// for some applications. The length is limited to 4 million bytes.
        registerColumnType( Types.BLOB, "VARBINARY(4000000)" );
        registerColumnType( Types.CLOB, "VARCHAR(4000000)" );

	}
}
