//$Id: OracleDialect.java 8610 2005-11-18 18:30:27Z steveebersole $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * An SQL dialect for Oracle, compatible with Oracle 8.
 * @author Gavin King
 */
public class OracleDialect extends Oracle9Dialect {

	public OracleDialect() {
		super();
		// Oracle8 and previous define only a "DATE" type which
		//      is used to represent all aspects of date/time
		registerColumnType( Types.TIMESTAMP, "date" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
	}
}
