package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;


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

         Point[] refCascadeIndent = new Point[1];
         GraphController toAddTo = null;

         for (int i = 0; i < selectedNodes.length; i++)
         {
            if (selectedNodes[i].getDatabaseObjectType() == DatabaseObjectType.TABLE) ;
            {
               if (null == toAddTo)
               {
                  GraphController[] controllers = _plugin.getGraphControllers(_session);
                  if (0 == controllers.length)
                  {
                     toAddTo = _plugin.createNewGraphControllerForSession(_session);
                  }
                  else
                  {
                     GraphSelectionDialogController dlg = new GraphSelectionDialogController(controllers, _session.getApplication().getMainFrame());
                     dlg.doModal();

                     if(false == dlg.isOK())
                     {
                        return;
                     }
                     if(null == dlg.getSelectedController())
                     {
                        toAddTo = _plugin.createNewGraphControllerForSession(_session);
                     }
                     else
                     {
                        toAddTo = dlg.getSelectedController();
                     }
                  }
               }

               toAddTo.addTable(selectedNodes[i], refCascadeIndent);
            }

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
