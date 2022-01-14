package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class TableToGraph
{

   public static void sendToGraph(GraphPlugin plugin, ISession session, TableToAddWrapper... toAdd)
   {
      Positioner positioner = new Positioner();
      GraphController toAddTo = null;

      for (int i = 0; i < toAdd.length; i++)
      {
         if (toAdd[i].isTable()) ;
         {
            if (null == toAddTo)
            {
               GraphController[] controllers = plugin.getGraphControllers(session);
               if (0 == controllers.length)
               {
                  toAddTo = plugin.createNewGraphControllerForSession(session, false);
                  toAddTo.setMode(Mode.QUERY_BUILDER);
               }
               else
               {
                  GraphSelectionDialogController dlg = new GraphSelectionDialogController(controllers, session.getApplication().getMainFrame());
                  dlg.doModal();

                  if(false == dlg.isOK())
                  {
                     return;
                  }
                  if(null == dlg.getSelectedController())
                  {
                     toAddTo = plugin.createNewGraphControllerForSession(session, false);
                     toAddTo.setMode(Mode.QUERY_BUILDER);

                  }
                  else
                  {
                     toAddTo = dlg.getSelectedController();
                  }
               }
            }

            toAddTo.addTable(toAdd[i], positioner);
         }
      }
   }


}
