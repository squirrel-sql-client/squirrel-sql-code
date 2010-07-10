package net.sourceforge.squirrel_sql.plugins.oracle.expander;
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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class is an expander for the schema nodes. It will add various Object Type
 * nodes to the schema node.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SchemaExpander implements INodeExpander
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SchemaExpander.class);

	/** The plugin. */
	private OraclePlugin _plugin;

	/**
	 * Ctor.
	 */
	public SchemaExpander(OraclePlugin plugin)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("OraclePlugin == null");
		}
		_plugin = plugin;
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
		throws SQLException
	{
		final List childNodes = new ArrayList();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
											schemaName, "PACKAGE",
											IObjectTypes.PACKAGE_PARENT, md);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.addExpander(new PackageParentExpander(_plugin));
		childNodes.add(child);

		ObjectType objType;
		objType = new ObjectType(IObjectTypes.CONSUMER_GROUP_PARENT, "CONSUMER GROUP",
										IObjectTypes.CONSUMER_GROUP);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.FUNCTION_PARENT, "FUNCTION",
									DatabaseObjectType.FUNCTION);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.INDEX_PARENT, "INDEX", DatabaseObjectType.INDEX);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.LOB_PARENT, "LOB", IObjectTypes.LOB);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName,
										schemaName, "SEQUENCE",
										IObjectTypes.SEQUENCE_PARENT, md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(new SequenceParentExpander(_plugin));
		childNodes.add(node);

		objType = new ObjectType(IObjectTypes.TYPE_PARENT, "TYPE", IObjectTypes.TYPE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		return childNodes;
	}

	private ObjectTreeNode createObjectTypeNode(ISession session,
										String catalogName, String schemaName,
										SQLDatabaseMetaData md, ObjectType objType)
	{
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
										schemaName, objType._objectTypeColumnData,
										objType._dboType, md);
		ObjectTreeNode node = new ObjectTreeNode(session, dbinfo);
		node.addExpander(new ObjectTypeExpander(objType));
		return node;
	}
}
