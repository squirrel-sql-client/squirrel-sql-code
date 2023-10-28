package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (c) 2004 Jason Height.
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

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ProcedureTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.UDTTypeExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.util.Iterator;

/**
 * This is the model for the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeModel extends DefaultTreeModel
{

	/**
	 * Collection of <TT>INodeExpander</TT> objects. Each entry is a <TT>List</TT>
	 * of <TT>INodeExpander</TT> objects. The key to the list is the
	 * node type.
	 */
	private ObjectTreeExpanders _expanders = new ObjectTreeExpanders();


	/**
	 * ctor specifying session.
	 *
	 * @param	session	Current session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreeModel(final ISession session)
	{
		super(createRootNode(session), true);
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		final INodeExpander expander = new DatabaseExpander(session);

		SwingUtilities.invokeLater(() -> initExpanders(expander, session));
	}

	private void initExpanders(INodeExpander expander, ISession session)
	{
		addExpander(DatabaseObjectType.CATALOG, expander);
		addExpander(DatabaseObjectType.SCHEMA, expander);

		boolean foundTableExp = false;
		boolean foundProcExp = false;
		boolean foundUDTExp = false;
		boolean foundDatabaseExp = false;
		final IPluginManager pmgr = session.getApplication().getPluginManager();
		for (Iterator<SessionPluginInfo> pluginItr = pmgr.getSessionPluginIterator(); pluginItr.hasNext(); )
		{
			ISessionPlugin p = (pluginItr.next()).getSessionPlugin();
			INodeExpander tableExp = p.getDefaultNodeExpander(session, DatabaseObjectType.TABLE_TYPE_DBO);
			if (tableExp != null)
			{
				foundTableExp = true;
				addExpander(DatabaseObjectType.TABLE_TYPE_DBO, tableExp);
			}
			INodeExpander procExp = p.getDefaultNodeExpander(session, DatabaseObjectType.PROC_TYPE_DBO);
			if (procExp != null)
			{
				foundProcExp = true;
				addExpander(DatabaseObjectType.PROC_TYPE_DBO, procExp);
			}
			INodeExpander udtExp = p.getDefaultNodeExpander(session, DatabaseObjectType.UDT_TYPE_DBO);
			if (udtExp != null)
			{
				foundUDTExp = true;
				addExpander(DatabaseObjectType.UDT_TYPE_DBO, udtExp);
			}
			INodeExpander databaseExp = p.getDefaultNodeExpander(session, DatabaseObjectType.DATABASE_TYPE_DBO);
			if (databaseExp != null)
			{
				foundDatabaseExp = true;
				addExpander(DatabaseObjectType.SESSION, databaseExp);
			}
		}

		if (!foundTableExp)
		{
			addExpander(DatabaseObjectType.TABLE_TYPE_DBO, new TableTypeExpander());
		}
		if (!foundProcExp)
		{
			addExpander(DatabaseObjectType.PROC_TYPE_DBO, new ProcedureTypeExpander());
		}
		if (!foundUDTExp)
		{
			addExpander(DatabaseObjectType.UDT_TYPE_DBO, new UDTTypeExpander());
		}
		if (!foundDatabaseExp)
		{
			addExpander(DatabaseObjectType.SESSION, expander);
		}
		reload();
	}

	/**
	 * Add an expander for the specified database object type in the
	 * object tree.
	 *
	 * @param	dboType		Database object type.
	 * @param	expander	Expander called to add children to a parent node.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>INodeExpander</TT> or
	 * 			<TT>ObjectTreeNodeType</TT> passed.
	 */
	public void addExpander(DatabaseObjectType dboType, INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		_expanders.addExpander(dboType, expander);
	}



	/**
	 * Return the root node.
	 *
	 * @return	the root node.
	 */
	ObjectTreeNode getRootObjectTreeNode()
	{
		return (ObjectTreeNode)getRoot();
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
		return new ObjectTreeRootNode(session);
	}

	public boolean isRootNode(Object node)
   {
      return node instanceof ObjectTreeRootNode;
   }

	public INodeExpander[] getExpanders(DatabaseObjectType dboType)
	{
		return _expanders.getExpanders(dboType);
	}

	public void addKnownDatabaseObjectType(DatabaseObjectType dboType)
	{
		_expanders.addKnownDatabaseObjectType(dboType);
	}

	public DatabaseObjectType[] getDatabaseObjectTypes()
	{
		return _expanders.getDatabaseObjectTypes();
	}
}
