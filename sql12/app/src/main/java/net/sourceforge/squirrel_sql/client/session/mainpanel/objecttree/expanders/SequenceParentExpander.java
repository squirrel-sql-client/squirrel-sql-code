package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

/*
 * Copyright (C) 2009 Rob Manning
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
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class handles the expanding of the "Sequence Group" node. It will give a list of ObjectTreeNodes that
 * represent all the Sequences available in the schema. Finding sequences in a schema is database-specific and
 * is pluggable in this class using the ISequenceParentExtractor interface to implement the database-specific
 * part.  Plugins can register an expander which uses this class (with their database-specific sequence extractor
 * implementation) with a call to IObjectTreeApi.addExpander(DatabaseObjectType.SCHEMA, expander).  See the 
 * PostgresPlugin and the PostgresSequenceInodeExpanderFactory for an example.
 */
public class SequenceParentExpander implements INodeExpander
{

	private final static ILogger s_log = LoggerController.createLogger(SequenceParentExpander.class);
		
	/**
	 * The database-specific class that provides the query for "finding" sequences in a schema/catalog.
	 */
	private ISequenceParentExtractor extractor = null;

	/**
	 * Default ctor.
	 */
	public SequenceParentExpander()
	{
		super();
	}

	/**
	 * @param extractor
	 *           the extractor to set
	 */
	public void setExtractor(ISequenceParentExtractor extractor)
	{
		this.extractor = extractor;
	}

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
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();
		final ObjFilterMatcher filterMatcher = new ObjFilterMatcher(session.getProperties());

		final String sequenceParentQuerySql = extractor.getSequenceParentQuery();
		if (s_log.isDebugEnabled()) {
			s_log.debug("createChildren: running sequence parent query for sequence children: "
				+ sequenceParentQuerySql);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(sequenceParentQuerySql);		
			extractor.bindParameters(pstmt, parentDbinfo, filterMatcher);

			rs = pstmt.executeQuery();
			while (rs.next())
			{
				final IDatabaseObjectInfo si =
					new DatabaseObjectInfo(catalogName, schemaName, rs.getString(1), DatabaseObjectType.SEQUENCE,
						md);
				if (filterMatcher.matches(si.getSimpleName()))
				{
					childNodes.add(new ObjectTreeNode(session, si));
				}
			}
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(pstmt);
		}
		return childNodes;
	}
}
