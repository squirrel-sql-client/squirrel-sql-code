package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound.FinishedNotificationSoundHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Dimension;

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
   private QueryHolder _currentSql;

   CancelPanelCtrl(CancelPanelListener listener, ISession session, FinishedNotificationSoundHandler finishedNotificationSoundHandler)
   {
      _listener = listener;
      _panel = new CancelPanel(session);
      _finishedNotificationSoundHandler = finishedNotificationSoundHandler;

      _panel.cancelBtn.addActionListener(e -> onCancel());

      _panel.btnCopySqlToClip.addActionListener(e -> onCopyCurrentSqlToClip());

      _panel.btnShowExecutingSql.addActionListener(e -> onShowCurrentSql());

      _panel.closeBtn.addActionListener(e -> onClose());

      _panel.chkPlaySoundWhenFinished.addActionListener(e -> _finishedNotificationSoundHandler.onPlayFinishedSoundChecked(_panel.chkPlaySoundWhenFinished.isSelected()));

      _panel.btnConfigureFinishedSound.addActionListener(e -> _finishedNotificationSoundHandler.onConfigureFinishedSound(_panel));

      _timer = new TimerHolder(_panel.txtExecTimeCounter, _panel.txtNumberOfRowsRead, _panel.chkPlaySoundWhenFinished, _finishedNotificationSoundHandler);
   }

   void incCurrentQueryIndex()
   {
      ++_currentQueryIndex;
   }

   void setSQL(final QueryHolder queryHolder)
   {
      GUIUtils.processOnSwingEventThread(() -> onSetSql(queryHolder));
   }

   private void onSetSql(QueryHolder sql)
   {
      _currentSql = sql;
      String label = s_stringMgr.getString("SQLResultExecuterPanel.currentSQLLabel", String.valueOf(_currentQueryIndex), String.valueOf(_queryCount), sql.getCleanQuery());
      _panel.sqlLbl.setText(label);
   }

   private void onCopyCurrentSqlToClip()
   {
      if(null == _currentSql || StringUtilities.isEmpty(_currentSql.getOriginalQuery(), true))
      {
         return;
      }
      ClipboardUtil.copyToClip(_currentSql.getOriginalQuery());
   }

   private void onShowCurrentSql()
   {
      if(null == _currentSql || StringUtilities.isEmpty(_currentSql.getOriginalQuery(), true))
      {
         return;
      }
      JPopupMenu popupMenu = new JPopupMenu();

      JTextPane textPane = new JTextPane();
      textPane.setText(_currentSql.getOriginalQuery());

      textPane.setPreferredSize(new Dimension(_panel.getSize().width * 2 / 3, _panel.getSize().height * 2 / 3));

      final JScrollPane scrollPane = new JScrollPane(textPane);
      popupMenu.add(scrollPane);

      popupMenu.show(_panel.btnShowExecutingSql, 0, _panel.btnShowExecutingSql.getHeight());

      GUIUtils.forceScrollToBegin(scrollPane);
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
