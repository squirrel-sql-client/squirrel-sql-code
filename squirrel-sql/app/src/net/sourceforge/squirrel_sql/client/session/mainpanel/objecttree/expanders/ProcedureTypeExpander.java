package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;
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

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class handles the expanding of a Procedure Type node. It will build all the
 * procedures for the procedure type.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProcedureTypeExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ProcedureTypeExpander.class);

	/** SQL that retrieves the names of Oracle packages. */
	private static String ORACLE_PACKAGES_SQL =
		"select object_name from sys.all_objects where object_type = 'PACKAGE'" +
		" and owner = ? order by object_name";

	/**
	 * Ctor.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ProcedureTypeExpander(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
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
		throws BaseSQLException
	{
		final List childNodes = new ArrayList();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLConnection conn = session.getSQLConnection();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();
		final boolean isOracle = isOracle(conn);

		if (parentDbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.ORACLE_PACKAGE)
		{
			final String packageName = parentDbinfo.getSimpleName();
			childNodes.addAll(createProcedureNodes(session, packageName, schemaName));
		}
		else
		{
			if (isOracle)
			{
				try
				{
					childNodes.addAll(createOraclePackageNodes(session, schemaName));
				}
				catch (SQLException ex)
				{
					throw new BaseSQLException(ex);
				}
			}
			else
			{
				childNodes.addAll(createProcedureNodes(session, catalogName,
														schemaName));
			}
		}

		return childNodes;
	}

	private boolean isOracle(SQLConnection conn)
	{
		final String ORACLE = "oracle";
		String dbms = null;
		try
		{
			dbms = conn.getMetaData().getDatabaseProductName();
		}
		catch (BaseSQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		catch (SQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		return (dbms != null && dbms.substring(0, ORACLE.length()).equalsIgnoreCase(ORACLE));
	}

	private List createProcedureNodes(ISession session, String catalogName,
										String schemaName)
	{
		final SQLConnection conn = session.getSQLConnection();
		final List childNodes = new ArrayList();
		IProcedureInfo[] procs = null;
		try {
			procs = conn.getProcedures(catalogName, schemaName, "%");
		} catch (BaseSQLException ignore) {
			// Assume DBMS doesn't support procedures.
		}
		for (int i = 0; i < procs.length; ++i)
		{
			ObjectTreeNode child = new ObjectTreeNode(session, procs[i]);
//				child.setExpander(this);
			childNodes.add(child);
		}
		return childNodes;
	}

	private List createOraclePackageNodes(ISession session, String schemaName)
		throws SQLException, BaseSQLException
	{
		final SQLConnection conn = session.getSQLConnection();
		final List childNodes = new ArrayList();

		// Add node to contain standalone proceduers.
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo("", schemaName,
										"%",
										IDatabaseObjectTypes.ORACLE_PACKAGE,
										conn);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.setUserObject("Standalone");
		child.addExpander(this);
		childNodes.add(child);

		// Add node for each package.
		PreparedStatement pstmt = conn.prepareStatement(ORACLE_PACKAGES_SQL);
		try
		{
			pstmt.setString(1, schemaName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				dbinfo = new DatabaseObjectInfo(null, schemaName,
										rs.getString(1),
										IDatabaseObjectTypes.ORACLE_PACKAGE,
										conn);
				child = new ObjectTreeNode(session, dbinfo);
				child.addExpander(this);
				childNodes.add(child);
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}

}
