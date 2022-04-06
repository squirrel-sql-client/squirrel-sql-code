// $Id: JDataStoreDialect.java 7075 2005-06-08 07:06:50Z oneovthafew $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * A <tt>Dialect</tt> for JDataStore.
 * 
 * @author Vishy Kasar
 */
public class JDataStoreDialect extends Dialect {

	/**
	 * Creates new JDataStoreDialect
	 */
	public JDataStoreDialect() {
		super();

		registerColumnType( Types.BIT, "tinyint" );
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
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );

		registerColumnType( Types.BLOB, "varbinary" );
		registerColumnType( Types.CLOB, "varchar" );
	}
}
