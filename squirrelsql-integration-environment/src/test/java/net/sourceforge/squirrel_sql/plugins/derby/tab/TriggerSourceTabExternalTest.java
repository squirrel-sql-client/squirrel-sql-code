package net.sourceforge.squirrel_sql.plugins.derby.tab;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseSourceTabExternalTest;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AliasNames;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;


public class TriggerSourceTabExternalTest extends AbstractBaseSourceTabExternalTest
{

	protected String getSimpleName() {
		return "testTrigger";
	}
		
	protected BaseSourceTab getTabToTest() {
		return new TriggerSourceTab("a hint");
	}
	
	protected String getAlias() {
		return AliasNames.DERBY_DEST_ALIAS_NAME;
	}
	
}
