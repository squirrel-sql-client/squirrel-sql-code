package net.sourceforge.squirrel_sql.plugins.greenplum.exp;

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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the expanding of the "External Table" node. It will give a list of all the External 
 * Tables available in the schema.
 */
public class GreenplumExtTableParentExpander implements INodeExpander
{
	/**
	 * Default ctor.
	 */
	public GreenplumExtTableParentExpander()
    {
        super();
	}

    /** SQL used to load external tables */
	private static final String SQL =
		"SELECT c.relname " +
        "  FROM pg_class c " +
        "  LEFT OUTER JOIN pg_namespace nspace ON (nspace.oid=c.relnamespace) " +
        "  LEFT OUTER JOIN pg_exttable ext ON (ext.reloid=c.oid) " +
        "  LEFT OUTER JOIN pg_authid auth ON (auth.oid=c.relowner) " +
        " WHERE  (c.relkind = 'x' OR (c.relkind = 'r' AND c.relstorage = 'x')) " +
        "  AND nspname = ? " +
        //"  AND rolname = ? " +
        //"  AND dbname = ? " +
        " ORDER BY relname ";

	/*
	 * Create the child nodes for the passed parent node and return them. Note that this method should
	 * <B>not</B> actually add the child nodes to the parent node as this is taken care of in the caller.
	 *
	 * @param session
	 *        Current session.
	 * @param node
	 *        Node to be expanded.
	 *
	 * @return A list of <TT>ObjectTreeNode</TT> objects representing the child nodes for the passed node.
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();
		final ObjFilterMatcher filterMatcher = new ObjFilterMatcher(session.getProperties());

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, schemaName);

            //I dont know how to get the user and db_name
            //pstmt.setString(2, "gpadmin");
            //pstmt.setString(3, "db_name");

			rs = pstmt.executeQuery();

			while(rs.next())
			{
				IDatabaseObjectInfo si = new DatabaseObjectInfo(catalogName, schemaName, rs.getString(1), DatabaseObjectType.TABLE_TYPE_DBO, md);

				if(filterMatcher.matches(si.getSimpleName()))
				{
					childNodes.add(new ObjectTreeNode(session, si));
				}
			}
		}
        finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return childNodes;
	}
}
