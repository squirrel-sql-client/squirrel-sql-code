package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScale;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Font;
import java.awt.Frame;

public class ChartHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChartHandler.class);


   public static final int MAX_LEGEND_ENTRIES = 10;

   public static void doChart(DataScale xAxisDataScale,
                              DataScale yAxisDataScale,
                              DataScaleTable dataScaleTable,
                              Integer callDepth,
                              ChartConfigPanelTabMode chartConfigPanelTabMode,
                              ChartConfigMode mode,
                              Frame parent,
                              TimeScale timeScale)
   {
      try
      {

         String title;

         JFreeChart chart;
         String label;


         if (   chartConfigPanelTabMode == ChartConfigPanelTabMode.XY_CHART
             || chartConfigPanelTabMode == ChartConfigPanelTabMode.DIFFERENCES_CHART)
         {
            XYChartCreator xyChartCreator = new XYChartCreator(xAxisDataScale, yAxisDataScale, dataScaleTable, chartConfigPanelTabMode == ChartConfigPanelTabMode.DIFFERENCES_CHART, timeScale);
            title = xyChartCreator.getTitle();
            chart = xyChartCreator.getChart();
            label = xyChartCreator.getLabel();
         }
         else if (chartConfigPanelTabMode == ChartConfigPanelTabMode.SCATTER_CHART)
         {
            ScatterChartCreator scatterChartCreator = new ScatterChartCreator(xAxisDataScale, yAxisDataScale, dataScaleTable);
            title = scatterChartCreator.getTitle();
            chart = scatterChartCreator.getChart();
            label = scatterChartCreator.getLabel();
         }
         else
         {
            BarChartCreator barChartCreator = new BarChartCreator(xAxisDataScale, yAxisDataScale, callDepth, mode);
            title = barChartCreator.getTitle();
            chart = barChartCreator.getChart();
            label = barChartCreator.getLabel();

         }

         JFrame f = new JFrame(title);

         final ImageIcon icon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
         if (icon != null)
         {
            f.setIconImage(icon.getImage());
         }


         LegendTitle legendtitle = new LegendTitle(createLegendItemSource(chart));
         BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
         //blockcontainer.setBorder(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
         LabelBlock labelblock = new LabelBlock(label, new Font("SansSerif", 1, 12));
         //labelblock.setTextAnchor(RectangleAnchor.TOP_LEFT);
         labelblock.setPadding(5D, 5D, 5D, 5D);
         blockcontainer.add(labelblock, RectangleEdge.TOP);
         //LabelBlock labelblock1 = new LabelBlock(createLabel(newScale, rows));
         //labelblock1.setPadding(8D, 20D, 2D, 5D);
         //blockcontainer.add(labelblock1, RectangleEdge.BOTTOM);
         BlockContainer blockcontainer1 = legendtitle.getItemContainer();
         blockcontainer1.setPadding(2D, 10D, 5D, 2D);
         blockcontainer.add(blockcontainer1);
         legendtitle.setWrapper(blockcontainer);
         legendtitle.setPosition(RectangleEdge.BOTTOM);
         legendtitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
         chart.addSubtitle(legendtitle);


         ChartPanel chartPanel = new ChartPanel(chart);
         f.getContentPane().add(chartPanel);


         f.setLocation(parent.getLocationOnScreen());
         f.setSize(parent.getSize());

         f.setVisible(true);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }


   private static LegendItemSource createLegendItemSource(final JFreeChart chart)
   {
      return new LegendItemSource()
      {
         @Override
         public LegendItemCollection getLegendItems()
         {
            return reduceLegendItems(chart);
         }
      };
   }

   private static LegendItemCollection reduceLegendItems(JFreeChart chart)
   {
      LegendItemCollection legendItems = chart.getPlot().getLegendItems();

      LegendItemCollection ret = new LegendItemCollection();

      for (int i = 0; i < legendItems.getItemCount(); i++)
      {
         if(MAX_LEGEND_ENTRIES < i && MAX_LEGEND_ENTRIES < legendItems.getItemCount())
         {
            ret.add(new LegendItem("..."));
            ret.add(legendItems.get(legendItems.getItemCount() - 1));
            break;
         }

         ret.add(legendItems.get(i));
      }

      return ret;
   }

}
