package org.firebirdsql.squirrel.exp;

/*
 * Copyright (C) 2002 Colin Bell
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.firebirdsql.squirrel.util.IndexParentInfo;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

/**
 * This class is an expander for the table nodes. It will add various Object
 * Type nodes to the table node.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell </A>
 */
public class TableExpander implements INodeExpander {

    /** Logger for this class. */
    private static final ILogger s_log = LoggerController
            .createLogger(TableExpander.class);

    /** The plugin. */
    private FirebirdPlugin _plugin;

    private static String TRIGGER_SQL = 
        "select cast(rdb$trigger_name as varchar(31)) as " +
        "rdb$trigger_name from rdb$triggers where rdb$relation_name = ?";
    
    
    /**
     * Ctor.
     */
    public TableExpander(FirebirdPlugin plugin) {
        super();
        if (plugin == null) { throw new IllegalArgumentException(
                "FirebirdPlugin == null"); }
        _plugin = plugin;
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

        /*
        IDatabaseObjectInfo dbinfo = new TriggerParentInfo(parentDbinfo,
                schemaName, md);
        ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
        child.addExpander(new TriggerParentExpander(_plugin));
        childNodes.add(child);
        */
        
        final ISQLConnection conn = session.getSQLConnection();
        
        PreparedStatement pstmt = conn.prepareStatement(TRIGGER_SQL);
        try {
            pstmt.setString(1, parentDbinfo.getSimpleName());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                IDatabaseObjectInfo dbinfo = new TriggerParentInfo(parentDbinfo,
                        schemaName, md);
                ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
                child.addExpander(new TriggerParentExpander(_plugin));
                childNodes.add(child);
            }
            
        } finally {
            pstmt.close();
        }

        IDatabaseObjectInfo doi = new IndexParentInfo(parentDbinfo, md);
        ObjectTreeNode child = new ObjectTreeNode(session, doi);
        childNodes.add(child);
        
        return childNodes;
    }
}