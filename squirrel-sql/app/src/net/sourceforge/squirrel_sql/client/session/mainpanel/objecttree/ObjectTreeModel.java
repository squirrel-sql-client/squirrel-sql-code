package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
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
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is the model for the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeModel extends DefaultTreeModel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ObjectTreeModel.class);

	/**
	 * ctor specifying session.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreeModel(ISession session)
	{
		super(createRootNode(session), true);
		//		_treeLoadedListeners = new ArrayList();
		//		setSession(session);
	}

	/**
	 * Create the root node for this tree.
	 * 
	 * @param	session		Current session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	private static ObjectTreeNode createRootNode(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return new RootNode(session);
	}

	private static final class RootNode extends ObjectTreeNode
	{
		RootNode(ISession session)
		{
			super(session, createDbo(session));
			setExpander(new DatabaseExpander());
		}

		private static final IDatabaseObjectInfo createDbo(ISession session)
		{
			return new DatabaseObjectInfo(null, null, session.getAlias().getName(),
											IDatabaseObjectTypes.DATABASE,
											session.getSQLConnection());
		}
	}

	private static final class DatabaseExpander implements INodeExpander
	{
		public List expand(ISession session, ObjectTreeNode parentNode)
			throws SQLException, BaseSQLException
		{
			String currentCatalogName = null;
			String currentSchemaName = null;

			IDatabaseObjectInfo dbinfo = parentNode.getDatabaseObjectInfo();
			switch (dbinfo.getDatabaseObjectType())
			{
				case IDatabaseObjectTypes.CATALOG:
				{
					currentCatalogName = dbinfo.getSimpleName();
					break;
				}
				case IDatabaseObjectTypes.SCHEMA:
				{
					currentSchemaName = dbinfo.getSimpleName();
					break;
				}
			}

			final SQLConnection conn = session.getSQLConnection();
			boolean supportsCatalogs = false;
			try
			{
				supportsCatalogs = conn.supportsCatalogs();
			}
			catch (BaseSQLException ex)
			{
			}

			boolean supportsSchemas = false;
			try
			{
				supportsSchemas = conn.supportsSchemas();
			}
			catch (BaseSQLException ex)
			{
			}

			List list = new ArrayList();

			if (dbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.DATABASE)
			{
				if (supportsCatalogs)
				{
					final String[] catalogs = conn.getCatalogs();
					for (int i = 0; i < catalogs.length; ++i)
					{
						final String catalogName = catalogs[i];
						DatabaseObjectInfo dbo = new DatabaseObjectInfo(null, null,
														catalogName,
														IDatabaseObjectTypes.CATALOG,
														conn);
						ObjectTreeNode child = new ObjectTreeNode(session, dbo, true);
						child.setExpander(this);
						list.add(child);
					}
				}
				else if (supportsSchemas)
				{
					final String[] schemas = conn.getSchemas();
					for (int i = 0; i < schemas.length; ++i)
					{
						final String schemaName = schemas[i];
						DatabaseObjectInfo dbo = new DatabaseObjectInfo(null, null,
														schemaName,
														IDatabaseObjectTypes.SCHEMA,
														conn);
						ObjectTreeNode child = new ObjectTreeNode(session, dbo, true);
						child.setExpander(this);
						list.add(child);
					}
				}
			}
			else if (dbinfo.getDatabaseObjectType() == IDatabaseObjectTypes.CATALOG)
			{
				if (supportsSchemas)
				{
					final String[] schemas = conn.getSchemas();
					for (int i = 0; i < schemas.length; ++i)
					{
						final String schemaName = schemas[i];
						DatabaseObjectInfo dbo = new DatabaseObjectInfo(
														currentCatalogName,
														null, schemaName,
														IDatabaseObjectTypes.SCHEMA,
														conn);
						ObjectTreeNode child = new ObjectTreeNode(session, dbo, true);
						child.setExpander(this);
						list.add(child);
					}
				}
			}

			return list;
		}
	}
}
