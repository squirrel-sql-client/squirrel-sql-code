package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.sqlgen.QueryBuilderSQLGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class QueryBuilderController
{
   private static final String PREF_KEY_SQL_DOCK_HEIGHT = "Squirrel.graph.sqldock.height";
   private static final String PREF_KEY_RESULT_DOCK_HEIGHT = "Squirrel.graph.resultdock.height";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryBuilderController.class);

   private JPanel _panel;
   private JToggleButton _btnSQL;
   private JToggleButton _btnResult;
   private GraphDockHandle _sqlDockHandle;
   private GraphDockHandle _resultDockHandle;
   private TableFramesModel _tableFramesModel;
   private ISession _session;
   private GraphQuerySQLPanelCtrl _graphQuerySQLPanelCtrl;
   private GraphQueryResultPanelCtrl _graphQueryResultPanelCtrl;
   private SessionAdapter _sessionAdapter;

   public QueryBuilderController(TableFramesModel tableFramesModel, GraphControllerFacade graphControllerFacade, ISession session, GraphPlugin plugin, StartButtonHandler startButtonHandler)
   {
      _tableFramesModel = tableFramesModel;
      _session = session;
      _panel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _panel.add(startButtonHandler.getButton(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      _btnSQL = new JToggleButton(s_stringMgr.getString("QueryBuilderController.SQL"));
      _panel.add(_btnSQL, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _btnResult = new JToggleButton(s_stringMgr.getString("QueryBuilderController.Result"));
      _panel.add(_btnResult, gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _panel.add(new JPanel(), gbc);

      GraphPluginResources rsrc = new GraphPluginResources(plugin);
      _graphQuerySQLPanelCtrl = new GraphQuerySQLPanelCtrl(_session, new HideDockButtonHandler(_btnSQL, rsrc), createSQLSyncListener());
      _graphQueryResultPanelCtrl = new GraphQueryResultPanelCtrl(_session, new HideDockButtonHandler(_btnResult, rsrc), createResultSyncListener());

      initHandels(graphControllerFacade);



      _sessionAdapter = new SessionAdapter()
      {
         @Override
         public void sessionClosing(SessionEvent evt)
         {
            onSessionClosing();
         }
      };

      _session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);


      _btnSQL.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onSQL();
         }
      });

      _btnResult.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onResult();
         }
      });

      _tableFramesModel.addTableFramesModelListener(new TableFramesModelListener()
      {
         @Override
         public void modelChanged(TableFramesModelChangeType changeType)
         {
            onModelChanged();
         }
      });
   }

   private SyncListener createResultSyncListener()
   {
      return new SyncListener()
      {
         @Override
         public void synRequested()
         {
            _graphQueryResultPanelCtrl.execSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel));
         }
      };
   }

   private SyncListener createSQLSyncListener()
   {
      return new SyncListener()
      {
         @Override
         public void synRequested()
         {
            _graphQuerySQLPanelCtrl.setSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel));
         }
      };

   }


   private void onModelChanged()
   {
      if (_sqlDockHandle.isShowing() && _graphQuerySQLPanelCtrl.isAutoSync())
      {
         _graphQuerySQLPanelCtrl.setSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel));
      }
      else if(_resultDockHandle.isShowing()  && _graphQueryResultPanelCtrl.isAutoSync())
      {
         _graphQueryResultPanelCtrl.execSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel));
      }
   }

   private void initHandels(GraphControllerFacade graphControllerFacade)
   {
      int sqlHeight = Preferences.userRoot().getInt(PREF_KEY_SQL_DOCK_HEIGHT, 250);
      _sqlDockHandle = new GraphDockHandle(graphControllerFacade, _graphQuerySQLPanelCtrl.getGraphQuerySQLPanel(), sqlHeight);

      int resHeight = Preferences.userRoot().getInt(PREF_KEY_RESULT_DOCK_HEIGHT, 250);
      _resultDockHandle = new GraphDockHandle(graphControllerFacade, _graphQueryResultPanelCtrl.getGraphQuerySQLPanel(), resHeight);
   }

   private void onSessionClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_SQL_DOCK_HEIGHT, _sqlDockHandle.getLastHeigth());
      Preferences.userRoot().putInt(PREF_KEY_RESULT_DOCK_HEIGHT, _resultDockHandle.getLastHeigth());

      // To prevent memory leaks
      _session.getApplication().getSessionManager().removeSessionListener(_sessionAdapter);

   }

   private void onResult()
   {
      if (_btnResult.isSelected())
      {
         if (_btnSQL.isSelected())
         {
            _sqlDockHandle.hide();
            _btnSQL.setSelected(false);
         }
         _resultDockHandle.show();
         onModelChanged();
      }
      else
      {
         _resultDockHandle.hide();
      }
   }

   private void onSQL()
   {
      if (_btnSQL.isSelected())
      {
         if (_btnResult.isSelected())
         {
            _resultDockHandle.hide();
            _btnResult.setSelected(false);
         }
         _sqlDockHandle.show();
         onModelChanged();
      }
      else
      {
         _sqlDockHandle.hide();
      }
   }

   public JPanel getBottomPanel()
   {
      return _panel;
   }

   public void activate(boolean b)
   {
      if(false == b)
      {
         _btnSQL.setSelected(false);
         _btnResult.setSelected(false);
      }
   }
}
