package net.sourceforge.squirrel_sql.fw.util;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * This class was introduced for bug #1502:
 *   "Many Look and Feel jars are not picking up Mac OS keyboard shortcuts in popup dialogs"
 *
 * See https://stackoverflow.com/questions/60827013/shortcut-key-bindings-for-cross-platform-look-feelmetal-lf
 */
public class SquirrelLookAndFeelHandler
{
   public static void setLookAndFeel(LookAndFeel laf) throws UnsupportedLookAndFeelException
   {
      Object textField_focusInputMap = null;

      try
      {
         if(null != laf
            && "javax.swing.plaf.metal.MetalLookAndFeel".equals(laf.getClass().getName())
            && SystemInfo.isMacOS())
         {
            textField_focusInputMap = UIManager.getDefaults().get("TextField.focusInputMap");
         }

         UIManager.setLookAndFeel(laf);
      }
      finally
      {
         if(null != textField_focusInputMap)
         {
            UIManager.getDefaults().put("TextField.focusInputMap", textField_focusInputMap);
         }
      }
   }


   public static void setLookAndFeel(String lafClassName) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      Object textField_focusInputMap = null;

      try
      {
         if(   "javax.swing.plaf.metal.MetalLookAndFeel".equals(lafClassName)
            && SystemInfo.isMacOS())
         {
            textField_focusInputMap = UIManager.getDefaults().get("TextField.focusInputMap");
         }

         UIManager.setLookAndFeel(lafClassName);
      }
      finally
      {
         if(null != textField_focusInputMap)
         {
            UIManager.getDefaults().put("TextField.focusInputMap", textField_focusInputMap);
         }
      }
   }
}
