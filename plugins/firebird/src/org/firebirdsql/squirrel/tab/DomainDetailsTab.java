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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This class will display the details for an Oracle sequence.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DomainDetailsTab extends BasePreparedStatementTab
{
    
    /** SQL that retrieves the data. */
    private static String SQL =
        "select rdb$field_name, " +
        "rdb$validation_source, " +
        "rdb$computed_source, " +
        "rdb$default_source, " +
        "rdb$field_length, " +
        "rdb$field_scale, " +
        "rdb$field_type, " +
        "rdb$field_sub_type, " +
        "rdb$missing_source, " +
        "rdb$edit_string, " +
        "rdb$character_length, " +
        "rdb$collation_name, " +
        "rdb$character_set_name, " +
        "rdb$field_precision " +
        "rdb$description " +
        "from rdb$fields f " +
        "left outer join rdb$character_sets cs on cs.rdb$character_set_id = f.rdb$character_set_id " +
        "left outer join rdb$collations cl on (cl.rdb$collation_id = f.rdb$collation_id and cl.rdb$character_set_id = f.rdb$character_set_id) " +
        "where " +
        "  rdb$field_name = ?";
    
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Details";
		String HINT = "Display domain details";
	}

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DomainDetailsTab.class);

	public DomainDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
