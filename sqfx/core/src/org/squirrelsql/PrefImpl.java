package org.squirrelsql;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.squirrelsql.services.Dao;
import org.squirrelsql.table.tableedit.StringInterpreter;

import java.util.Properties;

public class PrefImpl
{
   private Timeline _timeline;

   private Properties _preferences;

   public PrefImpl()
   {
   }

   private void checkInit()
   {
      if (null == _preferences)
      {
         _preferences = Dao.loadPreferences();

         _timeline =  new Timeline(new KeyFrame(Duration.millis(500), ae -> doWrite()));

         AppState.get().addSaveSettingsListener(() -> doWrite());
      }
   }


   private void doWrite()
   {
      checkInit();
      _timeline.stop();
      Dao.writePreferences(_preferences);
   }

   public Double getDouble(String key, double def)
   {
      checkInit();
      return (Double) StringInterpreter.interpret(_preferences.getProperty(key, "" + def), Double.class);
   }

   public void set(String key, double val)
   {
      checkInit();
      _preferences.setProperty(key, "" + val);
      triggerWrite();
   }

   public void set(String key, int val)
   {
      checkInit();
      _preferences.setProperty(key, "" + val);
      triggerWrite();
   }

   public String getString(String key, String def)
   {
      checkInit();
      return _preferences.getProperty(key, def);
   }

   public void set(String key, String val)
   {
      checkInit();
      _preferences.setProperty(key, val);
      triggerWrite();
   }

   public boolean getBoolean(String key, boolean def)
   {
      checkInit();
      return (Boolean) StringInterpreter.interpret(_preferences.getProperty(key, "" + def), Boolean.class);
   }

   public void set(String key, boolean val)
   {
      checkInit();
      _preferences.setProperty(key, "" + val);
      triggerWrite();
   }

   public int getInt(String key, int def)
   {
      checkInit();
      return (Integer) StringInterpreter.interpret(_preferences.getProperty(key, "" + def), Integer.class);
   }


   private void triggerWrite()
   {
      _timeline.play();
   }

   public void flush()
   {
      doWrite();
   }
}
