package org.squirrelsql;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;


public class Props
{
   private Class _clazz;

   public Props(Class clazz)
   {
      _clazz = clazz;
   }

   public Image getImage(String nameInPackage)
   {
      URL resource = _clazz.getResource(nameInPackage);

      if(null == resource)
      {
         resource = _clazz.getResource("/org/squirrelsql/globalicons/" + nameInPackage);
      }
      return new Image(resource.toString());
   }

   public ImageView getImageView(String nameInPackage)
   {
      return new ImageView(getImage(nameInPackage));
   }
}
