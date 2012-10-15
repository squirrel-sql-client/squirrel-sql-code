package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

public class TabWindowController implements DockTabDesktopPaneHolder
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DockTabDesktopPaneHolder.class);


   private final DockTabDesktopPane _dockTabDesktopPane;
   private IApplication _app;
   private final JMenu _mnuSession;

   public TabWindowController(Point locationOnScreen, Dimension size, final IApplication app)
   {
      _app = app;
      JFrame f = new JFrame(_app.getMainFrame().getTitle() + " " +s_stringMgr.getString("docktabdesktop.TabWindowController.titlePostFix"));

      f.setLocation(locationOnScreen);
      f.setSize(size);
      f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      _dockTabDesktopPane = new DockTabDesktopPane(app, false);
      _dockTabDesktopPane.setDesktopManager(new SquirrelDesktopManager(app));

      f.getContentPane().add(_dockTabDesktopPane);

      JMenuBar mnuBar = new JMenuBar();
      _mnuSession = cloneMenu(app.getMainFrame().getSessionMenu());
      mnuBar.add(_mnuSession);

      f.setJMenuBar(mnuBar);

      WindowFocusListener l = new WindowFocusListener()
      {
         @Override
         public void windowGainedFocus(WindowEvent e)
         {
            onWindowFocusGained(app);
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


      f.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      });

      f.setVisible(true);
   }

   private void onWindowClosing()
   {
      ArrayList<TabHandle> handels = _dockTabDesktopPane.getAllHandels();
      TabHandle[] clone = handels.toArray(new TabHandle[handels.size()]);
      for (TabHandle handel : clone)
      {
         handel.removeTab(DockTabDesktopPane.TabClosingMode.DISPOSE);
      }

      _app.getMultipleWindowsHandler().unregisterDesktop(this);
   }


   @Override
   public DockTabDesktopPane getDockTabDesktopPane()
   {
      return _dockTabDesktopPane;
   }

   @Override
   public void setSelected(boolean b)
   {
      _dockTabDesktopPane.setSelected(true);
      adjustSessionMenu();
   }

   private void adjustSessionMenu()
   {
      if(null == _dockTabDesktopPane.getSelectedWidget())
      {
         _mnuSession.setEnabled(false);
      }
      else
      {
         _mnuSession.setEnabled(true);
      }
   }

   @Override
   public void tabDragedAndDroped()
   {
      adjustSessionMenu();
   }

   private void onWindowFocusGained(IApplication app)
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
