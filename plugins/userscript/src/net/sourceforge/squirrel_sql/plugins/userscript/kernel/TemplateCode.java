package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

public class TemplateCode
{
	public static final String CODE =
	"package pack;\n" +
	"\n" +
	"import java.sql.Connection;\n" +
	"import java.sql.DriverManager;\n" +
	"import java.io.PrintStream;\n" +
	"import java.lang.reflect.Method;\n" +
	"\n" +
	"public class TemplateUserScript\n" +
	"{\n" +
	"	public static final String DB_OBJECT_TYPE_TABLE = \"TABLE\";\n" +
	"	public static final String DB_OBJECT_TYPE_VIEW = \"VIEW\";\n" +
	"	public static final String DB_OBJECT_TYPE_PROCEDURE = \"PROCEDURE\";\n" +
	"	public static final String DB_OBJECT_TYPE_CONNECTION = \"CONNECTION\";\n" +
	"	public static final String DB_OBJECT_TYPE_SQL_STATEMENT = \"SQL\";\n" +
	"\n" +
	"	/**\n" +
	"	 * Will be set from inside SQuirreL via reflection.\n" +
	"	 */\n" +
	"	public Object environment;\n" +
	"\n" +
	"	/**\n" +
	"	 *\n" +
	"	 * Place your script code in this method.\n" +
	"	 *\n" +
	"	 * @param dbObjectType one of the DB_OBJECT_TYPE_... constants\n" +
	"	 * @param info\n" +
	"	 *        table name if DB_OBJECT_TYPE_TABLE.equals(DB_OBJECT_TYPE_TABLE)\n" +
	"	 *        view name if DB_OBJECT_TYPE_TABLE.equals(DB_OBJECT_TYPE_VIEW)\n" +
	"	 *        procedure name if DB_OBJECT_TYPE_TABLE.equals(DB_OBJECT_TYPE_PROCEDURE)\n" +
	"	 *        null if DB_OBJECT_TYPE_TABLE.equals(DB_OBJECT_TYPE_CONNECTION)\n" +
	"	 *        SQL String if DB_OBJECT_TYPE_TABLE.equals(DB_OBJECT_TYPE_SQL_STATEMENT)\n" +
	"	 * @param con The connection of the DB session. If you work with transactions your\n" +
	"	 *        script will executed in the same transaction. Closing the the connection will break the session.\n" +
	"	 */\n" +
	"	public void execute(String dbObjectType, String info, Connection con)\n" +
	"		throws Exception\n" +
	"	{\n" +
	"		PrintStream ps;\n" +
	"		ps = createPrintStream();\n" +
	"		ps.println(\"PS 1\");\n" +
	"		ps.println(\"type: \" + dbObjectType);\n" +
	"		ps.println(\"info: \" + info);\n" +
	"		ps.println(\"URL: \" + con.getMetaData().getURL());\n" +
	"\n" +
	"		ps = createPrintStream(\"Bean\");\n" +
	"		ps.println(\"PS 2\");\n" +
	"		ps.println(\"type: \" + dbObjectType);\n" +
	"		ps.println(\"info: \" + info);\n" +
	"		ps.println(\"URL: \" + con.getMetaData().getURL());\n" +
	"\n" +
	"		ps = getSQLAreaPrintStream();\n" +
	"		ps.println(\"To SQL Area: \" + dbObjectType);\n" +
	"		ps.println(\"type: \" + dbObjectType);\n" +
	"		ps.println(\"info: \" + info);\n" +
	"		ps.println(\"URL: \" + con.getMetaData().getURL());\n" +
	"\n" +
	"	}\n" +
	"\n" +
	"\n" +
	"	//////////////////////////////////////////////////////////////////////////////\n" +
	"	// Service Methods\n" +
	"	//////////////////////////////////////////////////////////////////////////////\n" +
	"\n" +
	"\n" +
	"	/**\n" +
	"	 * The output to these print streams will be presented in a tabbed window.\n" +
	"	 */\n" +
	"	PrintStream createPrintStream()\n" +
	"	{\n" +
	"		return createPrintStream(null);\n" +
	"	}\n" +
	"\n" +
	"	/**\n" +
	"	 * The output to these print streams will be presented in a tabbed window.\n" +
	"	 */\n" +
	"	PrintStream createPrintStream(String tabTitle)\n" +
	"	{\n" +
	"		try\n" +
	"		{\n" +
	"			Method m = environment.getClass().getMethod(\"createPrintStream\", new Class[]{String.class});\n" +
	"			return (PrintStream) m.invoke(environment, new Object[]{tabTitle});\n" +
	"		}\n" +
	"		catch (Exception e)\n" +
	"		{\n" +
	"			throw new RuntimeException(e);\n" +
	"		}\n" +
	"	}\n" +
	"\n" +
	"	/**\n" +
	"	 * The output to this print stream will be appended to the  SQL text area of the db session\n" +
	"	 */\n" +
	"	PrintStream getSQLAreaPrintStream()\n" +
	"	{\n" +
	"		try\n" +
	"		{\n" +
	"			Method m = environment.getClass().getMethod(\"getSQLAreaPrintStream\", new Class[0]);\n" +
	"			return (PrintStream) m.invoke(environment, new Object[0]);\n" +
	"		}\n" +
	"		catch (Exception e)\n" +
	"		{\n" +
	"			throw new RuntimeException(e);\n" +
	"		}\n" +
	"	}\n" +
	"\n" +
	"\n" +
	"\n" +
	"\n" +
	"	///////////////////////////////////////////////////////////////////////////\n" +
	"	// To test the script outside SQuirreL\n" +
	"	//////////////////////////////////////////////////////////////////////////\n" +
	"	public static void main(String[] args)\n" +
	"		throws Exception\n" +
	"	{\n" +
	"		Class.forName(\"COM.ibm.db2.jdbc.net.DB2Driver\");\n" +
	"		Connection con = DriverManager.getConnection(\"jdbc:db2://localhost/TestDB\", \"db2inst1\", \"db2inst1\");\n" +
	"\n" +
	"\n" +
	"		TemplateUserScript s = new TemplateUserScript();\n" +
	"		s.environment = new TestEnvironment();\n" +
	"		s.execute(DB_OBJECT_TYPE_TABLE, \"MyTable\", con);\n" +
	"	}\n" +
	"\n" +
	"}\n" +
	"\n" +
	"\n" +
	"class TestEnvironment\n" +
	"{\n" +
	"\n" +
	"	public PrintStream createPrintStream()\n" +
	"	{\n" +
	"		return System.out;\n" +
	"	}\n" +
	"\n" +
	"	public PrintStream getSQLAreaPrintStream()\n" +
	"	{\n" +
	"		return System.out;\n" +
	"	}\n" +
	"} ";


}
