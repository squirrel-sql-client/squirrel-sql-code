package net.sourceforge.squirrel_sql.client.session.properties;
/**
 * Copyright (C) 2003-2004 Glenn Griffin
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Edit Where Cols dialog gui.
 * JASON: Rename to EditWhereColsInternalFrame 
 */
@SuppressWarnings("serial")
public class EditWhereColsSheet extends BaseSessionInternalFrame
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EditWhereColsSheet.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		/** Title  */
		// i18n[editWhereColsSheet.editWhereColumns=Edit 'WHERE' columns]
		String TITLE = s_stringMgr.getString("editWhereColsSheet.editWhereColumns");
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(EditWhereColsSheet.class);

	/** A reference to a class containing information about the database metadata. */
	private IDatabaseObjectInfo _objectInfo;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	/** A reference to a panel for the EditWhereCols list. */
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
		super(session, i18n.TITLE, true);
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}

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

				if (isDebug)
				{
					start = System.currentTimeMillis();
				}

				if (isDebug)
				{
					s_log.debug("Panel " + _editWhereColsPanel.getTitle()
						+ " initialized in "
						+ (System.currentTimeMillis() - start) + "ms");
				}

				pack();
				/*
				 * TODO: Work out why
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

//	/**
//	 * Set title of this frame. Ensure that the title label matches the frame title.
//	 *
//	 * @param	title	New title text.
//	 */
//	public void setTitle(String title)
//	{
//      super
//	}

	/**
	 * Dispose of the sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _objectInfo;
	}

	/**
	 * Reset button pressed.  Reset the data to the way it was when we started
	 * this round of editing.
	 *
	 */
	private void performReset()
	{
		_editWhereColsPanel.reset();
	}

	/**
	 * OK button pressed. Save data to EditWhereCols repository
	 * then close dialog.
	 */
	private void performOk()
	{
		// try to save the selection.
		// do not dispose of this panel if there is a problem
		if (_editWhereColsPanel.ok())
			dispose();
	}

	/**
	 * Create the GUI elements for the sheet and pass in the setup data to the panel.
	 */
	private void createGUI()
	{
		SortedSet<String> columnNames = new TreeSet<String>();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final ISession session = getSession();

		try
		{
			final ISQLConnection conn = session.getSQLConnection();
				TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo((ITableInfo)_objectInfo);
				for (int i = 0; i < infos.length; i++) {
					 TableColumnInfo info = infos[i];
					 columnNames.add(info.getColumnName());
				}
		}
		catch (SQLException ex)
		{
			session.getApplication().showErrorDialog(
				// i18n[editWhereColsSheet.unableToEdit=Unable to get list of columns, {0}]
				s_stringMgr.getString("editWhereColsSheet.unableToEdit", ex));
		}
		String unambiguousname = 
            ContentsTab.getUnambiguousTableName(session, 
                                                _objectInfo.getQualifiedName());
		_editWhereColsPanel =
			new EditWhereColsPanel(session,
                                   (ITableInfo)_objectInfo,
                                   columnNames,  
                                   unambiguousname);

		final JScrollPane sp = new JScrollPane(_editWhereColsPanel);
		sp.setBorder(BorderFactory.createEmptyBorder());

		_titleLbl.setText(getTitle() + ": " + _objectInfo.getSimpleName());


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

		// leave a blank line just to make it look a bit nicer
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(
			// i18n[editWhereColsSheet.limitSizeOfWhereClause=Limit the size of the WHERE clause used behind the scenes when editing cell contents.]
			new JLabel(s_stringMgr.getString("editWhereColsSheet.limitSizeOfWhereClause")), gbc);
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(
			// i18n[editWhereColsSheet.shouldIncludePKs=The 'use' window should include at least the primary keys for the table.]
			new JLabel(s_stringMgr.getString("editWhereColsSheet.shouldIncludePKs")), gbc);

		// leave a blank line just to make it look a bit nicer
		gbc.gridx = 0;
		++gbc.gridy;
		contentPane.add(new JLabel(" "), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(_editWhereColsPanel, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
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

		// i18n[editWherColsSheet.ok=OK]
		JButton okBtn = new JButton(s_stringMgr.getString("editWherColsSheet.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		// i18n[editWherColsSheet.reset=Reset]
		JButton resetBtn = new JButton(s_stringMgr.getString("editWherColsSheet.reset"));
		resetBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performReset();
			}
		});
		// i18n[editWherColsSheet.close=Close]
		JButton closeBtn = new JButton(s_stringMgr.getString("editWherColsSheet.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(resetBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, resetBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}
