package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import static org.junit.Assert.assertEquals;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommandLineArgumentProcessorTest
{
	private static final String SOURCE_SCHEMA = "dbcopydest";
	private static final String DEST_SCHEMA = "public";
	private static final String FIRST_TABLE = "tableA";
	private static final String SECOND_TABLE = "tableB";
	private static final String TWO_TABLE_LIST = FIRST_TABLE +","+SECOND_TABLE;
	private static final String DEST_SESSION_NAME = "PostgreSQL (10.0.1.42:5432)";
	private static final String SOURCE_SESSION_NAME = "Oracle 11g (oracle-01 - dbcopydest)";
	private CommandLineArgumentProcessor classUnderTest;

	private static final String[] singleTableArgs = new String[] { 
		"--" + CommandLineArgumentProcessor.SOURCE_SESSION,
		SOURCE_SESSION_NAME,
		"--" + CommandLineArgumentProcessor.DEST_SESSION,
		DEST_SESSION_NAME,
		"--" + CommandLineArgumentProcessor.TABLE_LIST,
		FIRST_TABLE,
		"--" + CommandLineArgumentProcessor.SOURCE_SCHEMA,
		SOURCE_SCHEMA,
		"--" + CommandLineArgumentProcessor.DEST_SCHEMA,
		DEST_SCHEMA
	};
	
	private static final String[] twoTableArgs = new String[] { 
		"--" + CommandLineArgumentProcessor.SOURCE_SESSION,
		SOURCE_SESSION_NAME,
		"--" + CommandLineArgumentProcessor.DEST_SESSION,
		DEST_SESSION_NAME,
		"--" + CommandLineArgumentProcessor.TABLE_LIST,
		TWO_TABLE_LIST,
		"--" + CommandLineArgumentProcessor.SOURCE_SCHEMA,
		SOURCE_SCHEMA,		
		"--" + CommandLineArgumentProcessor.DEST_SCHEMA,
		DEST_SCHEMA
	};

	
	@Before
	public void setUp() throws ParseException {
		classUnderTest = new CommandLineArgumentProcessor(singleTableArgs);
		
	}
	
	@After
	public void tearDown() {
		classUnderTest = null;
	}
	
	@Test
	public void testGetSourceAliasName() throws ParseException
	{
		assertEquals(SOURCE_SESSION_NAME, classUnderTest.getSourceAliasName());	
	}

	@Test
	public void testGetDestAliasName()
	{
		assertEquals(DEST_SESSION_NAME, classUnderTest.getDestAliasName());
	}

	@Test
	public void testGetDestSchemaName()
	{
		assertEquals(DEST_SCHEMA, classUnderTest.getDestSchemaName());
	}

	@Test
	public void testGetTableList() throws ParseException
	{
		assertEquals(FIRST_TABLE, classUnderTest.getTableList().get(0));
		classUnderTest = new CommandLineArgumentProcessor(twoTableArgs);		
		assertEquals(FIRST_TABLE, classUnderTest.getTableList().get(0));
		assertEquals(SECOND_TABLE, classUnderTest.getTableList().get(1));
	}

}
