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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
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
			_tableTypes = session.getSQLConnection().getSQLMetaData().getTableTypes();
		}
		catch (SQLException ex)
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
		throws SQLException
	{
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = conn.getSQLMetaData();

		boolean supportsCatalogs = false;
		try
		{
			supportsCatalogs = md.supportsCatalogs();
		}
		catch (SQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsCatalogs()", ex);
		}

		boolean supportsSchemas = false;
		try
		{
			supportsSchemas = md.supportsSchemas();
		}
		catch (SQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsSchemas()", ex);
		}

		List childNodes = new ArrayList();

		if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.SESSION)
		{
			if (supportsCatalogs)
			{
				childNodes.addAll(createCatalogNodes(session, md));
			}
			else if (supportsSchemas)
			{
				childNodes.addAll(createSchemaNodes(session, md, null));
			}
			else
			{
				childNodes.addAll(createObjectTypeNodes(session, null, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.CATALOG)
		{
			final String catalogName = parentDbinfo.getSimpleName();
			if (supportsSchemas)
			{
				childNodes.addAll(createSchemaNodes(session, md, catalogName));
			}
			else
			{
				childNodes.addAll(createObjectTypeNodes(session, catalogName, null));
			}
		}
		else if (parentDbinfo.getDatabaseObjectType() == DatabaseObjectType.SCHEMA)
		{
			final String catalogName = parentDbinfo.getCatalogName();
			final String schemaName = parentDbinfo.getSimpleName();
			childNodes.addAll(createObjectTypeNodes(session, catalogName, schemaName));
		}

		return childNodes;
	}

	private List createCatalogNodes(ISession session, SQLDatabaseMetaData md)
		throws SQLException
	{
		final List childNodes = new ArrayList();
		final String[] catalogs = md.getCatalogs();
		for (int i = 0; i < catalogs.length; ++i)
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(null, null,
											catalogs[i],
											DatabaseObjectType.CATALOG,
											md);
			childNodes.add(new ObjectTreeNode(session, dbo));
		}
		return childNodes;
	}

	private List createSchemaNodes(ISession session, SQLDatabaseMetaData md,
										String catalogName)
		throws SQLException
	{
		final List childNodes = new ArrayList();
		final String[] schemas = md.getSchemas();
		for (int i = 0; i < schemas.length; ++i)
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName, null,
											schemas[i],
											DatabaseObjectType.SCHEMA, md);
			childNodes.add(new ObjectTreeNode(session, dbo));
		}
		return childNodes;
	}
		
	private List createObjectTypeNodes(ISession session, String catalogName,
											String schemaName)
	{
		final SQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = conn.getSQLMetaData();
		final List list = new ArrayList();

		// Add table types to list.
		if (_tableTypes.length > 0)
		{
			for (int i = 0; i < _tableTypes.length; ++i)
			{
				IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
												schemaName, _tableTypes[i],
												IObjectTreeAPI.TABLE_TYPE_DBO, md);
				ObjectTreeNode child = new ObjectTreeNode(session, dbo);
				list.add(child);
			}
		}
		else
		{
			s_log.debug("List of table types is empty so trying null table type to load all tables");
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
											schemaName, null,
											IObjectTreeAPI.TABLE_TYPE_DBO, md);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			child.setUserObject("TABLE");
			list.add(child);
		}

		// Add stored proc parent node.
		boolean supportsStoredProcs = false;
		try
		{
			supportsStoredProcs = md.supportsStoredProcedures();
		}
		catch (SQLException ex)
		{
			s_log.debug("DBMS doesn't support 'supportsStoredProcedures()'", ex);
		}
		if (supportsStoredProcs)
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
												schemaName, "PROCEDURE",
												IObjectTreeAPI.PROC_TYPE_DBO, md);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			list.add(child);
		}

		// Add UDT parent node.
		{
			IDatabaseObjectInfo dbo = new DatabaseObjectInfo(catalogName,
										schemaName, "UDT",
										IObjectTreeAPI.UDT_TYPE_DBO, md);
			ObjectTreeNode child = new ObjectTreeNode(session, dbo);
			list.add(child);
		}

		return list;
	}

}
