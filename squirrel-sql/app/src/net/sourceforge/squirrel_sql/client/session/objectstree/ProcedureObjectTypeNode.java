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
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.BaseNode.TreeNodesLoader;

class ProcedureObjectTypeNode extends ObjectTypeNode {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String PROCEDURE = "PROCEDURE";
    }

    ProcedureObjectTypeNode(ISession session, ObjectsTreeModel treeModel,
                                    TableTypesGroupNode parentNode) {
        super(session, treeModel, parentNode, i18n.PROCEDURE);
    }

    public void expand() {
		if (getChildCount() == 0) 
        {
        	getSession().getApplication().getThreadPool().addTask(new ProcedureObjectLoader(addLoadingNode()));
        }
		else
		{
        	fireExpanded();
        }
    }
    

	public boolean equals(Object obj)
   {
    	return (obj instanceof ProcedureObjectTypeNode);
   }
	/*
	 * @see BaseNode#getTreeNodesLoader()
	 */
	protected TreeNodesLoader getTreeNodesLoader()
	{
		return new ProcedureObjectLoader(null);
	}

	protected class ProcedureObjectLoader extends BaseNode.TreeNodesLoader
	{
		ProcedureObjectLoader(MutableTreeNode loading)
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
            IProcedureInfo[] procs = null;
            try {
                procs = conn.getProcedures(catalogId, schemaId, "%");
            } catch (BaseSQLException ignore) {
                // Assume DBMS doesn't support procedures.
            }

			if (procs != null)
			{
				for (int i = 0; i < procs.length; ++i)
				{
					listNodes.add(new ProcedureNode(session, model, procs[i]));
				}
			}
			return listNodes;
		}
	}    
}

