package org.firebirdsql.squirrel.tab;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseSourceTabExternalTest;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AliasNames;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;

public class ViewSourceTabTest extends AbstractBaseSourceTabExternalTest
{

	@Override
	protected String getSimpleName()
	{
		return "TestView";
	}

	@Override
	protected BaseSourceTab getTabToTest()
	{
		return new ViewSourceTab("");
	}

	@Override
	protected String getAlias()
	{
		return AliasNames.FIREBIRD_DEST_ALIAS_NAME;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseSourceTabExternalTest#getSchemaName()
	 */
	@Override
	protected String getSchemaName()
	{
		return "";
	}


}
