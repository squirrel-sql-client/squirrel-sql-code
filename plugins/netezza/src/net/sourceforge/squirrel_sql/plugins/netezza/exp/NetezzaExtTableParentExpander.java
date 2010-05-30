package net.sourceforge.squirrel_sql.plugins.netezza.exp;

/*
 * Copyright (C) 2010 Rob Manning
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

/**
 * This class handles the expanding of the "External Table" node. It will give a list of all the External 
 * Tables available in the schema.
 */
public class NetezzaExtTableParentExpander implements INodeExpander
{
	/**
	 * Default ctor.
	 */
	public NetezzaExtTableParentExpander() {
		super();
	}

	/**
	 * Though this implementation looks incomplete, it is nonetheless correct.  Since the Netezza
	 * driver knows about "EXTERNAL TABLE" table types, it returns them from DatabaseMetaData.getTables.
	 * So it is enough to merely plugin an expander that supports this type and when SQuirreL attempts to 
	 * populate the "EXTERNAL TABLE" tree node, the tables are provided by the driver.  There was a real 
	 * implementation here before that executed the following SQL:
	 * 
	 * SELECT * FROM _v_objs_owned 
     * where class = 'EXTERNAL TABLE' 
     * and owner = ?
     * and database = ?
     * 
     * However, for reasons mentioned above, this resulted in duplicate entries in the object tree under the 
     * "EXTERNAL TABLE" node.
	 * 
	 * @param session
	 *        Current session.
	 * @param parentNode
	 *        Node to be expanded.
	 * 
	 * @return an empty list of ObjectTreeNode type
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	      throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		return childNodes;
	}
}
