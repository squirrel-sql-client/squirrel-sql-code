package org.firebirdsql.squirrel.exp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class TriggerParentExpander implements INodeExpander {

    /** Logger for this class. */
    private static final ILogger s_log = LoggerController
            .createLogger(TriggerParentExpander.class);

    private static String SQL = "select cast(rdb$trigger_name as varchar(31)) as rdb$trigger_name from rdb$triggers where rdb$relation_name = ?";

    /** The plugin. */
    private final FirebirdPlugin _plugin;

    /**
     * Ctor.
     * 
     * @throws IllegalArgumentException
     *             Thrown if <TT>null</TT> <TT>FirebirdPlugin</TT> passed.
     */
    public TriggerParentExpander(FirebirdPlugin plugin) {
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
        final ISQLConnection conn = session.getSQLConnection();
        final SQLDatabaseMetaData md = session.getSQLConnection()
                .getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();
        final IDatabaseObjectInfo tableInfo = ((TriggerParentInfo) parentDbinfo)
                .getTableInfo();
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        try {
            pstmt.setString(1, tableInfo.getSimpleName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DatabaseObjectInfo doi = new DatabaseObjectInfo(null, "", rs.getString(1),
                        DatabaseObjectType.TRIGGER, md);
                childNodes.add(new ObjectTreeNode(session, doi));
            }
        } finally {
            pstmt.close();
        }

        return childNodes;
    }

}