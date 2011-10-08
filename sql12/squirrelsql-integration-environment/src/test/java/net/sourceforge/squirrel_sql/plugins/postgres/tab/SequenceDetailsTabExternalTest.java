/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.postgres.tab;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabExternalTest;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AliasNames;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;

public class SequenceDetailsTabExternalTest extends AbstractBasePreparedStatementTabExternalTest
{
	
	
	private static final String TEST_SEQUENCE = "testSequence";

	protected String getSimpleName() {
		return TEST_SEQUENCE;
	}
	
	protected BasePreparedStatementTab getTabToTest() {
		return new SequenceDetailsTab();
	}
	
	protected String getAlias() {
		return AliasNames.POSTGRES_DEST_ALIAS_NAME;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabExternalTest#getSchemaName()
	 */
	@Override
	protected String getSchemaName()
	{
		return "public";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabExternalTest#getSetupStatements()
	 */
	@Override
	protected List<String> getSetupStatements()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add("create sequence "+TEST_SEQUENCE);
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabExternalTest#getTeardownStatements()
	 */
	@Override
	protected List<String> getTeardownStatements()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add("drop sequence "+TEST_SEQUENCE);
		return result;		
	}

}
