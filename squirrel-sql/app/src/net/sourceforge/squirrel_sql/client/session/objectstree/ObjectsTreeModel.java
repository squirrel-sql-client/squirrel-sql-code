package net.sourceforge.squirrel_sql.client.session.objectstree;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.tree.MutableTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.NoConnectionException;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObject;
import net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;

public class ObjectsTreeModel extends DefaultTreeModel
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ObjectsTreeModel.class);

	private ISession _session;
	private ArrayList _treeLoadedListeners;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String DATABASE = "Database";
		String NO_CATALOG = "No Catalog"; // i18n or Replace with md.getCatalogueTerm.
		String PROCEDURE = "PROCEDURE";
		String UDT = "UDT";
	}

	public ObjectsTreeModel(ISession session)
	{
		super(new DefaultMutableTreeNode());
		_treeLoadedListeners = new ArrayList();
		setSession(session);
	}

	private void setSession(ISession session)
	{
		_session = session;
		DatabaseNode rootNode = new DatabaseNode(session, this);
		/*i18n*/
		setRoot(rootNode);
	}

	public void addTreeLoadedListener(TreeLoadedListener listener)
	{
		if (listener != null && !_treeLoadedListeners.contains(listener))
		{
			_treeLoadedListeners.add(listener);
		}
	}
	public void removeTreeLoadedListener(TreeLoadedListener listener)
	{
		if (listener != null)
		{
			_treeLoadedListeners.remove(listener);
		}
	}
	protected void fireTreeLoaded()
	{
		for (int i = _treeLoadedListeners.size(); --i >= 0;)
		{
			((TreeLoadedListener) _treeLoadedListeners.get(i)).treeLoaded();
		}
	}

	public void fillTree()
	{
		Object o = getRoot();
		if(o instanceof BaseNode)
		{
			try
			{
				((BaseNode)o).expand();
			} catch(Exception e)
			{
				s_log.error("couldn't expand tree" , e);
			}
		}
	}

	private SQLConnection getConnection()
	{
		return _session.getSQLConnection();
	}

	public List refresh() throws BaseSQLException
	{
		Object o = getRoot();
		if (o instanceof BaseNode)
		{
			return ((BaseNode) o).refresh();
		}
		return null;
	}

	String[] getTableTypes()
	{
		try
		{
			return getConnection().getTableTypes();
		}
		catch (BaseSQLException ignore)
		{
			return new String[] {
			};
			// Assume driver doesn't handle getTableTypes().
		}
	}
}