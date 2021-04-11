package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.aboutdialog.AboutBoxDialog;

import java.awt.Desktop;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;

public class DesktopSupport
{
   public static void handleDesktop()
   {
      if (false == java.awt.Desktop.isDesktopSupported())
      {
         return;
      }

      if (java.awt.Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT))
      {
         java.awt.Desktop.getDesktop().setAboutHandler(new AboutHandler()
         {
            @Override
            public void handleAbout(java.awt.desktop.AboutEvent e)
            {
               AboutBoxDialog.showAboutBox();
            }
         });
      }

      if (java.awt.Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER))
      {
         java.awt.Desktop.getDesktop().setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(QuitEvent qe, QuitResponse qr)
            {
               final boolean disposeAllowed = Main.getApplication().getMainFrame().requestDispose();

               if(disposeAllowed)
               {
                  Main.getApplication().getMainFrame().execDisposeNoQuestion();
                  qr.performQuit();
               }
               else
               {
                  qr.cancelQuit();
               }
            }
         });
      }

   }
}
