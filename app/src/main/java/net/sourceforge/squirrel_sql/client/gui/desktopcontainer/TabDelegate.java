package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;

public class TabDelegate implements ITabDelegate
{
   private TabWidget _widget;
   private String _title;
   private TabHandle _tabHandle;

   private WidgetEventCaster _eventCaster = new WidgetEventCaster();
   private JPanel _contentPane = new JPanel();
   private int _defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;
   private Icon _frameIcon;
   private boolean _visible;
   private HashMap<Object, Object> _clientProperties = new HashMap<Object, Object>();


   public TabDelegate(TabWidget widget, String title)
   {
      _widget = widget;
      _title = title;
   }

   public void setTabHandle(TabHandle tabHandle)
   {
      _tabHandle = tabHandle;

      _tabHandle.addTabHandleListener(new TabHandleListener()
      {
         public void tabClosing(TabHandleEvent tabHandleEvent)
         {
            onTabClosing(tabHandleEvent);
         }

         public void tabClosed(TabHandleEvent tabHandleEvent)
         {
            onTabClosed(tabHandleEvent);
         }

         public void tabAdded(TabHandleEvent tabHandleEvent)
         {
            onTabAdded(tabHandleEvent);
         }

         public void tabSelected(TabHandleEvent tabHandleEvent)
         {
            _eventCaster.fireWidgetActivated(new WidgetEvent(tabHandleEvent, _widget));
         }

         public void tabDeselected(TabHandleEvent tabHandleEvent)
         {
            _eventCaster.fireWidgetDeactivated(new WidgetEvent(tabHandleEvent, _widget));
         }
      });

   }

   private void onTabAdded(TabHandleEvent tabHandleEvent)
   {
      _tabHandle.setTitle(_title);
      _tabHandle.setIcon(_frameIcon);
      _widget.setVisible(true);
      _eventCaster.fireWidgetOpened(new WidgetEvent(tabHandleEvent, _widget));
   }

   private void onTabClosed(TabHandleEvent tabHandleEvent)
   {
      if (WindowConstants.DO_NOTHING_ON_CLOSE != _defaultCloseOperation)
      {
         _eventCaster.fireWidgetClosed(new WidgetEvent(tabHandleEvent, _widget));
         _widget.dispose();
      }
   }

   private void onTabClosing(TabHandleEvent tabHandleEvent)
   {
      _eventCaster.fireWidgetClosing(new WidgetEvent(tabHandleEvent, _widget));
      if (WindowConstants.DO_NOTHING_ON_CLOSE != _defaultCloseOperation)
      {
         _eventCaster.fireWidgetDeactivated(new WidgetEvent(tabHandleEvent, _widget));
         _widget.setVisible(false);
      }
   }


   public void addTabWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addTabWidgetListener(widgetListener);
   }

   public void removeTabWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeTabWidgetListener(widgetListener);
   }

   @Override
   public int getDefaultCloseOperation()
   {
      return _defaultCloseOperation;
   }

   public boolean isVisible()
   {
      return _visible;
   }

   public void _moveToFront()
   {
      _tabHandle.select();
   }

   public void setDefaultCloseOperation(int operation)
   {
      _defaultCloseOperation = operation;
   }

   public Container getContentPane()
   {
      return _contentPane;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void pack()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getTitle()
   {
      return _title;
   }

   public void makeToolWindow(boolean isToolWindow)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void _dispose()
   {
      _tabHandle.removeTab(DockTabDesktopPane.TabClosingMode.DISPOSE);
      _eventCaster.fireWidgetClosed(new WidgetEvent(new TabHandleEvent(_tabHandle,null), _widget));
   }

   public void _setTitle(String title)
   {
      _title = title;
      if(null != _tabHandle)
      {
         _tabHandle.setTitle(title);
      }
   }

   @Override
   public void addSmallTabButton(SmallTabButton smallTabButton)
   {
      _tabHandle.addSmallTabButton(smallTabButton);
   }

   @Override
   public void removeSmallTabButton(SmallTabButton smallTabButton)
   {
      _tabHandle.removeSmallTabButton(smallTabButton);
   }


   public void _updateUI()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void _setVisible(boolean aFlag)
   {
      _visible = aFlag;
   }

   public void _addNotify()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void centerWithinDesktop()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public Container getAwtContainer()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setContentPane(JPanel contentPane)
   {
      _contentPane = contentPane;
   }

   public void showOk(String msg)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public Dimension getSize()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setSize(Dimension size)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void addFocusListener(FocusListener focusListener)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void removeFocusListener(FocusListener focusListener)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public JInternalFrame getInternalFrame()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setBounds(Rectangle rectangle)
   {
      _widget.setBounds(rectangle);
   }
   
	@Override
	public Rectangle getBounds()
	{
		return _widget.getBounds();
	}   

   public void setSelected(boolean b) throws PropertyVetoException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setLayer(Integer layer)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void putClientProperty(Object key, Object prop)
   {
      _clientProperties.put(key, prop);
   }

   public Object getClientProperty(Object key)
   {
      return _clientProperties.get(key);
   }

   public void fireWidgetClosing()
   {
      _eventCaster.fireWidgetClosing(new WidgetEvent(new TabHandleEvent(_tabHandle, null), _widget));
   }

   public void fireWidgetClosed()
   {
      _eventCaster.fireWidgetClosed(new WidgetEvent(new TabHandleEvent(_tabHandle, null), _widget));
   }

   public void validate()
   {
      _contentPane.validate();
   }

   public void setFrameIcon(Icon icon)
   {
      _frameIcon = icon;

      if(null != _tabHandle)
      {
         _tabHandle.setIcon(_frameIcon);
      }
   }

   public void toFront()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void requestFocus()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setMaximum(boolean b) throws PropertyVetoException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setBorder(Border border)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setPreferredSize(Dimension dimension)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean isToolWindow()
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean isClosed()
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean isIcon()
   {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
   }

}
