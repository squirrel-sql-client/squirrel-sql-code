package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphQueryResultPanelCtrl
{
   private GraphQueryResultPanel _graphQueryResultPanel;
   private String _lastSQL;
   private boolean _isExecuting;
   private String _nextSQL;

   public GraphQueryResultPanelCtrl(ISession session, HideDockButtonHandler hideDockButtonHandler, final SyncListener syncListener)
   {
      _graphQueryResultPanel = new GraphQueryResultPanel(session, hideDockButtonHandler);

      _graphQueryResultPanel.btnSyncSQLResultNow.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            syncListener.synRequested(true);
         }
      });

      _graphQueryResultPanel.chkAutoSyncSQL.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAutoSyncChanged(syncListener);
         }
      });

      _graphQueryResultPanel.resultExecuterPanel.addSQLExecutionListener(new SQLExecutionAdapter(){
         @Override
         public void executionFinished()
         {
            onSQLExecutionFinished();
         }
      });
   }

   private void onSQLExecutionFinished()
   {
      _isExecuting = false;
      if(null != _nextSQL)
      {
         String nextSQL = _nextSQL;
         _nextSQL = null;
         execSQL(nextSQL, false);
      }
   }

   private void onAutoSyncChanged(SyncListener syncListener)
   {
      if(_graphQueryResultPanel.chkAutoSyncSQL.isSelected())
      {
         syncListener.synRequested(false);
      }
   }


   public void execSQL(String sql, boolean force)
   {
      if (false == force && null != _lastSQL && _lastSQL.trim().equals(sql))
      {
         return;
      }
      _lastSQL = null;


      if(null == sql || 0 == sql.trim().length())
      {
         return;
      }



      if(_isExecuting)
      {
         _nextSQL = sql;
         return;
      }

      _isExecuting = true;
      _graphQueryResultPanel.resultExecuterPanel.executeSQL(sql);
      _lastSQL = sql;
   }

   public GraphQueryResultPanel getGraphQueryResultPanel()
   {
      return _graphQueryResultPanel;
   }

   public boolean isAutoSync()
   {
      return _graphQueryResultPanel.chkAutoSyncSQL.isSelected();
   }
}
