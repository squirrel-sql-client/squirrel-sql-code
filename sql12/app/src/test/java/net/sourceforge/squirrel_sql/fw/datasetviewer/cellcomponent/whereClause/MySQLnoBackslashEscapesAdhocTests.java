package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

public class MySQLnoBackslashEscapesAdhocTests extends AbstractAdhocTests {

	public MySQLnoBackslashEscapesAdhocTests() {
		super("wis", "wis", "jdbc:mysql://localhost/wis",
				"com.mysql.jdbc.Driver", DialectType.MYSQL5, "MySQL",
				new String[] { "/usr/share/java/mysql.jar" });
	}
}
