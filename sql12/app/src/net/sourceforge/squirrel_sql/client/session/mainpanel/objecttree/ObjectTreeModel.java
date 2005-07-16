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
import net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ProcedureTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.UDTTypeExpander;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.*;
/**
 * This is the model for the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeModel extends DefaultTreeModel
{
   private static ILogger logger =
     LoggerController.createLogger(ObjectTreeModel.class);


	/**
	 * Collection of <TT>INodeExpander</TT> objects. Each entry is a <TT>List</TT>
	 * of <TT>INodeExpander</TT> objects. The key to the list is the
	 * node type.
	 */
	private Map _expanders = new HashMap();

	private final Set _objectTypes = new TreeSet(new DatabaseObjectTypeComparator());

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

		// Standard expanders.
		final INodeExpander expander = new DatabaseExpander(session);
		addExpander(DatabaseObjectType.SESSION, expander);
		addExpander(DatabaseObjectType.CATALOG, expander);
		addExpander(DatabaseObjectType.SCHEMA, expander);

		boolean foundTableExp = false;
		boolean foundProcExp = false;
		boolean foundUDTExp = false;
		final PluginManager pmgr = session.getApplication().getPluginManager();
		for (Iterator pluginItr = pmgr.getSessionPluginIterator(); pluginItr.hasNext();)
		{
			ISessionPlugin p = ((SessionPluginInfo)pluginItr.next()).getSessionPlugin();
			INodeExpander tableExp = p.getDefaultNodeExpander(session, IObjectTreeAPI.TABLE_TYPE_DBO);
			if (tableExp != null)
			{
				foundTableExp = true;
				addExpander(IObjectTreeAPI.TABLE_TYPE_DBO, tableExp);
			}
			INodeExpander procExp = p.getDefaultNodeExpander(session, IObjectTreeAPI.PROC_TYPE_DBO);
			if (procExp != null)
			{
				foundProcExp = true;
				addExpander(IObjectTreeAPI.PROC_TYPE_DBO, procExp);
			}
			INodeExpander udtExp = p.getDefaultNodeExpander(session, IObjectTreeAPI.UDT_TYPE_DBO);
			if (udtExp != null)
			{
				foundUDTExp = true;
				addExpander(IObjectTreeAPI.UDT_TYPE_DBO, udtExp);
			}
		}

		if (!foundTableExp) 
		{
			addExpander(IObjectTreeAPI.TABLE_TYPE_DBO, new TableTypeExpander());
		}
		if (!foundProcExp)
		{
			addExpander(IObjectTreeAPI.PROC_TYPE_DBO, new ProcedureTypeExpander());
		}
		if (!foundUDTExp)
		{
			addExpander(IObjectTreeAPI.UDT_TYPE_DBO, new UDTTypeExpander());
		}
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
		addKnownDatabaseObjectType(dboType);
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
	 * Retrieve details about all object types that can be in this
	 * tree.
	 *
	 * @return	DatabaseObjectType[]	Array of object type info objects.
	 */
	public synchronized DatabaseObjectType[] getDatabaseObjectTypes()
	{
		DatabaseObjectType[] ar = new DatabaseObjectType[_objectTypes.size()];
		return (DatabaseObjectType[])_objectTypes.toArray(ar);
	}

	synchronized void addKnownDatabaseObjectType(DatabaseObjectType dboType)
	{
		_objectTypes.add(dboType);
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

   public TreePath getPathToDbInfo(IDatabaseObjectInfo dbObjectInfo, ObjectTreeNode startNode, boolean useExpanders)
   {
      if(dbObjectInfoEquals(dbObjectInfo, startNode.getDatabaseObjectInfo()))
      {
         return new TreePath(startNode.getPath());
      }
      else
      {
         if(useExpanders && 0 == startNode.getChildCount())
         {
            INodeExpander[] expanders = getExpanders(startNode.getDatabaseObjectType());


            for (int i = 0; i < expanders.length; i++)
            {
               try
               {
                  List children = expanders[i].createChildren(startNode.getSession(), startNode);

                  for (int j = 0; j < children.size(); j++)
                  {
                     ObjectTreeNode newChild = (ObjectTreeNode) children.get(j);
                     if(0 == getExpanders(newChild.getDatabaseObjectType()).length)
                     {
                        newChild.setAllowsChildren(false);
                     }
                     else
                     {
                        newChild.setAllowsChildren(true);
                     }

                     startNode.add(newChild);
                  }
               }
               catch (Exception e)
               {
                  String msg =
                     "Error loading object type " +  startNode.getDatabaseObjectType() +
                     ". Error: " + e.toString() +  ". See SQuirreL Logs for stackttrace.";
                  startNode.getSession().getApplication().getMessageHandler().showErrorMessage(msg);

                  e.printStackTrace();
                  logger.error(msg, e);
               }
            }
         }

         for(int i=0; i < startNode.getChildCount(); ++i)
         {
            TreePath ret = getPathToDbInfo(dbObjectInfo, (ObjectTreeNode) startNode.getChildAt(i), useExpanders);
            if(null != ret)
            {
               return ret;
            }
         }
      }
      return null;
   }

   private boolean dbObjectInfoEquals(IDatabaseObjectInfo doi1, IDatabaseObjectInfo doi2)
   {
      if(null == doi1 && null == doi2)
      {
         return true;
      }
      else if(null != doi1 && null == doi2)
      {
         return false;
      }
      else if(null == doi1 && null != doi2)
      {
         return false;
      }
      else
      {
         return
               doi1.getDatabaseObjectType().equals(doi2.getDatabaseObjectType())
            && doi1.getQualifiedName().equals(doi2.getQualifiedName());
      }

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

	private static final class DatabaseObjectTypeComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((DatabaseObjectType)o1).getName()
				.compareToIgnoreCase(((DatabaseObjectType)o2).getName());
		}
	}
}
