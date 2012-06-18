/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.gui.DualProgressBarDialog;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

/**
 * A description of this class goes here...
 */

public class CopyProgressMonitor extends I18NBaseObject
                                 implements CopyTableListener,
                                            UICallbacks {

    private SessionInfoProvider prov = null;
    
    /** the window we use to display dialogs to the user */
    private JFrame parent = null;
    
    /** whether or not to delete all table data */
    private boolean deleteAllTableData = false;

    /** the class that does the actual copying */
    private CopyExecutor executor = null;
    
    /** Logger for this class. */
    private final static ILogger log = 
                         LoggerController.createLogger(CopyProgressMonitor.class);
    
    private ProgressMonitor pm = null;
    
    private static CommentSpec[] commentSpecs =
        new CommentSpec[]
        {
            new CommentSpec("/*", "*/"),
            new CommentSpec("--", "\n")
        };
  
  private static CodeReformator formatter = 
      new CodeReformator(CodeReformatorConfigFactory.createConfig(";", commentSpecs));
  
    
    public CopyProgressMonitor(SessionInfoProvider provider) {
        prov = provider;
        parent = prov.getDestSession().getApplication().getMainFrame();
    }
    
    // CopyTableListener interface methods
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyStarted()
     */
    public void copyStarted(CopyEvent e) {
        if (pm != null) {
            pm.setProgress(pm.getMaximum());
        }
        prov = e.getSessionInfoProvider();
        int numTables = prov.getSourceDatabaseObjects().size();
        int[] tableCounts = e.getTableCounts();
        
        createProgressDialog();
        DualProgressBarDialog.setBottomBarMinMax(0, numTables);
        DualProgressBarDialog.setBottomBarValue(0);
        DualProgressBarDialog.setTopBarValue(0);
        DualProgressBarDialog.setTableCounts(tableCounts);
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableCopyStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void tableCopyStarted(TableEvent e) {
        String bottomMessage = getMessage("CopyProgressMonitor.copyingTable", 
                                           new String[] {e.getTableName(),
                                                      ""+e.getTableNumber(),
                                                      ""+e.getTableCount()});
        DualProgressBarDialog.setBottomMessage(bottomMessage);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#recordCopied(net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent)
     */
    public void recordCopied(RecordEvent e) {
        DualProgressBarDialog.setTopBarMinMax(0, e.getRecordCount());
        String topMessage = getMessage("CopyProgressMonitor.copyingRecords",
                                       new String[]{""+e.getRecordNumber(), 
                                                    ""+e.getRecordCount()});
        DualProgressBarDialog.setTopMessage(topMessage);
        DualProgressBarDialog.incrementTopBar(1);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#statementExecuted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent)
     */
    public void statementExecuted(StatementEvent e) {
        /* Do Nothing */
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableCopyFinished(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void tableCopyFinished(TableEvent e) {
        DualProgressBarDialog.setTopBarValue(0);
        DualProgressBarDialog.incrementBottomBar(1);
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#copyFinished(int)
     */
    public void copyFinished(int seconds) {
        closeProgress();
        String title = getMessage("CopyProgressMonitor.successTitle");
        String message = getMessage("CopyProgressMonitor.successMessage",
                                    seconds);
        showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
    }

   private void closeProgress()
   {
      DualProgressBarDialog.stopTimer();
      DualProgressBarDialog.setVisible(false);
      DualProgressBarDialog.dispose();
   }

   private String wordWrap(String data, int length) {
        String result = "";
        if (data.length() > length) {
            String[] parts = data.split("\\s");
            StringBuffer tmp = new StringBuffer();
            int count = 0;
            for (int i = 0; i < parts.length; i++) {
                count += parts[i].length();
                if (count > length) {
                    count = 0;
                    tmp.append("\n");
                } else {
                    tmp.append(" ");
                }
                tmp.append(parts[i]);
            }
            result = tmp.toString();
        } else {
            result = data;
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#handleError(net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent)
     */
    public void handleError(ErrorEvent e) {
        DualProgressBarDialog.stopTimer();
        DualProgressBarDialog.setVisible(false);
        if (e.getType() == ErrorEvent.SETUP_AUTO_COMMIT_TYPE) {
            String exMsg = "";
            if (e.getException() != null) {
                exMsg = e.getException().getMessage();
            }            
            String message = 
                getMessage("CopyProgressMonitor.setupAutoCommitException",
                           exMsg);
            String title = 
                getMessage("CopyProgressMonitor.setupAutoCommitExceptionTitle");
            int messageType = JOptionPane.ERROR_MESSAGE;
            showMessageDialog(message, title, messageType);
        }
        if (e.getType() == ErrorEvent.RESTORE_AUTO_COMMIT_TYPE) {
            String exMsg = "";
            if (e.getException() != null) {
                exMsg = e.getException().getMessage();
            }
            String message = 
                getMessage("CopyProgressMonitor.restoreAutoCommitException", 
                           exMsg);
            String title =
                getMessage("CopyProgressMonitor.restoreAutoCommitExceptionTitle");
            int messageType = JOptionPane.ERROR_MESSAGE;
            showMessageDialog(message, title, messageType);                                  
        }
        if (e.getType() == ErrorEvent.SQL_EXCEPTION_TYPE) {
            String exMessage = wordWrap(e.getException().getMessage(), 80);
            String sql = formatter.reformat(DBUtil.getLastStatement());
            String values = DBUtil.getLastStatementValues();
            String sqlAndValues = sql;
            if (values != null) {
                sqlAndValues += values;
            } else {
                sqlAndValues += "\n(No bind variables)";
            }
            int errorCode = ((SQLException)e.getException()).getErrorCode();
            log.error("SQL Error code = "+errorCode+" sql = "+sqlAndValues,
                      e.getException());
            String message = getMessage("CopyProgressMonitor.sqlErrorMessage",
                                        new String[]{exMessage, 
                                                     ""+errorCode,
                                                     sqlAndValues});
            String title = getMessage("CopyProgressMonitor.sqlErrorTitle");
            showMessageDialog(message, title, JOptionPane.ERROR_MESSAGE);
        }
        if (e.getType() == ErrorEvent.MAPPING_EXCEPTION_TYPE) {
            String title = getMessage("CopyProgressMonitor.mappingErrorTitle");
            String message = getMappingExceptionMessage(e.getException());
            log.error(message, e.getException());
            showMessageDialog(message, title, JOptionPane.ERROR_MESSAGE);
            if (pm != null) {
                pm.setProgress(pm.getMaximum());
            }
        }
        if (e.getType() == ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE) {
            String title = getMessage("CopyProgressMonitor.cancelledTitle");
            String message = getMessage("CopyProgressMonitor.cancelledMessage");
            showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);            
        }
        if (e.getType() == ErrorEvent.GENERIC_EXCEPTION) {
            String exmessage = e.getException().getMessage();
            String message = getMessage("CopyProgressMonitor.errorMessage",
                                        new String[]{exmessage});
            String title = getMessage("CopyProgressMonitor.errorTitle");
            showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
            
        }
        if (e.getException() != null) {
            log.error("handleError: exception="+e.getException().getMessage(), 
                      e.getException());
        }
        // TODO: ask the user if they want to "undo" the changes that the 
        // paste made.  Alternatively show them the specific problem and 
        // let them fix it.  Then allow them to retry the operation, starting
        // from the point at which the previous operation failed.

        closeProgress();
    }

    private String getMappingExceptionMessage(Exception e) {
        String message = "";
        if (e.getMessage().indexOf(":") != -1) {
            String[] parts = e.getMessage().split(":");
            try {
                int typeCode = Integer.parseInt(parts[1].trim());
                String typeName = JDBCTypeMapper.getJdbcTypeName(typeCode);
                message = getMessage("CopyProgressMonitor.mappingErrorMessage",
                                     new String[]{e.getMessage(), typeName});
            } catch (NumberFormatException nfe) {
                message = e.getMessage();
            }
        } else {
            message = e.getMessage();
        }                
        return message;
    }    
    
    private void showMessageDialog(final String message, 
                                   final String title, 
                                   final int messageType) 
    {
        final JFrame f = parent;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(f, 
                        message, 
                        title, 
                        messageType);
            }
        });
    }
    
    private void createProgressDialog() {
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.cancel();
            }
        };
        DualProgressBarDialog.getDialog(parent,
                                        getMessage("CopyProgressMonitor.copyProgressDialogTitle"), 
                                        false, 
                                        listener);
        DualProgressBarDialog.startTimer();
    }

    /**
     * @param executor The executor to set.
     */
    public void setExecutor(CopyExecutor executor) {
        this.executor = executor;
    }

    /**
     * @return Returns the executor.
     */
    public CopyExecutor getExecutor() {
        return executor;
    }

    /**
     * 
     * @param tableName
     * @return
     */
    private int showConfirmDeleteDialog(String tableName) {
        final String message = getMessage("CopyProgressMonitor.deleteRecordsMessage",
                                          tableName);
        
        final ConfirmMessageResult result = new ConfirmMessageResult();
        
        final String[] buttons = {"Yes", "Yes to all", "No", "Cancel" };
        
        if (SwingUtilities.isEventDispatchThread()) {
            result.option = 
                JOptionPane.showOptionDialog(parent, 
                                             message, 
                                             "Confirmation",
                                             JOptionPane.DEFAULT_OPTION,
                                             JOptionPane.QUESTION_MESSAGE,
                                             null,
                                             buttons,
                                             buttons[2]);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        result.option = 
                            JOptionPane.showOptionDialog(parent, 
                                                         message, 
                                                         "Confirmation",
                                                         JOptionPane.DEFAULT_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         null,
                                                         buttons,
                                                         buttons[2]);
                    }
                });
            } catch (Exception e) {
                log.error(
                        "showConfirmDeleteDialog: encountered unexpected exception ",
                        e);
            }
        }
        return result.option;
    }    
    
    private String showTextInputDialog(final Object message,
                                       final String title,
                                       final int messageType,
                                       final Icon icon,
                                       final Object initialValue) {
        
        final StringBuffer result = new StringBuffer();
        if (SwingUtilities.isEventDispatchThread()) {
            String tmp = (String)JOptionPane.showInputDialog(parent, 
                                                             message, 
                                                             title, 
                                                             messageType, 
                                                             icon, 
                                                             null, 
                                                             initialValue);
            result.append(tmp);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        String tmp = 
                            (String)JOptionPane.showInputDialog(parent, 
                                                                message, 
                                                                title, 
                                                                messageType, 
                                                                icon, 
                                                                null, 
                                                                initialValue);
                        result.append(tmp);
                    }
                });
            } catch (Exception e) {
                log.error(
                    "showTextInputDialog: encountered unexpected exception ",
                    e);
            }
        }
        return result.toString();
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.CopyPreference#deleteTableData(java.lang.String)
     */
    public boolean deleteTableData(String tableName) 
        throws UserCancelledOperationException 
    {
        if (deleteAllTableData) {
            return true;
        }
        int option = showConfirmDeleteDialog(tableName);
        if (option == 0) { // Yes 
            return true;
        }
        if (option == 1) { // Yes to all
            deleteAllTableData = true;
            return true;
        }
        if (option == 2) { // No
            return false;
        }                  
        if (option == 3) { // Cancel
            throw new UserCancelledOperationException();
        }
        return false;
    }
    
    public boolean appendRecordsToExisting() {

        return PreferencesManager.getPreferences().isAppendRecordsToExisting();
    }

    class ConfirmMessageResult {
        int option;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#analyzingTable(net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent)
     */
    public void analyzingTable(TableEvent e) {
        if (pm.isCanceled()) {
            
        }
        // i18n[CopyProgressMonitor.analyzingTableMessage=Analyzing table ]
        pm.setNote(getMessage("CopyProgressMonitor.analyzingTableMessage")+e.getTableName());
        pm.setProgress(e.getTableNumber());
    }

    /**
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener#tableAnalysisStarted(net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent)
     */
    public void tableAnalysisStarted(AnalysisEvent e) {
        SessionInfoProvider prov = e.getSessionInfoProvider(); 
        // TODO: set the total for the progress bar.
        pm = new ProgressMonitor(parent,  
                                 "Analyzing column names in tables to be copied",
                                 "",
                                 0,
                                 prov.getSourceDatabaseObjects().size()); 
        
    }

}
