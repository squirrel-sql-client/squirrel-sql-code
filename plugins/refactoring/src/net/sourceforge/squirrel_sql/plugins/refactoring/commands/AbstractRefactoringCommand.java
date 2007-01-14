package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DropTableDialog;

public abstract class AbstractRefactoringCommand implements ICommand {

    /** Current session */
    protected ISession _session;
    
    /** Selected table(s) */
    protected final IDatabaseObjectInfo[] _info;

    protected ColumnListDialog columnListDialog = null;
    
    protected ColumnDetailDialog columnDetailDialog = null;
    
    protected DropTableDialog dropTableDialog = null;
    
    protected String pkName = null;
    
    public AbstractRefactoringCommand(ISession session, 
                                      IDatabaseObjectInfo[] info) 
    {
        if (session == null)
        {
            throw new IllegalArgumentException("ISession cannot be null");
        }
        if (info == null)
        {
            throw new IllegalArgumentException("IDatabaseObjectInfo[] cannot be null");
        }        
        _session = session;
        _info = info;
    }
    
    protected void showColumnListDialog(ActionListener oklistener, 
                                        ActionListener showSqlListener, 
                                        int mode) 
        throws SQLException 
    {

        ITableInfo ti = (ITableInfo)_info[0];
        TableColumnInfo[] columns = getTableColumns(ti, mode);

        
        //Show the user a dialog with a list of columns and ask them to select
        // one or more columns to drop
        if (columnListDialog == null) {
            columnListDialog = new ColumnListDialog(columns, mode);
            columnListDialog.addColumnSelectionListener(oklistener);
            columnListDialog.addEditSQLListener(new EditSQLListener());
            columnListDialog.addShowSQLListener(showSqlListener);
            MainFrame mainFrame = _session.getApplication().getMainFrame();
            columnListDialog.setLocationRelativeTo(mainFrame);  
            columnListDialog.setMultiSelection();
        }
        columnListDialog.setTableName(ti.getQualifiedName());
        if (mode == ColumnListDialog.ADD_PRIMARY_KEY_MODE) {
            // Set a default primary key name based on the name of the table
            String pkName = "PK_"+columns[0].getTableName().toUpperCase();
            columnListDialog.setPrimaryKeyName(pkName);
        }
        if (mode == ColumnListDialog.DROP_PRIMARY_KEY_MODE) {
            SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
            PrimaryKeyInfo[] infos = md.getPrimaryKey(ti);
            String pkName = infos[0].getSimpleName();
            columnListDialog.setPrimaryKeyName(pkName);
        }
        columnListDialog.setVisible(true);
    }

    protected void showDropTableDialog(ActionListener oklistener, 
                                       ActionListener showSqlListener) {
        if (dropTableDialog == null) {
            ITableInfo[] tableInfos = new ITableInfo[_info.length];
            for (int i = 0; i < tableInfos.length; i++) {
                tableInfos[i] = (ITableInfo)_info[i];
            }
            dropTableDialog = new DropTableDialog(tableInfos);
            dropTableDialog.addExecuteListener(oklistener);
            dropTableDialog.addShowSQLListener(showSqlListener);
            dropTableDialog.addEditSQLListener(new EditSQLListener());
            MainFrame mainFrame = _session.getApplication().getMainFrame();
            dropTableDialog.setLocationRelativeTo(mainFrame);              
        }
        dropTableDialog.setVisible(true);
    }
    
    protected void setPKName(String pkName) {
        this.pkName = pkName;
    }
    
    protected String getPKName() {
        return this.pkName;
    }
    
    private TableColumnInfo[] getTableColumns(ITableInfo ti, int mode) 
    throws SQLException 
    {
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        if (mode == ColumnListDialog.DROP_PRIMARY_KEY_MODE) {
            ArrayList result= new ArrayList();
            PrimaryKeyInfo[] pkCols = md.getPrimaryKey(ti);
            TableColumnInfo[] colInfos = md.getColumnInfo(ti);
            for (int i = 0; i < pkCols.length; i++) {
                PrimaryKeyInfo pkInfo = pkCols[i];
                setPKName(pkInfo.getSimpleName());
                String pkColName = pkInfo.getQualifiedColumnName();
                for (int j = 0; j < colInfos.length; j++) {
                    TableColumnInfo colInfo = colInfos[j];
                    if (colInfo.getSimpleName().equals(pkColName)) {
                        result.add(colInfo);
                    }
                }
            }
            return (TableColumnInfo[])result.toArray(new TableColumnInfo[result.size()]);
        } 
        return _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);        
    }
    
    protected class CommandExecHandler extends DefaultSQLExecuterHandler {
        private boolean exceptionEncountered = false;
        
        public CommandExecHandler(ISession session) {
            super(session);
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler#sqlExecutionException(java.lang.Throwable, java.lang.String)
         */
        public void sqlExecutionException(Throwable th, String postErrorString) {
            super.sqlExecutionException(th, postErrorString);
            setExceptionEncountered(true);
        }

        /**
         * @param exceptionEncountered the exceptionEncountered to set
         */
        public void setExceptionEncountered(boolean exceptionEncountered) {
            this.exceptionEncountered = exceptionEncountered;
        }

        /**
         * @return the exceptionEncountered
         */
        public boolean exceptionEncountered() {
            return exceptionEncountered;
        }
    }
    
    /**
     * The subclass should implement this so that the action listeners can get
     * the SQL 
     * @return
     */
    protected abstract String[] getSQLFromDialog();
    
    /**
     *                      
     *                      
     *
     */
    protected class EditSQLListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String[] sqls = getSQLFromDialog();
            StringBuffer sql = new StringBuffer();
            for (int i = 0; i < sqls.length; i++) {
                sql.append(sqls[i]);
                if (i < sqls.length) {
                    sql.append("\n\n");
                }
            }
            if (columnListDialog != null) {
                columnListDialog.setVisible(false);
            }
            if (columnDetailDialog != null) {
                columnDetailDialog.setVisible(false);
            }
            if (dropTableDialog != null) {
                dropTableDialog.setVisible(false);
            }
            _session.getSQLPanelAPIOfActiveSessionWindow().appendSQLScript(sql.toString(), true);
            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
        }
        
    }
    
    protected boolean tableHasPrimaryKey() throws SQLException {
        if (! (_info[0] instanceof ITableInfo)) {
            return false;
        }
        ITableInfo ti = (ITableInfo)_info[0];
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        PrimaryKeyInfo[] pks = md.getPrimaryKey(ti);
        return (pks != null && pks.length > 0);
    }

}
