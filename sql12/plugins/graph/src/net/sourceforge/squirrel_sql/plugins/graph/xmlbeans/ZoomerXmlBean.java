package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


public class ZoomerXmlBean
{
   private double zoom;
   private double oldZoom;
   private boolean enabled;
   private boolean hideScrollbars;

   public double getZoom()
   {
      return zoom;
   }

   public void setZoom(double zoom)
   {
      this.zoom = zoom;
   }

   public double getOldZoom()
   {
      return oldZoom;
   }

   public void setOldZoom(double oldZoom)
   {
      this.oldZoom = oldZoom;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public boolean isHideScrollbars()
   {
      return hideScrollbars;
   }

   public void setHideScrollbars(boolean hideScrollbars)
   {
      this.hideScrollbars = hideScrollbars;
   }
}
