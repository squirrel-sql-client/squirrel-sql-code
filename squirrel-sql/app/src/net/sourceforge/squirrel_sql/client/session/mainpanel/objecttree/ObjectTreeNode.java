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

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is a node in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeNode extends DefaultMutableTreeNode
{
	/** Type of this node. */
	private int _nodeType;

	/** Current session. */
	private ISession _session;

	/** <TT>true</TT> if node can have children. */
	boolean _allowsChildren;

	/**
	 * Ctor that assumes node can have children.
	 * 
	 * @param	session		Current session.
	 * @param	nodeType	The type of node this is. @see INodeTypes.
	 * @param	userObject	Object to store in node. Can be <TT>null</TT>.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreeNode(ISession session, int nodeType, Object userObject)
	{
		this(session, nodeType, userObject, true);
	}

	/**
	 * Ctor.
	 * 
	 * @param	session		Current session.
	 * @param	nodeType	The type of node this is. @see INodeTypes.
	 * @param	userObject	Obejct to store in node. Can be <TT>null</TT>.
	 * @param	allowsChildren	<TT>true</TT> if node can have children.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreeNode(ISession session, int nodeType, Object userObject,
							boolean allowsChildren)
	{
		super(userObject);
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_nodeType = nodeType;
		_session = session;
		_allowsChildren = allowsChildren;
	}

	/**
	 * Return the type of this node. See <TT>INodeTypes</TT>.
	 * 
	 * @return	the type of this node. See <TT>INodeTypes</TT>.
	 */
	public int getNodeType()
	{
		return _nodeType;
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

	/**
	 * Return <TT>true</TT> to indicate that this node can have children.
	 * 
	 * @return	<TT>true</TT> to indicate that this node can have children.
	 */
	public boolean getAllowsChildren()
	{
		return _allowsChildren;
	}

}