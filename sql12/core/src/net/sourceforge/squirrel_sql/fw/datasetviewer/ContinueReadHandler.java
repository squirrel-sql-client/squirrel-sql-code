package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class ContinueReadHandler
{
   private JTable _table;

   private Timer _timer;
   private ContinueReadChannel _continueReadChannel;
   private AdjustmentListener _adjustmentListener;


   public ContinueReadHandler(JTable table)
   {
      _table = table;

      _timer = new Timer(300 , new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onCheckTableEndReached();
         }
      });


      _timer.setRepeats(false);

      _adjustmentListener = new AdjustmentListener()
      {
         @Override
         public void adjustmentValueChanged(AdjustmentEvent e)
         {
            runTimer();
         }
      };


   }

   public void setContinueReadChannel(ContinueReadChannel continueReadChannel)
   {
      _continueReadChannel = continueReadChannel;
      if (readyForAdjustmentListening())
      {
         JScrollPane scrollPane = (JScrollPane) _table.getParent().getParent();
         scrollPane.getVerticalScrollBar().addAdjustmentListener(_adjustmentListener);
      }

   }

   private boolean readyForAdjustmentListening()
   {
      if(null == _continueReadChannel || null == _table.getParent() || false == _table.getParent().getParent() instanceof JScrollPane)
      {
         return false;
      }
      return true;
   }


   private void runTimer()
   {
      _timer.restart();
   }

   private void onCheckTableEndReached()
   {
      if(null == _continueReadChannel)
      {
         return;
      }

      Rectangle visibleRect = _table.getVisibleRect();
      int row = _table.rowAtPoint(new Point(0, visibleRect.y + visibleRect.height - 3));

      if(row == _table.getRowCount() - 1)
      {
         _continueReadChannel.readMoreResults();

      }
   }

   public void disableContinueRead()
   {
      if(readyForAdjustmentListening())
      {
         JScrollPane scrollPane = (JScrollPane) _table.getParent().getParent();
         scrollPane.getVerticalScrollBar().removeAdjustmentListener(_adjustmentListener);
      }

//      if(null != _continueReadChannel)
//      {
//         _continueReadChannel.closeStatementAndResultSet();
//
//      }
   }
}
