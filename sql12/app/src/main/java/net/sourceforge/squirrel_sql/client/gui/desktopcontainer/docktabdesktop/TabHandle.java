package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabWidget;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class TabHandle
{
   //private JButton _closeButton;
   private TabWidget _tabWidget;
   private DockTabDesktopPane _dockTabDesktopPane;
   private boolean _isSelected;

   private ArrayList<TabHandleListener> _tabHandleListeners = new ArrayList<TabHandleListener>();

   private boolean _fireClosingProceedingOrDone;

   public TabHandle(TabWidget tabWidget, DockTabDesktopPane dockTabDesktopPane)
   {
      _tabWidget = tabWidget;
      _dockTabDesktopPane = dockTabDesktopPane;
   }


   public TabWidget getWidget()
   {
      return _tabWidget;
   }

   public void addTabHandleListener(TabHandleListener l)
   {
      _tabHandleListeners.add(l);
   }

   public void removeTabHandleListener(TabHandleListener l)
   {
      _tabHandleListeners.remove(l);
   }


   public void fireClosing(ActionEvent e)
   {
      _fireClosingProceedingOrDone = true;
      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         listener.tabClosing(new TabHandleEvent(this, e));
      }
   }

   public void fireClosed(ActionEvent e)
   {
      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         listener.tabClosed(new TabHandleEvent(this, e));
      }
   }

   public void fireAdded()
   {
      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         listener.tabAdded(new TabHandleEvent(this, null));
      }
   }

   public void fireDeselected(ActionEvent e)
   {
      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         listener.tabDeselected(new TabHandleEvent(this, null));
      }
   }

   public void fireSelected(ActionEvent e)
   {
      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         listener.tabSelected(new TabHandleEvent(this, null));
      }
   }




   public boolean isSelected()
   {
      return _isSelected;
   }

   public void setSelected(boolean b)
   {
      _setSelected(b, false);
   }

   public void _setSelected(boolean b, boolean force)
   {
      if(_isSelected == b && false == force)
      {
         return;
      }

      _isSelected = b;

      TabHandleListener[] clone = _tabHandleListeners.toArray(new TabHandleListener[_tabHandleListeners.size()]);

      for (TabHandleListener listener : clone)
      {
         if(_isSelected)
         {
            listener.tabSelected(new TabHandleEvent(this, null));
         }
         else
         {
            listener.tabDeselected(new TabHandleEvent(this, null));
         }
      }
   }

   public void setTitle(String title)
   {
      _dockTabDesktopPane.setTabTitle(this, title);
   }

   public void addSmallTabButton(SmallTabButton smallTabButton)
   {
      _dockTabDesktopPane.addSmallTabButton(this, smallTabButton);
   }

   public void removeSmallTabButton(SmallTabButton smallTabButton)
   {
      _dockTabDesktopPane.removeSmallTabButton(this, smallTabButton);
   }


   public String getTitle()
   {
      return _dockTabDesktopPane.getTabTitle(this);
   }

   public void setIcon(Icon frameIcon)
   {
      _dockTabDesktopPane.setTabIcon(this, frameIcon);
   }

   public void removeTab(DockTabDesktopPane.TabClosingMode tabClosingMode)
   {
      _dockTabDesktopPane.removeTab(this, null, tabClosingMode);
   }

   public void select()
   {
      _dockTabDesktopPane.selectTab(this);
   }

   public boolean isFireClosingProceedingOrDone()
   {
      return _fireClosingProceedingOrDone;
   }

}
