package org.squirrelsql;


import javafx.scene.image.Image;

public class Props
{
   private Class _clazz;

   public Props(Class clazz)
   {
      _clazz = clazz;
   }

   public Image getImage(String nameInPackage)
   {
      return new Image(_clazz.getResource(nameInPackage).toString());
   }
}
