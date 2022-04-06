//$Id: MckoiDialect.java 9328 2006-02-23 17:32:47Z steveebersole $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect compatible with McKoi SQL
 * @author Doug Currie, Gabe Hicks
 */
public class MckoiDialect extends Dialect {
	public MckoiDialect() {
		super();
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "varbinary" );
		registerColumnType( Types.NUMERIC, "numeric" );
		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.CLOB, "clob" );

	}
}
