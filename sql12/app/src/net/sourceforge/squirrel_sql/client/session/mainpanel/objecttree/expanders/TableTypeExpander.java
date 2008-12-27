package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

/*
 * Copyright (C) 2002 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

/**
 * This class handles the expanding of a Table Type node. It will build all the tables for the table type.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableTypeExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(TableTypeExpander.class);

	/**
	 * Create the child nodes for the passed parent node and return them. Note that this method should
	 * <B>not</B> actually add the child nodes to the parent node as this is taken care of in the caller.
	 * 
	 * @param session
	 *           Current session.
	 * @param node
	 *           Node to be expanded.
	 * @return A list of <TT>ObjectTreeNode</TT> objects representing the child nodes for the passed node.
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		Statement stmt = null;
		try
		{
			final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
			final ISQLConnection conn = session.getSQLConnection();
			final String catalogName = parentDbinfo.getCatalogName();
			final String schemaName = parentDbinfo.getSchemaName();
			final String tableType = parentDbinfo.getSimpleName();

			String[] types = tableType != null ? new String[] { tableType } : null;
			session.getSchemaInfo().waitTillTablesLoaded();
			final ITableInfo[] tables =
				session.getSchemaInfo().getITableInfos(catalogName, schemaName,
					new ObjFilterMatcher(session.getProperties()), types);

			if (session.getProperties().getShowRowCount())
			{
				stmt = conn.createStatement();
			}

			for (int i = 0; i < tables.length; ++i)
			{
				ObjectTreeNode child = new ObjectTreeNode(session, tables[i]);
				child.setUserObject(getNodeDisplayText(stmt, tables[i]));
				childNodes.add(child);
			}
		}
		finally
		{
			SQLUtilities.closeStatement(stmt);
		}

		return childNodes;
	}

	private String getNodeDisplayText(Statement rowCountStmt, IDatabaseObjectInfo dbinfo)
	{
		if (rowCountStmt != null)
		{
			try
			{
				ResultSet rs = rowCountStmt.executeQuery("select count(*) from " + dbinfo.getQualifiedName());
				try
				{
					long nbrRows = 0;
					if (rs.next())
					{
						nbrRows = rs.getLong(1);
					}
					StringBuilder buf = new StringBuilder(dbinfo.getSimpleName());
					buf.append(" (").append(nbrRows).append(")");
					return buf.toString();
				}
				finally
				{
					SQLUtilities.closeResultSet(rs);
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Error retrieving row count for: " + dbinfo.getQualifiedName(), ex);
				return dbinfo.getSimpleName();
			}
		}
		else
		{
			return dbinfo.getSimpleName();
		}
	}

}
