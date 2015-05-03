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

      String globalName = "/org/squirrelsql/globalicons/" + nameInPackage;

      if(null == resource)
      {
         resource = _clazz.getResource(globalName);
      }

      if(null == resource)
      {
         String localName = _clazz.getPackage().getName().replaceAll("\\.", "/") + "/" + nameInPackage;
         throw new IllegalArgumentException("Could find neither " + localName + " nor " + globalName);
      }

      return new Image(resource.toString());
   }

   public ImageView getImageView(String nameInPackage)
   {
      return new ImageView(getImage(nameInPackage));
   }
   public ImageView getImageView(Image imageName){
	   return new ImageView(imageName);
   }   
}
