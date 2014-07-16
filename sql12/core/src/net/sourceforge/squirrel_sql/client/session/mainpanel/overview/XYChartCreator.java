package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.ArrayList;

class XYChartCreator
{
   private DataScale _xAxisDataScale;
   private DataScale _yAxisDataScale;
   private DataScaleTable _dataScaleTable;
   private String _title;
   private JFreeChart _chart;
   private String _label;

   public XYChartCreator(DataScale xAxisDataScale, DataScale yAxisDataScale, DataScaleTable dataScaleTable)
   {
      _xAxisDataScale = xAxisDataScale;
      _yAxisDataScale = yAxisDataScale;
      _dataScaleTable = dataScaleTable;

      _title = "Chart for x = " + _xAxisDataScale.getColumnDisplayDefinition().getColumnName() + ", y = " + _yAxisDataScale.getColumnDisplayDefinition().getColumnName();

      String category = _xAxisDataScale.getColumnDisplayDefinition().getColumnName();

      DefaultXYDataset defaultXYDataset = new DefaultXYDataset();


      ArrayList<Double> xValues = _dataScaleTable.getDoubleValuesForColumn(_xAxisDataScale.getColumnDisplayDefinition());
      ArrayList<Double> yValues = _dataScaleTable.getDoubleValuesForColumn(_yAxisDataScale.getColumnDisplayDefinition());

      ArrayList<XYPair> pairs = XYPair.createSortedPairs(xValues, yValues);

      double[][] series = new double[2][pairs.size()];

      for (int i = 0; i < pairs.size(); i++)
      {
         series[0][i] = pairs.get(i).getX();
         series[1][i] = pairs.get(i).getY();
      }


      defaultXYDataset.addSeries(_title, series);


      _chart =  ChartFactory.createXYLineChart(
            _title,   // chart title
            category,               // domain axis label
            _yAxisDataScale.getColumnDisplayDefinition().getColumnName(),  // range axis label
            defaultXYDataset,                  // data
            PlotOrientation.VERTICAL,
            false,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
      );

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
