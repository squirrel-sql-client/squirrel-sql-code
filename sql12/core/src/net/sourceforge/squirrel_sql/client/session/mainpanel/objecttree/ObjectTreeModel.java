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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
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
/**
 * This is the model for the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 1L;


    private static ILogger logger =
     LoggerController.createLogger(ObjectTreeModel.class);


	/**
	 * Collection of <TT>INodeExpander</TT> objects. Each entry is a <TT>List</TT>
	 * of <TT>INodeExpander</TT> objects. The key to the list is the
	 * node type.
	 */
	private Map<IIdentifier, List<INodeExpander>> _expanders = 
        new HashMap<IIdentifier, List<INodeExpander>>();

	private final Set<DatabaseObjectType> _objectTypes = 
        new TreeSet<DatabaseObjectType>(new DatabaseObjectTypeComparator());

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

		// Standard expanders.
        session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                // must *not* be done in the GUI thread
                final INodeExpander expander = new DatabaseExpander(session);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // *must* be done in the GUI thread
                        
                        addExpander(DatabaseObjectType.CATALOG, expander);
                        addExpander(DatabaseObjectType.SCHEMA, expander);

                        boolean foundTableExp = false;
                        boolean foundProcExp = false;
                        boolean foundUDTExp = false;
                        boolean foundDatabaseExp = false;
                        final IPluginManager pmgr = session.getApplication().getPluginManager();
                        for (Iterator<SessionPluginInfo> pluginItr = pmgr.getSessionPluginIterator(); pluginItr.hasNext();)
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
                            if (databaseExp != null) {
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
                });
            }
        });
		
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
		List<INodeExpander> list = getExpandersList(dboType);
		return list.toArray(new INodeExpander[list.size()]);
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
		return _objectTypes.toArray(ar);
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
	private List<INodeExpander> getExpandersList(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		IIdentifier key = dboType.getIdentifier();
		List<INodeExpander> list = _expanders.get(key);
		if (list == null)
		{
			list = new ArrayList<INodeExpander>();
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

   public TreePath getPathToDbInfo(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders)
   {
      if(dbObjectInfoEquals(catalog, schema, objectMatcher, startNode.getDatabaseObjectInfo()))
      {
         return new TreePath(startNode.getPath());
      }
      else
      {
         if(useExpanders &&  startNode.getAllowsChildren() && 0 == startNode.getChildCount() && startNode.hasNoChildrenFoundWithExpander() == false)
         {
            INodeExpander[] expanders = getExpanders(startNode.getDatabaseObjectType());

            for (int i = 0; i < expanders.length; i++)
            {
               try
               {
                  List<ObjectTreeNode> children = 
                      expanders[i].createChildren(startNode.getSession(), startNode);

                  if(children.isEmpty()){
                	  startNode.setNoChildrenFoundWithExpander(true);
                  }else{
                	  for (int j = 0; j < children.size(); j++)
                	  {
                		  ObjectTreeNode newChild = children.get(j);
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
               }
               catch (Exception e)
               {
                  String msg =
                     "Error loading object type " +  startNode.getDatabaseObjectType() +
                     ". Error: " + e.toString() +  ". See SQuirreL Logs for stackttrace.";
                  startNode.getSession().showErrorMessage(msg);
                  logger.error(msg, e);
               }
            }
         }

         for(int i=0; i < startNode.getChildCount(); ++i)
         {
            TreePath ret = getPathToDbInfo(catalog, schema, objectMatcher, (ObjectTreeNode) startNode.getChildAt(i), useExpanders);
            if(null != ret)
            {
               return ret;
            }
         }
      }
      return null;
   }

   private boolean dbObjectInfoEquals(String catalog, String schema, FilterMatcher objectMatcher, IDatabaseObjectInfo doi)
   {
      if(null != catalog)
      {
         if(false == catalog.equalsIgnoreCase(doi.getCatalogName()))
         {
            return false;
         }
      }

      if(null != schema)
      {
         if(false == schema.equalsIgnoreCase(doi.getSchemaName()))
         {
            return false;
         }
      }

      if(null != objectMatcher.getMetaDataMatchString())
      {
         if(   false == objectMatcher.matches(doi.getSimpleName())
            && false == objectMatcher.getMetaDataMatchString().equalsIgnoreCase(doi.getQualifiedName()))
         {
            return false;
         }
      }

      return true;
   }

   public boolean isRootNode(Object node)
   {
      return node instanceof RootNode;
   }

   private static final class RootNode extends ObjectTreeNode
   {
      private static final long serialVersionUID = 1L;

      RootNode(ISession session)
      {
         super(session, createDbo(session));
      }

      private static final IDatabaseObjectInfo createDbo(ISession session)
      {
         return new DatabaseObjectInfo(null, null, session.getAlias().getName(),
                                 DatabaseObjectType.SESSION,
                                 session.getMetaData());
      }
   }

	private static final class DatabaseObjectTypeComparator 
                         implements Comparator<DatabaseObjectType>, Serializable
	{
        private static final long serialVersionUID = 1L;

        public int compare(DatabaseObjectType o1, DatabaseObjectType o2)
		{
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
}
