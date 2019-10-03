package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound.FinishedNotificationSoundHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class CancelPanelCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CancelPanelCtrl.class);

   private CancelPanel _panel;
   private FinishedNotificationSoundHandler _finishedNotificationSoundHandler;


   /**
    * Total number of queries that will be executed.
    */
   private int _queryCount;

   /**
    * Number of the query currently being executed (starts from 1).
    */
   private int _currentQueryIndex = 0;
   private CancelPanelListener _listener;
   private final TimerHolder _timer;

   CancelPanelCtrl(CancelPanelListener listener, ISession session, FinishedNotificationSoundHandler finishedNotificationSoundHandler)
   {
      _listener = listener;
      _panel = new CancelPanel(session);
      _finishedNotificationSoundHandler = finishedNotificationSoundHandler;

      _panel.cancelBtn.addActionListener(e -> onCancel());

      _panel.closeBtn.addActionListener(e -> onClose());

      _panel.chkPlaySoundWhenFinished.addActionListener(e -> _finishedNotificationSoundHandler.onPlayFinishedSoundChecked(_panel.chkPlaySoundWhenFinished.isSelected()));

      _panel.btnConfigureFinishedSound.addActionListener(e -> _finishedNotificationSoundHandler.onConfigureFinishedSound(_panel));

      _timer = new TimerHolder(_panel.txtExecTimeCounter, _panel.txtNumberOfRowsRead, _panel.chkPlaySoundWhenFinished, _finishedNotificationSoundHandler);
   }
   void incCurrentQueryIndex()
   {
      ++_currentQueryIndex;
   }

   void setSQL(final String sql)
   {
      GUIUtils.processOnSwingEventThread(() -> onSetSql(sql));
   }

   private void onSetSql(String sql)
   {
      // i18n[SQLResultExecuterPanel.currentSQLLabel={0} of {1} - {2}]
      String label = s_stringMgr.getString("SQLResultExecuterPanel.currentSQLLabel", String.valueOf(_currentQueryIndex),String.valueOf(_queryCount),sql);

      _panel.sqlLbl.setText(label);
   }

   void setStatusLabel(final String text)
   {
      GUIUtils.processOnSwingEventThread(() -> _panel.currentStatusLbl.setText(text));
   }

   void setQueryCount(int value)
   {
      _queryCount = value;
      _currentQueryIndex = 0;
   }

   int getTotalCount()
   {
      return _queryCount;
   }

   int getCurrentQueryIndex()
   {
      return _currentQueryIndex;
   }


   private void onCancel()
   {
      _finishedNotificationSoundHandler.onPlayFinishedSoundChecked(false);
      _listener.cancelRequested();
   }

   private void onClose()
   {
      _panel.cancelBtn.doClick();
      _listener.closeRquested();
   }



   CancelPanel getPanel()
   {
      return _panel;
   }

   public void wasRemoved()
   {
      _timer.stop();
   }

   public void setResultSetDataSetInProgress(ResultSetDataSet rsds)
   {
      _timer.setResultSetDataSetInProgress(rsds);
   }
}
