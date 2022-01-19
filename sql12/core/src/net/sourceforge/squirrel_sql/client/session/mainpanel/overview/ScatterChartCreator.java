package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.ArrayList;

/**
 * Very old Demo Sources:
 * https://github.com/ngadde/playground/tree/master/com.iis.sample1/src/main/java/demo
 */
public class ScatterChartCreator
{
   private final String _title;
   private final JFreeChart _chart;
   private final String _label;

   public ScatterChartCreator(DataScale xAxisDataScale, DataScale yAxisDataScale, DataScaleTable dataScaleTable)
   {
      _title = "Scatter chart for x = " + xAxisDataScale.getColumnDisplayDefinition().getColumnName()
               + ", y = " + yAxisDataScale.getColumnDisplayDefinition().getColumnName();

      ArrayList<Double> xValues = dataScaleTable.getDoubleValuesForColumn(xAxisDataScale.getColumnDisplayDefinition());
      ArrayList<Double> yValues = dataScaleTable.getDoubleValuesForColumn(yAxisDataScale.getColumnDisplayDefinition());

      ArrayList<XYPair> pairs = XYPair.createSortedPairs(xValues, yValues);

      DefaultXYDataset defaultXYDataset = new DefaultXYDataset();

      double[][] series = new double[2][pairs.size()];

      for (int i = 0; i < pairs.size(); i++)
      {
         series[0][i] = pairs.get(i).getX();
         series[1][i] = pairs.get(i).getY();
      }


      defaultXYDataset.addSeries(_title, series);


      //ScatterRenderer scatterRenderer = new ScatterRenderer();
      //FastScatterPlot fastScatterPlot = new FastScatterPlot();

      _chart = ChartFactory.createScatterPlot(_title, xAxisDataScale.getColumnDisplayDefinition().getColumnName(), yAxisDataScale.getColumnDisplayDefinition().getColumnName(), defaultXYDataset);


      StandardChartTheme.createJFreeTheme().apply(_chart);

      _label = "";

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
