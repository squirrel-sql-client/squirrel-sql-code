package net.sourceforge.squirrel_sql.plugins.graph;

public interface ZoomerListener
{
   void zoomChanged(double newZoom, double oldZoom, boolean adjusting);
   void zoomEnabled(boolean b);
   void setHideScrollBars(boolean b);
}
