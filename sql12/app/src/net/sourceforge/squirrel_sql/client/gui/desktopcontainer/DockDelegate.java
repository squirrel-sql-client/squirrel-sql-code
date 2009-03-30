package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockHandle;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockHandleEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockHandleListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;

public class DockDelegate implements IDockDelegate
{
   private JPanel _contentPane = new JPanel();
   private IApplication _app;
   private String _title;
   private DockWidget _dockWidget;
   private WidgetEventCaster _eventCaster = new WidgetEventCaster();
   private DockHandle _dockHandle;
   private int _defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;


   public DockDelegate(IApplication app, String title, DockWidget dockWidget)
   {
      _app = app;
      _title = title;
      _dockWidget = dockWidget;
   }

   public void addDockWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.addDockWidgetListener(widgetListener);
   }

   public void removeDockWidgetListener(WidgetListener widgetListener)
   {
      _eventCaster.removeDockWidgetListener(widgetListener);
   }


   public boolean isVisible()
   {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void _moveToFront()
   {
      _dockHandle.openDock();
   }

   public void setDefaultCloseOperation(int defaultCloseOperation)
   {
      _defaultCloseOperation = defaultCloseOperation;
   }

   @Override
   public int getDefaultCloseOperation()
   {
      return _defaultCloseOperation;
   }

   public Container getContentPane()
   {
      return _contentPane;
   }


   public String getTitle()
   {
      return _title;
   }


   public void _setTitle(String title)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void _setVisible(boolean aFlag)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public Container getAwtContainer()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setContentPane(JPanel contentPane)
   {
      //To change body of implemented methods use File | Settings | File Templates.
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

   public void _addNotify()
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


   public void setBounds(Rectangle rectangle)
   {
   	
   }

	@Override
	public Rectangle getBounds()
	{
		return _dockWidget.getBounds();
	}   
   
   public void setSelected(boolean b) throws PropertyVetoException
   {
      if(b)
      {
         _dockHandle.openDock();
      }
      else
      {
         _dockHandle.closeDock();
      }
   }

   public void setLayer(Integer layer)
   {
   }

   public void pack()
   {
   }

   public void makeToolWindow(boolean isToolWindow)
   {
   }

   public void _dispose()
   {
   }

   public void _updateUI()
   {
   }

   public void centerWithinDesktop()
   {
   }

   public JInternalFrame getInternalFrame()
   {
      return null;
   }


   public void setDockHandle(DockHandle dockHandle)
   {
      _dockHandle = dockHandle;

      _app.getSessionManager().addSessionListener(new SessionAdapter()
      {
         public void sessionConnected(SessionEvent evt)
         {
            _dockHandle.mayAutoHide();
         }
      });


      _dockHandle.addDockHandleListener(new DockHandleListener()
      {
         public void dockClosing(DockHandleEvent e)
         {
            _eventCaster.fireWidgetClosing(new WidgetEvent(e, _dockWidget));
         }

         public void dockOpened(DockHandleEvent e)
         {
            _eventCaster.fireWidgetOpened(new WidgetEvent(e, _dockWidget));
         }
      });
   }


   public void putClientProperty(Object key, Object prop)
   {
      _contentPane.putClientProperty(key, prop);
   }

   public Object getClientProperty(Object key)
   {
      return _contentPane.getClientProperty(key);
   }

   public void fireWidgetClosing()
   {
      ActionEvent ae = new ActionEvent(_dockWidget, ActionEvent.ACTION_PERFORMED, "fireWidgetClosing");
      _eventCaster.fireWidgetClosing(new WidgetEvent(new DockHandleEvent(ae), _dockWidget));
   }

   public void fireWidgetClosed()
   {
      ActionEvent ae = new ActionEvent(_dockWidget, ActionEvent.ACTION_PERFORMED, "fireWidgetClosed");
      _eventCaster.fireWidgetClosed(new WidgetEvent(new DockHandleEvent(ae), _dockWidget));
   }

   public void validate()
   {
      _contentPane.validate();
   }

   public void setFrameIcon(Icon icon)
   {
      
   }

   public void toFront()
   {
   }

   public void requestFocus()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setMaximum(boolean b)
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
      return _dockHandle.isClosed();
   }

   public boolean isIcon()
   {
      return false;
   }

}
