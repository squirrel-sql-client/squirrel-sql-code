package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ZoomPrintController
{
   ZoomPrintPanel _panel = new ZoomPrintPanel();

   Zoomer _zoomer;
   private boolean _dontReactToSliderChanges = false;

   public ZoomPrintController(ZoomerXmlBean zoomerXmlBean)
   {
      _zoomer = new Zoomer(zoomerXmlBean);

      _panel.setVisible(false);

      _panel.sldZoom.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            onSliderChanged();
         }
      });

      _panel.chkHideScrollBars.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onHideScrollbars();
         }
      });

   }

   private void onHideScrollbars()
   {
      _zoomer.setHideScrollBars(_panel.chkHideScrollBars.isSelected());
   }

   private void onSliderChanged()
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
}
