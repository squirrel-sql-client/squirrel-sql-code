package org.firebirdsql.squirrel.act;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
/**
 * This abstract command is a command that takes a table
 * as a parameter.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
abstract class AbstractMultipleSQLCommand implements ICommand
{
    /** Current session. */
    private ISession _session;

    /** Current plugin. */
    private final IPlugin _plugin;

    /**
     * Ctor specifying the current session.
     *
     * @throws    IllegalArgumentException
     *             Thrown if a?<TT>null</TT> <TT>ISession</TT>,
     *             <TT>Resources</TT> or <TT>IPlugin</TT> passed.
     */
    public AbstractMultipleSQLCommand(ISession session, IPlugin plugin)
    {
        super();
        if (session == null)
        {
            throw new IllegalArgumentException("ISession == null");
        }
        if (plugin == null)
        {
            throw new IllegalArgumentException("IPlugin == null");
        }

        _session = session;
        _plugin = plugin;
    }

    /**
     * Execute this command.
     */
    public void execute()
    {
        final StringBuffer buf = new StringBuffer(2048);
        final String sep = 
            "\n" + _session.getQueryTokenizer().getSQLStatementSeparator();

        final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
        final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

        for (int i = 0; i < dbObjs.length; ++i)
        {
            final String cmd = getSQL(dbObjs[i]);
            if (cmd != null && cmd.length() > 0)
            {
                buf.append(cmd).append(sep).append('\n');
            }
        }

        // Execute the SQL command in the SQL tab and then display the SQL tab.
        if (buf.length() > 0)
        {
            _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
            _session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
        }
    }

    /**
     * Retrieve the SQL to run.
     *
     * @return    the SQL to run.
     */
    protected abstract String getSQL(IDatabaseObjectInfo dbObj);
}
