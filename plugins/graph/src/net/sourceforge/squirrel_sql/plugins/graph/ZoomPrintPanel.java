package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;


public class ZoomPrintPanel extends JPanel
{
   static final int MIN_PERCENT = 20;
   static final int MAX_PERCENT = 400;
   JSlider sldZoom;
   JCheckBox chkHideScrollBars;

   public ZoomPrintPanel()
   {
      setLayout(new GridLayout(1,1));

      add(createZoomPanel());


   }

   private JPanel createZoomPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(new JLabel("Zoom"), BorderLayout.WEST);

      sldZoom = new JSlider(JSlider.HORIZONTAL, MIN_PERCENT, MAX_PERCENT, 100);
      ret.add(sldZoom, BorderLayout.CENTER);

      chkHideScrollBars = new JCheckBox("Hide scrollbars");
      ret.add(chkHideScrollBars, BorderLayout.EAST);
      return ret;
   }

}
