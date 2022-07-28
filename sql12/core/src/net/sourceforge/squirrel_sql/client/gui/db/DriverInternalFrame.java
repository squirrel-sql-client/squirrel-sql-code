package net.sourceforge.squirrel_sql.client.gui.db;

/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.gui.DefaultFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.FileListBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IFileListBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverClassLoader;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import static net.sourceforge.squirrel_sql.client.preferences.PreferenceType.DRIVER_DEFINITIONS;

/**
 * This dialog allows maintenance of a JDBC driver definition.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
public class DriverInternalFrame extends DialogWidget
{
	/** Different types of maintenance that can be done. */
	public interface MaintenanceType
	{
		int NEW = 1;

		int MODIFY = 2;

		int COPY = 3;
	}

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DriverInternalFrame.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(DriverInternalFrame.class);

	public static final String PREF_KEY_LAST_DRIVER_DIR = "DriverInternalFrame.PREF_KEY_LAST_DRIVER_DIR";

	/** Number of characters to display in D/E fields. */
	private static final int COLUMN_COUNT = 25;

	/** Width to make listboxes. */
	private static final int LIST_WIDTH = 400;

	/** Application API. */
	private final IApplication _app;

	/** JDBC driver being maintained. */
	private final ISQLDriver _sqlDriver;

	/** Type of maintenance being done. @see MaintenanceType. */
	private final int _maintType;

	/** Frame title. */
	private final JLabel _titleLbl = new JLabel();

	/** Control for the <TT>ISQLDriver.IPropertyNames.NAME</TT> property. */
	private final JTextField _driverName = new JTextField();

	/** Control for the <TT>ISQLDriver.IPropertyNames.DRIVER_CLASS</TT> property. */
	private final JComboBox _driverClassCmb = new JComboBox();

	/** Control for the <TT>ISQLDriver.IPropertyNames.URL</TT> property. */
	private final JTextField _url = new JTextField();

	private final JTextField _weburl = new JTextField();

	/** Listbox containing the Java class path. */
	private final FileListBox _javaClassPathList = new FileListBox();

	/** Listbox containing the extra class path. */
	private final FileListBox _extraClassPathList = new FileListBox(new DefaultFileListBoxModel());

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
	 * @param app
	 *           Application API.
	 * @param sqlDriver
	 *           JDBC driver definition to be maintained.
	 * @param maintType
	 *           Maintenance type. @see MaintenanceType.
	 * @throws IllegalArgumentException
	 *            Thrown if <TT>null</TT> passed for <TT>app</TT> or <TT>sqlDriver</TT> or an invalid value
	 *            passed for <TT>maintType</TT>.
	 */
	DriverInternalFrame(IApplication app, ISQLDriver sqlDriver, int maintType)
	{
		super("", true, app);
		if (app == null) { throw new IllegalArgumentException("Null IApplication passed"); }
		if (sqlDriver == null) { throw new IllegalArgumentException("Null ISQLDriver passed"); }
		if (maintType < MaintenanceType.NEW || maintType > MaintenanceType.COPY) { throw new IllegalArgumentException(
		// i18n[DriverInternalFrame.error.illegalvalue=Illegal value of {0} passed for Maintenance type]
			s_stringMgr.getString("DriverInternalFrame.error.illegalvalue", maintType)); }

		_app = app;
		_sqlDriver = sqlDriver;
		_maintType = maintType;

		createGUI();
		loadData();
		pack();
	}

	/**
	 * Set title of this frame. Ensure that the title label matches the frame title.
	 * 
	 * @param title
	 *           New title text.
	 */
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Return the driver that is being maintained.
	 * 
	 * @return the driver that is being maintained.
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
		_weburl.setText(_sqlDriver.getWebSiteUrl());

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
	 * OK button pressed. Edit data and if ok save to drivers model and then close dialog.
	 */
	private void performOk()
	{
		try
		{
			applyFromDialog();
			if (_maintType == MaintenanceType.NEW || _maintType == MaintenanceType.COPY)
			{
				_app.getAliasesAndDriversManager().addDriver(_sqlDriver, _app.getMessageHandler());
			}
			else
			{
				_app.getAliasesAndDriversManager().refreshDriver(_sqlDriver, _app.getMessageHandler());
			}

			_app.savePreferences(DRIVER_DEFINITIONS);
			dispose();
		}
		catch (Throwable th)
		{
			displayErrorMessage(th);
		}
	}

	/**
	 * Apply data from the data entry controls to the JDBC driver definition.
	 */
	private void applyFromDialog() throws ValidationException
	{
		_sqlDriver.setName(_driverName.getText().trim());
		_sqlDriver.setJarFileNames(_extraClassPathList.getTypedModel().getFileNames());

		String driverClassName = (String) _driverClassCmb.getSelectedItem();
		_sqlDriver.setDriverClassName(driverClassName != null ? driverClassName.trim() : null);

		_sqlDriver.setUrl(_url.getText().trim());
		_sqlDriver.setWebSiteUrl(_weburl.getText().trim());
	}

	/**
	 * Display an error msg in a dialog. Uses <TT>SwingUtilities.invokeLater()</TT> because this may be called
	 * before the main dialog is displayed.
	 * 
	 * @param th
	 *           The exception containing the error message.
	 */
	private void displayErrorMessage(final Throwable th)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_app.showErrorDialog(th);
			}
		});
	}

	private void createGUI()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// This is a tool window.
		makeToolWindow(true);

		String winTitle;
		if (_maintType == MaintenanceType.MODIFY)
		{
			winTitle = s_stringMgr.getString("DriverInternalFrame.changedriver", _sqlDriver.getName());
		}
		else
		{
			winTitle = s_stringMgr.getString("DriverInternalFrame.adddriver");
		}
		setTitle(winTitle);

		_driverName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);

		// Reset the background to the colour that the current Look
		// and Feel uses for internal frames.
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
		tabPnl.addTab(s_stringMgr.getString("DriverInternalFrame.jdbc.driver.classpath"), createJDBCDriverClasspathPanel());
		tabPnl.addTab(s_stringMgr.getString("DriverInternalFrame.squirrel.java.classpath"), createJavaClassPathPanel());

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

		GUIUtils.enableCloseByEscape(this);
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.close"));
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
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("DriverInternalFrame.driver")));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.name"), SwingConstants.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.egurl"), SwingConstants.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.weburl"), SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		gbc.gridy = 0;
		++gbc.gridx;
		pnl.add(_driverName, gbc);

		++gbc.gridy;
		pnl.add(_url, gbc);

		++gbc.gridy;
		pnl.add(_weburl, gbc);

		return pnl;
	}

	private Component createDriverClassPanel()
	{
		_driverClassCmb.setEditable(true);

		JPanel pnl = new JPanel(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(s_stringMgr.getString("DriverInternalFrame.classname"), SwingConstants.RIGHT), gbc);

		gbc.weightx = 1.0;
		++gbc.gridx;
		pnl.add(_driverClassCmb, gbc);

		return pnl;
	}

	/**
	 * Create the panel that displays the current class path.
	 * 
	 * @return Panel that displays the current class path.
	 */
	private JPanel createJavaClassPathPanel()
	{
		_javaClasspathListDriversBtn = new ListDriversButton(_javaClassPathList);
		_javaClasspathListDriversBtn.setEnabled(_javaClassPathList.getModel().getSize() > 0);
		// _javaClassPathList.addListSelectionListener(new JavaClassPathListBoxListener());

		IFileListBoxModel model = _javaClassPathList.getTypedModel();
		if (model.getSize() > 0)
		{
			_javaClassPathList.setSelectedIndex(0);
		}

		JPanel pnl = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 1.0;

		// Scrollbars are "shown always" to stop sheet resizing when they
		// are shown/hidden.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		JScrollPane sp =
			new JScrollPane(_javaClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		final Dimension dm = sp.getPreferredSize();
		dm.width = LIST_WIDTH; // Required otherwise it gets too wide.
		sp.setPreferredSize(dm);
		pnl.add(sp, gbc);

		++gbc.gridx;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.0;
		pnl.add(_javaClasspathListDriversBtn, gbc);

		return pnl;
	}

	/**
	 * Create the panel that displays the extra class path.
	 * 
	 * @return Panel that displays the extra class path.
	 */
	private JPanel createJDBCDriverClasspathPanel()
	{
		_extraClasspathListDriversBtn = new ListDriversButton(_extraClassPathList);
		_extraClassPathList.addListSelectionListener(new ExtraClassPathListBoxListener());
		_extraClassPathList.getModel().addListDataListener(new ExtraClassPathListDataListener());

		_extraClasspathUpBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.up"));
		_extraClasspathUpBtn.setEnabled(false);
		_extraClasspathUpBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_extraClassPathList)
				{
					int idx = _extraClassPathList.getSelectedIndex();
					if (idx > 0)
					{
						IFileListBoxModel model = _extraClassPathList.getTypedModel();
						File file = model.removeFile(idx);
						--idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		_extraClasspathDownBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.down"));
		_extraClasspathDownBtn.setEnabled(false);
		_extraClasspathDownBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_extraClassPathList)
				{
					int idx = _extraClassPathList.getSelectedIndex();
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					if (idx > -1 && idx < (model.getSize() - 1))
					{
						File file = model.removeFile(idx);
						++idx;
						model.insertFileAt(file, idx);
						_extraClassPathList.setSelectedIndex(idx);
					}
				}
			}
		});

		JButton newBtn = new AddListEntryButton();

		_extraClasspathDeleteBtn = new JButton(s_stringMgr.getString("DriverInternalFrame.delete"));
		_extraClasspathDeleteBtn.setEnabled(false);
		_extraClasspathDeleteBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int idx = _extraClassPathList.getSelectedIndex();
				if (idx != -1)
				{
					IFileListBoxModel model = _extraClassPathList.getTypedModel();
					model.removeFile(idx);
					final int size = model.getSize();
					if (idx < size)
					{
						_extraClassPathList.setSelectedIndex(idx);
					}
					else if (size > 0)
					{
						_extraClassPathList.setSelectedIndex(size - 1);
					}
				}
			}
		});

		JPanel pnl = new JPanel(new GridBagLayout());
		//pnl.setBorder(BorderFactory.createLineBorder(Color.RED));

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(new MultipleLineLabel(s_stringMgr.getString("DriverInternalFrame.jdbc.driver.classpath.description")), gbc);


		// Scrollbars are "shown always" to stop sheet resizing when they
		// are shown/hidden.
		gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,0), 0,0);
		JScrollPane sp = new JScrollPane(_extraClassPathList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// Required otherwise it gets too wide.
		GUIUtils.setPreferredWidth(sp, LIST_WIDTH);
		pnl.add(sp, gbc);

		gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
		pnl.add(createJDBCDiverClassPathButtonPanel(newBtn), gbc);

		return pnl;
	}

	private JPanel createJDBCDiverClassPathButtonPanel(JButton newBtn)
	{
		JPanel pnl = new JPanel(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(newBtn, gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(new JSeparator(), gbc);

		gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(_extraClasspathListDriversBtn, gbc);

		gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(new JSeparator(), gbc);

		gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(_extraClasspathUpBtn, gbc);

		gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(_extraClasspathDownBtn, gbc);

		gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(new JSeparator(), gbc);

		gbc = new GridBagConstraints(0,7,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0), 0,0);
		pnl.add(_extraClasspathDeleteBtn, gbc);

		return pnl;
	}

	/**
	 * Button that allows user to enter new items in the Extra Class Path list.
	 */
	private final class AddListEntryButton extends JButton implements ActionListener
	{
		private JFileChooser _chooser;

		AddListEntryButton()
		{
			super(s_stringMgr.getString("DriverInternalFrame.add"));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_chooser == null)
			{
				_chooser = new JFileChooser();
				_chooser.setFileHidingEnabled(false);
				_chooser.setMultiSelectionEnabled(true);
				_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				_chooser.addChoosableFileFilter(new FileExtensionFilter( s_stringMgr.getString("DriverInternalFrame.jarfiles"), new String[] { ".jar", ".zip" }));
			}

			String lastDirString = Props.getString(DriverInternalFrame.PREF_KEY_LAST_DRIVER_DIR, System.getProperty("user.home"));

			File lastDir = new File(lastDirString);

			_chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			if (lastDir.exists() && lastDir.isDirectory())
			{
				_chooser.setCurrentDirectory(lastDir);
			}
			else if (lastDir.exists() && lastDir.isFile() && null != lastDir.getParentFile())
			{
				_chooser.setCurrentDirectory(lastDir.getParentFile());
			}


			int returnVal = _chooser.showOpenDialog(getParent());

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File[] selFiles = _chooser.getSelectedFiles();
				if (selFiles != null)
				{
					if (0 < selFiles.length)
					{
						Props.putString(DriverInternalFrame.PREF_KEY_LAST_DRIVER_DIR, selFiles[0].getParentFile().getPath());
					}

					IFileListBoxModel myModel = _extraClassPathList.getTypedModel();
					for (int i = 0; i < selFiles.length; ++i)
					{
						myModel.addFile(selFiles[i]);
					}
					_extraClassPathList.setSelectedIndex(myModel.getSize() - 1);
				}
			}
		}
	}

	/**
	 * Button that will list all the drivers in the file current selected in a listbox.
	 */
	private final class ListDriversButton extends JButton implements ActionListener
	{
		private FileListBox _listBox;

		ListDriversButton(FileListBox listBox)
		{
			super(s_stringMgr.getString("DriverInternalFrame.listdrivers"));
			setEnabled(false);
			_listBox = listBox;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			_driverClassCmb.removeAllItems();
			final String[] fileNames = _listBox.getTypedModel().getFileNames();

			if (fileNames.length > 0)
			{
				_app.getThreadPool().addTask(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							final URL[] urls = new URL[fileNames.length];
							for (int i = 0; i < fileNames.length; ++i)
							{
								urls[i] = new File(fileNames[i]).toURI().toURL();
							}

							SQLDriverClassLoader cl = new SQLDriverClassLoader(urls);
							@SuppressWarnings("unchecked")
							// This can take a long time for big jars - so it is not done on the EDT.
							Class[] classes = cl.getDriverClasses(s_log);
							for (int i = 0; i < classes.length; ++i)
							{
								addDriverClassToCombo(classes[i].getName());
							}
						}
						catch (MalformedURLException ex)
						{
							displayErrorMessage(ex);
						}

					}
				});
			}

			if (_driverClassCmb.getItemCount() > 0)
			{
				_driverClassCmb.setSelectedIndex(0);
			}
		}
	}

	private void addDriverClassToCombo(final String driverClassName)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				_driverClassCmb.addItem(driverClassName);
			}

		});
	}

	private class ExtraClassPathListDataListener implements ListDataListener
	{
		public void contentsChanged(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
		}

		public void intervalAdded(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
		}

		public void intervalRemoved(ListDataEvent evt)
		{
			final boolean enable = _extraClassPathList.getModel().getSize() > 0;
			_extraClasspathListDriversBtn.setEnabled(enable);
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
			//
			// boolean enable = false;
			// if (selIdx != -1)
			// {
			// File file = _extraClassPathList.getSelectedFile();
			// if (file != null)
			// {
			// enable = file.isFile();
			// }
			// }
			// _extraClasspathListDriversBtn.setEnabled(enable);
		}
	}
}
