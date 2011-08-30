package net.sourceforge.squirrel_sql.plugins.derby.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseSourceTabExternalTest;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AliasNames;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;

public class ProcedureSourceTabExternalTest extends AbstractBaseSourceTabExternalTest
{
	
	protected String getSimpleName() {
		return "testView";
	}
	
	protected BaseSourceTab getTabToTest() {
		return new MyStatementCreator();
	}
	
	protected String getAlias() {
		return AliasNames.DERBY_DEST_ALIAS_NAME;
	}

	private class MyStatementCreator extends ProcedureSourceTab {
		public MyStatementCreator()
		{
			super("","");
		}

		@Override
		public PreparedStatement createStatement() throws SQLException
		{
			return super.createStatement();
		}
		
	}
}
