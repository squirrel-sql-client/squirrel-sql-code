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
import javax.swing.tree.MutableTreeNode;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;

public final class UDTObjectTypeNode extends ObjectTypeNode {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String UDT = "UDT";
    }

    public UDTObjectTypeNode(ISession session, ObjectsTreeModel treeModel,
                                TableTypesGroupNode parentNode) {
        super(session, treeModel, parentNode, i18n.UDT);
    }

		public void expand()
		{
			if (getChildCount() == 0)
			{
			}
			if (getChildCount() == 0)
			{
				getSession().getApplication().getThreadPool().addTask(
					new UDTObjectLoader(addLoadingNode()));
			}
			else
			{
				fireExpanded();
			}

		}
		
		public boolean equals(Object o)
		{
			return o instanceof UDTObjectTypeNode;
		}
    
	protected TreeNodesLoader getTreeNodesLoader()
	{
		return new UDTObjectLoader(null);
	}
    
	protected class UDTObjectLoader extends BaseNode.TreeNodesLoader
	{
		UDTObjectLoader(MutableTreeNode loading)
		{
			super(loading);
		}
		
		/*
		 * @see TreeNodesLoader#getNodeList(ISession, SQLConnection)
		 */
		public List getNodeList(ISession session, SQLConnection conn, ObjectsTreeModel model)
			throws BaseSQLException
		{
			final ArrayList listNodes = new ArrayList();
			final String catalogId = getParentNode().getCatalogIdentifier();
			final String schemaId = getParentNode().getSchemaIdentifier();
			IUDTInfo[] udts = null;
			try {
				udts = conn.getUDTs(catalogId, schemaId, "%", null);
            } catch (BaseSQLException ignore) {
				// Assume DBMS doesn't support UDTs.
            }

			if (udts != null)
			{
				for (int i = 0; i < udts.length; ++i)
				{
					listNodes.add(new UDTNode(session, model, udts[i]));
				}
			}
			return listNodes;
		}
	}    
}
