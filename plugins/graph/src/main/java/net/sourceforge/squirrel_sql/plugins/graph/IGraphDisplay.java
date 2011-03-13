package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface IGraphDisplay
{
   void showGraph(GraphMainPanelTab graphPane, ISession session);

   void removeGraph(GraphMainPanelTab graphPane, ISession session);

   void renameGraph(GraphMainPanelTab graphPane, ISession session, String newName);
}
