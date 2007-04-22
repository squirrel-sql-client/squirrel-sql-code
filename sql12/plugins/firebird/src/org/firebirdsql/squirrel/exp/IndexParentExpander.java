package org.firebirdsql.squirrel.exp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

import org.firebirdsql.squirrel.util.IndexParentInfo;
import org.firebirdsql.squirrel.util.SystemTables;

public class IndexParentExpander implements INodeExpander
{
    private static final String STD_INDICES_SQL =
        "SELECT " +
            SystemTables.IIndexTable.COL_NAME + "," +
            SystemTables.IIndexTable.COL_RELATION_NAME +
        " FROM " +
            SystemTables.IIndexTable.TABLE_NAME;

    /** SQL used to load all indices in database. */
    private static final String ALL_INDICES_SQL =
        STD_INDICES_SQL +
        " ORDER BY " +
            SystemTables.IIndexTable.COL_NAME;

    /**
     * SQL used to load only those indices related to a single object in
     * the database.
     */
    private static final String RELATED_INDICES_SQL =
        STD_INDICES_SQL +
        " WHERE " +
            SystemTables.IIndexTable.COL_RELATION_NAME + " = ?" +
        " ORDER BY " +
            SystemTables.IIndexTable.COL_NAME;

    /** Logger for this class. */
    private static final ILogger s_log =
        LoggerController.createLogger(IndexParentExpander.class);

    /**
     * Default ctor.
     */
    public IndexParentExpander()
    {
        super();
    }

    /**
     * Create the child nodes for the passed parent node and return them. Note
     * that this method should <B>not</B> actually add the child nodes to the
     * parent node as this is taken care of in the caller.
     *
     * @param    session    Current session.
     * @param    node    Node to be expanded.
     *
     * @return    A list of <TT>ObjectTreeNode</TT> objects representing the child
     *            nodes for the passed node.
     *
     * @throws    SQLException
     *            Thrown if an SQL error occurs.
     */
    public List createChildren(ISession session, ObjectTreeNode parentNode)
        throws SQLException
    {
        final List childNodes = new ArrayList();
        final ISQLConnection conn = session.getSQLConnection();
        final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
        final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
        final IDatabaseObjectInfo roi = ((IndexParentInfo)parentDbinfo).getRelatedObjectInfo();

        PreparedStatement pstmt;
        if (roi == null)
        {
            pstmt = conn.prepareStatement(ALL_INDICES_SQL);
        }
        else
        {
            pstmt = conn.prepareStatement(RELATED_INDICES_SQL);
            pstmt.setString(1, roi.getSimpleName());
        }

        try
        {
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, null,
                                            rs.getString(1),
                                            DatabaseObjectType.INDEX, md);
                childNodes.add(new ObjectTreeNode(session, doi));
            }
        }
        finally
        {
            pstmt.close();
        }
        return childNodes;
    }
}
