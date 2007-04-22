package net.sourceforge.squirrel_sql.plugins.oracle.tab;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
/**
 * This class will display the source for an Oracle object.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectSourceTab extends BaseSourceTab
{
	/** SQL that retrieves the source of a stored procedure. */
	private static String SQL =
		"select text from sys.all_source where type = ?"
			+ " and owner = ? and name = ? order by line";

	private final String _columnData;

	public ObjectSourceTab(String columnData, String hint) {
          this(columnData, null, hint);
        }

        public ObjectSourceTab(String columnData, String title, String hint)
	{
		super(title, hint);
		if (columnData == null)
		{
			throw new IllegalArgumentException("Column Data is null");
		}
		_columnData = columnData;
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, ObjectSourceTab.this._columnData);
		pstmt.setString(2, doi.getSchemaName());
		pstmt.setString(3, doi.getSimpleName());
		return pstmt;
	}
}
