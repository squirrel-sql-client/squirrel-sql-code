package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

public class OracleAdhocTests extends AbstractAdhocTests {

	public OracleAdhocTests() {
		super(
				"wis",
				"wis",
				"jdbc:oracle:thin:@localhost:1521:XE",
				"oracle.jdbc.driver.OracleDriver",
				DialectType.ORACLE,
				"Oracle",
				new String[] { "/usr/lib/oracle/xe/app/oracle/product/10.2.0/server/jdbc/lib/ojdbc14.jar" });
	}
}
