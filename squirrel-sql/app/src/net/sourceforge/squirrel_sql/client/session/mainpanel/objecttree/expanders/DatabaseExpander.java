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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * This class handles the expanding of database, catalog and schema nodes
 * in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseExpander implements INodeExpander
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(DatabaseExpander.class);

	/** Array of the different types of tables in this database. */
	private String[] _tableTypes;

	/**
	 * Ctor.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DatabaseExpander(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		try
		{
			_tableTypes = session.getSQLConnection().getTableTypes();
		}
		catch (BaseSQLException ex)
		{
			_tableTypes = new String[] {};
			s_log.debug("DBMS doesn't support 'getTableTypes()", ex);
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
		throws SQLException, BaseSQLException
	{
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLConnection conn = session.getSQLConnection();

		boolean supportsCatalogs = false;
		try
		{
			supportsCatalogs = conn.supportsCatalogs();
		}
		catch (BaseSQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsCatalogs()", ex);
		}

		boolean supportsSchemas = false;
		try
		{
			supportsSchemas = conn.supportsSchemas();
		}
		catch (BaseSQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsSchemas()", ex);
		}

		List childNodes = new ArrayList();

		if (parentDbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.DATABASE)
		{
			if (supportsCatalogs)
			{
				childNodes.addAll(createCatalogNodes(session));
			}
			else if (supportsSchemas)
			{
				childNodes.addAll(createSchemaNodes(session, null));
			}
			else
			{
				childNodes.addAll(createObjectTypeNodes(session, null, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.CATALOG)
		{
			final String catalogName = parentDbinfo.getSimpleName();
			if (supportsSchemas)
			{
				childNodes.addAll(createSchemaNodes(session, catalogName));
			}
			else
			{
				childNodes.addAll(createObjectTypeNodes(session, catalogName, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.SCHEMA)
		{
			final String catalogName = parentDbinfo.getCatalogName();
			final String schemaName = parentDbinfo.getSimpleName();
			childNodes.addAll(createObjectTypeNodes(session, catalogName, schemaName));
		}

		return childNodes;
	}

	private List createCatalogNodes(ISession session)
		throws BaseSQLException
	{
		final List childNodes = new ArrayList();
		final SQLConnection conn = session.getSQLConnection();
		final String[] catalogs = conn.getCatalogs();
		for (int i = 0; i < catalogs.length; ++i)
		{
			final String catalogName = catalogs[i];
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(null, null,
											catalogName,
											IDatabaseObjectTypes.CATALOG,
											conn);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
//			child.addExpander(this);
			childNodes.add(child);
		}
		return childNodes;
	}

	private List createSchemaNodes(ISession session, String catalogName)
		throws BaseSQLException
	{
		final List childNodes = new ArrayList();
		final SQLConnection conn = session.getSQLConnection();
		final String[] schemas = conn.getSchemas();
		for (int i = 0; i < schemas.length; ++i)
		{
			final String schemaName = schemas[i];
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName, null,
											schemaName,
											IDatabaseObjectTypes.SCHEMA,
											conn);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
//			child.addExpander(this);
			childNodes.add(child);
		}
		return childNodes;
	}
		
	private List createObjectTypeNodes(ISession session, String catalogName,
											String schemaName)
	{
		final SQLConnection conn = session.getSQLConnection();
		final List list = new ArrayList();

		// Add table types to list.
//		final TableTypeExpander tableTypeExp = new TableTypeExpander(session);
		for (int i = 0; i < _tableTypes.length; ++i)
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(
											catalogName, schemaName,
											_tableTypes[i],
											IDatabaseObjectTypes.GENERIC_FOLDER,
											conn);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			child.setNodeType(ObjectTreeNode.IObjectTreeNodeType.TABLE_TYPE_NODE);
//			child.addExpander(tableTypeExp);
			list.add(child);
		}

		// Add UDT parent node.
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
										schemaName, "UDT",
										IDatabaseObjectTypes.GENERIC_FOLDER,
										conn);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			child.setNodeType(ObjectTreeNode.IObjectTreeNodeType.UDT_TYPE_NODE);
//			child.addExpander(new UDTTypeExpander(session));
			list.add(child);
		}

		// Add stored proc parent node.
		boolean supportsStoredProcs = false;
		try
		{
			supportsStoredProcs = conn.supportsStoredProcedures();
		}
		catch (BaseSQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsStoredProcedures()'", ex);
		}
		if (supportsStoredProcs)
		{
//			ProcedureTypeExpander exp = new ProcedureTypeExpander(session);
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
												schemaName, "PROCEDURE",
												IDatabaseObjectTypes.GENERIC_FOLDER,
												conn);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			child.setNodeType(ObjectTreeNode.IObjectTreeNodeType.PROCEDURE_TYPE_NODE);
//			child.addExpander(exp);
			list.add(child);
		}

		return list;
	}

}
