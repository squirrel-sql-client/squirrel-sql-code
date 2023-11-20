package net.sourceforge.squirrel_sql.client.session.action.dataimport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;

public class EDTMessageBoxUtil
{
   public static void showMessageDialogOnEDT(String string, String title)
   {
      showMessageDialogOnEDT(string, title, JOptionPane.ERROR_MESSAGE);
   }

   public static void showMessageDialogOnEDT(final String message)
   {
      showMessageDialogOnEDT(message, null, JOptionPane.DEFAULT_OPTION);
   }

   public static void showMessageDialogOnEDT(final String message, final String title, final int messageType)
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         @Override
         public void run()
         {
            if (null == title)
            {
               JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), message);
            }
            else
            {
               JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), message, title, messageType);
            }
         }
      }, true);
   }
}
