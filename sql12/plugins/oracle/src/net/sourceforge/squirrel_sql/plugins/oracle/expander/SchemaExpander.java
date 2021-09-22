package net.sourceforge.squirrel_sql.plugins.oracle.expander;
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an expander for the schema nodes. It will add various Object Type
 * nodes to the schema node.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SchemaExpander implements INodeExpander
{

   private IObjectTypes _objectTypes;

   /**
	 * Ctor.
    * @param objectTypes
    */
	public SchemaExpander(IObjectTypes objectTypes)
	{
		super();
      _objectTypes = objectTypes;
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
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName, schemaName, "PACKAGE", _objectTypes.getPackageParent(), md);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.addExpander(new PackageParentExpander(_objectTypes));
		childNodes.add(child);

		ObjectType objType;
		objType = new ObjectType(_objectTypes.getConsumerGroupParent(), "CONSUMER GROUP", _objectTypes.getConsumerGroup());
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName, md, objType));

		objType = new ObjectType(_objectTypes.getFunctionParent(), "FUNCTION", DatabaseObjectType.FUNCTION);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName, md, objType));

		objType = new ObjectType(_objectTypes.getIndexParent(), "INDEX", DatabaseObjectType.INDEX);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName, md, objType));

		objType = new ObjectType(_objectTypes.getLobParent(), "LOB", _objectTypes.getLob());
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName, md, objType));

		IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName, schemaName, "SEQUENCE",_objectTypes.getSequenceParent(), md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(new SequenceParentExpander());
		childNodes.add(node);

		objType = new ObjectType(_objectTypes.getTypeParent(), "TYPE", _objectTypes.getType());
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName, md, objType));

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
