package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.BaseObjectPanelTab;

/**
 * This is the tab displaying information about a database object.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectInfoTab extends BaseObjectPanelTab
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface TableInfoi18n
	{
		String TITLE = "Info";
		String HINT = "Basic information";
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(DatabaseObjectInfoTab.class);

	/** Component to be displayed. */
	private DatabaseObjectInfoComponent _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return TableInfoi18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return TableInfoi18n.HINT;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new DatabaseObjectInfoComponent(getSession());
		}
		return _comp;
	}

	/**
	 * Rebuild the tab. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	public void rebuild()
	{
		super.rebuild();
		_comp = null;
	}

	/**
	 * @see BaseObjectPanelTab#clear()
	 */
	public void clear()
	{
		((DatabaseObjectInfoComponent)getComponent()).clear();
	}

	/**
	 * Refresh the component displaying the <TT>ITableInfo</TT> object.
	 */
	public synchronized void refreshComponent() throws IllegalStateException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		if (doi == null)
		{
			throw new IllegalStateException("Null IDatabaseObjectInfo");
		}
		((DatabaseObjectInfoComponent)getComponent()).load(session, doi);
	}

	/**
	 * Component for this tab.
	 */
	public class DatabaseObjectInfoComponent extends JPanel
	{
		private JavabeanDataSet _ds;
		private IDataSetViewer _viewer;

		DatabaseObjectInfoComponent(ISession session)
		{
			super(new BorderLayout());
			String destClassName = session.getProperties().getMetaDataOutputClassName();
			_viewer = BaseDataSetViewerDestination.getInstance(destClassName);
			_ds = new JavabeanDataSet();
			Runnable run = new Runnable()
			{
				public void run()
				{
					add(new JScrollPane(_viewer.getComponent()), BorderLayout.CENTER);
				}
			};
			SwingUtilities.invokeLater(run);
		}

		void clear()
		{
			if (_viewer != null)
			{
				_viewer.clear();
			}
		}

		void load(final ISession session, final IDatabaseObjectInfo doi)
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							_ds.setJavabean(doi);
							_viewer.show(_ds);
						}
						catch (DataSetException ex)
						{
							s_log.error("Error", ex);
							session.getMessageHandler().showErrorMessage(ex);
						}
					}
				});
			}
			catch (Exception ex)
			{
				s_log.error("Error", ex);
				session.getMessageHandler().showErrorMessage(ex);
			}
		}
	}
}