package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelPanelCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CancelPanelCtrl.class);

   private CancelPanel _panel;


   /**
    * Total number of queries that will be executed.
    */
   private int _queryCount;

   /**
    * Number of the query currently being executed (starts from 1).
    */
   private int _currentQueryIndex = 0;
   private CancelPanelListener _listener;
   private final Timer _timer;
   private final long _beginMillis;

   CancelPanelCtrl(CancelPanelListener listener, ISession session)
   {
      _listener = listener;
      _panel = new CancelPanel(session);

      _beginMillis = System.currentTimeMillis();
      _timer = new Timer(300, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onUpdateExecutionTime();
         }
      });
      _timer.setRepeats(true);
      _timer.start();


      _panel.cancelBtn.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });

      _panel.closeBtn.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onClose();
         }
      });


   }

   private void onUpdateExecutionTime()
   {
      _panel.txtExecTimeCounter.setText("" + (System.currentTimeMillis() - _beginMillis));
   }

   void incCurrentQueryIndex()
   {
      ++_currentQueryIndex;
   }

   void setSQL(final String sql)
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            // i18n[SQLResultExecuterPanel.currentSQLLabel={0} of {1} - {2}]
            String label =
                  s_stringMgr.getString("SQLResultExecuterPanel.currentSQLLabel",
                        new Object[]{String.valueOf(_currentQueryIndex),
                              String.valueOf(_queryCount),
                              sql});

            _panel.sqlLbl.setText(label);
         }
      };

      GUIUtils.processOnSwingEventThread(runnable);

   }

   void setStatusLabel(final String text)
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            _panel.currentStatusLbl.setText(text);
         }
      };

      GUIUtils.processOnSwingEventThread(runnable);
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
      _listener.cancelRequested();
   }

   private void onClose()
   {
      _panel.cancelBtn.doClick();
      _listener.closeRquested();
      _timer.stop();
   }



   CancelPanel getPanel()
   {
      return _panel;
   }
}
