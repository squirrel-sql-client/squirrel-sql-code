package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.IndexedColumnFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

class XYChartCreator
{
   private String _title;
   private JFreeChart _chart;
   private String _label;

   public XYChartCreator(DataScale xAxisDataScale, DataScale yAxisDataScale, DataScaleTable dataScaleTable, boolean isDifferencesChart, TimeScale timeScale)
   {
      if (isDifferencesChart)
      {
         _title = "Difference chart for x = " + xAxisDataScale.getColumnDisplayDefinition().getColumnName() + ", y = \u0394 (" + yAxisDataScale.getColumnDisplayDefinition().getColumnName() + ")";
      }
      else
      {
         _title = "Chart for x = " + xAxisDataScale.getColumnDisplayDefinition().getColumnName() + ", y = " + yAxisDataScale.getColumnDisplayDefinition().getColumnName();
      }

      DefaultXYDataset defaultXYDataset = new DefaultXYDataset();

      ArrayList<Double> xValues = dataScaleTable.getDoubleValuesForColumn(xAxisDataScale.getColumnDisplayDefinition());
      ArrayList<Double> yValues = dataScaleTable.getDoubleValuesForColumn(yAxisDataScale.getColumnDisplayDefinition());

      ArrayList<XYPair> pairs = XYPair.createSortedPairs(xValues, yValues);

      if(isDifferencesChart)
      {
         convertYValuesToDifferences(pairs, timeScale);
      }

      double[][] series = new double[2][pairs.size()];

      for (int i = 0; i < pairs.size(); i++)
      {
         series[0][i] = pairs.get(i).getX();
         series[1][i] = pairs.get(i).getY();
      }


      defaultXYDataset.addSeries(_title, series);


      createChart(_title, defaultXYDataset, xAxisDataScale.getColumnDisplayDefinition(), yAxisDataScale.getColumnDisplayDefinition(), isDifferencesChart);

      _label = "";
   }

   private void createChart(String title, DefaultXYDataset defaultXYDataset, ColumnDisplayDefinition xColumnDisplayDefinition, ColumnDisplayDefinition yColumnDisplayDefinition, boolean isDifferencesChart)
   {
//      _chart =  ChartFactory.createXYLineChart(
//            title,   // chart title
//            xColumnDisplayDefinition.getColumnName(),  // domain axis label
//            yColumnDisplayDefinition.getColumnName(),  // range axis label
//            defaultXYDataset,                  // data
//            PlotOrientation.VERTICAL,
//            false,                     // include legend
//            true,                     // tooltips?
//            false                     // URLs?
//      );

      ValueAxis xAxis;
      if(IndexedColumnFactory.isTemporal(xColumnDisplayDefinition))
      {
         xAxis = new DateAxis(xColumnDisplayDefinition.getColumnName());
      }
      else
      {
         xAxis = new NumberAxis(xColumnDisplayDefinition.getColumnName());
         ((NumberAxis)xAxis).setAutoRangeIncludesZero(false);
      }

      ValueAxis yAxis;
      if(false == isDifferencesChart && IndexedColumnFactory.isTemporal(yColumnDisplayDefinition))
      {
         yAxis = new DateAxis(yColumnDisplayDefinition.getColumnName());
      }
      else
      {
         if (isDifferencesChart)
         {
            yAxis = new NumberAxis("\u0394 (" + yColumnDisplayDefinition.getColumnName() + ")");
         }
         else
         {
            yAxis = new NumberAxis(yColumnDisplayDefinition.getColumnName());
         }
         ((NumberAxis)yAxis).setAutoRangeIncludesZero(false);
      }


      XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
      XYPlot plot = new XYPlot(defaultXYDataset, xAxis, yAxis, renderer);
      plot.setOrientation(PlotOrientation.VERTICAL);

      StandardXYToolTipGenerator toolTipGenerator = createToolTipGenerator(xColumnDisplayDefinition, yColumnDisplayDefinition, isDifferencesChart);
      renderer.setDefaultToolTipGenerator(toolTipGenerator);

      _chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
      StandardChartTheme.createJFreeTheme().apply(_chart);
   }

   private StandardXYToolTipGenerator createToolTipGenerator(ColumnDisplayDefinition xColumnDisplayDefinition, ColumnDisplayDefinition yColumnDisplayDefinition, boolean isDifferencesChart)
   {
      StandardXYToolTipGenerator toolTipGenerator;

      boolean isXTemporal = IndexedColumnFactory.isTemporal(xColumnDisplayDefinition);

      boolean isYTemporal = IndexedColumnFactory.isTemporal(yColumnDisplayDefinition) && false == isDifferencesChart;

      if (isXTemporal && isYTemporal)
      {
         toolTipGenerator = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getInstance(), DateFormat.getInstance());
      }
      else if (false == isXTemporal && isYTemporal)
      {
         toolTipGenerator = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(), DateFormat.getInstance());
      }
      else if (isXTemporal && false == isYTemporal)
      {
         toolTipGenerator = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getInstance(), NumberFormat.getNumberInstance());
      }
      else
      {
         toolTipGenerator = new StandardXYToolTipGenerator();
      }
      return toolTipGenerator;
   }

   private void convertYValuesToDifferences(ArrayList<XYPair> pairsSortedByXValues, TimeScale timeScale)
   {
      if(pairsSortedByXValues.size() < 2)
      {
         return;
      }


      double formerYValue = pairsSortedByXValues.get(0).getY();

      for (int i = 1; i < pairsSortedByXValues.size(); i++)
      {
         double buf = pairsSortedByXValues.get(i).getY();

         double diff = buf - formerYValue;

         if (null != timeScale)
         {
            diff = timeScale.scale(diff);
         }

         pairsSortedByXValues.get(i - 1).setY(diff);

         formerYValue = buf;
      }

      int lastIx = pairsSortedByXValues.size() - 1;
      pairsSortedByXValues.get(lastIx).setY(pairsSortedByXValues.get(lastIx-1).getY());

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

}
