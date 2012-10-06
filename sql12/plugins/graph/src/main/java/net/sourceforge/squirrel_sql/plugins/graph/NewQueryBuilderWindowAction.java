package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import java.awt.event.ActionEvent;

public class NewQueryBuilderWindowAction extends SquirrelAction implements ISessionAction
{
   private GraphPlugin _graphPlugin;
   private ISession _session;

   public NewQueryBuilderWindowAction(IApplication app, PluginResources resources, GraphPlugin graphPlugin)
   {
      super(app, resources);
      _graphPlugin = graphPlugin;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      GraphController gc = _graphPlugin.createNewGraphControllerForSession(_session, true);
      gc.showQueryBuilderInWindowBesidesObjectTree();
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;

      setEnabled(null != _session);

   }
}
