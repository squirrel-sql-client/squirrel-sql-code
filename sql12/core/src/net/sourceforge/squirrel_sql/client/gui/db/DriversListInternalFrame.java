package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * This windows displays a list of JDBC drivers and allows the user
 * to maintain their details, add new ones etc.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriversListInternalFrame extends BaseListInternalFrame
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DriversListInternalFrame.class);

	/** Application API. */
	private IApplication _app;

	/** User Interface facory. */
	private DriverUserInterfaceFactory _uiFactory;

	/**
	 * Default ctor.
	 */
	public DriversListInternalFrame(DriversList list)
	{
		super(new DriverUserInterfaceFactory(list));
		_app = Main.getApplication();
		_uiFactory = (DriverUserInterfaceFactory)getUserInterfaceFactory();
		_uiFactory.setDriversListInternalFrame(this);


      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               // i18n[DriversListInternalFrame.error.ctrlF4key=Probably closed by the ctrl F4 key. See BasicDesktopPaneUi.CloseAction]
               throw new PropertyVetoException(s_stringMgr.getString("DriversListInternalFrame.error.ctrlF4key"), evt);
            }
         }
      });

      addWidgetListener(new WidgetAdapter()
      {
         @Override
         public void widgetOpened(WidgetEvent evt)
         {
            nowVisible(true);
         }
         
         @Override
         public boolean widgetClosing(WidgetEvent evt)
         {
            nowVisible(false);
            return true;
         }

         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            nowVisible(false);
         }
      });



      _app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				_uiFactory.propertiesChanged(propName);
			}
		});
	}


   public void nowVisible(final boolean b)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _app.getMainFrame().setEnabledDriversMenu(b);
            _uiFactory.getDriversList().requestFocus();
         }
      });
   }


}
