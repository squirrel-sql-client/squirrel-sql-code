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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.tree.TreeNode;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public final class TableNode extends DatabaseObjectNode implements ITableInfo
{
	private final ITableInfo _tableInfo;
	private final TableNode[] _children;

	public TableNode(ISession session, ObjectsTreeModel treeModel,
						ITableInfo tableInfo, Statement rowCountStmt)
		throws SQLException
	{
		super(session, treeModel, tableInfo);
		if (tableInfo == null)
		{
			throw new IllegalArgumentException("Null ITableInfo passed");
		}
		_tableInfo = tableInfo;
		setUserObject(getDisplayText(rowCountStmt));
		ITableInfo[] infoChildren = tableInfo.getChildTables();
		if (infoChildren != null)
		{
			_children = new TableNode[infoChildren.length];
			for (int i = 0; i < _children.length; ++i)
			{
				_children[i] =
					new TableNode(
						session,
						treeModel,
						infoChildren[i],
						rowCountStmt);
			}
		}
		else
		{
			_children = null;
		}
	}

	public String getCatalogName()
	{
		return _tableInfo.getCatalogName();
	}

	public String getSchemaName()
	{
		return _tableInfo.getSchemaName();
	}

	public String getSimpleName()
	{
		return _tableInfo.getSimpleName();
	}

	public String getQualifiedName()
	{
		return _tableInfo.getQualifiedName();
	}

	public String getType()
	{
		return _tableInfo.getType();
	}

	public String getRemarks()
	{
		return _tableInfo.getRemarks();
	}

	public ITableInfo[] getChildTables()
	{
		return _tableInfo.getChildTables();
	}

	public Enumeration children()
	{
		return new Enumeration()
		{
			int pos = 0;
			public boolean hasMoreElements()
			{
				return (_children != null && _children.length < pos);
			}
			public Object nextElement()
			{
				return _children[pos++];
			}
		};
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int i)
	{
		return _children[i];
	}

	public int getChildCount()
	{
		return (_children == null) ? 0 : _children.length;
	}

	public JComponent getDetailsPanel()
	{
		final ISession session = getSession();
		final IPlugin plugin = session.getApplication().getDummyAppPlugin();
		TablePanel pnl =
			(TablePanel) session.getPluginObject(
				plugin,
				ISession.ISessionKeys.TABLE_DETAIL_PANEL_KEY);
		//	  if (pnl == null) {
		//		  pnl = new TablePanel(session);
		//		  session.putPluginObject(plugin, ISession.ISessionKeys.TABLE_DETAIL_PANEL_KEY, pnl);
		//	  }
		pnl.setTableInfo(this);
		return pnl;
	}

	public boolean isLeaf()
	{
		return _children == null;
	}

	private String getDisplayText(Statement rowCountStmt)
	{
		if (rowCountStmt != null)
		{
			try
			{
				ResultSet rs =
					rowCountStmt.executeQuery(
						"select count(*) from "
							+ _tableInfo.getQualifiedName());
				try
				{
					long nbrRows = 0;
					if (rs.next())
					{
						nbrRows = rs.getLong(1);
					}
					return _tableInfo.getSimpleName() + " (" + nbrRows + ")";
				}
				finally
				{
					rs.close();
				}
			}
			catch (SQLException ex)
			{
				return _tableInfo.getSimpleName();
			}
		}
		else
		{
			return _tableInfo.getSimpleName();
		}
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof TableNode)
		{
			return ((TableNode) obj)._tableInfo.equals(_tableInfo);
		}
		return false;
	}
}