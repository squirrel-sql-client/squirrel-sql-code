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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
/**
 * This panel allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesPanel extends JPanel
{
	private interface i18n
	{
//		String CLOSE = "Close";
		String INSTRUCTIONS = "For every driver property that you want to " +
								"specify check the \"Specify\" checkbox " +
								"and enter its value in the \"Value\" column. " +
								"Normally you won't use the \"user\" and " +
								"\"password\" properties as these will be setup " +
								"from the \"user\" and \"password\" entered " +
								"in the connection dialog.";
//		String OK = "OK";
	}

	/** Listeners for this object. */
//	private EventListenerList _listenerList = new EventListenerList();

	/** JTable containing the properties. */
	private DriverPropertiesTable _tbl;

	/** The OK button. */
//	private JButton _okBtn;

	/**
	 * Display the description for the currently selected property in this
	 * control.
	 */
	private final MultipleLineLabel _descriptionLbl = new MultipleLineLabel();

	public DriverPropertiesPanel(SQLDriverPropertyCollection props)
	{
		super(new GridBagLayout());
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	/**
	 * Adds a listener for actions in this panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> that
	 * 				will be notified when actions are performed
	 * 				in this panel.
	 */
//	public synchronized void addListener(IDriverPropertiesPanelListener lis)
//	{
//		_listenerList.add(IDriverPropertiesPanelListener.class, lis);
//	}

	/**
	 * Removes a listener from this panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> to
	 * 				be removed.
	 */
//	public synchronized void removeListener(IDriverPropertiesPanelListener lis)
//	{
//		_listenerList.remove(IDriverPropertiesPanelListener.class, lis);
//	}

	/**
	 * Retrieve the database properties.
	 *
	 * @return		the database properties.
	 */
	public SQLDriverPropertyCollection getSQLDriverProperties()
	{
		return _tbl.getTypedModel().getSQLDriverProperties();
	}

//	private void fireButtonPressed(JButton btn)
//	{
//		// Guaranteed to be non-null.
//		Object[] listeners = _listenerList.getListenerList();
//		// Process the listeners last to first, notifying
//		// those that are interested in this event.
//		EventObject evt = null;
//		for (int i = listeners.length - 2; i >= 0; i-=2 )
//		{
//			if (listeners[i] == IDriverPropertiesPanelListener.class)
//			{
//				// Lazily create the event:
//				if (evt == null)
//				{
//					evt = new EventObject(this);
//				}
//				IDriverPropertiesPanelListener lis = (IDriverPropertiesPanelListener)listeners[i + 1];
//				if (btn == _okBtn)
//				{
//					lis.okPressed(evt);
//				}
//				else
//				{
//					lis.closePressed(evt);
//				}
//			}
//		}
//	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		_tbl = new DriverPropertiesTable(props);

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;

		gbc.gridx = gbc.gridy = 0;
		gbc.weighty = 1.0;
		JScrollPane sp = new JScrollPane(_tbl);
		add(sp, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		++gbc.gridy;
		add(createInfoPanel(), gbc);

//		++gbc.gridy;
//		add(createButtonsPanel(), gbc);

		_tbl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				updateDescription(_tbl.getSelectedRow());
			}
		});

		if (_tbl.getRowCount() > 0)
		{
			_tbl.setRowSelectionInterval(0, 0);
		}
	}

	private void updateDescription(int idx)
	{
		if (idx != -1)
		{
			String desc = (String)_tbl.getValueAt(idx, DriverPropertiesTableModel.IColumnIndexes.IDX_DESCRIPTION);
			_descriptionLbl.setText(desc);
		}
		else
		{
			_descriptionLbl.setText(" ");
		}
	}

	private Box createInfoPanel()
	{
		final Box pnl = Box.createVerticalBox();
		pnl.add(new JSeparator());
		pnl.add(_descriptionLbl);
		pnl.add(new JSeparator());
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));
		
		return pnl;
	}

//	private JPanel createButtonsPanel()
//	{
//		final JPanel pnl = new JPanel();
//
//		_okBtn = new JButton(i18n.OK);
//		_okBtn.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				fireButtonPressed(_okBtn);
//			}
//		});
//
//		final JButton closeBtn = new JButton(i18n.CLOSE);
//		closeBtn.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				fireButtonPressed(closeBtn);
//			}
//		});
//
//		pnl.add(_okBtn);
//		pnl.add(closeBtn);
//
//		GUIUtils.setJButtonSizesTheSame(new JButton[] {_okBtn, closeBtn});
//
//		return pnl;
//	}
}

