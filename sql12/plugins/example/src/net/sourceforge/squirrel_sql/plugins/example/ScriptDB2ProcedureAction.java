package net.sourceforge.squirrel_sql.plugins.example;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.Resources;

public class ScriptDB2ProcedureAction extends SquirrelAction {
    private static final long serialVersionUID = 1L;

    transient private ISession _session;

    // /////////////////////////////////////////////////////////
    // IBM DB 2 specific code to read procedure definitions.    
    private static final String SQL = 
        "SELECT TEXT " +
        "FROM SYSIBM.SYSPROCEDURES " +
        "WHERE PROCNAME = ? ";
    
    public ScriptDB2ProcedureAction(IApplication app, Resources rsrc,
            ISession session) {
        super(app, rsrc);
        _session = session;
    }

    public void actionPerformed(ActionEvent evt) {
        PreparedStatement stat = null;
        ResultSet res = null;
        try {

            IDatabaseObjectInfo[] dbObjs = _session.getSessionInternalFrame()
                    .getObjectTreeAPI().getSelectedDatabaseObjects();

            stat = _session.getSQLConnection().prepareStatement(SQL);

            StringBuffer script = new StringBuffer();
            for (int i = 0; i < dbObjs.length; i++) {
                IProcedureInfo pi = (IProcedureInfo) dbObjs[i];
                stat.setString(1, pi.getSimpleName());
                res = stat.executeQuery();
                res.next();
                res.getString("TEXT");

                script.append(res.getString("TEXT"));
                script.append(getStatementSeparator());
                res.close();
            }
            stat.close();

            SessionInternalFrame sessMainFrm = _session
                    .getSessionInternalFrame();
            sessMainFrm.getSQLPanelAPI().appendSQLScript(script.toString());
            sessMainFrm.getSessionPanel().selectMainTab(
                    ISession.IMainPanelTabIndexes.SQL_TAB);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SQLUtilities.closeResultSet(res);
            SQLUtilities.closeStatement(stat);
        }
    }

    private String getStatementSeparator() {
        String statementSeparator = _session.getQueryTokenizer()
                .getSQLStatementSeparator();

        if (1 < statementSeparator.length()) {
            statementSeparator = "\n" + statementSeparator + "\n";
        }

        return statementSeparator;
    }

}
