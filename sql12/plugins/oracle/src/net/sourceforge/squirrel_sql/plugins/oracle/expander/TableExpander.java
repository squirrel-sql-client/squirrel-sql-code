package net.sourceforge.squirrel_sql.plugins.oracle.expander;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.IndexParentExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.IndexParentInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is an expander for the table nodes. It will add various Object Type
 * nodes to the table node.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableExpander implements INodeExpander
{
   private IObjectTypes _objectTypes;

   /**
	 * Default ctor.
    * @param objectTypes
    */
	public TableExpander(IObjectTypes objectTypes)
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
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode) throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String schemaName = parentDbinfo.getSchemaName();

		IDatabaseObjectInfo dbinfo = new TriggerParentInfo(parentDbinfo, schemaName, md, _objectTypes);
		addNode(session, childNodes, dbinfo);
		
		IDatabaseObjectInfo cstrinfo = new ConstraintParentInfo(parentDbinfo, schemaName, md, _objectTypes);
		addNode(session, childNodes, cstrinfo);
		
		
		IDatabaseObjectInfo indexInfo = new IndexParentInfo(parentDbinfo, schemaName, md);
		
		IndexParentExpander tableIndexExpander = new IndexParentExpander();
		tableIndexExpander.setTableIndexExtractor(new OracleTableIndexExtractor());
		addNode(session, childNodes, indexInfo, tableIndexExpander);
		
		return childNodes;
	}

	/**
	 * Adds a new {@link ObjectTreeNode} to the nodes.
	 * @param session Session to use
	 * @param childNodes Current Child nodes
	 * @param node The new node
	 */
	private void addNode(ISession session, final List<ObjectTreeNode> childNodes, IDatabaseObjectInfo node, INodeExpander ...expanders) {
		ObjectTreeNode child = new ObjectTreeNode(session, node);
		for (INodeExpander expander : expanders) {
			child.addExpander(expander);
		}
		childNodes.add(child);
	}
}
