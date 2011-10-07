package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is a node in the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeNode extends DefaultMutableTreeNode
{
	/** Current session. */
//	private final ISession _session;

    private static final long serialVersionUID = 1L;

    /** Application API. */
	private final IApplication _app;

	/** ID of the session for this window. */
	private final IIdentifier _sessionId;

	/** Describes the database object represented by this node. */
	private final IDatabaseObjectInfo _dboInfo;

	/** If <TT>true</TT> node can be expanded. */
	private boolean _allowsChildren = true;

	/** Collection of <TT>INodeExpander</TT> objects for this node. */
	private final List<INodeExpander> _expanders = new ArrayList<INodeExpander>();
	
	private boolean noChildrenFoundWithExpander = false;

   /**
	 * Ctor that assumes node cannot have children.
	 *
	 * @param	session	Current session.
	 * @param	dbinfo	Describes this object in the database.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> or
	 *			<TT>IDatabaseObjectInfo</TT> passed.
	 */
	public ObjectTreeNode(ISession session, IDatabaseObjectInfo dboInfo)
	{
		super(getNodeTitle(dboInfo));
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (dboInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
//		_session = session;
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
		_dboInfo = dboInfo;

	}

   public void add(MutableTreeNode newChild)
   {
      super.add(newChild);
      newChild.setParent(this);
   }

	/**
	 * Return the current session.
	 *
	 * @return	the current session.
	 */
	public ISession getSession()
	{
		return _app.getSessionManager().getSession(_sessionId);
	}

	/**
	 * Return the <TT>IDatabaseObjectInfo</TT> object that describes the
	 * database object represented by this node.
	 */
	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _dboInfo;
	}

	/**
	 * Convenience method to get the database object type for this node.
	 *
	 * @return	the database object type of this node.
	 */
	public DatabaseObjectType getDatabaseObjectType()
	{
		return _dboInfo.getDatabaseObjectType();
	}

	/**
	 * Returns <TT>true</TT> if this node can have children.
	 *
	 * @return	<TT>true</TT> if this node can have children.
	 */
	public boolean getAllowsChildren()
	{
		return _allowsChildren;
	}

	public boolean isLeaf()
	{
		return !_allowsChildren;
	}

	/**
	 * Return the expanders for this node. Remember that these are in addition
	 * to the standard expanders stored in the object tree model. Normally
	 * this would be empty.
	 *
	 * @return	The <TT>INodeExpander</TT> objects for this node.
	 */
	public INodeExpander[] getExpanders()
	{
		return _expanders.toArray(new INodeExpander[_expanders.size()]);
	}

	/**
	 * Adds an expander to this node.
	 *
	 * @param	value	New <TT>INodeExpander</TT> for this node.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT>INodeExpander</TT> passed.
	 */
	public void addExpander(INodeExpander value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("INodeExpander == null");
		}

		_expanders.add(value);
	}

	/**
	 * Specify whether this node can have children.
	 *
	 * @param	value	<TT>true</TT> if this node can have children.
	 */
	public void setAllowsChildren(boolean value)
	{
		super.setAllowsChildren(value);
		_allowsChildren = value;
	}

	private static String getNodeTitle(IDatabaseObjectInfo dbinfo)
	{
		if (dbinfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		return dbinfo.toString();
	}

   public Icon getIcon()
   {
      return _dboInfo.getDatabaseObjectType().getIcon();
   }

/**
 * @return the noChildrenFoundWithExpander
 */
public boolean hasNoChildrenFoundWithExpander() {
	return noChildrenFoundWithExpander;
}

/**
 * @param noChildrenFoundWithExpander the noChildrenFoundWithExpander to set
 */
public void setNoChildrenFoundWithExpander(boolean noChildrenFoundWithExpander) {
	this.noChildrenFoundWithExpander = noChildrenFoundWithExpander;
}
}
