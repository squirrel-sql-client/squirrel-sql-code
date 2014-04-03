package org.squirrelsql.services;

import javafx.stage.Stage;
import javafx.stage.Window;

public class StageDimensionSaver
{
   public static final String PREF_POST_FIX_MAIN_WIN_WIDTH = ".width";
   public static final String PREF_POST_FIX_MAIN_WIN_HEIGHT = ".height";
   public static final String PREF_POST_FIX_MAIN_WIN_X = ".x";
   public static final String PREF_POST_FIX_MAIN_WIN_Y = ".y";

   private String _prefPrefix;
   private Stage _stage;
   private Pref _pref;

   public StageDimensionSaver(final String prefPrefix, final Stage stage, final Pref pref, double initialWidth, double initialHeight, Window owner)
   {
      _prefPrefix = prefPrefix;
      _stage = stage;
      _pref = pref;
      stage.setWidth(pref.getDouble(prefPrefix + PREF_POST_FIX_MAIN_WIN_WIDTH, initialWidth));
      stage.setHeight(pref.getDouble(prefPrefix + PREF_POST_FIX_MAIN_WIN_HEIGHT, initialHeight));
      if (null == owner)
      {
         stage.setX(pref.getDouble(prefPrefix + PREF_POST_FIX_MAIN_WIN_X, 0));
         stage.setY(pref.getDouble(prefPrefix + PREF_POST_FIX_MAIN_WIN_Y, 0));
      }
      else
      {
         double x = ((owner.getWidth() - stage.getWidth()) / 2) + owner.getX();
         double y = ((owner.getHeight() - stage.getHeight()) / 2) + owner.getY();
         if (y < owner.getY())
         {
            y = owner.getY();
         }

         stage.setX(x);
         stage.setY(y);
      }

      stage.setOnHiding((windowEvent) -> save());
   }

   public void save()
   {
      _pref.set(_prefPrefix + PREF_POST_FIX_MAIN_WIN_WIDTH, _stage.getWidth());
      _pref.set(_prefPrefix + PREF_POST_FIX_MAIN_WIN_HEIGHT, _stage.getHeight());
      _pref.set(_prefPrefix + PREF_POST_FIX_MAIN_WIN_X, _stage.getX());
      _pref.set(_prefPrefix + PREF_POST_FIX_MAIN_WIN_Y, _stage.getY());
   }
}
