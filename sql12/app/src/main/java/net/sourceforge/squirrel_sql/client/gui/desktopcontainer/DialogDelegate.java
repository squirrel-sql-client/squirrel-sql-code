package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.beans.VetoableChangeListener;

public class DialogDelegate extends JDialog implements IDialogDelegate
{

   private IWidget _widget;

   private boolean _inWidgetSetVisible;

   private WidgetEventCaster _eventCaster = new WidgetEventCaster();
   private HashMap _clientProperties = new HashMap();
   private boolean _isToolWindow;


   public DialogDelegate(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, IWidget widget, Window parent)
   {
      super(parent, title);

      setResizable(resizable);
      _widget = widget;

      addWindowListener(new WindowListener()
      {
         public void windowOpened(WindowEvent e)
         {
            _eventCaster.fireWidgetOpened(new WidgetEvent(e, _widget));
         }

         public void windowClosing(WindowEvent e)
         {
            _eventCaster.fireWidgetClosing(new WidgetEvent(e, _widget));
         }

         public void windowClosed(WindowEvent e)
         {
            _eventCaster.fireWidgetClosed(new WidgetEvent(e, _widget));
         }

         public void windowIconified(WindowEvent e)
         {
            _eventCaster.fireWidgetIconified(new WidgetEvent(e, _widget));
         }

         public void windowDeiconified(WindowEvent e)
         {
            _eventCaster.fireWidgetDeiconified(new WidgetEvent(e, _widget));
         }

         public void windowActivated(WindowEvent e)
         {
            _eventCaster.fireWidgetActivated(new WidgetEvent(e, _widget));
         }

         public void windowDeactivated(WindowEvent e)
         {
            _eventCaster.fireWidgetDeactivated(new WidgetEvent(e, _widget));
         }
      });

   }

   public void _dispose()
   {
      super.dispose();
   }
   @Override
   public void dispose()
   {
      super.dispose();
      if(null != _widget)
      {
         _widget.dispose();
      }
   }

   public void _setTitle(String title)
   {
      super.setTitle(title);
   }
   @Override
   public void setTitle(String title)
   {
      super.setTitle(title);
      if(null != _widget)
      {
         _widget.setTitle(title);
      }
   }

   public void _setVisible(boolean aFlag)
   {
      super.setVisible(aFlag);
   }
   @Override
   public void setVisible(boolean b)
   {
      super.setVisible(b);
      if(null != _widget)
      {
         _widget.setVisible(b);
      }
   }

   public void _addNotify()
   {
      super.addNotify();
   }
   @Override
   public void addNotify()
   {
      super.addNotify();
      if(null != _widget)
      {
         _widget.addNotify();
      }
   }

   public void _updateUI()
   {
   }
   
   public void centerWithinDesktop()
   {
      GUIUtils.centerWithinParent(this);
   }

   public Container getAwtContainer()
   {
      return getContentPane();
   }

   public void setContentPane(JPanel contentPane)
   {
      super.setContentPane(contentPane);
   }

   public void showOk(String msg)
   {
      Dialogs.showOk(this, msg);
   }

   public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener)
   {
   }

   public void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener)
   {
   }

   public JInternalFrame getInternalFrame()
   {
      return null;
   }

   public void setSelected(boolean b)
   {
      requestFocus();      
   }

   public void setLayer(Integer layer)
   {
   }


   public void makeToolWindow(boolean isToolWindow)
   {
      _isToolWindow = isToolWindow;
   }

   public void _moveToFront()
   {
      //_setVisible(true); Bug 2644778: The widgets setVisible() wasn't called.

      if(false == _inWidgetSetVisible)
      {
         try
         {
            _inWidgetSetVisible = true;
            _widget.setVisible(true);
         }
         finally
         {
            // _inWidgetSetVisible was introduced to allow moveToFront to be called
            // from within setVisible. See EditWhereColsSheet or SQLFilterSheet
            _inWidgetSetVisible = false;
         }
      }
      setSelected(true);
      requestFocus();
   }

   public void addDialogWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addDialogWidgetListener(widgetListener);
   }

   public void removeDialogWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeDialogWidgetListener(widgetListener);
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
      _eventCaster.fireWidgetClosing(new WidgetEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING), _widget));
   }

   public void fireWidgetClosed()
   {
      _eventCaster.fireWidgetClosed(new WidgetEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED), _widget));
   }


   public void setFrameIcon(Icon icon)
   {
   }

   public void setMaximum(boolean b)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setBorder(Border border)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean isToolWindow()
   {
      return _isToolWindow;   
   }

   public boolean isClosed()
   {
      return isVisible();
   }

   public boolean isIcon()
   {
      return false;
   }
}
