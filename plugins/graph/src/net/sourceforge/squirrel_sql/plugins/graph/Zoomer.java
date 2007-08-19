package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import java.util.Vector;


public class Zoomer
{
   private double _zoom = 1;
   private double _oldZoom = 1;
   Vector<ZoomerListener> _listeners = new Vector<ZoomerListener>();
   private boolean _enabled;
   private boolean _hideScrollBars;

   public Zoomer(ZoomerXmlBean xmlBean)
   {
      if(null != xmlBean)
      {
         _zoom = xmlBean.getZoom();
         _oldZoom = xmlBean.getOldZoom();
         _enabled = xmlBean.isEnabled();
         _hideScrollBars = xmlBean.isHideScrollbars();
      }
   }

   public void setEnabled(boolean b)
   {
      _enabled = b;

      if(_enabled)
      {
         setZoom(_oldZoom, false);
      }
      else
      {
         setZoom(1, false);
      }

      ZoomerListener[] listeners = _listeners.toArray(new ZoomerListener[_listeners.size()]);
      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].zoomEnabled(b);
      }
   }

   public void setZoom(double zoom, boolean adjusting)
   {
      _oldZoom = _zoom;
      _zoom = zoom;

      ZoomerListener[] listeners = _listeners.toArray(new ZoomerListener[_listeners.size()]);
      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].zoomChanged(_zoom, _oldZoom, adjusting);
      }
   }

   public double getZoom()
   {
      return _zoom;
   }

   public void addZoomListener(ZoomerListener l)
   {
      _listeners.remove(l);
      _listeners.add(l);
   }

   public void removeZoomListener(ZoomerListener l)
   {
      _listeners.remove(l);
   }

   public boolean isEnabled()
   {
      return _enabled;
   }

   public void setHideScrollBars(boolean b)
   {
      _hideScrollBars =b;
      ZoomerListener[] listeners = _listeners.toArray(new ZoomerListener[_listeners.size()]);
      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].setHideScrollBars(b);
      }
   }

   public ZoomerXmlBean getXmlBean()
   {
      ZoomerXmlBean xmlBean = new ZoomerXmlBean();
      xmlBean.setZoom(_zoom);
      xmlBean.setOldZoom(_oldZoom);
      xmlBean.setEnabled(_enabled);
      xmlBean.setHideScrollbars(_hideScrollBars);
      return xmlBean;
   }

   public boolean isHideScrollbars()
   {
      return _hideScrollBars;
   }
}
