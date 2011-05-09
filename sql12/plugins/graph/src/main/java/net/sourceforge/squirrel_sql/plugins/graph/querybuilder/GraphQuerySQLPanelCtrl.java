package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.EntryPanelManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class GraphQuerySQLPanelCtrl
{
   private GraphQuerySQLPanel _graphQuerySQLPanel;
   private EntryPanelManager _entryPanelManager;

   private static final String PREF_KEY_SQUIRREL_GRAPH_SQL_AUTO_SYNC = "Squirrel.graph.sqlAutoSync";


   public GraphQuerySQLPanelCtrl(ISession session, HideDockButtonHandler hideDockButtonHandler, final SyncListener syncListener)
   {
      _entryPanelManager = new EntryPanelManager(session);
      _entryPanelManager.init(null, null);
      _graphQuerySQLPanel = new GraphQuerySQLPanel(_entryPanelManager.getComponent(), hideDockButtonHandler);

      _graphQuerySQLPanel.chkAutoSyncSQL.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_SQUIRREL_GRAPH_SQL_AUTO_SYNC, true));

      _graphQuerySQLPanel.chkAutoSyncSQL.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAutoSyncChanged(syncListener);
         }
      });

      _graphQuerySQLPanel.btnSyncSQLNow.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            syncListener.synRequested();
         }
      });



   }


   private void onAutoSyncChanged(SyncListener syncListener)
   {
      Preferences.userRoot().putBoolean(PREF_KEY_SQUIRREL_GRAPH_SQL_AUTO_SYNC, _graphQuerySQLPanel.chkAutoSyncSQL.isSelected());

      if(_graphQuerySQLPanel.chkAutoSyncSQL.isSelected())
      {
         syncListener.synRequested();
      }
   }

   public GraphQuerySQLPanel getGraphQuerySQLPanel()
   {
      return _graphQuerySQLPanel;
   }


   public void setSQL(String sql)
   {
      _entryPanelManager.getEntryPanel().setText(sql, false);
   }

   public boolean isAutoSync()
   {
      return _graphQuerySQLPanel.chkAutoSyncSQL.isSelected();
   }
}
