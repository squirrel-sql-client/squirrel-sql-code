package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum AggregateFunction
{
   NONE("aggfct.png", "agg.function.none"),
   SUM("aggsum.png", "agg.function.sum"),
   MAX("aggmax.png", "agg.function.max"),
   MIN("aggmin.png", "agg.function.min"),
   COUNT("aggcount.png", "agg.function.count");


   private final String _imageName;
   private String _title;


   AggregateFunction(String imageName, String title)
   {
      _imageName = imageName;
      _title = new I18n(AggregateFunction.class).t(title);
   }

   /**
    * Images must be created. If not the same image would
    * tried to be rendered at different positions which leads repaint troubles.
    */
   public ImageView createImage()
   {
      return new ImageView(new Props(AggregateFunction.class).getImage(_imageName));
   }

   public String getTitle()
   {
      return _title;
   }

   /**
    * Images must be created. If not the same image would
    * tried to be rendered at different positions which leads repaint troubles.
    */
   public static ImageView createDisabledImage()
   {
      return new ImageView(new Props(AggregateFunction.class).getImage("aggfct_disabled.png"));
   }
}
