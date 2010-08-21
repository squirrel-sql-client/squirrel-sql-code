package org.firebirdsql.squirrel.act;
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
/**
 * This <TT>Action</TT> will run a &quot;EXPLAIN TABLE&quot; over the
 * currently selected tables.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DeactivateIndexAction extends SquirrelAction implements ISessionAction 
{
	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final IPlugin _plugin;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	rsrc		Plugins resources.
	 * @param	plugin		This plugin.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a<TT>null</TT> <TT>IApplication</TT>,
	 * 			<TT>Resources</TT> or <TT>IPlugin</TT> passed.
	 */
	public DeactivateIndexAction(IApplication app, Resources rsrc,
							IPlugin plugin)
	{
		super(app, rsrc);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Resources == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}

		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new AlterIndexCommand(_session, _plugin, false).execute();
			}
			catch (Throwable th)
			{
				_session.showErrorMessage(th);
			}
		}
	}

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}
}
