package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002 Colin Bell
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
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is a node in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeNode extends DefaultMutableTreeNode
{
	/** Current session. */
	private ISession _session;

	/** <TT>true</TT> if node can have children. */
	boolean _allowsChildren;

	/** Describes the database object represented by this node. */
	private IDatabaseObjectInfo _dbinfo;

	/** Expander for this node (can be null). */
	private INodeExpander _expander;

	/**
	 * Ctor that assumes node cannot have children.
	 * 
	 * @param	session		Current session.
	 * @param	dbinfo			Describes this object in the database.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreeNode(ISession session, IDatabaseObjectInfo dbinfo)
	{
		this(session, dbinfo, false);
	}

	/**
	 * Ctor.
	 * 
	 * @param	session		Current session.
	 * @param	dbinfo		Describes this object in the database.
	 * @param	allowsChildren	<TT>true</TT> if node can have children.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> or
	 * 			<TT>IDatabaseObjectInfo</TT> passed.
	 */
//??TODO: Get rid of this ctor.
	private ObjectTreeNode(ISession session, IDatabaseObjectInfo dbinfo,
							boolean allowsChildren)
	{
		super(getNodeTitle(dbinfo));
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
		_allowsChildren = false; // Let setting the expander take care of this. allowsChildren;
		_dbinfo = dbinfo;
	}

	/**
	 * Return the current session.
	 * 
	 * @return	the current session.
	 */
	public ISession getSession()
	{
		return _session;
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _dbinfo;
	}

	/**
	 * Return <TT>true</TT> to indicate that this node can have children.
	 * 
	 * @return	<TT>true</TT> to indicate that this node can have children.
	 */
	public boolean getAllowsChildren()
	{
		return _allowsChildren;
	}

	/**
	 * Return the expander for this node. May be null.
	 * 
	 * @return	The <TT>INodeExpander</TT> for this node. May be <TT>null</TT>.
	 */
	public INodeExpander getExpander()
	{
		return _expander;
	}

	/**
	 * Set the expander for this node. May be null.
	 * 
	 * @param	value	New <TT>INodeExpander</TT> for this node.
	 */
	public void setExpander(INodeExpander value)
	{
		_expander = value;
		_allowsChildren = (_expander != null);
	}

	private static String getNodeTitle(IDatabaseObjectInfo dbinfo)
	{
		if (dbinfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		return dbinfo.toString();
	}
}