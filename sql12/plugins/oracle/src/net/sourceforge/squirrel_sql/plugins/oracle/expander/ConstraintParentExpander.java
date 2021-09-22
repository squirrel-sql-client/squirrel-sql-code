/*
 * Copyright (C) 2010 Bogdan Cristian Paulon
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
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.timeoutproxy.StatementExecutionTimeOutHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the expanding of the Constraint node. It will
 * show all the constraints for selected table.
 * 
 * @author bpaulon
 */
public class ConstraintParentExpander implements INodeExpander {
	
	private static String SQL = "select constraint_name"
			+ " from all_constraints" 
			+ " where owner = ?"
			+ " and table_name = ?" 
			+ " order by constraint_name asc";
   private IObjectTypes _objectTypes;

   public ConstraintParentExpander(IObjectTypes objectTypes) {
		super();
      _objectTypes = objectTypes;
   }
	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException {
		
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final IDatabaseObjectInfo tableInfo = ((ConstraintParentInfo)parentDbinfo).getTableInfo();
		final String schemaName = parentDbinfo.getSchemaName();
		//final PreparedStatement pstmt = conn.prepareStatement(SQL);
		final PreparedStatement pstmt = StatementExecutionTimeOutHandler.prepareStatement(conn, SQL);


		try	{
			pstmt.setString(1, tableInfo.getSchemaName());
			pstmt.setString(2, tableInfo.getSimpleName());
			ResultSet rs = pstmt.executeQuery();
			try {
				while (rs.next()) {
					DatabaseObjectInfo doi = new DatabaseObjectInfo(null,
												schemaName, 
												rs.getString(1),
												_objectTypes.getConstraint(),
												md);
					childNodes.add(new ObjectTreeNode(session, doi));
				}
			} finally {
				rs.close();
			}
		} finally {
			pstmt.close();
		}
		
		return childNodes;
	}
}