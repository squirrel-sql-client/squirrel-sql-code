package net.sourceforge.squirrel_sql.plugins.exportconfig.gui;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.DefaultFormBuilder;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPreferences;
/**
 * This builder creates the component that allows the user to select what they
 * want to export and where it should be exported to.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ExportPanelBuilder
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ExportPanelBuilder.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportPanelBuilder.class);

	/** The last directory saved to. */
	private static File s_lastDir;

	/**
	 * Update the status of the GUI controls as the user makes changes.
	 */
	private final ControlMediator _mediator = new ControlMediator();

	/** Export Preferences checkbox. */
	private JCheckBox _exportPrefsChk;

	/** Export preferences file name. */
	private JTextField _exportPrefsText;

	/** Export preferences browse button. */
	private JButton _exportPrefsBtn;

	/** Export Drivers checkbox. */
	private JCheckBox _exportDriversChk;

	/** Export drivers file name. */
	private JTextField _exportDriversText;

	/** Export drivers browse button. */
	private JButton _exportDriversBtn;

	/** Export Aliases checkbox. */
	private JCheckBox _exportAliasesChk;

	/** Export aliases file name. */
	private JTextField _exportAliasesText;

	/** Export aliases browse button. */
	private JButton _exportAliasesBtn;

	/** Include user names in export checkbox. */
	private JCheckBox _includeUserNamesChk;

	/** Include passwords in export checkbox. */
	private JCheckBox _includePasswordsChk;

	public ExportPanelBuilder(IApplication app)
	{
		super();
	}

	public JPanel buildPanel(ExportConfigPreferences prefs)
	{
		initComponents(prefs);

		final FormLayout layout = new FormLayout(
				"12dlu, left:max(40dlu;pref), 3dlu, 150dlu:grow(1.0), 3dlu, "
			  + "right:max(40dlu;pref), 3dlu",
				"");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setLeadingColumnOffset(1);

		builder.appendSeparator(s_stringMgr.getString("ExportPanel.prefs"));
		builder.append(_exportPrefsChk);
		builder.append(_exportPrefsText);
		builder.append(_exportPrefsBtn);

		builder.nextLine();
		builder.appendSeparator(s_stringMgr.getString("ExportPanel.drivers"));
		builder.append(_exportDriversChk);
		builder.append(_exportDriversText);
		builder.append(_exportDriversBtn);

		builder.nextLine();
		builder.appendSeparator(s_stringMgr.getString("ExportPanel.aliases"));
		builder.append(_exportAliasesChk);
		builder.append(_exportAliasesText);
		builder.append(_exportAliasesBtn);

		builder.setLeadingColumnOffset(3);
		builder.nextLine();
		builder.append(_includeUserNamesChk);

		builder.nextLine();
		builder.append(_includePasswordsChk);
		builder.setLeadingColumnOffset(1);

		builder.nextLine();
		builder.appendSeparator();
		builder.append(createButtonBar(), 5);

		return builder.getPanel();
	}

	/**
	 * Write the user data from the panel to the passed preferences object.
	 * 
	 * @param	prefs	Preferences object to be updated.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ExportConfigPreferences</TT> passed.
	 *
	 */
	public void writeToPerferences(ExportConfigPreferences prefs)
	{
		if (prefs == null)
		{
			throw new IllegalArgumentException("ExportConfigPreferences == null");
		}

		prefs.setExportPreferences(_exportPrefsChk.isSelected());
		prefs.setExportDrivers(_exportDriversChk.isSelected());
		prefs.setExportAliases(_exportAliasesChk.isSelected());

		prefs.setPreferencesFileName(_exportPrefsText.getText());
		prefs.setDriversFileName(_exportDriversText.getText());
		prefs.setAliasesFileName(_exportAliasesText.getText());

		prefs.setIncludeUserNames(_includeUserNamesChk.isSelected());
		prefs.setIncludePasswords(_includePasswordsChk.isSelected());
	}

	private void updateControlStatus()
	{
		boolean isSelected = _exportPrefsChk.isSelected();
		_exportPrefsText.setEditable(isSelected);
		_exportPrefsBtn.setEnabled(isSelected);

		isSelected = _exportDriversChk.isSelected();
		_exportDriversText.setEditable(isSelected);
		_exportDriversBtn.setEnabled(isSelected);

		isSelected = _exportAliasesChk.isSelected();
		_exportAliasesText.setEditable(isSelected);
		_exportAliasesBtn.setEnabled(isSelected);
		_includeUserNamesChk.setEnabled(isSelected);
		_includePasswordsChk.setEnabled(isSelected);
	}

	private void initComponents(ExportConfigPreferences prefs)
	{
		final File here = new File(".");

		final String export = s_stringMgr.getString("ExportPanel.export");
		_exportPrefsChk = new JCheckBox(export);
		_exportDriversChk = new JCheckBox(export);
		_exportAliasesChk = new JCheckBox(export);
		
		_exportPrefsText = new JTextField();
		_exportDriversText = new JTextField();
		_exportAliasesText = new JTextField();

		final String btnTitle = s_stringMgr.getString("ExportPanel.browse"); 
		_exportPrefsBtn = new JButton(btnTitle);
		_exportDriversBtn = new JButton(btnTitle);
		_exportAliasesBtn = new JButton(btnTitle);

//		final ApplicationFiles appFiles = new ApplicationFiles();
//		_exportPrefsText.setText(getFileName(here, appFiles.getUserPreferencesFile().getName()));
//		_exportDriversText.setText(getFileName(here, appFiles.getDatabaseDriversFile().getName()));
//		_exportAliasesText.setText(getFileName(here, appFiles.getDatabaseAliasesFile().getName()));

		_includeUserNamesChk = new JCheckBox(s_stringMgr.getString("ExportPanel.includeusers"));
		_includePasswordsChk = new JCheckBox(s_stringMgr.getString("ExportPanel.includepasswords"));

		_exportPrefsChk.addActionListener(_mediator);
		_exportDriversChk.addActionListener(_mediator);
		_exportAliasesChk.addActionListener(_mediator);

		_exportPrefsBtn.addActionListener(new BrowseButtonListener(_exportPrefsText));
		_exportDriversBtn.addActionListener(new BrowseButtonListener( _exportDriversText));
		_exportAliasesBtn.addActionListener(new BrowseButtonListener(_exportAliasesText));

		_exportPrefsChk.setSelected(prefs.getExportPreferences());
		_exportDriversChk.setSelected(prefs.getExportDrivers());
		_exportAliasesChk.setSelected(prefs.getExportAliases());

		_includeUserNamesChk.setSelected(prefs.getIncludeUserNames());
		_includePasswordsChk.setSelected(prefs.getIncludePasswords());

		_exportPrefsText.setText(prefs.getPreferencesFileName());
		_exportDriversText.setText(prefs.getDriversFileName());
		_exportAliasesText.setText(prefs.getAliasesFileName());

		updateControlStatus();
	}

	private JPanel createButtonBar()
	{
		ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addGlue();
		builder.addGridded(new JButton("Export"));                      
		builder.addRelatedGap();                   
		builder.addGridded(new JButton("Cancel"));

		return builder.getPanel();  
	}

//	private String getFileName(File dir, String name)
//	{
//		
//		return getFileName(new File(dir, name));
//	}
//
	private String getFileName(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException ex)
		{
			s_log.error("Error resolving file name", ex);
		}
		return file.getAbsolutePath();
	}

	/**
	 * This class will update the status of the GUI controls as the user
	 * makes changes.
	 */
	private final class ControlMediator implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateControlStatus();
		}
	}

	private final class BrowseButtonListener implements ActionListener
	{
		private final JTextField _tf;

		BrowseButtonListener(JTextField tf)
		{
			super();
			_tf = tf;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new FileExtensionFilter("XML files",
													new String[] { ".xml" }));
			chooser.setSelectedFile(new File(_tf.getText()));
//			chooser.setDialogTitle("Select???");

			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				_tf.setText(getFileName(chooser.getSelectedFile()));
			}
		}
	}
}
