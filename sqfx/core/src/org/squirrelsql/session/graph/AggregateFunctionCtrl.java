package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

public class AggregateFunctionCtrl extends BorderPane
{
   private final ImageView _imageEnabled = new ImageView(new Props(getClass()).getImage("aggfct.png"));
   private final ImageView _imageDisabled = new ImageView(new Props(getClass()).getImage("aggfct_disabled.png"));
   private boolean _enabled;

   public AggregateFunctionCtrl(boolean enabled)
   {
      _enabled = enabled;
      updateGraphics();
   }

   private void updateGraphics()
   {
      if(_enabled)
      {
         setCenter(_imageEnabled);
      }
      else
      {
         setCenter(_imageDisabled);
      }
   }


   public void setEnabled(boolean b)
   {
      _enabled = b;
      updateGraphics();
   }
}
