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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode.IObjectTreeNodeType;
/**
 * This class is an expander for the schema nodes. It will add various Object Type
 * nodes to the schema node.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class SchemaExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(SchemaExpander.class);

	/** The plugin. */
	private OraclePlugin _plugin;

	/**
	 * Ctor.
	 */
	SchemaExpander(OraclePlugin plugin)
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
		final SQLConnection conn = session.getSQLConnection();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
											schemaName, "PACKAGE",
											IDatabaseObjectTypes.GENERIC_FOLDER,
											conn);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.setNodeType(IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		child.addExpander(new PackageTypeExpander(_plugin));
		childNodes.add(child);

		ObjectType objType = new ObjectType("CONSUMER GROUP",
								IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		objType = new ObjectType("FUNCTION", IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		objType = new ObjectType("INDEX", IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		objType = new ObjectType("LOB", IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		objType = new ObjectType("SEQUENCE", IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		objType = new ObjectType("TYPE", IDatabaseObjectTypes.GENERIC_LEAF,
								IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											conn, objType));

		return childNodes;
	}

	private ObjectTreeNode createObjectTypeNode(ISession session,
										String catalogName, String schemaName,
										SQLConnection conn, ObjectType objType)
	{
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
										schemaName, objType._objectTypeColumnData,
										IDatabaseObjectTypes.GENERIC_FOLDER,
										conn);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.setNodeType(IObjectTreeNodeType.GENERIC_OBJECT_TYPE_NODE);
		child.addExpander(new ObjectTypeExpander(objType));
		return child;
	}
}
