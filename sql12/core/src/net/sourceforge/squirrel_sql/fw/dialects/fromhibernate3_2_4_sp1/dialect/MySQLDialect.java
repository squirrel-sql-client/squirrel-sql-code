//$Id: MySQLDialect.java 10964 2006-12-08 16:06:49Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect for MySQL (prior to 5.x).
 *
 * @author Gavin King
 */
public class MySQLDialect extends Dialect {

	public MySQLDialect() {
		super();
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "datetime" );
		registerColumnType( Types.VARBINARY, "longblob" );
		registerColumnType( Types.VARBINARY, 16777215, "mediumblob" );
		registerColumnType( Types.VARBINARY, 65535, "blob" );
		registerColumnType( Types.VARBINARY, 255, "tinyblob" );
		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
		registerColumnType( Types.BLOB, "longblob" );
		registerColumnType( Types.BLOB, 16777215, "mediumblob" );
		registerColumnType( Types.BLOB, 65535, "blob" );
		registerColumnType( Types.CLOB, "longtext" );
		registerColumnType( Types.CLOB, 16777215, "mediumtext" );
		registerColumnType( Types.CLOB, 65535, "text" );
		registerColumnType( Types.VARCHAR, "longtext" );
		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
		registerColumnType( Types.VARCHAR, 65535, "text" );
		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
	}

}