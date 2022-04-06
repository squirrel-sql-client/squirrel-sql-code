//$Id: InformixDialect.java 11178 2007-02-09 15:43:15Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * Informix dialect.<br>
 * <br>
 * Seems to work with Informix Dynamic Server Version 7.31.UD3,
 * Informix JDBC driver version 2.21JC3.
 * @author Steve Molitor
 */
public class InformixDialect extends Dialect {

	/**
	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
	 * Informix type mappings.
	 */
	public InformixDialect() {
		registerColumnType(Types.BIGINT, "int8");
		registerColumnType(Types.BINARY, "byte");
		registerColumnType(Types.BIT, "smallint"); // Informix doesn't have a bit type
		registerColumnType(Types.CHAR, "char($l)");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal");
        registerColumnType(Types.DOUBLE, "float");
        registerColumnType(Types.FLOAT, "smallfloat");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.LONGVARBINARY, "blob"); // or BYTE
		registerColumnType(Types.LONGVARCHAR, "clob"); // or TEXT?
		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
		registerColumnType(Types.REAL, "smallfloat");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
		registerColumnType(Types.TIME, "datetime hour to second");
		registerColumnType(Types.TINYINT, "smallint");
		registerColumnType(Types.VARBINARY, "byte");
		registerColumnType(Types.VARCHAR, "varchar($l)");
		registerColumnType(Types.VARCHAR, 255, "varchar($l)");
		registerColumnType(Types.VARCHAR, 32739, "lvarchar($l)");

	}
}