//$Id: PostgreSQLDialect.java 11366 2007-03-29 13:26:25Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect for Postgres
 * @author Gavin King
 */
public class PostgreSQLDialect extends Dialect {

	public PostgreSQLDialect() {
		super();
		registerColumnType( Types.BIT, "bool" );
		registerColumnType( Types.BIGINT, "int8" );
		registerColumnType( Types.SMALLINT, "int2" );
		registerColumnType( Types.TINYINT, "int2" );
		registerColumnType( Types.INTEGER, "int4" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float4" );
		registerColumnType( Types.DOUBLE, "float8" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "bytea" );
		registerColumnType( Types.CLOB, "text" );
		registerColumnType( Types.BLOB, "oid" );
		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
	}

	public boolean supportsSequences() {
		return true;
	}
}
