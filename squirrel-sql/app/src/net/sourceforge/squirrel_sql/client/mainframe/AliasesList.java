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
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.sourceforge.squirrel_sql.fw.gui.ModifiedDefaultListCellRenderer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This is a <TT>JList</TT> that displays all the <TT>ISQLAlias</TT>
 * objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesList extends JList {
	/** Application API. */
	private final IApplication _app;

	/** Model for this component. */
	private final AliasesListModel _model;

	public AliasesList(IApplication app) {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_model = new AliasesListModel(_app);
		setModel(_model);
		setLayout(new BorderLayout());
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setCellRenderer(new ModifiedDefaultListCellRenderer());

		// Select first item in list.
		if (getModel().getSize() > 0) {
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
	 * Return the <TT>AliasesListModel</TT> that controls this list.
	 */
	public AliasesListModel getTypedModel() {
		return _model;
	}

	/**
	 * Return the <TT>ISQLAlias</TT> that is currently selected.
	 */
	public ISQLAlias getSelectedAlias() {
		return (ISQLAlias)getSelectedValue();
	}

	/**
	 * Return the description for the alias that the mouse is currently
	 * over as the tooltip text.
	 *
	 * @param   event   Used to determine the current mouse position.
	 */
	public String getToolTipText(MouseEvent evt) {
		String tip = null;
		final int idx = locationToIndex(evt.getPoint());
		if (idx != -1) {
			tip = ((ISQLAlias)getModel().getElementAt(idx)).getName();
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
		return "List of database aliases that can be connected to"; // i18n
	}
}
