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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
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
	 * <TT>IDatabaseObjectTypes</TT> entry. I.E. each list contains all the
	 * expanders for a <TT>ObjectTreeNode</TT> that represents an object of
	 * a particular <TT>IDatabaseObjectTypes</TT> value.
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
		_session = session;

		// Standard expander for database, catalog and schema nodes.
		INodeExpander expander = new DatabaseExpander(session);
		registerExpander(IDatabaseObjectTypes.DATABASE, expander);
		registerExpander(IDatabaseObjectTypes.CATALOG, expander);
		registerExpander(IDatabaseObjectTypes.SCHEMA, expander);

		//		_treeLoadedListeners = new ArrayList();
		//		setSession(session);
	}

	/**
	 * Register an expander for the specified database object type in the
	 * object tree.
	 * 
	 * @param	dbObjectType	Database object type.
	 *							@see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes
	 * @param	expander		Expander called to add children to a parent node.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>INodeExpander</TT> thrown.
	 */
	public synchronized void registerExpander(int dbObjectType,
													INodeExpander expander)
	{
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		getExpandersList(dbObjectType).add(expander);
	}

	/**
	 * Return an array of the node expanders for the passed database object type.
	 * 
	 * @return	an array of the node expanders for the passed database object type.
	 */
	public synchronized INodeExpander[] getExpanders(int dbObjectType)
	{
		List list = getExpandersList(dbObjectType);
		return (INodeExpander[])list.toArray(new INodeExpander[list.size()]);
	}

	private List getExpandersList(int dbObjectType)
	{
		Integer key = new Integer(dbObjectType);
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
			//addExpander(new DatabaseExpander(session));
		}

		private static final IDatabaseObjectInfo createDbo(ISession session)
		{
			return new DatabaseObjectInfo(null, null, session.getAlias().getName(),
											IDatabaseObjectTypes.DATABASE,
											session.getSQLConnection());
		}
	}
}
