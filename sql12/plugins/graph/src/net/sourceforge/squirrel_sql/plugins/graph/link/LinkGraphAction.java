package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;

import java.awt.event.ActionEvent;

public class LinkGraphAction extends SquirrelAction implements ISessionAction
{
   private GraphPlugin _graphPlugin;
   private ISession _session;

   public LinkGraphAction(IApplication app, PluginResources resources, GraphPlugin graphPlugin)
   {
      super(app, resources);
      _graphPlugin = graphPlugin;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(null == _session)
      {
         return;
      }

      new LinkGraphController(_graphPlugin, _session);
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
   }
}
