/*
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

package net.sourceforge.squirrel_sql.plugins.vertica.tab;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.DataSetUpdateableTableModelImpl;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.OrderByClausePanel;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.WhereClausePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ContentPlusTab extends ContentsTab {
	
	private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(ContentPlusTab.class);

    private final static ILogger s_log = LoggerController.createLogger(ContentPlusTab.class);
    
    /**
	 * This interface defines locale specific strings.
	 */
	private interface i18n
	{
		String HINT  = s_stringMgr.getString("ContentPlusTab.hint");
		String TITLE = s_stringMgr.getString("ContentPlusTab.title"); 
	}

    private DataSetUpdateableTableModelImpl _dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();
	
	private SQLFilterClauses _sqlFilterClauses = new SQLFilterClauses();


	public ContentPlusTab(ObjectTreePanel treePanel)
    {
		super(treePanel);
	}
	
	/**
    * Create the <TT>IDataSet</TT> to be displayed in this tab.
    */
    protected IDataSet createDataSet() throws DataSetException
    {
        final ISession session = getSession();
        final ISQLConnection conn = session.getSQLConnection();
        ISQLDatabaseMetaData md = session.getMetaData();

        try
        {
            final Statement stmt = conn.createStatement();
            try
            {
	            final ITableInfo ti = getTableInfo();
                    _dataSetUpdateableTableModel.setTableInfo(ti);
                    _dataSetUpdateableTableModel.setSession(session);
                
                final SessionProperties props = session.getProperties();
                if (props.getContentsLimitRows())
                {
                   try
                   {
    	              if(!ti.getType().equals("SYSTEM TABLE"))
    		              stmt.setMaxRows(props.getContentsNbrRowsToShow());
                   }
                   catch (Exception ex)
                   {
                      s_log.error("Error on Statement.setMaxRows()", ex);
                   }
                }
                

                /**
                 * If the table has a pseudo-column that is the best unique
                 * identifier for the rows (like Oracle's rowid), then we
                 * want to include that field in the query so that it will
                 * be available if the user wants to edit the data later.
                 */
                String pseudoColumn = "";

                try
                {
                   BestRowIdentifier[] rowIDs = md.getBestRowIdentifier(ti);
                   for (int i = 0; i < rowIDs.length; ++i)
                   {
                      short pseudo = rowIDs[i].getPseudoColumn();
                      if (pseudo == DatabaseMetaData.bestRowPseudo)
                      {
                         pseudoColumn = " ," + rowIDs[i].getColumnName();
                         break;
                      }
                   }
                }

                // Some DBMS's (EG Think SQL) throw an exception on a call to
                // getBestRowIdentifier.
                catch (Throwable th)
                {
    	            if (s_log.isDebugEnabled()) {
    	               s_log.debug("getBestRowIdentifier not supported for this table ", th);
    	            }
                }

                ResultSet rs = null;
                try
                {
                   // Note. Some DBMSs such as Oracle do not allow:
                   // "select *, rowid from table"
                   // You cannot have any column name in the columns clause
                   // if you have * in there. Aliasing the table name seems to
                   // be the best way to get around the problem.
                   final StringBuffer buf = new StringBuffer();
                   buf.append("select tbl.*")
                      .append(pseudoColumn)
                      .append(" from ")
                      .append(ti.getQualifiedName())
                      .append(" tbl");

                   String clause = _sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
                   if ((clause != null) && (clause.length() > 0))
                   {
                     buf.append(" where ").append(clause);
                   }
                   clause = _sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
                   if ((clause != null) && (clause.length() > 0))
                   {
                     buf.append(" order by ").append(clause);
                   }
                   if(!ti.getType().equals("SYSTEM TABLE"))
    	               buf.append(" limit "+props.getContentsNbrRowsToShow());

                   if (s_log.isDebugEnabled()) {
                       s_log.debug("createDataSet running SQL: "+buf.toString());
                   }
                               
                   //showWaitDialog(stmt);               

                   rs = stmt.executeQuery(buf.toString());

                }
                catch (SQLException ex)
                {
                    if (s_log.isDebugEnabled()) {
                            s_log.debug(
                                "createDataSet: exception from pseudocolumn query - "
                                        + ex, ex);
                        }
                    // We assume here that if the pseudoColumn was used in the query,
                    // then it was likely to have caused the SQLException.  If not, 
                    // (length == 0), then retrying the query won't help - just throw
                    // the exception.
                   if (pseudoColumn.length() == 0)
                   {
                      throw ex;
                   }
                   // pseudocolumn query failed, so reset it.  Otherwise, we'll 
                   // mistake the last column for a pseudocolumn and make it 
                   // uneditable 
                   pseudoColumn = "";

                   // Some tables have pseudo column primary keys and others
                   // do not.  JDBC on some DBMSs does not handle pseudo
                   // columns 'correctly'.  Also, getTables returns 'views' as
                   // well as tables, so the thing we are looking at might not
                   // be a table. (JDBC does not give a simple way to
                   // determine what we are looking at since the type of
                   // object is described in a DBMS-specific encoding.)  For
                   // these reasons, rather than testing for all these
                   // conditions, we just try using the pseudo column info to
                   // get the table data, and if that fails, we try to get the
                   // table data without using the pseudo column.
                   // TODO: Should we change the mode from editable to
                   // non-editable?
                   final StringBuffer buf = new StringBuffer();
                   buf.append("select *")
                      .append(" from ")
                      .append(ti.getQualifiedName())
                      .append(" tbl");

                   String clause = _sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
                   if ((clause != null) && (clause.length() > 0))
                   {
                     buf.append(" where ").append(clause);
                   }
                   clause = _sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
                   if ((clause != null) && (clause.length() > 0))
                   {
                     buf.append(" order by ").append(clause);
                   }
                   if(!ti.getType().equals("SYSTEM TABLE"))
    	               buf.append(" limit "+props.getContentsNbrRowsToShow());

                   rs = stmt.executeQuery(buf.toString());
                }

                final ResultSetDataSet rsds = new ResultSetDataSet(md.getColumnInfo(ti));

                // to allow the fw to save and reload user options related to
                // specific columns, we construct a unique name for the table
                // so the column can be associcated with only that table.
                // Some drivers do not provide the catalog or schema info, so
                // those parts of the name will end up as null.  That's ok since
                // this string is never viewed by the user and is just used to
                // distinguish this table from other tables in the DB.
                // We also include the URL used to connect to the DB so that
                // the same table/DB on different machines is treated differently.
                rsds.setContentsTabResultSet(rs,
                                             _dataSetUpdateableTableModel.getFullTableName(),
                                             DialectFactory.getDialectType(md));
                if (rs != null) {
                    try { rs.close(); } catch (SQLException e) {}
                }


                //?? remember which column is the rowID (if any) so we can
                //?? prevent editing on it
                if (pseudoColumn.length() > 0)
                {
                   _dataSetUpdateableTableModel.setRowIDCol(rsds.getColumnCount() - 1);
                }

                return rsds;
            }
            finally
            {
                 SQLUtilities.closeStatement(stmt);
            }
        }
        catch (SQLException ex)
        {
            throw new DataSetException(ex);
        }
    }

    public String getTitle()
    {
        return i18n.TITLE;
    }

	public String getHint()
	{
		return i18n.HINT;
	}

}
