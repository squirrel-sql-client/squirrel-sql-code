package org.firebirdsql.squirrel.tab;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public abstract class BasePreparedStatementTab extends BaseObjectTab {
    /** Title to display for tab. */
    private final String _title;

    /** Hint to display for tab. */
    private final String _hint;

    private boolean _firstRowOnly;

    /** Component to display in tab. */
    private DataSetScrollingPanel _comp;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(BasePreparedStatementTab.class);

    public BasePreparedStatementTab(String title, String hint)
    {
        this(title, hint, false);
    }

    public BasePreparedStatementTab(String title, String hint, boolean firstRowOnly)
    {
        super();
        if (title == null)
        {
            throw new IllegalArgumentException("Title == null");
        }
        _title = title;
        _hint = hint != null ? hint : title;
        _firstRowOnly = firstRowOnly;
    }

    /**
     * Return the title for the tab.
     *
     * @return  The title for the tab.
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Return the hint for the tab.
     *
     * @return  The hint for the tab.
     */
    public String getHint()
    {
        return _hint;
    }

    public void clear()
    {
    }

    public Component getComponent()
    {
        if (_comp == null)
        {
            _comp = new DataSetScrollingPanel();
        }
        return _comp;
    }

    protected void refreshComponent() throws DataSetException
    {
        ISession session = getSession();
        if (session == null)
        {
            throw new IllegalStateException("Null ISession");
        }
        try
        {
            PreparedStatement pstmt = createStatement();
            try
            {
                ResultSet rs = pstmt.executeQuery();
                try
                {
                    final SessionProperties props = session.getProperties();
                    final String destClassName = props.getMetaDataOutputClassName();
//                    final LargeResultSetObjectInfo rsoi = props.getLargeResultSetObjectInfo();
//                    final IDataSet ds = createDataSetFromResultSet(rs, rsoi);
                  final IDataSet ds = createDataSetFromResultSet(rs);
                    _comp.load(ds, destClassName);
                }
                    finally
                {
                    rs.close();
                }
            }
            finally
            {
                pstmt.close();
            }
        }
        catch (SQLException ex)
        {
            throw new DataSetException(ex);
        }
    }

    protected abstract PreparedStatement createStatement() throws SQLException;

    protected IDataSet createDataSetFromResultSet(ResultSet rs) throws DataSetException
    {
        final ResultSetDataSet rsds = new ResultSetDataSet();
        rsds.setResultSet(rs);
        if (!_firstRowOnly)
        {
            return rsds;
        }

        final int columnCount = rsds.getColumnCount();
        final ColumnDisplayDefinition[] colDefs = rsds.getDataSetDefinition().getColumnDefinitions();
        final Map data = new HashMap();
        if (rsds.next(null))
        {
            for (int i = 0; i < columnCount; ++i)
            {
                data.put(colDefs[i].getLabel(), rsds.get(i));
            }
        }
        return new MapDataSet(data);

    }

}
