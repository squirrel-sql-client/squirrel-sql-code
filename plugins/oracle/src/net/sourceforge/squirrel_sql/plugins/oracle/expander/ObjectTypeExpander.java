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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class handles the expanding of an Oracle specific object type node.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTypeExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ObjectTypeExpander.class);

	/** SQL that retrieves the objects for the object types. */
	private static String SQL =
		"select object_name from sys.all_objects where object_type = ?" +
		" and owner = ? order by object_name";

	/** Type of the objects to be displayed in the child nodes. */
	private ObjectType _objectType;

	/**
	 * Ctor.
	 * 
	 * @param	objectType	Object type to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> objectType passed.
	 */
	ObjectTypeExpander(ObjectType objectType)
	{
		super();
		if (objectType == null)
		{
			throw new IllegalArgumentException("ObjectType == null");
		}
		_objectType = objectType;
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
		final String schemaName = parentDbinfo.getSchemaName();

		childNodes.addAll(createNodes(session, catalogName, schemaName));
		return childNodes;
	}

	private List createNodes(ISession session, String catalogName,
											String schemaName)
		throws SQLException
	{
		final SQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = conn.getSQLMetaData();
		final List childNodes = new ArrayList();

		// Add node for each object.
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{
			pstmt.setString(1, _objectType._objectTypeColumnData);
			pstmt.setString(2, schemaName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
										schemaName, rs.getString(1),
										_objectType._childDboType, md);
				childNodes.add(new ObjectTreeNode(session, dbinfo));
//System.out.println("Function:     " + (_objectType._childDboType == IObjectTypes.FUNCTION));
//System.out.println("Function Grp: " + (_objectType._childDboType == IObjectTypes.FUNCTION_GRP));
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}
}
