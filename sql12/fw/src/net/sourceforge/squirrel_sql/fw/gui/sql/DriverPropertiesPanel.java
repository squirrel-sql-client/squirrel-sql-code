package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.sql.event.IDriverPropertiesPanelListener;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
/**
 * This panel allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesPanel extends JPanel
{
	/** The driver we want to show properties for. */
	private final Driver _driver;

	/** The database URL to show properties for. */
	private final String _url;

	/** Listeners for this object. */
	private EventListenerList _listenerList = new EventListenerList();

	/** JTable containing the properties. */
	private DriverPropertiesTable _tbl;

	/** The OK button. */
	private JButton _okBtn;

	/**
	 * Ctor specifying driver and URL.
	 *
	 * @param	driver		The <TT>Driver</TT> properties are to be
	 * 						shown for.
	 * @param	url			URL to the database.
	 * @param	override	Used to override the value of some or all
	 * 						of the properties instead of using the 
	 * 						default values.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Driver</TT>
	 * 			or <TT>url</TT> thrown.
	 *
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public DriverPropertiesPanel(Driver driver, String url,
								SQLDriverProperty[] override)
		throws SQLException
	{
		super(new BorderLayout());
		if (driver == null)
		{
			throw new IllegalArgumentException("Driver == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		}

		_driver = driver;
		_url = url;
		createUserInterface(override);
	}

	/**
	 * Adds a listener for actions in this panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> that
	 * 				will be notified when actions are performed
	 * 				in this panel.
	 */
	public synchronized void addListener(IDriverPropertiesPanelListener lis)
	{
		_listenerList.add(IDriverPropertiesPanelListener.class, lis);
	}

	/**
	 * Removes a listener from this panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> to
	 * 				be removed.
	 */
	public synchronized void removeListener(IDriverPropertiesPanelListener lis)
	{
		_listenerList.remove(IDriverPropertiesPanelListener.class, lis);
	}

	/**
	 * Retrieve the database properties.
	 *
	 * @return		the database properties.
	 */
	public DriverPropertyInfo[] getDriverPropertyInfo()
	{
		return _tbl.getTypedModel().getDriverPropertyInfo();
	}

	private void fireButtonPressed(JButton btn)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		EventObject evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IDriverPropertiesPanelListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new EventObject(this);
				}
				IDriverPropertiesPanelListener lis = (IDriverPropertiesPanelListener)listeners[i + 1];
				if (btn == _okBtn)
				{
					lis.okPressed(evt);
				}
				else
				{
					lis.closePressed(evt);
				}
			}
		}
	}

	private void createUserInterface(SQLDriverProperty[] override)
		throws SQLException
	{
		_tbl = new DriverPropertiesTable(_driver, _url);
		add(new JScrollPane(_tbl), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	private JPanel createButtonsPanel()
	{
		final JPanel pnl = new JPanel();

		_okBtn = new JButton("OK");
		_okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(_okBtn);
			}
		});

		final JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(closeBtn);
			}
		});

		pnl.add(_okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] {_okBtn, closeBtn});

		return pnl;
	}
}

