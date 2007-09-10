package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import java.awt.event.ActionEvent;

public class ScriptCdlAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private CachePlugin _plugin;

   public ScriptCdlAction(IApplication app, Resources rsrc, CachePlugin plugin)
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
      new ScriptCdlCommand(_session, _plugin).execute();
   }
}
