package net.sourceforge.squirrel_sql.plugins.mssql.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Provides the query and parameter binding behavior for MS SQL Server's trigger catalog.
 *
 * @author damluar
 */
public class MssqlTableTriggerExtractorImpl implements ITableTriggerExtractor {

        /** Logger for this class */
    private final static ILogger s_log =
        LoggerController.createLogger(MssqlTableTriggerExtractorImpl.class);

    /** The query that finds the triggers for a given table */
    private static String SQL =
        "SELECT     sys.triggers.name AS [trigger] " +
        "FROM       sys.schemas JOIN " +
        "sys.tables ON sys.schemas.schema_id = sys.tables.schema_id JOIN " +
        "sys.triggers ON sys.tables.object_id = sys.triggers.parent_id " +
        "WHERE sys.tables.name = ? " +
        "AND sys.schemas.name = ?";

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor#bindParamters(java.sql.PreparedStatement, net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
     */
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as first bind value");
            s_log.debug("Binding schema name "+dbo.getSchemaName()+
                        " as second bind value");
        }
        pstmt.setString(1, dbo.getSimpleName());
        pstmt.setString(2, dbo.getSchemaName());
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor#getTableTriggerQuery()
     */
    public String getTableTriggerQuery() {
        return SQL;
    }

}
