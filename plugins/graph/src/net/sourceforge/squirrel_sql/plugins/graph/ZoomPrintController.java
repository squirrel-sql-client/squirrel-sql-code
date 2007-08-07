package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.graphtofiles.GraphToFilesCtrlr;
import net.sourceforge.squirrel_sql.plugins.graph.graphtofiles.SaveToFilePageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.ArrayList;


public class ZoomPrintController
{
   Zoomer _zoomer;
   private boolean _dontReactToSliderChanges = false;
   private ISession _session;
   private FormatController _formatController;
   private EdgesListener _edgesListener;
   private EdgesGraphComponent _edgesGraphComponent;
   private GraphPlugin _plugin;
   private GraphPrintable _printable;

   ZoomPrintPanel _panel = null;


   public ZoomPrintController(ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean, EdgesListener edgesListener, GraphPrintable printable, ISession session, GraphPlugin plugin)
   {
      _printable = printable;
      _plugin = plugin;

      _panel = new ZoomPrintPanel(new GraphPluginResources(_plugin));

      initZoom(session, zoomerXmlBean);
      initPrint(printXmlBean, edgesListener);
   }

   private void initPrint(PrintXmlBean printXmlBean, EdgesListener edgesListener)
   {
      _edgesListener = edgesListener;

      FormatControllerListener fcl = new FormatControllerListener()
      {
         public void formatsChanged(FormatXmlBean selectedFormat)
         {
            onFormatsChanged(selectedFormat);
         }
      };

      if(null != printXmlBean)
      {
         _formatController = new FormatController(_session, _plugin, fcl);
         _panel.sldEdges.setValue(printXmlBean.getEdgesScale());
         _panel.chkShowEdges.setSelected(printXmlBean.isShowEdges());
      }
      else
      {
         _formatController = new FormatController(_session, _plugin, fcl);
      }

      FormatXmlBean[] formats =_formatController.getFormats();

      FormatXmlBean toSelect = null;
      for (int i = 0; i < formats.length; i++)
      {
         _panel.cboFormat.addItem(formats[i]);
         if(formats[i].isSelected())
         {
            toSelect = formats[i];
         }
      }
      if(null != toSelect)
      {
         _panel.cboFormat.setSelectedItem(toSelect);
      }

      _panel.sldEdges.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            onSldEdgesChanged();
         }
      });


      _panel.chkShowEdges.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onShowEdges();
         }
      });

      onShowEdges();

      _panel.btnPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onPrint();
         }
      });

      _panel.btnSaveImages.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSaveImages();
         }
      });

      _panel.cboFormat.addItemListener(new ItemListener()
      {
         public void itemStateChanged(ItemEvent e)
         {
            onSelectedFormatChanged(e);
         }
      });

   }

   private void onShowEdges()
   {
      _panel.btnFormat.setEnabled(_panel.chkShowEdges.isSelected());
      _panel.cboFormat.setEnabled(_panel.chkShowEdges.isSelected());
      _panel.sldEdges.setEnabled(_panel.chkShowEdges.isSelected());
      _panel.btnPrint.setEnabled(_panel.chkShowEdges.isSelected());

      fireEdgesGraphComponentChanged(_panel.chkShowEdges.isSelected() && _panel.isVisible());
   }

   private void onSelectedFormatChanged(ItemEvent e)
   {
      if(ItemEvent.SELECTED == e.getStateChange())
      {
         fireEdgesGraphComponentChanged(_panel.chkShowEdges.isSelected());
      }
   }

   private void onFormatsChanged(FormatXmlBean selectedFormat)
   {
      FormatXmlBean[] formats = _formatController.getFormats();
      _panel.cboFormat.removeAllItems();
      for (int i = 0; i < formats.length; i++)
      {
         _panel.cboFormat.addItem(formats[i]);
         if(formats[i] == selectedFormat)
         {
            formats[i].setSelected(true);
            _panel.cboFormat.setSelectedItem(formats[i]);
         }
         else
         {
            formats[i].setSelected(false);
         }
      }
   }

   private void onPrint()
   {
      PrinterJob printJob = PrinterJob.getPrinterJob();

      PageFormat pf = initPrint(false);

      printJob.setPrintable(_printable, pf);
      if (printJob.printDialog())
      {
         try
         {
            printJob.print();
         }
         catch (Exception ex)
         {
            throw new RuntimeException(ex);
         }
      }
   }

   private void onSaveImages()
   {
      try
      {

         ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();


         if (_panel.chkShowEdges.isSelected())
         {

            PageFormat pf = initPrint(true);

            FormatXmlBean format = (FormatXmlBean) _panel.cboFormat.getSelectedItem();

            PixelCalculater pc = new PixelCalculater(format);

            for (int pageIndex = 0; ; ++pageIndex)
            {
               int pxWidth = pc.getPixelWidth();
               int pxHeight = pc.getPixelHeight();
               BufferedImage img = prepareImage(pxWidth, pxHeight);
               int pageState = _printable.print(img.getGraphics(), pf, pageIndex);

               if (Printable.NO_SUCH_PAGE == pageState)
               {
                  break;
               }

               images.add(img);
            }
         }
         else
         {
            // No paper edges. We print the Graph as it is to a single image.

            Dimension graphPixelSize = _printable.initPrintNoScaleSinglePage();
            SaveToFilePageFormat pf = new SaveToFilePageFormat(graphPixelSize);

            BufferedImage img = prepareImage(graphPixelSize.width, graphPixelSize.height);
            _printable.print(img.getGraphics(), pf, 0);

            images.add(img);
         }

         new GraphToFilesCtrlr(images.toArray(new BufferedImage[images.size()]),
                                              _session.getApplication().getMainFrame());

      }
      catch (PrinterException e)
      {
         throw new RuntimeException(e);
      }
   }

   private BufferedImage prepareImage(int pxWidth, int pxHeight)
   {
      BufferedImage img = new BufferedImage(pxWidth, pxHeight, BufferedImage.TYPE_INT_RGB);
      img.getGraphics().setColor(Color.white);
      img.getGraphics().fillRect(0, 0, pxWidth, pxHeight);
      img.getGraphics().setColor(Color.black);
      return img;
   }

   private PageFormat initPrint(boolean isSaveToFile)
   {
      FormatXmlBean format = (FormatXmlBean)_panel.cboFormat.getSelectedItem();
      _printable.initPrint(format.getWidth(), format.getHeight(), _panel.sldEdges.getValue() / 100.0);

      PageFormat pf = isSaveToFile ? new SaveToFilePageFormat(format) : new PageFormat();

      if(format.isLandscape())
      {
         pf.setOrientation(PageFormat.LANDSCAPE);
      }
      else
      {
         pf.setOrientation(PageFormat.PORTRAIT);
      }


      return pf;
   }


   private void fireEdgesGraphComponentChanged(boolean showEdges)
   {
      if(null == _edgesGraphComponent)
      {
         _edgesGraphComponent = new EdgesGraphComponent();
      }

      if(showEdges)
      {
         FormatXmlBean format = (FormatXmlBean) _panel.cboFormat.getSelectedItem();
         _edgesGraphComponent.init(format, _panel.sldEdges.getValue() / 100.0, _panel.sldEdges.getValueIsAdjusting());
         _edgesListener.edgesGraphComponentChanged(_edgesGraphComponent, true);

      }
      else
      {
         _edgesListener.edgesGraphComponentChanged(_edgesGraphComponent, false);
      }

   }

   private void onSldEdgesChanged()
   {
      fireEdgesGraphComponentChanged(_panel.chkShowEdges.isSelected());
   }

   private void initZoom(ISession session, ZoomerXmlBean zoomerXmlBean)
   {
      _session = session;
      _zoomer = new Zoomer(zoomerXmlBean);

      _panel.setVisible(false);

      _panel.sldZoom.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            onSldZoomChanged();
         }
      });

      _panel.chkHideScrollBars.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onHideScrollbars();
         }
      });

      _panel.btnFormat.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onBtnFormat();
         }
      });
   }

   private void onBtnFormat()
   {
      _formatController.setVisible(true);
   }

   private void onHideScrollbars()
   {
      _zoomer.setHideScrollBars(_panel.chkHideScrollBars.isSelected());
   }

   private void onSldZoomChanged()
   {
      if(_dontReactToSliderChanges)
      {
         return;
      }
      _zoomer.setZoom(_panel.sldZoom.getValue() / 100.0, _panel.sldZoom.getValueIsAdjusting());
   }

   public ZoomPrintPanel getPanel()
   {
      return _panel;
   }

   public void setVisible(boolean b)
   {
      _panel.setVisible(b);
      _zoomer.setEnabled(b);
      fireEdgesGraphComponentChanged(b);

      onShowEdges();

      try
      {
         _dontReactToSliderChanges = true;
         _panel.sldZoom.setValue((int)(_zoomer.getZoom() * 100 + 0.5));
      }
      finally
      {
         _dontReactToSliderChanges = false;
      }
   }

   public Zoomer getZoomer()
   {
      return _zoomer;
   }

   public PrintXmlBean getPrintXmlBean()
   {
      PrintXmlBean ret = new PrintXmlBean();
      ret.setShowEdges(_panel.chkShowEdges.isSelected());
      ret.setEdgesScale(_panel.sldEdges.getValue());

      return ret;
   }

   public void sessionEnding()
   {
      _formatController.close();
   }
}
