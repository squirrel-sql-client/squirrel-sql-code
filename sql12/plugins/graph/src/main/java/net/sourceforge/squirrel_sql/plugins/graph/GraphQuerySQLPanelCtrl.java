package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.EntryPanelManager;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;

public class GraphQuerySQLPanelCtrl
{
   private GraphQuerySQLPanel _graphQuerySQLPanel;
   private EntryPanelManager _entryPanelManager;

   public GraphQuerySQLPanelCtrl(ISession session)
   {
      _entryPanelManager = new EntryPanelManager(session);
      _entryPanelManager.init(null, null);
      _graphQuerySQLPanel = new GraphQuerySQLPanel(_entryPanelManager.getComponent());
   }

   public GraphQuerySQLPanel getGraphQuerySQLPanel()
   {
      return _graphQuerySQLPanel;
   }


   public void setSQL(String sql)
   {
      _entryPanelManager.getEntryPanel().setText(sql, false);
   }
}
