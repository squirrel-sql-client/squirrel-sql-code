package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
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
public class DriverMaintSheet extends BaseSheet {
	/** Different types of maintenance that can be done. */
	public interface MaintenanceType {
		int NEW = 1;
		int MODIFY = 2;
		int COPY = 3;
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DriverMaintSheet.class);

	private static final int COLUMN_COUNT = 25;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String ADD = "Add Driver";
		String CHANGE = "Change Driver";
		String DRIVER = "Class Name:";
		String JAR_FILE = "JAR File:";
		String LOAD_WHERE = "Load from CLASSPATH:";
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

	/** Control for the <TT>ISQLDriver.IPropertyNames.USES_CLASSPATH</TT> property. */
	private JCheckBox _usesClassPathChk = new JCheckBox(i18n.LOAD_WHERE);

	/** Control for the <TT>ISQLDriver.IPropertyNames.JARFILE_NAME</TT> property. */
	private JTextField _jarFileName = new JTextField();

	/** Control for the <TT>ISQLDriver.IPropertyNames.DRIVER_CLASS</TT> property. */
	private JComboBox _driverClassCmb = new JComboBox();

	/** Control for the <TT>ISQLDriver.IPropertyNames.URL</TT> property. */
	private JTextField _url = new JTextField();

	/** Button allows searching for a JDBC jar file. */
	private JButton _searchBtn = new JButton("...");

	/**
	 * Used for comparison purposes to see if the selected JDBC driver
	 * jar file has been changed.
	 */
	private String _currentJarFileText = "";

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
	DriverMaintSheet(IApplication app, ISQLDriver sqlDriver, int maintType) {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlDriver == null) {
			throw new IllegalArgumentException("Null ISQLDriver passed");
		}
		if (maintType < MaintenanceType.NEW || maintType > MaintenanceType.COPY) {
			throw new IllegalArgumentException("Illegal value of " + maintType + " passed for Maintenance type");
		}

		_app = app;
		_sqlDriver = sqlDriver;
		_maintType = maintType;

		createUserInterface();
		loadData();
		pack();
	}

	/**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title) {
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Return the driver that is being maintained.
	 *
	 * @return	the driver that is being maintained.
	 */
	ISQLDriver getSQLDriver() {
		return _sqlDriver;
	}

	/**
	 * Load data from the JDBC driver definition into the maintenance controls.
	 */
	private void loadData() {
		_driverName.setText(_sqlDriver.getName());
		_usesClassPathChk.setSelected(_sqlDriver.getUsesClassPath());
		setJarFileName(_sqlDriver.getJarFileName());
		_driverClassCmb.setSelectedItem(_sqlDriver.getDriverClassName());
		_url.setText(_sqlDriver.getUrl());
	}

	/**
	 * User has requested close so get rid of this maintenance dialog.
	 */
	private void performClose() {
		dispose();
	}

	/**
	 * OK button pressed. Edit data and if ok save to drivers model
	 * and then close dialog.
	 */
	private void performOk() {
		try {
			applyFromDialog();
			if (_maintType == MaintenanceType.NEW ||
					_maintType == MaintenanceType.COPY) {
				_app.getDataCache().addDriver(_sqlDriver);
			}
			dispose();
		} catch(Exception ex) {
			displayErrorMessage(ex);
		}
	}

	/**
	 * Apply data from the data entry controls to the JDBC driver definition.
	 */
	private void applyFromDialog() throws ValidationException {
		_sqlDriver.setName(_driverName.getText().trim());
		_sqlDriver.setUsesClassPath(_usesClassPathChk.isSelected());
		_sqlDriver.setJarFileName(_jarFileName.getText().trim());
		_sqlDriver.setDriverClassName(((String)_driverClassCmb.getSelectedItem()).trim());
		_sqlDriver.setUrl(_url.getText().trim());
	}

	/**
	 * Retrieve the class names of all JDBC drivers in the currently specified
	 * jar file into the Drivers combo box.
	 */
	private void loadDriversCombo() {
		_driverClassCmb.removeAllItems();
		if (!_usesClassPathChk.isSelected()) {
			try {
				File file = new File(_jarFileName.getText().trim());
				SQLDriverClassLoader cl = new SQLDriverClassLoader(file.toURL());
				Class[] classes = cl.getDriverClasses(s_log);
				for (int i = 0; i < classes.length; ++i) {
					_driverClassCmb.addItem(classes[i].getName());
				}
			} catch (MalformedURLException ex) {
				displayErrorMessage(ex);
			} catch (IOException ex) {
				displayErrorMessage(ex);
			}
		}
	}

	private void setJarFileName(String fileName) {
		if (fileName != null && !_currentJarFileText.equals(fileName)) {
			_jarFileName.setText(fileName);
			loadDriversCombo();
			_currentJarFileText = fileName;
		}
	}

	/**
	 * Display an error msg in a dialog. Uses
	 * <TT>SwingUtilities.invokeLater()</TT> because this may be called
	 * before the main dialog is displayed.
	 *
	 * @param   ex	  The <TT>Exception</TT> containing the error
	 *				  message.
	 */
	private void displayErrorMessage(final Exception ex) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_app.showErrorDialog(ex);
			}
		});
	}

	private void createUserInterface() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final String title = _maintType == MaintenanceType.MODIFY
										? (i18n.CHANGE + " " + _sqlDriver.getName())
										: i18n.ADD;
		setTitle(title);

		_driverName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);
		_jarFileName.setColumns(COLUMN_COUNT);

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = gbc.weighty = 1;

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
		++gbc.gridy;
		contentPane.add(createLocationPanel(), gbc);

		// Separated by a line.
		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);
	}

	private JPanel createButtonsPanel() {
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performOk();
			}
		});
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] {okBtn, closeBtn});
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private JPanel createDriverPanel() {
		_driverName.setColumns(25);

		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createTitledBorder("Driver"));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new RightLabel(i18n.NAME), gbc);

		++gbc.gridy;
		pnl.add(new RightLabel(i18n.URL), gbc);

		gbc.weightx = 1.0;
		gbc.gridy = 0;
		++gbc.gridx;
		pnl.add(_driverName, gbc);

		++gbc.gridy;
		pnl.add(_url, gbc);

		return pnl;
	}

	private JPanel createLocationPanel() {
		_driverClassCmb.setEditable(true);

		// Set height of search button to be the same as its edit
		// entry control.
		final Dimension _jarFileNamePs = _jarFileName.getPreferredSize();
		Dimension ps = _searchBtn.getPreferredSize();
		ps.height = _jarFileNamePs.height;
		ps.width = ps.height;
		_searchBtn.setPreferredSize(ps);

		_searchBtn.setVerticalTextPosition(_searchBtn.CENTER);
		_searchBtn.setHorizontalTextPosition(_searchBtn.CENTER);

		// If the Uses Class Path check box is selected then the user cannot
		// enter the name of a jar file.
		_usesClassPathChk.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				boolean allowJarFileEntry = !_usesClassPathChk.isSelected();
				_jarFileName.setEnabled(allowJarFileEntry);
				_searchBtn.setEnabled(allowJarFileEntry);
			}
		});

		// Clicking on the search button displays a FileChooser allowing user
		// to select a JAR file containing a JDBC driver.
		_searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JFileChooser chooser = new JFileChooser(_jarFileName.getText().trim());
				chooser.addChoosableFileFilter(
					new FileExtensionFilter("JAR files", new String[] {".jar", ".zip"}));
				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = chooser.getSelectedFile();
					if (selFile != null) {
						setJarFileName(selFile.getAbsolutePath());
					}
				}
			}
		});

		// When focus is lost from the jar file name text box then we need to load
		// information about the specified jar file.
		_jarFileName.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent evt) {
			}
			public void focusLost(FocusEvent evt) {
				setJarFileName(_jarFileName.getText().trim());
			}
		});

		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createTitledBorder("Driver Location"));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 1;
		pnl.add(new RightLabel(i18n.JAR_FILE), gbc);

		++gbc.gridy;
		pnl.add(new RightLabel(i18n.DRIVER), gbc);

		gbc.weightx = 1.0;
		gbc.gridy = 0;
		++gbc.gridx;
		pnl.add(_usesClassPathChk, gbc);

		++gbc.gridy;
		pnl.add(_jarFileName, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		pnl.add(_driverClassCmb, gbc);

		gbc.weightx = 0;
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		++gbc.gridx;
		pnl.add(_searchBtn, gbc);

		return pnl;
	}

	private final static class RightLabel extends JLabel {
		RightLabel(String title) {
			super(title, SwingConstants.RIGHT);
		}
	}
}
