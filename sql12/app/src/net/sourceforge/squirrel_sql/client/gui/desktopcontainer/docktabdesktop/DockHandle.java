package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class DockHandle
{
   private String _title;
   private DockFrame _dockFrame;


   private JToggleButton _btn;
   private ArrayList<DockHandleListener> _dockHandleListeners = new ArrayList<DockHandleListener>();
   private static final String PREFS_KEY_DOCK_DIVIDER_LOC = "squirrelSql_dock_divider_loc." ;
   private IApplication _app;

   public DockHandle(IApplication app, Container comp, String title, JToggleButton btn)
   {
      _app = app;
      _dockFrame = new DockFrame(_app, comp, title);
      _title = title;
      _btn = btn;
      _dockFrame.getMinimizeButton().addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeDock();
         }
      });


      _btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            fireDockHandleEvent(e);
         }
      });
   }

   private void fireDockHandleEvent(ActionEvent e)
   {
      DockHandleListener[] clone = _dockHandleListeners.toArray(new DockHandleListener[_dockHandleListeners.size()]);

      for (int i = 0; i < clone.length; i++)
      {
         if(_btn.isSelected())
         {
            clone[i].dockOpened(new DockHandleEvent(e));
         }
         else
         {
            clone[i].dockClosing(new DockHandleEvent(e));
         }
      }
   }

   public DockFrame getDockFrame()
   {
      return _dockFrame;
   }

   public int getDividerLocation()
   {
      return Preferences.userRoot().getInt(PREFS_KEY_DOCK_DIVIDER_LOC + _title, 150);
   }

   public void storeDividerLocation(int dividerLocation)
   {
      Preferences.userRoot().putInt(PREFS_KEY_DOCK_DIVIDER_LOC + _title, dividerLocation);
   }

   public void addDockHandleListener(DockHandleListener dockHandleListener)
   {
      _dockHandleListeners.add(dockHandleListener);
   }

   public void removeDockHandleListener(DockHandleListener dockHandleListener)
   {
      _dockHandleListeners.remove(dockHandleListener);
   }

   public void openDock()
   {
      if(false == _btn.isSelected())
      {
         _btn.doClick();
      }
   }

   public void closeDock()
   {
      if(true == _btn.isSelected())
      {
         _btn.doClick();
      }
   }

   public void wasClosedByOtherButton(ActionEvent otherButtonEvent)
   {
      fireDockHandleEvent(otherButtonEvent);
   }

   public boolean isClosed()
   {
      return !_btn.isSelected();
   }

   public void mayAutoHide()
   {
      if(_dockFrame.isAutoHide())
      {
         closeDock();
      }
   }
}
