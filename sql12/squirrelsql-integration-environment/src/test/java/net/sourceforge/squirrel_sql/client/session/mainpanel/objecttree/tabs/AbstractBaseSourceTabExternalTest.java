package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.dbcopy.cli.SessionUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public abstract class AbstractBaseSourceTabExternalTest
{
	
	protected BaseSourceTab classUnderTest = null;
	protected SessionUtil sessionUtil = new SessionUtil();
	protected IDatabaseObjectInfo dboi = null;

	protected abstract String getSimpleName();
	
	protected abstract BaseSourceTab getTabToTest();
	
	protected abstract String getAlias();
	
	protected String getSchemaName() {
		return null;
	}

	@Before
	public void setup() throws Exception
	{
		classUnderTest = getTabToTest();
		ISession session = sessionUtil.getSessionForAlias(getAlias());
		if (dboi == null) {
			dboi = Mockito.mock(IDatabaseObjectInfo.class);
			Mockito.when(dboi.getSchemaName()).thenReturn(getSchemaName());
			Mockito.when(dboi.getSimpleName()).thenReturn(getSimpleName());
		}
		classUnderTest.setSession(session);
		classUnderTest.setDatabaseObjectInfo(dboi);
	}

	@Test
	public void testCreateStatement() throws Exception
	{
		Method m = classUnderTest.getClass().getDeclaredMethod("createStatement", (Class<?>[])null);
		m.setAccessible(true);
		Object result = m.invoke(classUnderTest, (Object[])null);
	   PreparedStatement stmt = (PreparedStatement)result;
		stmt.executeQuery();
	}
	
	

}
