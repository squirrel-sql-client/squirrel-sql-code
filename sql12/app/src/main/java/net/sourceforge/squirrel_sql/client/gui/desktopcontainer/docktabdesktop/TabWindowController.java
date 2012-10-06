package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class TabWindowController implements DockTabDesktopPaneHolder
{

   private final DockTabDesktopPane _dockTabDesktopPane;

   public TabWindowController(final TabHandle tabHandle, Point locationOnScreen, Dimension size, final IApplication app)
   {
      JFrame f = new JFrame();

      f.setLocation(locationOnScreen);
      f.setSize(size);

      _dockTabDesktopPane = new DockTabDesktopPane(app);
      _dockTabDesktopPane.setDesktopManager(new SquirrelDesktopManager(app));

      f.getContentPane().add(_dockTabDesktopPane);

      JMenuBar mnuBar = new JMenuBar();
      mnuBar.add(cloneMenu(app.getMainFrame().getSessionMenu()));

      f.setJMenuBar(mnuBar);

      WindowFocusListener l = new WindowFocusListener()
      {
         @Override
         public void windowGainedFocus(WindowEvent e)
         {
            onWindowFocusGained(app, tabHandle);
         }

         @Override
         public void windowLostFocus(WindowEvent e)
         {
         }
      };
      f.addWindowFocusListener(l);


      final ImageIcon icon = app.getResources().getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
      if (icon != null)
      {
         f.setIconImage(icon.getImage());
      }



      f.setVisible(true);
      _dockTabDesktopPane.addWidget(tabHandle.getWidget());
   }


   @Override
   public DockTabDesktopPane getDockTabDesktopPane()
   {
      return _dockTabDesktopPane;
   }

   private void onWindowFocusGained(IApplication app, TabHandle tabHandle)
   {
      app.getMultipleWindowsHandler().selectDesktop(this);
   }

   private JMenu cloneMenu(JMenu menu)
   {
      JMenu ret = new JMenu(menu.getText());

      for (int i = 0; i < menu.getItemCount(); i++)
      {
         JMenuItem toClone = menu.getItem(i);

         if (toClone instanceof JMenu)
         {
            ret.add(cloneMenu((JMenu) toClone));
         }
         else if(toClone instanceof JMenuItem)
         {
            JMenuItem clone = new JMenuItem(toClone.getText(), toClone.getIcon());
            clone.setMnemonic(toClone.getMnemonic());
            clone.setAction(toClone.getAction());
            clone.setAccelerator(toClone.getAccelerator());
            clone.setToolTipText(toClone.getToolTipText());

            ret.add(clone);
         }
         else
         {
            ret.addSeparator();
         }
      }

      return ret;
   }
}
