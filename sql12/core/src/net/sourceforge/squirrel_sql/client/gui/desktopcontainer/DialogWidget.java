package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyVetoException;

public class DialogWidget implements IWidget
{
   private IDialogDelegate _delegate;

   public DialogWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, IApplication app)
   {
      this(title, resizeable, closeable, maximizeable, iconifiable, app, app.getMainFrame());
   }

   public DialogWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, IApplication app, Window parent)
   {
      _delegate = DesktopContainerFactory.createDialogDelegate(app, parent, title, resizeable, closeable, maximizeable, iconifiable, this);
   }


   public DialogWidget(String title, boolean resizeable, IApplication app)
   {
      this(title, resizeable, true, false, false, app);
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

   public Container getContentPane()
   {
      return _delegate.getContentPane();
   }

   public void pack()
   {
      _delegate.pack();
   }

   public JRootPane getRootPane()
   {
      return _delegate.getRootPane();
   }

   public String getTitle()
   {
      return _delegate.getTitle();
   }

   public void makeToolWindow(boolean isToolWindow)
   {
      _delegate.makeToolWindow(isToolWindow);
   }

   public static void centerWithinDesktop(DialogWidget instance)
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

   public void updateUI()
   {
      _delegate._updateUI();
   }

   public void setVisible(boolean aFlag)
   {
      _delegate._setVisible(aFlag);
   }

   public JInternalFrame getInternalFrame()
   {
      return _delegate.getInternalFrame();
   }

   public IDialogDelegate getDelegate()
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

   public void addWidgetListener(WidgetListener l)
   {
      _delegate.addDialogWidgetListener(l);
   }

   public void removeWidgetListener(WidgetListener l)
   {
      _delegate.removeDialogWidgetListener(l);
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
      return _delegate.isToolWindow();
   }

   public boolean isClosed()
   {
      return _delegate.isClosed();
   }

   public boolean isIcon()
   {
      return _delegate.isIcon();
   }

   public void setSize(Dimension dimension)
   {
      _delegate.setSize(dimension);
   }

   public Dimension getSize()
   {
      return _delegate.getSize();
   }

   public void setLayer(Integer layer)
   {
      _delegate.setLayer(layer);
   }

   public void fireWidgetClosing()
   {
      _delegate.fireWidgetClosing();
   }

   public void fireWidgetClosed()
   {
      _delegate.fireWidgetClosed();
   }

   public void addNotify()
   {
      _delegate._addNotify();
   }

   public void toFront()
   {
      _delegate.toFront();
   }

   public void requestFocus()
   {
      _delegate.requestFocus();
   }

   public void setBounds(Rectangle rect)
   {
      _delegate.setBounds(rect);
   }

   public void setMaximum(boolean b)
      throws PropertyVetoException
   {
      _delegate.setMaximum(b);
   }

   public void setBorder(Border border)
   {
      _delegate.setBorder(border);
   }

   public Rectangle getBounds()
   {
      return _delegate.getBounds();
   }

   public void setFrameIcon(Icon icon)
   {
      _delegate.setFrameIcon(icon);
   }

   public void setPreferredSize(Dimension dimension)
   {
      _delegate.setPreferredSize(dimension);
   }

   public int getWidth()
   {
      return _delegate.getWidth();
   }

   public void setSelected(boolean b) 
      throws PropertyVetoException
   {
      _delegate.setSelected(b);
   }


   public int getHeight()
   {
      return _delegate.getHeight();
   }
}
