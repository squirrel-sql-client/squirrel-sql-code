package org.squirrelsql.services;


import javafx.scene.image.ImageView;

public class Option
{
   private String _text;
   private ImageView _image;

   public Option(String text)
   {
      this(text, null);
   }
   public Option(String text, ImageView image)
   {
      _text = text;
      _image = image;
   }

   public static Option[] createStringOnly(String... options)
   {
      Option[] ret = new Option[options.length];

      for (int i = 0; i < options.length; i++)
      {
         ret[i] =new Option(options[i]);
      }

      return ret;
   }

   public String getText()
   {
      return _text;
   }

   public ImageView getImage()
   {
      return _image;
   }
}
