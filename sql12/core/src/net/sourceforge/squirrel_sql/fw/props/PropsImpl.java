package net.sourceforge.squirrel_sql.fw.props;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.Timer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

public class PropsImpl
{
   private static final String NULL_PROP = "__" + StringUtilities.NULL_AS_STRING + "__";

   private Timer _propsWriteTimer;

   private final SortedProperties _properties;

   private final static ILogger s_log = LoggerController.createLogger(PropsImpl.class);

   public PropsImpl()
   {
      try
      {
         _propsWriteTimer = new Timer(1000, e -> onWriteProps());
         _propsWriteTimer.setRepeats(false);


         _properties = new SortedProperties();
         File propsFile = new ApplicationFiles().getPropsFile();

         if (propsFile.exists())
         {
            _properties.load(new FileReader(propsFile));
         }

      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void onWriteProps()
   {
      try
      {
         _properties.store(new FileWriter(new ApplicationFiles().getPropsFile()), null);
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public void put(String propKey, int intValue)
   {
      putProperty(propKey, "" + intValue);
   }

   public int getInt(String propKey, int defaultIntValue)
   {
      return StringInterpreter.interpret(getProperty(propKey), Integer.class, defaultIntValue);
   }

   public boolean getBoolean(String propKey, boolean defaultBooleanValue)
   {
      return StringInterpreter.interpret(getProperty(propKey), Boolean.class, defaultBooleanValue);
   }

   public void put(String propKey, boolean booleanValue)
   {
      putProperty(propKey, "" + booleanValue);
   }

   public String getString(String propKey, String defaultString)
   {
      return getString(propKey, defaultString, false);
   }

   public String getString(String propKey, String defaultString, boolean allowWhiteSpacesOnly)
   {
      return StringInterpreter.interpret(getProperty(propKey), String.class, defaultString, allowWhiteSpacesOnly);
   }

   public void put(String propKey, String stringValue)
   {
      if(NULL_PROP.equals(stringValue))
      {
         String msg = "The property value \"" + NULL_PROP + "\" cannot be read. When it is read null will be returned instead.";
         s_log.error(msg, new IllegalArgumentException(msg));
      }

      if (null == stringValue)
      {
         putProperty(propKey, NULL_PROP);
      }
      else
      {
         putProperty(propKey, stringValue);
      }
   }


   private String getProperty(String propKey)
   {
      String ret;

      String newProp = _properties.getProperty(propKey);

      if(null != newProp)
      {
         ret = newProp;
      }
      else
      {
         //System.out.println("Reading old Preferences");

         String oldPref = Preferences.userRoot().get(propKey, NULL_PROP);
         putProperty(propKey, oldPref);

         if(false == NULL_PROP.equals(oldPref))
         {
            Preferences.userRoot().remove(propKey);
         }

         ret = oldPref;
      }

      if(NULL_PROP.equals(ret))
      {
         return null;
      }

      return ret;
   }

   private void putProperty(String propKey, String oldPref)
   {
      _properties.put(propKey, oldPref);

      _propsWriteTimer.restart();
   }

   public void saveProperties()
   {
      onWriteProps();
   }
}
