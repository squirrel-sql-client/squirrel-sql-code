package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.Interval;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.ScaleFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

class BarChartCreator
{
   private DataScale _xAxisDataScale;
   private DataScale _yAxisDataScale;
   private Integer _callDepth;
   private ChartConfigMode _mode;
   private String _title;
   private JFreeChart _chart;
   private String _label;

   public BarChartCreator(DataScale xAxisDataScale, DataScale yAxisDataScale, Integer callDepth, ChartConfigMode mode)
   {
      _xAxisDataScale = xAxisDataScale;
      _yAxisDataScale = yAxisDataScale;
      _callDepth = callDepth;
      _mode = mode;

      DataScale newScale;ArrayList<Object[]> rows = new ArrayList<Object[]>();

      for (Interval xAxisInterval : _xAxisDataScale.getIntervals())
      {
         rows.addAll(xAxisInterval.getResultRows());
      }


      ScaleFactory scaleFactory = new ScaleFactory(rows, _xAxisDataScale.getColumnIx(), _xAxisDataScale.getColumnDisplayDefinition(), _callDepth);

      DataScaleListener dumDataScaleListener = new DataScaleListener()
      {
         @Override
         public void intervalSelected(Interval interval, JButton intervalButtonClicked)
         {
         }

         @Override
         public void showInTableWin(Interval interval)
         {
         }

         @Override
         public void showInTable(Interval interval)
         {
         }

         @Override
         public void showIntervalDetails(String intervalDetailsHtml, Point dialogLocation)
         {
         }
      };


      newScale = scaleFactory.createScale(dumDataScaleListener);


      DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

      String category = _xAxisDataScale.getColumnDisplayDefinition().getColumnName();

      for (Interval interval : newScale.getIntervals())
      {
         categoryDataset.addValue(calculateValue(interval, _mode, _yAxisDataScale), interval.getLabel(), category);
      }


      _title = "Chart for column: " + _xAxisDataScale.getColumnDisplayDefinition().getColumnName();

      _chart = ChartFactory.createBarChart(
            _title,   // chart title
            category,               // domain axis label
            _mode.getYAxisLabel(_yAxisDataScale),  // range axis label
            categoryDataset,                  // data
            PlotOrientation.VERTICAL,
            false,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
      );

      _label = createLabel(newScale, rows);
   }


   public String getTitle()
   {
      return _title;
   }

   public JFreeChart getChart()
   {
      return _chart;
   }

   public String getLabel()
   {
      return _label;
   }


   private double calculateValue(Interval interval, ChartConfigMode mode, DataScale yAxisDataScale)
   {
      switch(mode)
      {
         case COUNT:
            return interval.getLen();
         case SUM:

            double retSum = 0;
            for (int i = 0; i < interval.getLen(); i++)
            {
               Object o = interval.get(i);
               if (null != o)
               {
                  retSum += ((Number) o).doubleValue();
               }
            }
            return retSum;
         case XY_COUNT_DISTINCT:
            HashSet distinctSet = new HashSet();

            for (int i = 0; i < interval.getLen(); i++)
            {
               int dataSetRowIndex = interval.getDataSetRowIndex(i);
               Object obj = yAxisDataScale.getIndexedColumn().getRow(dataSetRowIndex);
               distinctSet.add(obj);
            }

            return distinctSet.size();
         case XY_SUM_COL:
            double retXYSumCol = 0;

            for (int i = 0; i < interval.getLen(); i++)
            {
               int dataSetRowIndex = interval.getDataSetRowIndex(i);
               Object obj = yAxisDataScale.getIndexedColumn().getRow(dataSetRowIndex);
               if (null != obj)
               {
                  retXYSumCol += ((Number) obj).doubleValue();
               }
            }

            return retXYSumCol;

         default:
            throw new IllegalStateException("Dont know how to handle mode: " + mode);



      }
   }

   private String createLabel(DataScale dataScale, ArrayList<Object[]> rows)
   {
      String ret = "Contains " + rows.size() + " query result values in " + dataScale.getIntervals().size() + " intervals";

      String intervalWidth = dataScale.getIntervalWidth();
      if(null != intervalWidth)
      {
         ret += " of width " + intervalWidth;
      }

      ret += ".\nLegend shows the first and the last query result value of intervals. Intervals that contain no values are omitted.";


      return ret;
   }

}
