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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ProcedureTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.UDTTypeExpander;
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

	/** Current session. */
	private ISession _session;

	/**
	 * Collection of <TT>INodeExpander</TT> objects. Each entry is a <TT>List</TT>
	 * of <TT>INodeExpander</TT> objects. The key to the list is the
	 * node type.
	 */
	private Map _expanders = new HashMap();

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
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		// Standard expanders.
		final INodeExpander expander = new DatabaseExpander(session);
		addExpander(DatabaseObjectType.SESSION, expander);
		addExpander(DatabaseObjectType.CATALOG, expander);
		addExpander(DatabaseObjectType.SCHEMA, expander);
		addExpander(IObjectTreeAPI.TABLE_TYPE_DBO, new TableTypeExpander());
		addExpander(IObjectTreeAPI.PROC_TYPE_DBO, new ProcedureTypeExpander());
		addExpander(IObjectTreeAPI.UDT_TYPE_DBO, new UDTTypeExpander());
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
	public synchronized void addExpander(DatabaseObjectType dboType,
												INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		getExpandersList(dboType).add(expander);
	}

	/**
	 * Return an array of the node expanders for the passed database object type.
	 * 
	 * @param	dboType		Database object type.

	 * @return	an array of the node expanders for the passed database object type.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if null ObjectTreeNodeType passed.
	 */
	public synchronized INodeExpander[] getExpanders(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		List list = getExpandersList(dboType);
		return (INodeExpander[])list.toArray(new INodeExpander[list.size()]);
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
	 * Get the collection of expanders for the passed node type. If one
	 * doesn't exist then create an empty one.
	 * 
	 * @param	dboType		Database object type.
	 */
	private List getExpandersList(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		IIdentifier key = dboType.getIdentifier();
		List list = (List)_expanders.get(key);
		if (list == null)
		{
			list = new ArrayList();
			_expanders.put(key, list);
		}
		return list;
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
		}

		private static final IDatabaseObjectInfo createDbo(ISession session)
		{
			return new DatabaseObjectInfo(null, null, session.getAlias().getName(),
											DatabaseObjectType.SESSION,
											session.getSQLConnection().getSQLMetaData());
		}
	}
}
