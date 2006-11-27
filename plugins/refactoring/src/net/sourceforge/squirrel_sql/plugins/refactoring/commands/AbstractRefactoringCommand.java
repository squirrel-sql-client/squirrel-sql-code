package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public abstract class AbstractRefactoringCommand implements ICommand {

    /** Current session */
    protected ISession _session;
    
    /** Selected table */
    protected final IDatabaseObjectInfo _info;

    protected ColumnListDialog columnListDialog = null;
    
    protected String pkName = null;
    
    public AbstractRefactoringCommand(ISession session, 
                                      IDatabaseObjectInfo info) 
    {
        _session = session;
        _info = info;
    }
    
    protected void showColumnListDialog(ActionListener oklistener, 
                                        ActionListener showSqlListener, 
                                        int mode) 
        throws SQLException 
    {

        ITableInfo ti = (ITableInfo)_info;
        TableColumnInfo[] columns = getTableColumns(ti, mode);

        
        //Show the user a dialog with a list of columns and ask them to select
        // one or more columns to drop
        /*
        ArrayList tmp = new ArrayList();
        for (int i = 0; i < columns.length; i++) {
            TableColumnInfo info = columns[i];
            tmp.add(info.getColumnName());
        }
        */
        if (columnListDialog == null) {
            columnListDialog = new ColumnListDialog(columns, mode);
            columnListDialog.addColumnSelectionListener(oklistener);
            columnListDialog.addShowSQLListener(showSqlListener);
            MainFrame mainFrame = _session.getApplication().getMainFrame();
            columnListDialog.setLocationRelativeTo(mainFrame);  
            columnListDialog.setMultiSelection();
        }
        columnListDialog.setTableName(ti.getSimpleName());
        columnListDialog.setVisible(true);
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
    
}
