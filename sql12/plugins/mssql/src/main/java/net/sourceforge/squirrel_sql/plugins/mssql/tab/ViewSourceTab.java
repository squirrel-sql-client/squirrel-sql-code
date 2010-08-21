package net.sourceforge.squirrel_sql.plugins.mssql.tab;
/*
 * Copyright (C) 2005 Rob Manning
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This class will display the source for an MS-SQLServer view.
 * 
 * @author manningr
 */
public class ViewSourceTab extends BaseSourceTab
{

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ViewSourceTab.class);
    
    /**
	 * This interface defines locale specific strings. 
	 */
	private interface i18n
	{
        // i18n[ViewSourceTab.display=Show view source]
		String HINT = s_stringMgr.getString("ViewSourceTab.display");
	}
	
	/** 
	 * SQLServer's INFORMATION_SCHEMA.VIEWS VIEW_DEFINITION column only supports up to 4000 chars.  This
	 * query can return a result-set with multiple rows that should be concatenated.  They should be correctly
	 * ordered by the SYSCOMMENT.COLID field.
	 */
	private static final String BIGVIEW_SQL = 
		"SELECT text  FROM sysobjects o , syscomments c " +
		"where  o.name = ? " +
		"and o.id = c.id " +
		"order by c.colid ";		
		
		
    
	public ViewSourceTab()
	{
		super(i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(BIGVIEW_SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
