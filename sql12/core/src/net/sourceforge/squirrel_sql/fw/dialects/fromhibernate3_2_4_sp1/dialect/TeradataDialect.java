package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * A dialect for the Teradata database created by MCR as part of the
 * dialect certification process.
 *
 * @author Jay Nance
 */
public class TeradataDialect extends Dialect {

	/**
	 * Constructor
	 */
	public TeradataDialect() {
		super();
		//registerColumnType data types
		registerColumnType( Types.NUMERIC, "NUMERIC($p,$s)" );
		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
		registerColumnType( Types.BIGINT, "NUMERIC(18,0)" );
		registerColumnType( Types.BIT, "BYTEINT" );
		registerColumnType( Types.TINYINT, "BYTEINT" );
		registerColumnType( Types.VARBINARY, "VARBYTE($l)" );
		registerColumnType( Types.BINARY, "BYTEINT" );
		registerColumnType( Types.LONGVARCHAR, "LONG VARCHAR" );
		registerColumnType( Types.CHAR, "CHAR(1)" );
		registerColumnType( Types.DECIMAL, "DECIMAL" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.BOOLEAN, "BYTEINT" );  // hibernate seems to ignore this type...
		registerColumnType( Types.BLOB, "BLOB" );
		registerColumnType( Types.CLOB, "CLOB" );

	}
}