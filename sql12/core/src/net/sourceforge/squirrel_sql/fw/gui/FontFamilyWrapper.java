package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;

public class FontFamilyWrapper
{
   private Boolean _monospaced = null;
   private String _fontFamilyName;


   public FontFamilyWrapper(String fontFamilyName)
   {

      _fontFamilyName = fontFamilyName;
   }

   public FontFamilyWrapper(String fontFamilyName, boolean monospaced)
   {

      _fontFamilyName = fontFamilyName;
      _monospaced = monospaced;
   }

   public String getFontFamilyName()
   {
      return _fontFamilyName;
   }

   @Override
   public String toString()
   {
      if (null == _monospaced)
      {
         return _fontFamilyName; // + " (maybe monospaced)";
      }
      else if(_monospaced)
      {
         return _fontFamilyName + " (monospaced)";
      }
      else // if(_monospaced == false)
      {
         return _fontFamilyName; // + " (not monospaced)";
      }
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FontFamilyWrapper wrapper = (FontFamilyWrapper) o;

      return _fontFamilyName != null ? _fontFamilyName.equals(wrapper._fontFamilyName) : wrapper._fontFamilyName == null;
   }

   @Override
   public int hashCode()
   {
      return _fontFamilyName != null ? _fontFamilyName.hashCode() : 0;
   }



   public static FontFamilyWrapper[] createWrappers(Component comp)
   {
      String[] availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();


      FontFamilyWrapper[] ret = new FontFamilyWrapper[availableFontFamilyNames.length];

      for (int i = 0; i < availableFontFamilyNames.length; i++)
      {
         String availableFontFamilyName = availableFontFamilyNames[i];

         FontFamilyWrapper wrapper = wrap(comp, availableFontFamilyName);

         ret[i] = wrapper;
      }

      return ret;
   }

   public static FontFamilyWrapper wrap(Component comp, String availableFontFamilyName)
   {
      Font font = Font.decode(availableFontFamilyName);

      FontFamilyWrapper wrapper;

      if(null == font)
      {
         wrapper = new FontFamilyWrapper(availableFontFamilyName);
      }
      else
      {

         FontMetrics fm = comp.getFontMetrics(font);

         if(null != fm)
         {
            boolean isMonoSpaced = fm.stringWidth("m") == fm.stringWidth("i");
            wrapper = new FontFamilyWrapper(availableFontFamilyName, isMonoSpaced);

         }
         else
         {
            wrapper = new FontFamilyWrapper(availableFontFamilyName);
         }
      }
      return wrapper;
   }
}
