package net.sourceforge.squirrel_sql.client.session.objectstree;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class TableTypesGroupNode extends BaseNode
{
	private interface i18n
	{
		String NO_CATALOG = "No Catalog"; // i18n or Replace with md.getCatalogueTerm.
	}

	private String _catalogName;
	private String _schemaName;
	private String _catalogIdentifier;
	private String _schemaIdentifier;

	TableTypesGroupNode(
		ISession session,
		ObjectsTreeModel treeModel,
		String catalogName,
		String catalogIdentifier,
		String schemaName,
		String schemaIdentifier)
	{
		super(session, treeModel, generateName(catalogName, schemaName));
		_catalogIdentifier = catalogIdentifier;
		_schemaIdentifier = schemaIdentifier;
		_catalogName = catalogName;
		_schemaName = schemaName;
	}

	String getCatalogName()
	{
		return _catalogName;
	}

	String getCatalogIdentifier()
	{
		return _catalogIdentifier;
	}

	String getSchemaName()
	{
		return _schemaName;
	}

	String getSchemaIdentifier()
	{
		return _schemaIdentifier;
	}

	private static String generateName(String catalogName, String schemaName)
	{
		StringBuffer buf = new StringBuffer();
		if (catalogName != null)
		{
			buf.append(catalogName);
			if (schemaName != null)
			{
				buf.append(".");
			}
		}
		if (schemaName != null)
		{
			buf.append(schemaName);
		}
		if (buf.length() == 0)
		{
			buf.append(i18n.NO_CATALOG);
		}
		return buf.toString();
	}

	public void expand() throws java.sql.SQLException
	{
		if(getChildCount() == 0)
		{
			getSession().getApplication().getThreadPool().addTask(
				new TableTypesTreeLoader(addLoadingNode()));
		}
		super.expand();
	}

	protected TreeNodesLoader getTreeNodesLoader()
	{
		return new TableTypesTreeLoader(null);
	}

	public boolean isLeaf()
	{
		return false;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof TableTypesGroupNode)
		{
			TableTypesGroupNode info = (TableTypesGroupNode) obj;
			if ((info._catalogIdentifier == null && _catalogIdentifier == null)
				|| ((info._catalogIdentifier != null && _catalogIdentifier != null)
					&& info._catalogIdentifier.equals(_catalogIdentifier)))
			{
				if ((info._catalogName == null && _catalogName == null)
					|| ((info._catalogName != null && _catalogName != null)
						&& info._catalogName.equals(_catalogName)))
				{
					if ((info._schemaIdentifier == null && _schemaIdentifier == null)
						|| ((info._schemaIdentifier != null && _schemaIdentifier != null)
							&& info._schemaIdentifier.equals(_schemaIdentifier)))
					{
						return (
							(info._schemaName == null && _schemaName == null)
								|| ((info._schemaName != null && _schemaName != null)
									&& info._schemaName.equals(_schemaName)));
					}
				}
			}
		}
		return false;
	}

	protected class TableTypesTreeLoader extends BaseNode.TreeNodesLoader
	{
		/**
		 * Constructor for ObjectsTreeLoader.
		 * @param loading
		 */
		TableTypesTreeLoader(MutableTreeNode loading)
		{
			super(loading);
		}

		/*
		 * @see TreeNodesLoader#getNodeList(ISession, SQLConnection, ObjectsTreeModel)
		 */
		public List getNodeList(
			final ISession session,
			final SQLConnection conn,
			final ObjectsTreeModel treeModel)
			throws SQLException
		{
			final ArrayList tableTypeList = new ArrayList();
			if (conn != null)
			{
				String[] tableTypes = treeModel.getTableTypes();
				if(tableTypes.length == 0)
				{
						tableTypeList.add(new TableObjectTypeNode(session, treeModel, TableTypesGroupNode.this, "TABLE", "TABLE"));
				}
				else
				{
					for (int i = 0; i < tableTypes.length; ++i)
					{
						String tableType = tableTypes[i];
						tableTypeList.add(new TableObjectTypeNode(session, treeModel, TableTypesGroupNode.this, tableType, tableType));
					}
				}

				tableTypeList.add(new UDTObjectTypeNode(session, treeModel, TableTypesGroupNode.this));

				try
				{
					if (session.getSQLConnection().supportsStoredProcedures())
					{
						tableTypeList.add(new ProcedureObjectTypeNode(getSession(), getTreeModel(), TableTypesGroupNode.this));
					}
				}
				catch (SQLException ignore)
				{
					// Any probs just assume that db doesn't supports procs.
				}

				// Load object types from plugins.
				PluginManager mgr = getSession().getApplication().getPluginManager();
				IPluginDatabaseObjectType[] types = mgr.getDatabaseObjectTypes(session);
				for (int i = 0; i < types.length; ++i)
				{
					tableTypeList.add(new PluginGroupNode(session, treeModel, TableTypesGroupNode.this, types[i]));
				}
				}
				return tableTypeList;
		}
	}
}