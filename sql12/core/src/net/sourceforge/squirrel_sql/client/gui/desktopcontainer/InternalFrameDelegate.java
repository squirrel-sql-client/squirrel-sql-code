package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;

import javax.swing.*;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.ArrayList;

public class InternalFrameDelegate extends JInternalFrame implements IDialogDelegate, IDockDelegate, ITabDelegate
{
   private IWidget _widget;
   private boolean _inDispose;
   private boolean _inSetTitle;
   private boolean _inUpdateUI;
   private boolean _inSetVisible;
   private boolean _inAddNotify;

   private WidgetEventCaster _eventCaster = new WidgetEventCaster();

   public InternalFrameDelegate(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, IWidget widget)
   {
      super(title, resizable, closable, maximizable, iconifiable);

      _widget = widget;
      addInternalFrameListener(new InternalFrameListener()
      {
         public void internalFrameOpened(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetOpened(new WidgetEvent(e, _widget));
         }

         public void internalFrameClosing(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetClosing(new WidgetEvent(e, _widget));
         }

         public void internalFrameClosed(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetClosed(new WidgetEvent(e, _widget));
         }

         public void internalFrameIconified(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetIconified(new WidgetEvent(e, _widget));
         }

         public void internalFrameDeiconified(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetDeiconified(new WidgetEvent(e, _widget));
         }

         public void internalFrameActivated(InternalFrameEvent e)
         {
            _eventCaster.fireWidgetActivated(new WidgetEvent(e, _widget));
         }

         public void internalFrameDeactivated(InternalFrameEvent e)
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

   public void _updateUI()
   {
      super.updateUI();
   }

   @Override
   public void updateUI()
   {
      super.updateUI();
      if(null != _widget)
      {
         _widget.updateUI();
      }
   }

   public void _setVisible(boolean aFlag)
   {
      super.setVisible(aFlag);
   }

   @Override
   public void setVisible(boolean aFlag)
   {
      super.setVisible(aFlag);
      if(null != _widget)
      {
         _widget.setVisible(aFlag);
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

   public void centerWithinDesktop()
   {
      GUIUtils.centerWithinDesktop(this);
   }

   public Container getAwtContainer()
   {
      return this;
   }

   public void setContentPane(JPanel contentPane)
   {
      super.setContentPane(contentPane);
   }

   public void showOk(String msg)
   {
      Dialogs.showOk(this, msg);
   }

   public JInternalFrame getInternalFrame()
   {
      return this;
   }


   public void makeToolWindow(boolean isToolWindow)
   {
      GUIUtils.makeToolWindow(this, isToolWindow);
   }

   public void addDockWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addDockWidgetListener(widgetListener);
   }

   public void removeDockWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeDockWidgetListener(widgetListener);
   }

   public void addDialogWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addDialogWidgetListener(widgetListener);
   }

   public void removeDialogWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeDialogWidgetListener(widgetListener);
   }

   public void addTabWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addDialogWidgetListener(widgetListener);
   }

   public void removeTabWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeDialogWidgetListener(widgetListener);
   }

   @Override
   public void addSmallTabButton(SmallTabButton fileMenuSmallButton)
   {
      // Not implemented
   }

   @Override
   public void removeSmallTabButton(SmallTabButton smallTabButton)
   {
      // Not implemented
   }

   public void fireWidgetClosing()
   {
      _eventCaster.fireWidgetClosing(new WidgetEvent(new InternalFrameEvent(this, InternalFrameEvent.INTERNAL_FRAME_CLOSING), _widget));
   }

   public void fireWidgetClosed()
   {
      _eventCaster.fireWidgetClosing(new WidgetEvent(new InternalFrameEvent(this, InternalFrameEvent.INTERNAL_FRAME_CLOSED), _widget));
   }

   public boolean isToolWindow()
   {
      return GUIUtils.isToolWindow(this);
   }

   public void _moveToFront()
   {
      GUIUtils.moveToFront(this);
   }

   public IWidget getWidget()
   {
      return _widget;
   }
}
