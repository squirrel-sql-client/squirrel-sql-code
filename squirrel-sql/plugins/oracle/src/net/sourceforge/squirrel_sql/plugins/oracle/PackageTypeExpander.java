package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class handles the expanding of the "Package Type" or "Package Heading"
 * node. It will give a list of all the packages available in the schema.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PackageTypeExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(PackageTypeExpander.class);

	/** SQL that retrieves the names of Oracle packages. */
	private static String ORACLE_PACKAGES_SQL =
		"select object_name from sys.all_objects where object_type = 'PACKAGE'" +
		" and owner = ? order by object_name";

	/**
	 * Ctor.
	 */
	PackageTypeExpander()
	{
		super();
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note
	 * that this method should <B>not</B> actually add the child nodes to the
	 * parent node as this is taken care of in the caller.
	 * 
	 * @param	session	Current session.
	 * @param	node	Node to be expanded.
	 * 
	 * @return	A list of <TT>ObjectTreeNode</TT> objects representing the child
	 *			nodes for the passed node.
	 */
	public List createChildren(ISession session, ObjectTreeNode parentNode)
		throws BaseSQLException
	{
		final List childNodes = new ArrayList();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLConnection conn = session.getSQLConnection();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();

		try
		{
			childNodes.addAll(createOraclePackageNodes(session, schemaName));
		}
		catch (SQLException ex)
		{
			throw new BaseSQLException(ex);
		}

		return childNodes;
	}

	private List createOraclePackageNodes(ISession session, String schemaName)
		throws SQLException, BaseSQLException
	{
		final SQLConnection conn = session.getSQLConnection();
		final List childNodes = new ArrayList();

		// Add node to contain standalone proceduers.
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo("", schemaName,
										"%",
										IDatabaseObjectTypes.PACKAGE,
										conn);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.setUserObject("Standalone");
		childNodes.add(child);

		// Add node for each package.
		PreparedStatement pstmt = conn.prepareStatement(ORACLE_PACKAGES_SQL);
		try
		{
			pstmt.setString(1, schemaName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				dbinfo = new DatabaseObjectInfo(null, schemaName,
										rs.getString(1),
										IDatabaseObjectTypes.PACKAGE,
										conn);
				child = new ObjectTreeNode(session, dbinfo);
				childNodes.add(child);
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}
}
