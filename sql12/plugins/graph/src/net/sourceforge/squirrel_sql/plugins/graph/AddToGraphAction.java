package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import java.awt.event.ActionEvent;


public class AddToGraphAction extends SquirrelAction implements ISessionAction
{

   /**
    * Current session.
    */
   protected ISession _session;

   /**
    * Current plugin.
    */
   protected final GraphPlugin _plugin;

   public AddToGraphAction(IApplication app, Resources rsrc, GraphPlugin plugin)
   {
      super(app, rsrc);
      _plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_session != null)
      {
         ObjectTreeNode[] selectedNodes = _session.getSessionSheet().getObjectTreePanel().getSelectedNodes();
         TableToGraph.sendToGraph(_plugin, _session, TableToAddWrapper.wrap(selectedNodes));
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
