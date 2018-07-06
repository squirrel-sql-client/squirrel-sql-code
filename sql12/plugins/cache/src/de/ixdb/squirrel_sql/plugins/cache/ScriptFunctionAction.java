package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import java.awt.event.ActionEvent;

public class ScriptFunctionAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private CachePlugin _plugin;

   public ScriptFunctionAction(IApplication app, Resources rsrc, CachePlugin plugin)
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
      new ScriptFunctionCommand(_session, _plugin).execute();
   }
}
