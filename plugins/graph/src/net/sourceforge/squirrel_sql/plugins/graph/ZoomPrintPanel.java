package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;


public class ZoomPrintPanel extends JPanel
{
   static final int ZOOM_MIN_PERCENT = 10;
   static final int ZOOM_MAX_PERCENT = 400;

   static final int EDGES_MIN_PERCENT = 10;
   static final int EDGES_MAX_PERCENT = 150;

   JSlider sldZoom;
   JCheckBox chkHideScrollBars;
   JComboBox cboFormat;
   JButton btnFormat;
   JSlider sldEdges;
   JCheckBox chkShowEdges;
   JButton btnPrint;

   public ZoomPrintPanel()
   {
      setLayout(new GridLayout(1,2, 20, 0));
      add(createZoomPanel());
      add(createPrintPanel());
   }

   private JPanel createPrintPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());

      JPanel pnlLeft = new JPanel(new BorderLayout());

      chkShowEdges = new JCheckBox("Show/Zoom paper edges");
      pnlLeft.add(chkShowEdges, BorderLayout.WEST);

      JPanel pnlFormat = new JPanel(new BorderLayout());
      btnFormat = new JButton("Format ...");
      pnlFormat.add(btnFormat, BorderLayout.WEST);
      cboFormat = new JComboBox();
      pnlFormat.add(cboFormat, BorderLayout.CENTER);

      pnlLeft.add(pnlFormat, BorderLayout.CENTER);

      ret.add(pnlLeft, BorderLayout.WEST);


      sldEdges = new JSlider(JSlider.HORIZONTAL, EDGES_MIN_PERCENT, EDGES_MAX_PERCENT, 100);

      ret.add(sldEdges, BorderLayout.CENTER);

      // TODO use a label
      btnPrint = new JButton("Print");
      ret.add(btnPrint, BorderLayout.EAST);


      return ret;
   }

   private JPanel createZoomPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());
      ret.add(new JLabel("Zoom"), BorderLayout.WEST);

      sldZoom = new JSlider(JSlider.HORIZONTAL, ZOOM_MIN_PERCENT, ZOOM_MAX_PERCENT, 100);
      ret.add(sldZoom, BorderLayout.CENTER);

      chkHideScrollBars = new JCheckBox("Hide scrollbars");
      ret.add(chkHideScrollBars, BorderLayout.EAST);
      return ret;
   }

}
