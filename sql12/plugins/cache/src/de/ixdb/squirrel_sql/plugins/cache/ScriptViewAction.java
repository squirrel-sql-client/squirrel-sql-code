package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;

public class ScriptViewAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private CachePlugin _plugin;

   private static final String PREFIX_SQLUSER = "SQLUser.";

   public ScriptViewAction(IApplication app, Resources rsrc, CachePlugin plugin)
		throws IllegalArgumentException
	{
		super(app, rsrc);
      _plugin = plugin;
   }

   public void setSession(ISession session)
   {
      _session = session;
   }


	public void actionPerformed(ActionEvent evt)
	{
      new ScriptViewCommand(_session).execute();
   }
}
