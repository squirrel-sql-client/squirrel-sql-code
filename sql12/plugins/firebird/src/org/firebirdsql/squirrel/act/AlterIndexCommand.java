package org.firebirdsql.squirrel.act;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This command will run an &quot;ALTER INDEX&quot; over the
 * currently selected indices.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class AlterIndexCommand extends AbstractMultipleSQLCommand
{
    private final boolean _activate;
    /**
     * Ctor.
     *
     * @throws    IllegalArgumentException
     *             Thrown if a <TT>null</TT> <TT>ISession</TT>,
     *             <TT>Resources</TT> or <TT>IPlugin</TT> passed.
     * @param	activate	<tt>true</tt> to activate the index else <tt>false</tt> 
     */
    public AlterIndexCommand(ISession session, IPlugin plugin, boolean activate)
    {
        super(session, plugin);
        _activate = activate;
    }

    /**
     * Retrieve the SQL to run.
     *
     * @param	dbObj	The index to run the command over.
     *
     * @return    the SQL to run.
     */
    protected String getSQL(IDatabaseObjectInfo dbObj)
    {
        return "ALTER INDEX " + dbObj.getQualifiedName() + (_activate ? " ACTIVE" : " INACTIVE");
    }
}
