package net.sourceforge.squirrel_sql.plugins.laf.flatlaf;

import javax.swing.LookAndFeel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class FlatLafProxy
{
   public static final String FLAT_DARK_THEME_NAME = "Flat Dark";
   private Map<String, Class<? extends LookAndFeel>> standardThemes;
   private Constructor<?> themeCtor;
   private Field themeName;
   private Method createLaf;
   private Constructor<?> propsThemeCtor;

   FlatLafProxy(ClassLoader lafClassLoader)
   {
      try
      {
         init(lafClassLoader);
      }
      catch (ReflectiveOperationException e)
      {
         throw new IllegalStateException(e);
      }
   }

   private void init(ClassLoader lafClassLoader) throws ReflectiveOperationException
   {
      Map<String, Class<? extends LookAndFeel>> themes = new LinkedHashMap<>();
      themes.put("Flat Light", lafClassLoader.loadClass("com.formdev.flatlaf.FlatLightLaf").asSubclass(LookAndFeel.class));
      themes.put(FLAT_DARK_THEME_NAME, lafClassLoader.loadClass("com.formdev.flatlaf.FlatDarkLaf").asSubclass(LookAndFeel.class));
      themes.put("Flat IntelliJ", lafClassLoader.loadClass("com.formdev.flatlaf.FlatIntelliJLaf").asSubclass(LookAndFeel.class));
      themes.put("Flat Darcula", lafClassLoader.loadClass("com.formdev.flatlaf.FlatDarculaLaf").asSubclass(LookAndFeel.class));
      standardThemes = Collections.unmodifiableMap(themes);

      Class<?> themeClass = lafClassLoader.loadClass("com.formdev.flatlaf.IntelliJTheme");
      themeCtor = themeClass.getConstructor(InputStream.class);
      themeName = themeClass.getField("name");
      createLaf = themeClass.getMethod("createLaf", themeClass);

      themeClass = lafClassLoader.loadClass("com.formdev.flatlaf.FlatPropertiesLaf");
      propsThemeCtor = themeClass.getConstructor(String.class, Properties.class);
   }

   Map<String, Class<? extends LookAndFeel>> getStandardThemes()
   {
      return standardThemes;
   }

   Object createIntelliJTheme(InputStream in) throws IOException
   {
      try
      {
         return themeCtor.newInstance(in);
      }
      catch (InvocationTargetException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof IOException)
         {
            throw (IOException) cause;
         }
         throw uncheckedFromCause(e);
      }
      catch (ReflectiveOperationException e)
      {
         throw new IllegalStateException(e);
      }
   }

   String getIntelliJThemeName(Object theme)
   {
      try
      {
         return (String) themeName.get(theme);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException(e);
      }
   }

   LookAndFeel createLaf(Object theme)
   {
      try
      {
         return (LookAndFeel) createLaf.invoke(null, theme);
      }
      catch (InvocationTargetException e)
      {
         throw uncheckedFromCause(e);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException(e);
      }
   }

   LookAndFeel createPropsLaf(String name, Properties props)
   {
      try
      {
         return (LookAndFeel) propsThemeCtor.newInstance(name, props);
      }
      catch (InvocationTargetException e)
      {
         throw uncheckedFromCause(e);
      }
      catch (ReflectiveOperationException e)
      {
         throw new IllegalStateException(e);
      }
   }

   private RuntimeException uncheckedFromCause(Exception e)
   {
      Throwable cause = e.getCause();
      if (cause instanceof Error)
      {
         throw (Error) cause;
      }
      else if (cause instanceof RuntimeException)
      {
         return (RuntimeException) cause;
      }
      return new IllegalStateException(cause == null ? e : cause);
   }

}
