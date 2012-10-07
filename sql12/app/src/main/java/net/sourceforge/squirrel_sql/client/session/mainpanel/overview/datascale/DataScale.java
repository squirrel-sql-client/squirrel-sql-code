package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class DataScale implements Comparable<DataScale>
{
   private DataScalePanel _panel;

   private ArrayList<Interval> _intervals = new ArrayList<Interval>();
   private String _column;
   private DataScaleListener _dataScaleListener;
   private int _columnIndex;
   private ColumnDisplayDefinition _columnDisplayDefinition;
   private HashMap<JButton, Interval> _intervalByButton = new HashMap<JButton, Interval>();
   private Color _defaultButtonBackgroundColor;

   public DataScale(String column, DataScaleListener dataScaleListener, int columnIndex, ColumnDisplayDefinition columnDisplayDefinition)
   {
      _column = column;
      _dataScaleListener = dataScaleListener;

      // Needed just to define reasonable sorting of data column in overview table
      _columnIndex = columnIndex;
      _columnDisplayDefinition = columnDisplayDefinition;
   }

   public String getColumn()
   {
      return _column;
   }

   public void addInterval(Interval interval)
   {
      _intervals.add(interval);
   }


   public DataScalePanel getPanel()
   {
      if(null == _panel)
      {
         double[] wights = new double[_intervals.size()];

         for (int i = 0; i < _intervals.size(); i++)
         {
            wights[i] = _intervals.get(i).getWight();
         }

         _panel = new DataScalePanel(new DataScaleLayout(wights));

         for (int i = 0; i < _intervals.size(); i++)
         {
            _panel.add(createButton(i));
         }
      }

      return _panel;
   }

   private JButton createButton(final int intervalIx)
   {
      final String text = _intervals.get(intervalIx).getLabel();

      final JButton ret = new JButton(text);

      _defaultButtonBackgroundColor = ret.getBackground();
      ret.setToolTipText(_intervals.get(intervalIx).getToolTip());

      ret.setBorder(BorderFactory.createLineBorder(Color.black));

      ret.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onButtonPressed(_intervals.get(intervalIx), ret);
         }
      });

      _intervalByButton.put(ret, _intervals.get(intervalIx));

      return ret;
   }

   private void onButtonPressed(Interval interval, JButton ret)
   {
      _dataScaleListener.intervalSelected(interval, ret);
   }


   public double getSumWeights()
   {
      double ret = 0;

      for (Interval interval : _intervals)
      {
         ret += interval.getWight();
      }

      return ret;
   }

   public int getSumLens()
   {
      int ret = 0;

      for (Interval interval : _intervals)
      {
         ret += interval.getLen();
      }
      return ret;

   }

   @Override
   public int compareTo(DataScale other)
   {
      return ((Integer)_columnIndex).compareTo(other._columnIndex);
   }

   void showInTableSelected(JButton buttonPopupWasOpenedAt)
   {
      _dataScaleListener.showInTable(_intervalByButton.get(buttonPopupWasOpenedAt));
   }

   void showInTableWin(JButton buttonPopupWasOpenedAt)
   {
      _dataScaleListener.showInTableWin(_intervalByButton.get(buttonPopupWasOpenedAt));
   }

   public JButton[] getButtons()
   {
      return _intervalByButton.keySet().toArray(new JButton[_intervalByButton.keySet().size()]);
   }

   public ArrayList<String> getIntervalReports()
   {
      ArrayList<String> ret = new ArrayList<String>();

      for( int i = 0; i < _intervals.size(); i++ )
      {
         ret.add(_intervals.get(i).getReport());
      }

      return ret;
   }

   public ArrayList<Interval> getIntervals()
   {
      return _intervals;
   }

   public int getColumnIx()
   {
      return _columnIndex;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _columnDisplayDefinition;
   }

   public String getIntervalWidth()
   {
      if(0 == _intervals.size())
      {
         return null;
      }

      return _intervals.get(_intervals.size()-1).getWidth(); // Take the last because the first may start with NULL and no width can be calculated from that.
   }

   public IndexedColumn getIndexedColumn()
   {
      return _intervals.get(0).getIndexedColumn();
   }

   public void initButtonColor(JButton intervalButtonClicked)
   {
      for (JButton btn : getButtons())
      {
         if(btn == intervalButtonClicked)
         {
            btn.setBackground(Color.yellow);
         }
         else
         {
            btn.setBackground(_defaultButtonBackgroundColor);
         }
      }
   }
}
