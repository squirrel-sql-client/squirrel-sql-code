package net.sourceforge.squirrel_sql.plugins.greenplum.tab;

/*
 * Copyright (C) 2011 Adam Winn
 *
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
 * This class will display the details for a Greenplum external table.
 */
public class GreenplumExternalTableDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GreenplumExternalTableDetailsTab.class);

    private static ILogger s_log = LoggerController.createLogger(GreenplumExternalTableDetailsTab.class);

	/**
	 * This interface defines locale specific strings. This should be replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[GreenplumExternalTableDetailsTab.title=External Table Details]
		String TITLE = s_stringMgr.getString("ExternalTableDetailsTab.title");

		// i18n[GreenplumExternalTableDetailsTab.hint=Display external table details]
		String HINT = s_stringMgr.getString("ExternalTableDetailsTab.hint");
	}

	/** SQL that retrieves the data. */
    private static final String SQL =
		"SELECT " +
        "  nspace.nspname as schema, "+
        "  class.relname as external_table_name, "+
        "  auth.rolname as owner, "+
        "  array_to_string(ARRAY(SELECT att.attname FROM pg_class class LEFT OUTER JOIN pg_attribute att ON (att.attrelid=class.oid) WHERE attstattarget = -1 and relname = ?), ', ') as column_names, "+
        "  array_to_string(class.relacl, ', ') as permissions, "+
        "  array_to_string(ext.location, ', ') as file_location, " +
        "  ext.fmtopts as options, " +
        "  ext.command as command, " +
        "  ext.rejectlimit as reject_limit, " +
        "  ext.rejectlimittype as reject_limit_type, " +
        "  ext.fmterrtbl as error_table " +
        "FROM pg_class class " +
        "  LEFT OUTER JOIN pg_namespace nspace ON (nspace.oid=class.relnamespace) " +
        "  LEFT OUTER JOIN pg_exttable ext ON (ext.reloid=class.oid) " +
        "  LEFT OUTER JOIN pg_authid auth ON (auth.oid=class.relowner) " +
        "  LEFT OUTER JOIN pg_attribute att ON (att.attrelid=class.oid) " +
        "WHERE  (class.relkind = 'x' OR (class.relkind = 'r' AND class.relstorage = 'x')) " +
        "  AND nspname = ? " +
        "  AND class.relname = ? ";
        //"  AND rolname = ? " +
        //"  AND dbname = ? " +


	public GreenplumExternalTableDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
        //System.out.println(SQL);

		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSimpleName());
		pstmt.setString(2, doi.getSchemaName());
        pstmt.setString(3, doi.getSimpleName());

        //I dont know how to get the user and db_name
        //pstmt.setString(4, "gpadmin");
        //pstmt.setString(5, "db_name");

		return pstmt;
	}

}
