package net.sourceforge.squirrel_sql.plugins.derby.exp;
/*
 * Copyright (C) 2006 Rob Manning
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * 
 * @author manningr
 *
 */
public class TriggerParentExpander implements INodeExpander {

    /** Logger for this class. */
    private static final ILogger s_log = LoggerController
            .createLogger(TriggerParentExpander.class);

    private static String SQL = 
        "select tr.TRIGGERNAME " +
        "from SYS.SYSTRIGGERS tr, SYS.SYSTABLES t, SYS.SYSSCHEMAS s " +
        "where tr.TABLEID = t.TABLEID " +
        "and s.SCHEMAID = t.SCHEMAID " +
        "and t.TABLENAME = ? " +
        "and s.SCHEMANAME = ? ";
    
    /**
     * Ctor.
     * 
     */
    public TriggerParentExpander() {
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
        final SQLConnection conn = session.getSQLConnection();
        final SQLDatabaseMetaData md = 
            session.getSQLConnection().getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();
        final String catalogName = parentDbinfo.getCatalogName();
        final IDatabaseObjectInfo tableInfo = ((TriggerParentInfo) parentDbinfo)
                .getTableInfo();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, tableInfo.getSimpleName());
            pstmt.setString(2, tableInfo.getSchemaName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DatabaseObjectInfo doi = 
                    new DatabaseObjectInfo(catalogName, 
                                           schemaName, 
                                           rs.getString(1),
                                           DatabaseObjectType.TRIGGER, md);
                childNodes.add(new ObjectTreeNode(session, doi));
            }
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e){}
        }

        return childNodes;
    }

}