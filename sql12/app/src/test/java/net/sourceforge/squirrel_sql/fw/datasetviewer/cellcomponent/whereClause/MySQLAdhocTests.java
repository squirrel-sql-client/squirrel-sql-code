package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

public class MySQLAdhocTests extends AbstractAdhocTests {

	public MySQLAdhocTests() {
		super(
				"wis",
				"wis",
				"jdbc:mysql://localhost/wis?sessionVariables=sql_mode=NO_BACKSLASH_ESCAPES",
				"com.mysql.jdbc.Driver", DialectType.MYSQL5, "MySQL",
				new String[] { "/usr/share/java/mysql.jar" });
	}
}
