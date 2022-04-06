//$Id: IngresDialect.java 10964 2006-12-08 16:06:49Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;


/**
 * An Ingres SQL dialect.
 * <p/>
 * Known limitations:
 * - only supports simple constants or columns on the left side of an IN, making (1,2,3) in (...) or (<subselect) in (...) non-supported
 * - supports only 31 digits in decimal
 *
 * @author Ian Booth, Bruce Lunsford, Max Rydahl Andersen
 */
public class IngresDialect extends Dialect {

	public IngresDialect() {
		registerColumnType( Types.BIT, "tinyint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.REAL, "real" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "float" );
		registerColumnType( Types.NUMERIC, "decimal($p, $s)" );
		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
		registerColumnType( Types.BINARY, 32000, "byte($l)" );
		registerColumnType( Types.BINARY, "long byte" );
		registerColumnType( Types.VARBINARY, 32000, "varbyte($l)" );
		registerColumnType( Types.VARBINARY, "long byte" );
		registerColumnType( Types.LONGVARBINARY, "long byte" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, 32000, "varchar($l)" );
		registerColumnType( Types.VARCHAR, "long varchar" );
		registerColumnType( Types.LONGVARCHAR, "long varchar" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "date" );
		registerColumnType( Types.TIMESTAMP, "date" );
		registerColumnType( Types.BLOB, "long byte" );
		registerColumnType( Types.CLOB, "long varchar" );

	}

}
