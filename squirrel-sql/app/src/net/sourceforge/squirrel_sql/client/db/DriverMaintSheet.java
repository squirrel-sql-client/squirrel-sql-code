package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.gui.DefaultFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.FileListBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverClassLoader;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;

/**
 * This dialog allows maintenance of a JDBC driver definition.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverMaintSheet extends BaseSheet
{
	/** Different types of maintenance that can be done. */
	public interface MaintenanceType
	{
		int NEW = 1;
		int MODIFY = 2;
		int COPY = 3;
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DriverMaintSheet.class);

	/** Number of caracters to display in D/E fields. */
	private static final int COLUMN_COUNT = 25;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface DriverMaintSheetI18n
	{
		String ADD = "Add Driver";
		String CHANGE = "Change Driver";
		String DRIVER = "Class Name:";
		String NAME = "Name:";
		String URL = "Example URL:";
	}

	/** Application API. */
	private IApplication _app;

	/** JDBC driver being maintained. */
	private ISQLDriver _sqlDriver;

	/** Type of maintenance being done. @see MaintenanceType. */
	private int _maintType;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	/** Control for the <TT>ISQLDriver.IPropertyNames.NAME</TT> property. */
	private JTextField _driverName = new JTextField();

	/** Control for the <TT>ISQLDriver.IPropertyNames.DRIVER_CLASS</TT> property. */
	private JComboBox _driverClassCmb = new JComboBox();

	/** Control for the <TT>ISQLDriver.IPropertyNames.URL</TT> property. */
	private JTextField _url = new JTextField();

	/** Listbox containing the Java class path. */
	private FileListBox _javaClassPathList = new FileListBox();

	/** Listbox containing the extra class path. */
	private FileListBox _extraClassPathList = new FileListBox(new DefaultFileListBoxModel());

	/** Button to list drivers in a jar within the Java Class path list. */
	private ListDriversButton _javaClasspathListDriversBtn;

	/** Button to list drivers in a jar within the Extra Class path list. */
	private ListDriversButton _extraClasspathListDriversBtn;

	/** Button to delete entry from Extra Class path list. */
	private JButton _extraClasspathDeleteBtn;

	/** Button to move entry up in Extra Class path list. */
	private JButton _extraClasspathUpBtn;

	/** Button to move entry down in Extra Class path list. */
	private JButton _extraClasspathDownBtn;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	sqlDriver	JDBC driver definition to be maintained.
	 * @param	maintType	Maintenance type. @see MaintenanceType.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> passed for <TT>app</TT> or <TT>sqlDriver</TT> or
	 * 			an invalid value passed for <TT>maintType</TT>.
	 */
	DriverMaintSheet(IApplication app, ISQLDriver sqlDriver, int maintType)
	{
		super("", true);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("Null ISQLDriver passed");
		}
		if (maintType < MaintenanceType.NEW || maintType > MaintenanceType.COPY)
		{
			throw new IllegalArgumentException(
				"Illegal value of " + maintType + " passed for Maintenance type");
		}

		_app = app;
		_sqlDriver = sqlDriver;
		_maintType = maintType;

		createGUI();
		loadData();
		pack();
	}

	/**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Return the driver that is being maintained.
	 *
	 * @return	the driver that is being maintained.
	 */
	ISQLDriver getSQLDriver()
	{
		return _sqlDriver;
	}

	/**
	 * Load data from the JDBC driver definition into the maintenance controls.
	 */
	private void loadData()
	{
		_driverName.setText(_sqlDriver.getName());
		_driverClassCmb.setSelectedItem(_sqlDriver.getDriverClassName());
		_url.setText(_sqlDriver.getUrl());

		_extraClassPathList.removeAll();
		String[] fileNames = _sqlDriver.getJarFileNames();
		IFileListBoxModel model = _extraClassPathList.getTypedModel();
		for (int i = 0; i < fileNames.length; ++i)
		{
			model.addFile(new File(fileNames[i]));
		}

		if (model.getSize() > 0)
		{
			_extraClassPathList.setSelectedIndex(0);
		}

	}

	/**
	 * User has requested close so get rid of this maintenance dialog.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed. Edit data and if ok save to drivers model
	 * and then close dialog.
	 */
	private void performOk()
	{
		try
		{
			applyFromDialog();
			if (_maintType == MaintenanceType.NEW || _maintType == MaintenanceType.COPY)
			{
				_app.getDataCache().addDriver(_sqlDriver);
			}
			dispose();
		}
		catch (Exception ex)
		{
			displayErrorMessage(ex);
		}
	}

	/**
	 * Apply data from the data entry controls to the JDBC driver definition.
	 */
	private void applyFromDialog() throws ValidationException
	{
		_sqlDriver.setName(_driverName.getText().trim());
		_sqlDriver.setJarFileNames(_extraClassPathList.getTypedModel().getFileNames());

		String driverClassName = (String)_driverClassCmb.getSelectedItem();
		_sqlDriver.setDriverClassName(driverClassName != null ? driverClassName.trim() : null);

		_sqlDriver.setUrl(_url.getText().trim());
	}

	/**
	 * Display an error msg in a dialog. Uses
	 * <TT>SwingUtilities.invokeLater()</TT> because this may be called
	 * before the main dialog is displayed.
	 *
	 * @param   ex	  The <TT>Exception</TT> containing the error
	 *				  message.
	 */
	private void displayErrorMessage(final Exception ex)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_app.showErrorDialog(ex);
			}
		});
	}

	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final String title =
			_maintType == MaintenanceType.MODIFY
				? (DriverMaintSheetI18n.CHANGE + " " + _sqlDriver.getName())
				: DriverMaintSheetI18n.ADD;
		setTitle(title);

		_driverName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);

		// Reset the background the the current Look and Feel uses for
		// internal frames.
		Container contentPane = getContentPane();
		Color color = UIManager.getDefaults().getColor("Panel.background");
		if (color != null)
		{
			contentPane.setBackground(color);
		}

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

		// Title label at top.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(_titleLbl, gbc);

		// Separated by a line.
		++gbc.gridy;
		gbc.insets = new Insets(0, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		++gbc.gridy;
		contentPane.add(createDriverPanel(), gbc);

		JTabbedPane tabPnl = new JTabbedPane();
		tabPnl.addTab("Java Class Path", createJavaClassPathPanel());
		tabPnl.addTab("Extra Class Path", createExtraClassPathPanel());

		++gbc.gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		contentPane.add(tabPnl, gbc);

		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		contentPane.add(createDriverClassPanel(), gbc);

		// Separated by a line.
		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);
	}

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

	private JPanel createDriverPanel()
	{
		_driverName.setColumns(25);

		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Driver"));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(DriverMaintSheetI18n.NAME, SwingConstants.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel(DriverMaintSheetI18n.URL, SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		gbc.gridy = 0;
		++gbc.gridx;
		pnl.add(_driverName, gbc);

		++gbc.gridy;
		pnl.add(_url, gbc);

		return pnl;
	}

	private Component createDriverClassPanel()
	{
		_driverClassCmb.setEditable(true);

		JPanel pnl = new JPanel(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(DriverMaintSheetI18n.DRIVER, SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		++gbc.gridx;
		pnl.add(_driverClassCmb, gbc);

		return pnl;
	}

	/**
	 * Create the panel that displays the current class path.
	 * 
	 * @return	Panel that displays the current class path.
	 */
	private JPanel createJavaClassPathPanel()
	{
		_javaClasspathListDriversBtn = new ListDriversButton(_javaClassPathList);
		_javaClassPathList.addListSelectionListener(new JavaClassPathListBoxListener());

		IFileListBoxModel model = _javaClassPathList.getTypedModel();
		if (model.getSize() > 0)
		{
			_javaClassPathList.setSelectedIndex(0);
		}

		JPanel pnl = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = gbc.WEST;
		gbc.weighty = 1.0;

		// Scrollbars are "shown always" to stop sheet resizing when they
		// are shown/hidden.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = gbc.REMAINDER;
		gbc.fill = gbc.BOTH;
		gbc.weightx = 1.0;
		pnl.add(new JScrollPane(_javaClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);

		++gbc.gridx;
		gbc.gridheight = 1;
		gbc.fill = gbc.HORIZONTAL;
		gbc.anchor = gbc.NORTH;
		gbc.weightx = 0.0;
		pnl.add(_javaClasspathListDriversBtn, gbc);

		return pnl;
	}

	/**
	 * Create the panel that displays the extra class path.
	 * 
	 * @return	Panel that displays the extra class path.
	 */
	private JPanel createExtraClassPathPanel()
	{
		_extraClasspathListDriversBtn = new ListDriversButton(_extraClassPathList);
		_extraClassPathList.addListSelectionListener(new ExtraClassPathListBoxListener());

		_extraClasspathUpBtn = new JButton("Up");
		_extraClasspathUpBtn.setEnabled(false);
		_extraClasspathUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_extraClassPathList) {
					int idx = _extraClassPathList.getSelectedIndex();
					if (idx > 0) {
						IFileListBoxModel model = _extraClassPathList.getTypedModel();
						File file = model.removeFile(idx);
						--idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		_extraClasspathDownBtn = new JButton("Down");
		_extraClasspathDownBtn.setEnabled(false);
		_extraClasspathDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_extraClassPathList) {
					int idx = _extraClassPathList.getSelectedIndex();
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					if (idx > -1 && idx < (model.getSize() - 1)) {
						File file = model.removeFile(idx);
						++idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		JButton newBtn = new AddListEntryButton();

		_extraClasspathDeleteBtn = new JButton("Delete");
		_extraClasspathDeleteBtn.setEnabled(false);
		_extraClasspathDeleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int idx = _extraClassPathList.getSelectedIndex();
				if (idx != -1)
				{
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					model.removeFile(idx);
					final int size = model.getSize();
					if (idx < size) {
						_extraClassPathList.setSelectedIndex(idx);
					} else if (size > 0) {
						_extraClassPathList.setSelectedIndex(size - 1);
					}
				}
			}
		});

		JPanel pnl = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);

		// Scrollbars are "shown always" to stop sheet resizing when they
		// are shown/hidden.
		gbc.gridheight = gbc.REMAINDER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = gbc.BOTH;
		pnl.add(new JScrollPane(_extraClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);
		
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = gbc.HORIZONTAL;
		++gbc.gridx;
		pnl.add(_extraClasspathListDriversBtn, gbc);

		++gbc.gridy;
		gbc.insets = new Insets(5, 5, 5, 5);
		pnl.add(new JSeparator(), gbc);
		gbc.insets = new Insets(4, 4, 4, 4);

		++gbc.gridy;
		pnl.add(_extraClasspathUpBtn, gbc);

		++gbc.gridy;
		pnl.add(_extraClasspathDownBtn, gbc);

		++gbc.gridy;
		gbc.insets = new Insets(5, 5, 5, 5);
		pnl.add(new JSeparator(), gbc);
		gbc.insets = new Insets(4, 4, 4, 4);

		++gbc.gridy;
		pnl.add(newBtn, gbc);

		++gbc.gridy;
		pnl.add(_extraClasspathDeleteBtn, gbc);

		return pnl;
	}

	/**
	 * Button that allows user to enter new items in the Extra Class Path
	 * list.
	 */
	private final class AddListEntryButton extends JButton implements ActionListener
	{
		private JFileChooser _chooser;

		AddListEntryButton()
		{
			super("Add");
			addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			if (_chooser == null)
			{
				_chooser = new JFileChooser();
				_chooser.setMultiSelectionEnabled(true);
				_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				_chooser.addChoosableFileFilter(
						new FileExtensionFilter("JAR files", new String[] { ".jar", ".zip" }));
			}
			int returnVal = _chooser.showOpenDialog(getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File[] selFiles = _chooser.getSelectedFiles();
				if (selFiles != null)
				{
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					for (int i = 0; i < selFiles.length; ++i)
					{
						model.addFile(selFiles[i]);
					}
					_extraClassPathList.setSelectedIndex(model.getSize() - 1);
				}
			}
		}
	}

	/**
	 * Button that will list all the drivers in the file current selected
	 * in a listbox.
	 */
	private final class ListDriversButton extends JButton implements ActionListener
	{
		private FileListBox _listBox;
	
		ListDriversButton(FileListBox listBox)
		{
			super("List Drivers");
			setEnabled(false);
			_listBox = listBox;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			_driverClassCmb.removeAllItems();
			File file = _listBox.getSelectedFile();
			if (file != null)
			{
				try
				{
					SQLDriverClassLoader cl = new SQLDriverClassLoader(file.toURL());
					Class[] classes = cl.getDriverClasses(s_log);
					for (int i = 0; i < classes.length; ++i)
					{
						_driverClassCmb.addItem(classes[i].getName());
					}
				}
				catch (MalformedURLException ex)
				{
					displayErrorMessage(ex);
				}
				catch (IOException ex)
				{
					displayErrorMessage(ex);
				}
			}
			if (_driverClassCmb.getItemCount() > 0)
			{
				_driverClassCmb.setSelectedIndex(0);
			}
		}

	}

	private class JavaClassPathListBoxListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			final int selIdx = _javaClassPathList.getSelectedIndex();
			_javaClasspathListDriversBtn.setEnabled(selIdx != -1);
			boolean enable = false;
			if (selIdx != -1)
			{
				File file = _javaClassPathList.getSelectedFile();
				if (file != null)
				{
					enable = file.isFile();
				}
			}
			_javaClasspathListDriversBtn.setEnabled(enable);

		}
	}

	private class ExtraClassPathListBoxListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			final int selIdx = _extraClassPathList.getSelectedIndex();
			final ListModel model = _extraClassPathList.getModel();

			_extraClasspathDeleteBtn.setEnabled(selIdx != -1);

			_extraClasspathUpBtn.setEnabled(selIdx > 0 && model.getSize() > 1);
			_extraClasspathDownBtn.setEnabled(selIdx > -1 && selIdx < (model.getSize() - 1));

			boolean enable = false;
			if (selIdx != -1)
			{
				File file = _extraClassPathList.getSelectedFile();
				if (file != null)
				{
					enable = file.isFile();
				}
			}
			_extraClasspathListDriversBtn.setEnabled(enable);
		}
	}
}