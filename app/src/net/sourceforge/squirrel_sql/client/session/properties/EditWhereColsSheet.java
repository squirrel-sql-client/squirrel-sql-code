package net.sourceforge.squirrel_sql.client.session.properties;

/**
 * @author gwg
*
 * Adapted from SQLFilterSheet.java by Maury Hammel.
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
/**
 * Edit Where Cols dialog gui.
 *
 */
public class EditWhereColsSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		/** Title  */
		String TITLE = "Edit 'WHERE' columns";
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(EditWhereColsSheet.class);

	/** A reference to the current SQuirreL session */
	private ISession _session;

	/** A reference to a class containing information about the database metadata. */
	private IDatabaseObjectInfo _objectInfo;

	/** A list of panels that make up this sheet. */
	private List _panels = new ArrayList();

	/** A variable that contains a value that indicates which tab currently has focus. */
	private int _tabSelected;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	/** A button used to trigger the clearing of the information. */
	private JButton _clearFilter = new JButton();

	/** A reference to a panel for the Where Clause list. */
	private EditWhereColsPanel _editWhereColsPanel = null;


	/**
	 * Creates a new instance of SQLFilterSheet
	 *
	 * @param	session		A reference to the current SQuirreL session
	 * @param	objectInfo	An instance of a class containing database metadata
	 * 						information.
	 */
	public EditWhereColsSheet(ISession session, IDatabaseObjectInfo objectInfo)
	{
		super(i18n.TITLE, true);
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}
		_session = session;
		_objectInfo = objectInfo;
		createGUI();
	}

	/**
	 * Position and display the sheet.
	 *
	 * @param	show	A boolean that determines whether the sheet is shown
	 * 					or hidden.
	 */
	public synchronized void setVisible(boolean show)
	{
		if (show)
		{
			if (!isVisible())
			{
				final boolean isDebug = s_log.isDebugEnabled();
				long start = 0;
				for (Iterator it = _panels.iterator(); it.hasNext();)
				{
					EditWhereColsPanel pnl = (EditWhereColsPanel)it.next();
					if (isDebug)
					{
						start = System.currentTimeMillis();
					}
					pnl.initialize(_session);
					if (isDebug)
					{
						s_log.debug(
							"Panel "
								+ pnl.getTitle()
								+ " initialized in "
								+ (System.currentTimeMillis() - start)
								+ "ms");
					}
				}
				pack();
				/*
				 * KLUDGE: For some reason, I am not able to get the sheet to
				 * size correctly. It always displays with a size that causes
				 * the sub-panels to have their scrollbars showing. Add a bit
				 * of an increase in the size of the panel so the scrollbars
				 * are not displayed.
				 */
				Dimension d = getSize();
				d.width += 5;
				d.height += 5;
				setSize(d);
				/*
				 * END-KLUDGE
				 */
				GUIUtils.centerWithinDesktop(this);
			}
			moveToFront();
		}
		super.setVisible(show);
	}

	/**
	 * Set title of this frame. Ensure that the title label matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title)
	{
		_titleLbl.setText(title + ": " + _objectInfo.getSimpleName());
	}

	/**
	 * Dispose of the sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * Get the current SQuirreL session.
	 *
	 * @return	A reference to the current SQuirreL session
	 */
	public ISession getSession()
	{
		return _session;
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _objectInfo;
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model and
	 * then close dialog.
	 */
	private void performOk()
	{
		
		//?????????????????????????????????????????

		dispose();
	}

	/**
	 * Create the GUI elements for the sheet.
	 */
	private void createGUI()
	{
		SortedSet columnNames = new TreeSet();
		Map textColumns = new TreeMap();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(getTitle());

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);
/*********************************************************************
		try
		{
			SQLConnection sqlConnection = _session.getSQLConnection();
			ResultSet rs =
				sqlConnection.getSQLMetaData().getColumns((ITableInfo)_objectInfo);
			while (rs.next())
			{
				columnNames.add(rs.getString("COLUMN_NAME"));
				int dataType = rs.getInt("DATA_TYPE");

				if ((dataType == Types.CHAR)
					|| (dataType == Types.CLOB)
					|| (dataType == Types.LONGVARCHAR)
					|| (dataType == Types.VARCHAR))
				{
					textColumns.put(
						rs.getString("COLUMN_NAME"),
						new Boolean(true));
				}
			}
		}
		catch (SQLException ex)
		{
			_session.getApplication().showErrorDialog(
				"Unable to get list of columns, " + ex);
		}

		_editWhereColsPanel =
			new EditWhereColsPanel(columnNames, textColumns, _objectInfo.getQualifiedName());
		_panels.add(_editWhereColsPanel);

		JTabbedPane tabPane = UIFactory.getInstance().createTabbedPane();
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			EditWhereColsPanel pnl = (EditWhereColsPanel)it.next();
			String pnlTitle = pnl.getTitle();
			String hint = pnl.getHint();
			final JScrollPane sp = new JScrollPane(pnl.getPanelComponent());
			sp.setBorder(BorderFactory.createEmptyBorder());
			tabPane.addTab(pnlTitle, null, sp, hint);
		}

		tabPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				_clearFilter.setText("Clear ");
			}
		});
*******************************************/
		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = GridBagConstraints.REMAINDER;
		_clearFilter.setText("Clear ");
		_tabSelected = 0;
		_clearFilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearFilter();
			}
		});
//??		contentPane.add(_clearFilter);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1;
//?????		contentPane.add(tabPane, gbc);
contentPane.add(new JLabel("This panel not yet implemented."), gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

	/**
	 * Clear out the SQL Filter information for the appropriate tab.
	 */
	private void clearFilter()
	{
		_editWhereColsPanel.clearFilter();
	}

	/**
	 * Create a panel that contains the buttons that control the closing
	 * of the sheet.
	 *
	 * @return An instance of a JPanel.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}


}

