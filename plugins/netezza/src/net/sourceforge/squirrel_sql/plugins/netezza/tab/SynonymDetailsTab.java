package net.sourceforge.squirrel_sql.plugins.netezza.tab;

/*
 * Copyright (C) 2009 Rob Manning
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class will display the details for an Netezza synonym.
 */
public class SynonymDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SynonymDetailsTab.class);

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(SynonymDetailsTab.class);

	/**
	 * This interface defines locale specific strings. This should be replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[SynonymDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("SynonymDetailsTab.title");

		// i18n[SynonymDetailsTab.hint=Display synonym details]
		String HINT = s_stringMgr.getString("SynonymDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
	private static final String SQL =
		"SELECT " +
		"SYNONYM_NAME, " +
		"refobjname as Referenced_Object, " +
		"refdatabase as Referenced_Database , " +
		"refdatabase || '.' || synonym_name as Qualified_Name " +
		"FROM _v_synonym " +
		"where synonym_name = ? " +
		"and refschema = ? ";

		
	public SynonymDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Synonym details SQL: " + SQL);
			s_log.debug("Synonym name: " + doi.getSimpleName());
			s_log.debug("Synonym schema: " + doi.getSchemaName());
		}

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		pstmt.setString(2, doi.getSchemaName());
		return pstmt;
	}

}
