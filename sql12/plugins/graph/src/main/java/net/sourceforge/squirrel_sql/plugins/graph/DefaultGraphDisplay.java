package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class DefaultGraphDisplay implements IGraphDisplay
{
   @Override
   public void showGraph(GraphMainPanelTab graphPane, ISession session)
   {
      session.getSessionSheet().addMainTab(graphPane);
   }

   @Override
   public void removeGraph(GraphMainPanelTab graphPane, ISession session)
   {
      session.getSessionSheet().removeMainTab(graphPane);
   }

   @Override
   public void renameGraph(GraphMainPanelTab graphPane, ISession session, String newName)
   {
      int index = session.getSessionSheet().removeMainTab(graphPane);
      graphPane.setTitle(newName);
      session.getSessionSheet().insertMainTab(graphPane, index);
   }
}
