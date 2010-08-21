package org.firebirdsql.squirrel.tab;
/*
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
 * This class will display the details for a Firebird sequence.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GeneratorDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GeneratorDetailsTab.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{

		// i18n[firebird.genDetails=Details]
		String TITLE = s_stringMgr.getString("firebird.genDetails");
		// i18n[firebird.seqDetails=Display sequence details]
		String HINT = s_stringMgr.getString("firebird.seqDetails");
	}

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(GeneratorDetailsTab.class);

	public GeneratorDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        
        String sql = "SELECT CAST('" + doi.getSimpleName() + "' AS VARCHAR(31)) as generator_name, " +
            "gen_id(" + doi.getSimpleName() + ", 0) as current_value " +
            "from rdb$database";
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Preparing SQL: "+sql);
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		return pstmt;
	}
}
