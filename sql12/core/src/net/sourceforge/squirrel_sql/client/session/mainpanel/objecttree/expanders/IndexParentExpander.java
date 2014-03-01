package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

/*
 * Copyright (C) 2007 Rob Manning
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * 
 * @author manningr
 * 
 */
public class IndexParentExpander implements INodeExpander
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(IndexParentExpander.class);

	/** the db-specific extractor implementation */
	private ITableIndexExtractor extractor = null;

	/**
	 * Default ctor.
	 */
	public IndexParentExpander() {
		super();
	}

	/**
	 * Sets the db-specific index extractor.
	 * 
	 * @param extractor
	 *        this is provided by the plugin.
	 */
	public void setTableIndexExtractor(ITableIndexExtractor extractor)
	{
		this.extractor = extractor;
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note that this method should
	 * <B>not</B> actually add the child nodes to the parent node as this is taken care of in the caller.
	 * 
	 * @param session
	 *        Current session.
	 * @param node
	 *        Node to be expanded.
	 * 
	 * @return A list of <TT>ObjectTreeNode</TT> objects representing the child nodes for the passed node.
	 * 
	 * @throws SQLException
	 *         Thrown if an SQL error occurs.
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final IDatabaseObjectInfo tableInfo = ((IndexParentInfo) parentDbinfo).getTableInfo();

		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String query = extractor.getTableIndexQuery();
			if (s_log.isDebugEnabled())
			{
				s_log.debug("Running query for index extraction: " + query);
			}
			pstmt = conn.prepareStatement(query);
			extractor.bindParamters(pstmt, tableInfo);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				String indexName = rs.getString(1);
				String cat = parentDbinfo.getCatalogName();
				String schema = parentDbinfo.getSchemaName();
				DatabaseObjectInfo doi = new DatabaseObjectInfo(cat, schema, indexName, DatabaseObjectType.INDEX, md);
				childNodes.add(new ObjectTreeNode(session, doi));
			}
		} catch (SQLException e)
		{
			session.showErrorMessage(e);
			s_log.error(
			   "Unexpected exception while extracting indexes for " + "parent dbinfo: " + parentDbinfo, e);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}

		return childNodes;
	}
}
