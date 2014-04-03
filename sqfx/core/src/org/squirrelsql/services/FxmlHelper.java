package org.squirrelsql.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.ResourceBundle;

public class FxmlHelper<T>
{

   private final FXMLLoader _fxmlLoader;
   private final Region _region;

   public FxmlHelper(Class<T> fxmlViewClass)
   {

      try
      {
         _fxmlLoader = new FXMLLoader(getClass().getResource("/" + fxmlViewClass.getName().replaceAll("\\.", "/") + ".fxml"), ResourceBundle.getBundle(fxmlViewClass.getPackage().getName() + ".i18n"));
         _region = (Region) _fxmlLoader.load();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public Region getRegion()
   {
      return _region;
   }

   public T getView()
   {
      return _fxmlLoader.getController();
   }
}
