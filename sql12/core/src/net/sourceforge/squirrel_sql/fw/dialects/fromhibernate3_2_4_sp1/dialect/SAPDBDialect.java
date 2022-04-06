//$Id: SAPDBDialect.java 8749 2005-12-04 17:32:04Z oneovthafew $
// contributed by Brad Clow
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect compatible with SAP DB.
 * @author Brad Clow
 */
public class SAPDBDialect extends Dialect {

	public SAPDBDialect() {
		super();
		registerColumnType( Types.BIT, "boolean" );
		registerColumnType( Types.BIGINT, "fixed(19,0)" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "fixed(3,0)" );
		registerColumnType( Types.INTEGER, "int" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "long byte" );
		registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
		registerColumnType( Types.CLOB, "long varchar" );
		registerColumnType( Types.BLOB, "long byte" );
		
	}
}
