package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

/**
 * This is a <CODE>JList</CODE> that dispays all the <CODE>ISQLDriver</CODE>
 * objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriversList extends JList {
	/** Application API. */
	private IApplication _app;

	/** Model for this component. */
	private DriversListModel _model;

	public DriversList(IApplication app) throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_model = new DriversListModel(_app);
		setModel(_model);
		setLayout(new BorderLayout());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		SquirrelResources res = _app.getResources();
		setCellRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),res.getIcon("list.driver.notfound")));

		// Select first item in list.
		if (_model.getSize() > 0) {
			setSelectedIndex(0);
		}

		_model.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent evt) {
			}
			public void intervalAdded(ListDataEvent evt) {
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clearSelection();
						setSelectedIndex(idx);
					}
				});
			}
			public void intervalRemoved(ListDataEvent evt) {
				final int idx = evt.getIndex0();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						clearSelection();
						int size = getModel().getSize();
						if  (idx < size) {
							setSelectedIndex(idx);
						} else if (size > 0) {
							setSelectedIndex(size - 1);
						}
					}
				});
			}
		});
	}

	/**
	 * Component has been added to its parent.
	 */
	public void addNotify() {
		super.addNotify();
		// Register so that we can display different tooltips depending
		// which entry in list mouse is over.
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Component has been removed from its parent.
	 */
	public void removeNotify() {
		// Don't need tooltips any more.
		ToolTipManager.sharedInstance().unregisterComponent(this);
		super.removeNotify();
	}

	/**
	 * Return the <CODE>DriversListModel</CODE> that controls this list.
	 */
	public DriversListModel getTypedModel() {
		return _model;
	}

	/**
	 * Return the <CODE>ISQLDriver</CODE> that is currently selected.
	 */
	public ISQLDriver getSelectedDriver() {
		return (ISQLDriver)getSelectedValue();
	}

	/**
	 * Return the description for the driver that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param   event   Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt) {
		String tip = null;
		final int idx = locationToIndex(evt.getPoint());
		if (idx != -1) {
			tip = ((ISQLDriver)getModel().getElementAt(idx)).getName();
		} else {
			tip = getToolTipText();
		}
		return tip;
	}

	/**
	 * Return the tooltip used for this component if the mouse isn't over
	 * an entry in the list.
	 */
	public String getToolTipText() {
		return "List of database drivers that can be used to configure an alias"; //i18n
	}

}

