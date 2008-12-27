package org.firebirdsql.squirrel.exp;

/*
 * Copyright (C) 2002 Colin Bell
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

/**
 * This class handles the expanding of the "Sequence Group" node. It will give a list of all the Sequences
 * available in the schema.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell </A>
 */
public class DomainParentExpander implements INodeExpander
{

	/** SQL used to load info about sequences. */
	private static final String SQL = "select cast(rdb$field_name as varchar(31)) as rdb$domain_name"
	      + " from rdb$fields where not (rdb$field_name like 'RDB$%')";

	/** Logger for this class. */
	@SuppressWarnings("unused")
	private static final ILogger s_log = LoggerController.createLogger(DomainParentExpander.class);

	/** The plugin. */
	@SuppressWarnings("unused")
	private final FirebirdPlugin _plugin;

	/**
	 * Ctor.
	 * 
	 * @throws IllegalArgumentException
	 *         Thrown if <TT>null</TT> <TT>OraclePlugin</TT> passed.
	 */
	DomainParentExpander(FirebirdPlugin plugin) {
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("FirebirdPlugin == null");
		}

		_plugin = plugin;
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note that this method should <B>not
	 * </B> actually add the child nodes to the parent node as this is taken care of in the caller.
	 * 
	 * @param session
	 *        Current session.
	 * @param node
	 *        Node to be expanded.
	 * 
	 * @return A list of <TT>ObjectTreeNode</TT> objects representing the child nodes for the passed node.
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	      throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo si = new DatabaseObjectInfo(
				   catalogName, schemaName, rs.getString(1), DatabaseObjectType.DATATYPE, md);
				childNodes.add(new ObjectTreeNode(session, si));
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return childNodes;
	}
}