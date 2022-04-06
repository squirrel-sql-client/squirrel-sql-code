//$Id: SQLServerDialect.java 11304 2007-03-19 22:06:45Z steve.ebersole@jboss.com $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.sql.Types;

/**
 * A dialect for Microsoft SQL Server 2000 and 2005
 *
 * @author Gavin King
 */
public class SQLServerDialect extends SybaseDialect {

	public SQLServerDialect() {
		registerColumnType( Types.VARBINARY, "image" );
		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
	}

}
