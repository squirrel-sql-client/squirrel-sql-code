package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryAdapter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChartConfigPanel extends JScrollPane
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChartConfigPanel.class);


   public ChartConfigPanel(ArrayList<ChartConfigPanelTab> chartConfigPanelTabs, ChartConfigListener chartConfigListener)
   {

      JTabbedPane tabbedPane = UIFactory.getInstance().createTabbedPane();

      for (ChartConfigPanelTab chartConfigPanelTab : chartConfigPanelTabs)
      {
         tabbedPane.add(chartConfigPanelTab.getTabTitle(), createTabPanel(chartConfigPanelTab, chartConfigListener));
      }

      setViewportView(tabbedPane);
   }

   private JPanel createTabPanel(ChartConfigPanelTab chartConfigPanelTab, ChartConfigListener chartConfigListener)
   {
      JPanel ret = new JPanel(new BorderLayout());

      ret.add(createCloseButtonPanel(chartConfigListener), BorderLayout.NORTH);
      ret.add(chartConfigPanelTab, BorderLayout.CENTER);


      return ret;
   }

   private JPanel createCloseButtonPanel(final ChartConfigListener chartConfigListener)
   {
      JPanel ret = new JPanel(new BorderLayout());

      JButton btnClose = new JButton(s_stringMgr.getString("overview.ChartConfigPanel.close"));
      ret.add(btnClose, BorderLayout.WEST);
      ret.add(new JPanel(), BorderLayout.CENTER);

      btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            chartConfigListener.closeSplit();
         }
      });

      return ret;
   }

}
