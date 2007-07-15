package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;
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

/**
 * This class is an expander for the table nodes. It will add TRIGGER
 * Object Type nodes to the table node.
 * 
 * @author manningr
 */
public class TableWithTriggersExpander implements INodeExpander {

    private ITableTriggerExtractor extractor = null;
    
    /**
     * Ctor.
     */
    public TableWithTriggersExpander() {
        super();
    }

    /**
     * Method for injecting the component that allows this class to work with
     * a specific database, depending on the type of trigger extractor.
     * 
     * @param extractor the ITableTriggerExtractor implementation to use.
     */
    public void setTableTriggerExtractor(ITableTriggerExtractor extractor) {
        this.extractor = extractor;
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
    public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode)
        throws SQLException 
    {
        final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
        final IDatabaseObjectInfo parentDbinfo = parentNode
                .getDatabaseObjectInfo();
        final SQLDatabaseMetaData md = session.getSQLConnection()
                .getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();

        
        IDatabaseObjectInfo triggerParentInfo = 
            new TriggerParentInfo(parentDbinfo, schemaName, md);
        ObjectTreeNode triggerChild = new ObjectTreeNode(session, triggerParentInfo);
        TriggerParentExpander expander = new TriggerParentExpander();
        expander.setTableTriggerExtractor(extractor);
        triggerChild.addExpander(expander);
        
        childNodes.add(triggerChild);
        
        return childNodes;
    }
}