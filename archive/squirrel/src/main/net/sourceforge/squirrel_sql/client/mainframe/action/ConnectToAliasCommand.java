package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Frame;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionFactory;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.db.ConnectionDialog;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.*;

/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToAliasCommand implements ICommand {
    private IApplication _app;

    /** Owner of the connection dialog. */
    private Frame _frame;

    /** The <TT>ISQLAlias</TT> to connect to. */
    private ISQLAlias _sqlAlias;

    /**
     * Ctor.
     *
     * @param   app     The <TT>IApplication</TT> that defines app API.
     * @param   frame   Owner of the connection dialog.
     * @param   alias   The <TT>ISQLAlias</TT> to connect to.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
     */
    public ConnectToAliasCommand(IApplication app, Frame frame, ISQLAlias sqlAlias)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (sqlAlias == null) {
            throw new IllegalArgumentException("Null ISQLAlias passed");
        }
        _app = app;
        _frame = frame;
        _sqlAlias = sqlAlias;
    }

    /**
     * Display connection dialog and attempt to open a connection.
     */
    public void execute() {
        ConnectionDialog dlog = new ConnectionDialog(_app, _frame, _sqlAlias,
                                            new OkHandler(_app, _sqlAlias));
        dlog.setVisible(true);
    }
    /**
     * Handler used if user presses OK in the connection dialog.
     */
    private static class OkHandler implements ConnectionDialog.IOkHandler {
        private IApplication _app;

        /** <TT>ISQLAlias</TT> to connect to. */
        private ISQLAlias _alias;

        /**
         * Ctor specifying the <TT>ISQLAlias</TT>.
         */
        OkHandler(IApplication app, ISQLAlias alias) {
            super();
            if (app == null) {
                throw new IllegalArgumentException("Null IApplication passed");
            }
            if (alias == null) {
                throw new IllegalArgumentException("Null ISQLAlias passed");
            }
            _app = app;
            _alias = alias;
        }
        public boolean execute(ConnectionDialog connDlog, ConnectionDialog.DialogResult result) {
            SQLConnection conn = null;
            boolean rc = false;
            ISQLDriver sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());
            try {
                SQLDriverManager mgr = _app.getSQLDriverManager();
                conn = mgr.getConnection(sqlDriver, _alias, result._user, result._password);
                ISession session = SessionFactory.createSession(_app, sqlDriver, _alias, conn);
                SessionSheet child = new SessionSheet(session);
                session.setSessionSheet(child);
                MainFrame.getInstance().addInternalFrame(child);
                child.setVisible(true);
                rc = true;
            } catch (BaseSQLException ex) {
/* i18n*/       new ErrorDialog(connDlog, "Unable to open SQL Connection: " + ex.getMessage()).show();
            } catch (ClassNotFoundException ex) {
/* i18n*/       new ErrorDialog(connDlog, "JDBC Driver class not found: " + ex.getMessage()).show();
                Logger logger = _app.getLogger();
                logger.showMessage(Logger.ILogTypes.ERROR, "JDBC Driver class not found");
                logger.showMessage(Logger.ILogTypes.ERROR, ex);
            } catch (NoClassDefFoundError ex) {
/* i18n*/       new ErrorDialog(connDlog, "JDBC Driver class not found: " + ex.getMessage()).show();
                Logger logger = _app.getLogger();
                logger.showMessage(Logger.ILogTypes.ERROR, "JDBC Driver class not found");
                logger.showMessage(Logger.ILogTypes.ERROR, ex);
            } catch (Throwable ex) {
                Logger logger = _app.getLogger();
/* i18n */  logger.showMessage(Logger.ILogTypes.ERROR, "Unexpected Error occured attempting to open an SQL connection.");
                logger.showMessage(Logger.ILogTypes.ERROR, ex);
                closeConnection(conn);
                new ErrorDialog(connDlog, ex).show();
            }
            return rc;
        }

        private void closeConnection(SQLConnection conn) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger logger = _app.getLogger();
                    logger.showMessage(Logger.ILogTypes.ERROR, "Error occured closing Connection");
                    logger.showMessage(Logger.ILogTypes.ERROR, ex);
                }
            }
        }
    }
}
