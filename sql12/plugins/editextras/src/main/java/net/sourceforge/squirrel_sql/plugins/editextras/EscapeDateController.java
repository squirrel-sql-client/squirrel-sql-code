package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;


public class EscapeDateController
{
   private EscapeDateFrame _frame;
   private ISession _session;


   public EscapeDateController(ISession session, MainFrame mainFrame)
   {
      _session = session;
      _frame = new EscapeDateFrame(mainFrame);

      _frame.btnTimestamp.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onTimeStamp();
         }
      });
      _frame.btnDate.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDate();
         }
      });
      _frame.btnTime.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onTime();
         }
      });

      Calendar cal = Calendar.getInstance();

      _frame.txtYear.setText("" + cal.get(Calendar.YEAR));
      _frame.txtMonth.setText("" + (cal.get(Calendar.MONTH) + 1));
      _frame.txtDay.setText("" + cal.get(Calendar.DAY_OF_MONTH));
      _frame.txtHour.setText("" + cal.get(Calendar.HOUR_OF_DAY));
      _frame.txtMinute.setText("" + cal.get(Calendar.MINUTE));
      _frame.txtSecond.setText("" + cal.get(Calendar.SECOND));

      GUIUtils.centerWithinParent(_frame);
      _frame.setVisible(true);

      _frame.txtYear.requestFocus();

   }

   private String prefixNulls(String toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while(ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }

   private void onTime()
   {
      String esc = "{t '" + prefixNulls(_frame.txtHour.getText(), 2) + ":" +
                            prefixNulls(_frame.txtMinute.getText(), 2) + ":" +
                            prefixNulls(_frame.txtSecond.getText(),2) + "'}";

      _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().replaceSelection(esc);
      _frame.setVisible(false);
      _frame.dispose();
   }

   private void onDate()
   {
      String esc = "{d '" + prefixNulls(_frame.txtYear.getText(), 4) + "-" +
                            prefixNulls(_frame.txtMonth.getText(), 2) + "-" +
                            prefixNulls(_frame.txtDay.getText(), 2) + "'}";

      _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().replaceSelection(esc);
      _frame.setVisible(false);
      _frame.dispose();
   }

   private void onTimeStamp()
   {
      String esc = "{ts '" + prefixNulls(_frame.txtYear.getText(), 4) + "-" +
                             prefixNulls(_frame.txtMonth.getText(), 2) + "-" +
                             prefixNulls(_frame.txtDay.getText(), 2) + " "+
                             prefixNulls(_frame.txtHour.getText(), 2) + ":" +
                             prefixNulls(_frame.txtMinute.getText(), 2) + ":" +
                             prefixNulls(_frame.txtSecond.getText(), 2) + "'}";

      _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().replaceSelection(esc);
      _frame.setVisible(false);
      _frame.dispose();
   }

}
