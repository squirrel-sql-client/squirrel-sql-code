package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

public class TabWidget implements IWidget
{
   private ITabDelegate _delegate;

   public TabWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, IApplication app)
   {
      _delegate = DesktopContainerFactory.createTabDelegate(app, title, resizeable, closeable, maximizeable, iconifiable, this);
   }

   public TabWidget(String title, boolean resizeable, IApplication app)
   {
      this(title, resizeable, true, false, false, app);
   }

   public TabWidget(String title, boolean resizeable, boolean closeable, IApplication app)
   {
      this(title, resizeable, closeable, false, false, app);
   }

   public boolean isVisible()
   {
      return _delegate.isVisible();
   }

   public void moveToFront()
   {
      _delegate._moveToFront();
   }

   public Container getAwtContainer()
   {
      return _delegate.getAwtContainer();
   }

   public void setDefaultCloseOperation(int operation)
   {
      _delegate.setDefaultCloseOperation(operation);
   }

   public int getDefaultCloseOperation()
   {
      return _delegate.getDefaultCloseOperation();
   }


   public Container getContentPane()
   {
      return _delegate.getContentPane();
   }

   public void pack()
   {
      _delegate.pack();
   }

   public String getTitle()
   {
      return _delegate.getTitle();
   }

   public void makeToolWindow(boolean isToolWindow)
   {
      _delegate.makeToolWindow(isToolWindow);
   }

   public static void centerWithinDesktop(TabWidget instance)
   {
      instance._delegate.centerWithinDesktop();
   }

   public void dispose()
   {
      _delegate._dispose();
   }

   public void setTitle(String title)
   {
      _delegate._setTitle(title);
   }

   public void addSmallTabButton(SmallTabButton fileMenuSmallButton)
   {
      _delegate.addSmallTabButton(fileMenuSmallButton);
   }

   public void removeSmallTabButton(SmallTabButton fileMenuSmallButton)
   {
      _delegate.removeSmallTabButton(fileMenuSmallButton);
   }


   public void updateUI()
   {
      _delegate._updateUI();
   }

   public void setVisible(boolean aFlag)
   {
      _delegate._setVisible(aFlag);
   }

   public ITabDelegate getDelegate()
   {
      return _delegate;
   }

   public void setContentPane(JPanel contentPane)
   {
      _delegate.setContentPane(contentPane);
   }

   public void showOk(String msg)
   {
      _delegate.showOk(msg);
   }


   public Dimension getSize()
   {
      return _delegate.getSize();
   }

   public void setSize(Dimension windowSize)
   {
      _delegate.setSize(windowSize);
   }


   public void addFocusListener(FocusListener focusListener)
   {
      _delegate.addFocusListener(focusListener);
   }

   public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener)
   {
      _delegate.addVetoableChangeListener(vetoableChangeListener);
   }

   public void addWidgetListener(WidgetListener widgetListener)
   {
      _delegate.addTabWidgetListener(widgetListener);
   }

   public void removeWidgetListener(WidgetListener widgetListener)
   {
      _delegate.removeTabWidgetListener(widgetListener);
   }

   public void setBounds(Rectangle rectangle)
   {
      _delegate.setBounds(rectangle);
   }

   public Rectangle getBounds() {
   	return _delegate.getBounds();
   }
   
   public JInternalFrame getInternalFrame()
   {
      return _delegate.getInternalFrame();
   }

   public void setSelected(boolean b)
      throws PropertyVetoException
   {
      _delegate.setSelected(b);
   }


   public void putClientProperty(Object key, Object prop)
   {
      _delegate.putClientProperty(key, prop);
   }

   public Object getClientProperty(Object key)
   {
      return _delegate.getClientProperty(key);
   }

   public boolean isToolWindow()
   {
      return _delegate.isClosed();
   }

   public boolean isClosed()
   {
      return _delegate.isClosed();
   }

   public boolean isIcon()
   {
      return _delegate.isIcon();
   }

   public void fireWidgetClosing()
   {
      _delegate.fireWidgetClosing();
   }

   public void fireWidgetClosed()
   {
      _delegate.fireWidgetClosed();
   }


   public void validate()
   {
      _delegate.validate();
   }

   public void setFrameIcon(Icon icon)
   {
      _delegate.setFrameIcon(icon);
   }

   public void addNotify()
   {
      _delegate._addNotify();
   }
}