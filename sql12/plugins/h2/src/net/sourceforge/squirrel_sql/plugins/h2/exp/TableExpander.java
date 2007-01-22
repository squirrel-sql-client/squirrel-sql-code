package net.sourceforge.squirrel_sql.plugins.h2.exp;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.h2.util.IndexParentInfo;

/**
 * This class is an expander for the table nodes. It will add TRIGGER and INDEX
 * Object Type nodes to the table node.
 * 
 * @author manningr
 */
public class TableExpander implements INodeExpander {

    /** Logger for this class. */
    private static final ILogger s_log = 
        LoggerController.createLogger(TableExpander.class);
    
    /**
     * Ctor.
     */
    public TableExpander() {
        super();
    }

    /**
     * Create the child nodes for the passed parent node and return them. Note
     * that this method should <B>not </B> actually add the child nodes to the
     * parent node as this is taken care of in the caller.
     * 
     * @param session
     *            Current session.
     * @param node
     *            Node to be expanded.
     * 
     * @return A list of <TT>ObjectTreeNode</TT> objects representing the
     *         child nodes for the passed node.
     */
    public List createChildren(ISession session, ObjectTreeNode parentNode)
            throws SQLException {
        final List childNodes = new ArrayList();
        final IDatabaseObjectInfo parentDbinfo = parentNode
                .getDatabaseObjectInfo();
        final SQLDatabaseMetaData md = session.getSQLConnection()
                .getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();

        
        IDatabaseObjectInfo triggerParentInfo = 
            new TriggerParentInfo(parentDbinfo, schemaName, md);
        ObjectTreeNode triggerChild = new ObjectTreeNode(session, triggerParentInfo);
        triggerChild.addExpander(new TriggerParentExpander());
        
        IDatabaseObjectInfo indexParentInfo = 
            new IndexParentInfo(parentDbinfo, md);
        ObjectTreeNode indexChild = new ObjectTreeNode(session, indexParentInfo);
        indexChild.addExpander(new IndexParentExpander());
        
        childNodes.add(indexChild);
        childNodes.add(triggerChild);
        
        return childNodes;
    }
}