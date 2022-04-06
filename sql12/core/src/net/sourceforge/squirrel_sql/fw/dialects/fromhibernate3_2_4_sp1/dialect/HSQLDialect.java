//$Id: HSQLDialect.java 11286 2007-03-15 10:33:16Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect compatible with HSQLDB (Hypersonic SQL).
 * <p/>
 * Note this version supports HSQLDB version 1.8 and higher, only.
 *
 * @author Christoph Sturm, Phillip Baird
 */
public class HSQLDialect extends Dialect {


	public HSQLDialect() {
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.BINARY, "binary" );
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.DECIMAL, "decimal" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.NUMERIC, "numeric" );
		//HSQL has no Blob/Clob support .... but just put these here for now!
		registerColumnType( Types.BLOB, "longvarbinary" );
		registerColumnType( Types.CLOB, "longvarchar" );

	}

}
