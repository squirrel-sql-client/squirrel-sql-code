package net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class DatabasePanelTabComponent extends JPanel
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DatabasePanelTabComponent.class);

	/** Current session. */
	private ISession _session;

	/** Destination to display data in. */
	private IDataSetViewer _viewer;

	/**
	 * Ctor.
	 * 
	 * @param	session	Session.
	 * @param	viewer	Destination to display data in.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ISession</TT> or
	 * 			<TT>IDataSetViewer</TT> passed.
	 */
	public DatabasePanelTabComponent(ISession session, IDataSetViewer viewer)
	{
		super(new BorderLayout());
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (viewer == null)
		{
			throw new IllegalArgumentException("IDataSetViewer == null");
		}

		_session = session;
		_viewer = viewer;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				add(new JScrollPane(_viewer.getComponent()), BorderLayout.CENTER);
			}
		});
	}

	/**
	 * Load data into component.
	 * 
	 * @param	ds	Data to be displayed
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT><TT>IDataSet</TT> passed.
	 */
	public void load(final IDataSet ds)
	{
		if (ds == null)
		{
			throw new IllegalArgumentException("IDataSet == null");
		}

		try
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						_viewer.show(ds, _session.getMessageHandler());
					}
					catch (DataSetException dse)
					{
						s_log.error("Error", dse);
					}
				}
			});
		}
		catch (Exception ex)
		{
			s_log.error("Error", ex);
		}
	}

	/**
	 * Clear data from component.
	 */
	public void clear()
	{
		_viewer.clear();
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public Component getComponent()
	{
		return this;
	}
}
