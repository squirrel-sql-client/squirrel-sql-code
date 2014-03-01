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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

/**
 * This class is an expander for the schema nodes. It will add Object Type nodes beneath the schema node.
 * 
 * @author manningr
 */
public class SchemaExpander implements INodeExpander
{

	INodeExpanderFactory _inodeFactory = null;

	DatabaseObjectType _dbObjType = null;

	/**
	 * @param inodeExpFactory
	 *           the factory that produces INodeExpanders for objects under the schema node of the tree.
	 * @param dbObjType
	 *           the type of the object to create under the schema node of the tree.
	 */
	public SchemaExpander(INodeExpanderFactory inodeExpFactory, DatabaseObjectType dbObjType)
	{
		super();
		this._inodeFactory = inodeExpFactory;
		this._dbObjType = dbObjType;
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
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo seqInfo =
			new DatabaseObjectInfo(catalogName, schemaName, _inodeFactory.getParentLabelForType(_dbObjType),
				_dbObjType, md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(_inodeFactory.createExpander(_dbObjType));
		childNodes.add(node);

		return childNodes;
	}

}
