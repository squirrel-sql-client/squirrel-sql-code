package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;


public class ZoomPrintPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ZoomPrintPanel.class);

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
   JButton btnSaveImages;

   public ZoomPrintPanel(GraphPluginResources rsrc)
   {
      setLayout(new GridLayout(1,2, 20, 0));
      add(createZoomPanel());
      add(createPrintPanel(rsrc));
   }

   private JPanel createPrintPanel(GraphPluginResources rsrc)
   {
      JPanel ret = new JPanel(new BorderLayout());

      JPanel pnlLeft = new JPanel(new BorderLayout());

		// i18n[graph.showZoomPaper=Show/Zoom paper edges]
		chkShowEdges = new JCheckBox(s_stringMgr.getString("graph.showZoomPaper"));
      pnlLeft.add(chkShowEdges, BorderLayout.WEST);

      JPanel pnlFormat = new JPanel(new BorderLayout());

		// i18n[graph.format=Format ...]
		btnFormat = new JButton(s_stringMgr.getString("graph.format"));
      pnlFormat.add(btnFormat, BorderLayout.WEST);
      cboFormat = new JComboBox();
      pnlFormat.add(cboFormat, BorderLayout.CENTER);

      pnlLeft.add(pnlFormat, BorderLayout.CENTER);

      ret.add(pnlLeft, BorderLayout.WEST);


      sldEdges = new JSlider(JSlider.HORIZONTAL, EDGES_MIN_PERCENT, EDGES_MAX_PERCENT, 100);

      ret.add(sldEdges, BorderLayout.CENTER);




      btnPrint = new JButton(rsrc.getIcon(GraphPluginResources.IKeys.PRINT_IMAGE));
      Dimension printBtnPrefSize = btnPrint.getPreferredSize();
      //printBtnPrefSize.width -=10;
      btnPrint.setPreferredSize(printBtnPrefSize);
      // i18n[graph.printGraph=Print graph]
      btnPrint.setToolTipText(s_stringMgr.getString("graph.printGraph"));

      btnSaveImages = new JButton(rsrc.getIcon(GraphPluginResources.IKeys.SAVE_IMAGES_TO_FILE));
      Dimension btnCopyToClipPrefSize = btnPrint.getPreferredSize();
      //btnCopyToClipPrefSize.width -=10;
      btnSaveImages.setPreferredSize(btnCopyToClipPrefSize);
      // i18n[graph.saveGraphImagesToFile=Copy graph to clipboard]
      btnSaveImages.setToolTipText(s_stringMgr.getString("graph.saveGraphImagesToFile"));

      JPanel pnlButtons = new JPanel(new GridLayout(1,2));
      pnlButtons.add(btnSaveImages);
      pnlButtons.add(btnPrint);
      ret.add(pnlButtons, BorderLayout.EAST);


      return ret;
   }

   private JPanel createZoomPanel()
   {
      JPanel ret = new JPanel(new BorderLayout());
		// i18n[graph.zoom=Zoom]
		ret.add(new JLabel(s_stringMgr.getString("graph.zoom")), BorderLayout.WEST);

      sldZoom = new JSlider(JSlider.HORIZONTAL, ZOOM_MIN_PERCENT, ZOOM_MAX_PERCENT, 100);
      ret.add(sldZoom, BorderLayout.CENTER);

		// i18n[graph.hideScrollBars=Hide scrollbars]
		chkHideScrollBars = new JCheckBox(s_stringMgr.getString("graph.hideScrollBars"));
      ret.add(chkHideScrollBars, BorderLayout.EAST);
      return ret;
   }

}
