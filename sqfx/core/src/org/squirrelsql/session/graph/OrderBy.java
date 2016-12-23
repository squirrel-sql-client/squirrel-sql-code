package org.squirrelsql.session.graph;

import javafx.scene.image.ImageView;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum OrderBy
{
   NONE("sort.png",  "order.none"),
   ASC("sort_asc.gif", "order.asc"),
   DESC("sort_desc.gif", "order.desc");

   private final String _imageName;
   private final String _title;

   OrderBy(String imageName, String title)
   {
      _imageName = imageName;
      _title = new I18n(AggregateFunction.class).t(title);
   }

   public ImageView createImage()
   {
      return new ImageView(new Props(OrderBy.class).getImage(_imageName));
   }

   public String getTitle()
   {
      return _title;
   }


}
