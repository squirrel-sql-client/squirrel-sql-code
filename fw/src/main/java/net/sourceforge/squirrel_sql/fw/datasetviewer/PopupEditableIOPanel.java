package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BinaryDisplayConverter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextArea;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * @author gwg
 *
 * Class to handle IO between user and editable text, text and object,
 * and text/object to/from file.
 */
public class PopupEditableIOPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PopupEditableIOPanel.class);

	// The text area displaying the object contents
	private final JTextArea _ta;

	// the scroll pane that holds the text area
	private final JScrollPane scrollPane;

	// Description needed to handle conversion of data to/from Object
	transient private final ColumnDisplayDefinition _colDef;

	transient private MouseAdapter _lis;

	private final TextPopupMenu _popupMenu;

	// name of file to do export/import/process on
	private JTextField fileNameField;

	// command to use when processing data with an external program
	private JComboBox externalCommandCombo;

	// save the original value for re-use by CLOB/BLOB types in conversion
	private Object originalValue;

	// Binary data viewing option: which radix to use
	// This object is only non-null when the data is binary data
	private JComboBox radixList = null;
	private String previousRadixListItem = null;
	// Binary data viewing option: view ascii as char rather than as numeric value
	private JCheckBox showAscii = null;
	private boolean previousShowAscii;

	private IOUtilities _iou = new IOUtilitiesImpl();	
	
	class BinaryOptionActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// user asked to see binary data in a different format
			int base = 16;	// default to hex
			if (previousRadixListItem.equals("Decimal")) base = 10;
			else if (previousRadixListItem.equals("Octal")) base = 8;
			else if (previousRadixListItem.equals("Binary")) base = 2;

			Byte[] bytes = BinaryDisplayConverter.convertToBytes(_ta.getText(),
				base, previousShowAscii);

			// return the expected format for this data
			base = 16;	// default to hex
			if (radixList.getSelectedItem().equals("Decimal")) base = 10;
			else if (radixList.getSelectedItem().equals("Octal")) base = 8;
			else if (radixList.getSelectedItem().equals("Binary")) base = 2;

			((RestorableJTextArea)_ta).updateText(
				BinaryDisplayConverter.convertToString(bytes,
				base, showAscii.isSelected()));

			previousRadixListItem = (String)radixList.getSelectedItem();
			previousShowAscii = showAscii.isSelected();

			return;
		}
	}
	transient private BinaryOptionActionListener optionActionListener =
		new BinaryOptionActionListener();

	// text put in file name field to indicate that we should
	// create a temp file for export
	private final String TEMP_FILE_FLAG = "<temp file>";

	// Symbol used by user in Command field to indicate
	// "Put the file name here" when the command is executed.
	private final String FILE_REPLACE_FLAG = "%f";

	/**
	 * Constructor
	 */
	public PopupEditableIOPanel(ColumnDisplayDefinition colDef,
										 Object value, boolean isEditable) {

		originalValue = value;	// save for possible future use

		_popupMenu = new TextPopupMenu();

		_colDef = colDef;
		_ta = CellComponentFactory.getJTextArea(colDef, value);

		if (isEditable) {
			_ta.setEditable(true);
			_ta.setBackground(Color.yellow);	// tell user it is editable
		}
		else {
			_ta.setEditable(false);
		}


		_ta.setLineWrap(true);
		_ta.setWrapStyleWord(true);

		setLayout(new BorderLayout());

		// add a panel containing binary data editing options, if needed
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BorderLayout());
		scrollPane = new JScrollPane(_ta);
		/*
		 * TODO: When 1.4 is the earliest version supported, include
		 * the following line here:
		 * 	scrollPane.setWheelScrollingEnabled(true);
		 * The scroll-wheel function is important for ease of use, but the
		 * setWheelScrollingEnabled function is not available in java 1.3.
		 */

		displayPanel.add(scrollPane, BorderLayout.CENTER);
		if (CellComponentFactory.useBinaryEditingPanel(colDef)) {
			// this is a binary field, so allow for multiple viewing options

			String[] radixListData = { "Hex", "Decimal", "Octal", "Binary" };
			radixList = new JComboBox(radixListData);
			radixList.addActionListener(optionActionListener);
			previousRadixListItem = "Hex";

			showAscii = new JCheckBox();
			previousShowAscii = false;
			showAscii.addActionListener(optionActionListener);

			JPanel displayControlsPanel = new JPanel();
			// use default sequential layout

			// i18n[popupeditableIoPanel.numberBase=Number Base:]
			displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.numberBase")));
			displayControlsPanel.add(radixList);
			displayControlsPanel.add(new JLabel("    "));	// add some space
			displayControlsPanel.add(showAscii);
			// i18n[popupeditableIoPanel.showAscii=Show ASCII as chars]
			displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.showAscii")));
			displayPanel.add(displayControlsPanel, BorderLayout.SOUTH);
		}
		add(displayPanel, BorderLayout.CENTER);

		// add controls for file handling, but only if DataType
		// can do File operations
		if (CellComponentFactory.canDoFileIO(colDef)) {
			// yes it can, so add controls
			add(exportImportPanel(isEditable), BorderLayout.SOUTH);
		}

		_popupMenu.add(new LineWrapAction());
		_popupMenu.add(new WordWrapAction());
		_popupMenu.add(new XMLReformatAction());
		_popupMenu.setTextComponent(_ta);
	}

	/**
	 * build the user interface for export/import operations
	 */
	private JPanel exportImportPanel(boolean isEditable) {
		JPanel eiPanel = new JPanel();
		eiPanel.setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(4,4,4,4);
		gbc.gridx = 0;
		gbc.gridy = 0;

		// File handling controls
		// i18n[popupeditableIoPanel.useFile=Use File: ]
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.useFile")), gbc);

		// ensure, that the text field can use the extra space if the user resize the dialog.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx=0.5;
		fileNameField = new JTextField(TEMP_FILE_FLAG, 17);
		Dimension preferredSize = fileNameField.getPreferredSize();
		// ensure, that the text field has a suitable size, even the panel is too small.
		fileNameField.setMinimumSize(new Dimension(preferredSize.width/2, preferredSize.height));
		gbc.gridx++;
		
		eiPanel.add(fileNameField, gbc);
		gbc.fill = GridBagConstraints.NONE;;
		gbc.weightx=0.0;

		// add button for Brows
		// i18n[popupeditableIoPanel.browse=Browse]
		JButton browseButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.browse"));
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);


		gbc.gridx++;
		eiPanel.add(browseButton, gbc);


		// i18n[popupeditableIoPanel.export44=Export]
		JButton exportButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.export44"));
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(exportButton, gbc);

		// import and external processing can only be done if
		// panel is editable
		if ( isEditable == false)
			return eiPanel;

		// Add import control
		// i18n[popupeditableIoPanel.import44=Import]
		JButton importButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.import44"));
		importButton.setActionCommand("import");
		importButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(importButton, gbc);

		// add external processing command field and button
		gbc.gridy++;
		gbc.gridx = 0;
		// i18n[popupeditableIoPanel.withCommand=With command:]
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.withCommand")), gbc);

		// add combo box for command to execute
		gbc.gridx++;
		externalCommandCombo = new JComboBox(
			CellImportExportInfoSaver.getInstance().getCmdList());
		externalCommandCombo.setSelectedIndex(-1);	// no entry selected
		externalCommandCombo.setEditable(true);

		// make this the same size as the fileNameField
		externalCommandCombo.setPreferredSize(fileNameField.getPreferredSize());
		externalCommandCombo.setMinimumSize(fileNameField.getMinimumSize());
		
		// ensure, that the text field can use the extra space if the user resize the dialog.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx=0.5;
		eiPanel.add(externalCommandCombo, gbc);
		gbc.fill = GridBagConstraints.NONE;;
		gbc.weightx=0.0;
		
		
		// add button to execute external command
		// i18n[popupeditableIoPanel.execute34=Execute]
		JButton externalCommandButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.execute34"));
		externalCommandButton.setActionCommand("execute");
		externalCommandButton.addActionListener(this);

		gbc.gridx++;
		eiPanel.add(externalCommandButton, gbc);

		// add button for applying file & cmd info without doing anything else
		// i18n[popupeditableIoPanel.applyFile=Apply File & Cmd]
		JButton applyButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.applyFile"));
		applyButton.setActionCommand("apply");
		applyButton.addActionListener(this);

		gbc.gridx++;
		gbc.gridwidth = 2;
		eiPanel.add(applyButton, gbc);
		gbc.gridwidth = 1;	// reset width to normal	

		// add note to user about including file name in command
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;


		// i18n[popupeditableIoPanel.replaceFile=(In command, the string {0} is replaced by the file name when Executed.)]
		eiPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.replaceFile", FILE_REPLACE_FLAG)), gbc);

		// load filename and command with previously entered info
		// if not the default
		CellImportExportInfo info =
			CellImportExportInfoSaver.getInstance().get(_colDef.getFullTableColumnName());
		if (info != null) {
			// load the info into the text fields
			fileNameField.setText(info.getFileName());
			externalCommandCombo.getEditor().setItem(info.getCommand());
		}

		return eiPanel;
	}


	/**
	 * Return the contents of the editable text area as an Object
	 * converted by the correct DataType function.  Errors in converting
	 * from the text form into the Object are reported
	 * through the messageBuffer.
	 */
	public Object getObject(StringBuffer messageBuffer) {
		String text = null;
		try {
			text = getTextAreaCannonicalForm();
		}
		catch (Exception e) {
			messageBuffer.append(
				"Failed to convert binary text; error was:\n"+e.getMessage());
			return null;
		}
		return CellComponentFactory.validateAndConvertInPopup(_colDef,
					originalValue, text, messageBuffer);
	}

	/**
	 * When focus is passed to this panel, automatically pass it
	 * on to the text area.
	 */
	public void requestFocus() {
		_ta.requestFocus();
	}

	/**
	 * Handle actions on the buttons in the file operations panel
	 */
	public void actionPerformed(ActionEvent e) {

		//File object for doing IO
		File file;

		if (e.getActionCommand().equals("browse")) {
			JFileChooser chooser = new JFileChooser();
			String filename = fileNameField.getText();
            if (filename != null && !"".equals(filename)) {
                File f = new File(filename);
                String path = f.getAbsolutePath();
                if (path != null && !"".equals(path)) {
                    chooser.setCurrentDirectory(new File(path));
                }
            }
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				//	System.out.println("You chose to open this file: " +
				//		chooser.getSelectedFile().getName());
				try {
					fileNameField.setText(chooser.getSelectedFile().getCanonicalPath());
				}
				catch (Exception ex) {
					// should not happen since the file that was selected was
					// just being shown in the Chooser dialog, but just to be safe...
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.errorGettingPath=Error getting full path name for selected file]
						s_stringMgr.getString("popupeditableIoPanel.errorGettingPath"),
						// i18n[popupeditableIoPanel.fileChooserError=File Chooser Error]
						s_stringMgr.getString("popupeditableIoPanel.fileChooserError"),JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		else if (e.getActionCommand().equals("apply")) {
			// If file name default and cmd is null or empty,
			// make sure this entry is not being held in CellImportExportInfoSaver
			if ( (fileNameField.getText() != null &&
					fileNameField.getText().equals(TEMP_FILE_FLAG)) &&
				(externalCommandCombo.getEditor().getItem() == null ||
					((String)externalCommandCombo.getEditor().getItem()).length() == 0)) {
				// user has not entered anything or has reset to defaults,
				// so make sure there is no entry for this column in the
				// saved info
				CellImportExportInfoSaver.remove(_colDef.getFullTableColumnName());
			}
			else {
				// user has entered some non-default info, so save it
				CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(),fileNameField.getText(),
					((String)externalCommandCombo.getEditor().getItem()));
			}

		}

		 else if (e.getActionCommand().equals("import")) {

			 // IMPORT OBJECT FROM OSX_FILE

			 if (fileNameField.getText() == null ||
				 fileNameField.getText().equals(TEMP_FILE_FLAG)) {
				 // not allowed - must have existing file for import
				 JOptionPane.showMessageDialog(this,
					 // i18n[popupeditableIoPanel.selectImportDataFile=You must select an existing file to import data from.]
						s_stringMgr.getString("popupeditableIoPanel.selectImportDataFile"),
					 // i18n[popupeditableIoPanel.noFile=No File Selected]
						s_stringMgr.getString("popupeditableIoPanel.noFile"),JOptionPane.ERROR_MESSAGE);
				return;	// do not do import
			 }

			 // get name of file, which must exist
			if (fileNameField.getText() == null)
				fileNameField.setText("");	// guard against something really stupid
			file = new File(fileNameField.getText());
			if ( ! file.exists() || ! file.isFile() || ! file.canRead()) {
				// not something we can read

				// i18n[popupeditableIoPanel.fileDoesNotExist=File {0} does not exist,\nor is not a readable, normal file.\nPlease enter a valid file name or use Browse to select a file.]
				String msg = s_stringMgr.getString("popupeditableIoPanel.fileDoesNotExist", fileNameField.getText());

			   JOptionPane.showMessageDialog(this, msg,

						// i18n[popupeditableIoPanel.fileError=File Name Error]
						s_stringMgr.getString("popupeditableIoPanel.fileError"),JOptionPane.ERROR_MESSAGE);
				return;	// do not do import
			}

			// Now that we have the file, do the import.
			//
			// Note: the sequence of operations is divided into two sections
			// at this point.  The preceeding code ensures that we have
			// a readable file, and the code in the following method call
			// does the import.  The reason for splitting at this point is
			// that the Execute operation needs to do an import, and it will
			// already have the file to do the import from (which is the same
			// as the file it exported into).
			importData(file);

			// save the user options - we know that it is not the default
			// because we do not allow importing from "temp file"
			CellImportExportInfoSaver.getInstance().save(
				_colDef.getFullTableColumnName(), fileNameField.getText(),
				((String)externalCommandCombo.getEditor().getItem()));

		 }

		else {

			// GET OSX_FILE FOR EXPORT & EXTERNAL PROCESSING

			String canonicalFilePathName = fileNameField.getText();

			// file name verification operations are the same for both
			// export and execute, so do that work here for both.
			//
			// If file name is null or empty, do not proceed
			if (fileNameField.getText() == null ||
				fileNameField.getText().equals("")) {

				JOptionPane.showMessageDialog(this,

					// i18n[popupeditableIoPanel.noExportFile=No file name given for export.\nPlease enter a file name  or use Browse before clicking Export.]
					s_stringMgr.getString("popupeditableIoPanel.noExportFile"),
					// i18n[popupeditableIoPanel.exportError=Export Error]
					s_stringMgr.getString("popupeditableIoPanel.exportError"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			// create the file to open
			if (fileNameField.getText().equals(TEMP_FILE_FLAG)) {
				// user wants us to create a temp file
				try {
					file = File.createTempFile("squirrel", ".tmp");

					// get the real name for use later
					canonicalFilePathName = file.getCanonicalPath();
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.cannotCreateTempFile=Cannot create temp file..\nError was:\n{0}]
						s_stringMgr.getString("popupeditableIoPanel.cannotCreateTempFile", ex.getMessage()),
						// i18n[popupeditableIoPanel.exportError2=Export Error]
						s_stringMgr.getString("popupeditableIoPanel.exportError2"),
						JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else {
				//user must have supplied a file name.
				file = new File(fileNameField.getText());

				try {
					canonicalFilePathName = file.getCanonicalPath();
				}
				catch (Exception ex) {
					// an error here may mean that the file cannot be
					// reached or has moved or some such.  In any case,
					// the file cannot be used for export.
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.cannotAccessFile=Cannot access file name {0}\nAborting export.]
						s_stringMgr.getString("popupeditableIoPanel.cannotAccessFile", fileNameField.getText()),

						// i18n[popupeditableIoPanel.exportError3=Export Error]
						s_stringMgr.getString("popupeditableIoPanel.exportError3"),JOptionPane.ERROR_MESSAGE);
						return;
				}

				// see if file exists
				if (file.exists()) {
					if ( ! file.isFile()) {
						JOptionPane.showMessageDialog(this,
							// i18n[popupeditableIoPanel.notANormalFile=File is not a normal file.\n Cannot do export to a directory or system file.]
							s_stringMgr.getString("popupeditableIoPanel.notANormalFile"),
							// i18n[popupeditableIoPanel.exportError4=Export Error]
							s_stringMgr.getString("popupeditableIoPanel.exportError4"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					if ( ! file.canWrite()) {
						JOptionPane.showMessageDialog(this,
							// i18n[popupeditableIoPanel.notWriteable=File is not writeable.\nChange file permissions or select a differnt file for export.]
							s_stringMgr.getString("popupeditableIoPanel.notWriteable"),
							// i18n[popupeditableIoPanel.exportError5=Export Error]
							s_stringMgr.getString("popupeditableIoPanel.exportError5"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					// file exists, is normal and is writable, so see if user
					// wants to overwrite contents of file
					int option = JOptionPane.showConfirmDialog(this,
						// i18n[popupeditableIoPanel.fileOverwrite=File {0} already exists.\n\nDo you wish to overwrite this file?]
						s_stringMgr.getString("popupeditableIoPanel.fileOverwrite", canonicalFilePathName),
						// i18n[popupeditableIoPanel.overwriteWarning=File Overwrite Warning]
							s_stringMgr.getString("popupeditableIoPanel.overwriteWarning"), JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION) {
						// user does not want to overwrite the file

						// we could tell user here that export was canceled,
						// but I don't think its necessary, and that avoids
						// forcing user to do yet another annoying mouse click.
						return;
					}
					// user is ok with overwriting file
					// We do not need to do anything special to overwrite
					// (as opposed to appending) since the OutputString
					// starts at the beginning of the file by default.
				}
				else {
					// file does not already exist, so try to create it
					try {
						if ( ! file.createNewFile()) {
							JOptionPane.showMessageDialog(this,
								// i18n[popupeditableIoPanel.createFileError=Failed to create file {0}.\nChange file name or select a differnt file for export.]
								s_stringMgr.getString("popupeditableIoPanel.createFileError", canonicalFilePathName),
								// i18n[popupeditableIoPanel.exportError6=Export Error]
								s_stringMgr.getString("popupeditableIoPanel.exportError6"),JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					catch (Exception ex) {

						Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};
						JOptionPane.showMessageDialog(this,
							// i18n[popupeditableIoPanel.cannotOpenFile=Cannot open file {0}.\nError was:{1}]
							s_stringMgr.getString("popupeditableIoPanel.cannotOpenFile", args),
							// i18n[popupeditableIoPanel.exportError7=Export Error]
							s_stringMgr.getString("popupeditableIoPanel.exportError7"),JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			// at this point we have an actual file that we can output to,
			// so create the output stream
			// (so that data type objects do not have to).

			// We have done everything we can prior to this point
			// to ensure that the the file is accessible, but it is
			// still possible that an existing file was removed
			// at a bad moment.  Also, the compiler insists that we
			// put this in a try statement
			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(file);
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
					// i18n[popupeditableIoPanel.cannotFindFile=Cannot find file {0}\nCheck file name and re-try export.]
					s_stringMgr.getString("popupeditableIoPanel.cannotFindFile", canonicalFilePathName),
					// i18n[popupeditableIoPanel.exportError8=Export Error]
					s_stringMgr.getString("popupeditableIoPanel.exportError8"),JOptionPane.ERROR_MESSAGE);
				return;
			}

            String extCmdComboItemStr = null;
            if (externalCommandCombo != null
                    && externalCommandCombo.getEditor() != null) 
            {
                extCmdComboItemStr = 
                    (String)externalCommandCombo.getEditor().getItem();
            }
            
			// if user did anything other than default, then save
			// their options
			if ( ! TEMP_FILE_FLAG.equals(fileNameField.getText()) 
                    || (extCmdComboItemStr != null 
                            && extCmdComboItemStr.length() > 0)) {

				// This may be called either when the table is editable or when it is
				// read-only.  When it is read-only, there is no command to be saved,
				// but when it is editable, there may be a command.
				String commandString = extCmdComboItemStr;

				CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(), fileNameField.getText(),
					commandString);
			}

			if (e.getActionCommand().equals("export")) {

				// EXPORT OBJECT TO OSX_FILE

				if (exportData(outStream, canonicalFilePathName) == true) {

					// if we get here, then everything worked correctly, so
					// tell user that data was put into file.
					// This is different from the Import strategy
					// because the user may not know the name of the file
					// that was used if they selected the automatic temp file
					// operation, or they may not know what directory the file
					// was actually put into, so this tells them the full file path.
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.exportedToFile=Data Successfully exported to file {0}]
						s_stringMgr.getString("popupeditableIoPanel.exportedToFile", canonicalFilePathName),
						// i18n[popupeditableIoPanel.exportSuccess=Export Success]
						s_stringMgr.getString("popupeditableIoPanel.exportSuccess"),JOptionPane.INFORMATION_MESSAGE);
				}
			}

			else if (e.getActionCommand().equals("execute")) {

				// EXPORT OBJECT TO OSX_FILE, EXECUTE PROGRAM ON IT, IMPORT IT BACK

				if (((String)externalCommandCombo.getEditor().getItem()) == null ||
					((String)externalCommandCombo.getEditor().getItem()).length() == 0) {
					// cannot execute a null command
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.cannotExec=Cannot execute a null command.\nPlease enter a command in the Command field before clicking on Execute.]
						s_stringMgr.getString("popupeditableIoPanel.cannotExec"),
						// i18n[popupeditableIoPanel.executeError=Execute Error]
						s_stringMgr.getString("popupeditableIoPanel.executeError"),JOptionPane.ERROR_MESSAGE);
					return;
				}

				// replace any instance of flag in command with file name
				String command = ((String)externalCommandCombo.getEditor().getItem());

				int index;
				while ((index = command.indexOf(FILE_REPLACE_FLAG)) >= 0) {
					command = command.substring(0, index) +
						canonicalFilePathName +
						command.substring(index + FILE_REPLACE_FLAG.length());
				}

				// export data to file
				if (exportData(outStream, canonicalFilePathName) == false) {
					// bad export - do not proceed with command
					// The exportData() method has already put up a message
					// to the user saying the export failed.
					return;
				}

				int commandResult;
				BufferedReader err = null;
				try {
					// execute command
					Process cmdProcess = Runtime.getRuntime().exec(command);

					// wait for command to complete
					commandResult = cmdProcess.waitFor();

					// check the error stream for a problem
					//
					// This is a bit questionable since it is possible
					// for processes to output something on stderr
					// but continue processing.  But without this, some
					// problems are not seen (e.g. "bad argument" type
					// messages from the process).
					err = new BufferedReader(
						new InputStreamReader(cmdProcess.getErrorStream()));

					String errMsg = err.readLine();
					if (errMsg != null)
						throw new IOException(
							"text on error stream from command starting with:\n"+errMsg);
				}
				catch (Exception ex) {

					Object[] args = new Object[]{command, ex.getMessage()};
					JOptionPane.showMessageDialog(this,
						// i18n[popupeditableIoPanel.errWhileExecutin=Error while executing command.\nThe command was:\n {0}\nThe error was:\n{1}]
						s_stringMgr.getString("popupeditableIoPanel.errWhileExecutin", args),
						// i18n[popupeditableIoPanel.executeError2=Execute Error]
						s_stringMgr.getString("popupeditableIoPanel.executeError2"),JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
				    _iou.closeReader(err);
				}

				// check for possibly bad return from child
				if (commandResult != 0) {
					// command returned non-standard value.
					// ask user before proceeding.
					int option = JOptionPane.showConfirmDialog(this,
							// i18n[popupeditableIoPanel.commandReturnNot0=The convention for command returns is that 0 means success, but this command returned {0}.\nDo you wish to import the file contents anyway?]
							s_stringMgr.getString("popupeditableIoPanel.commandReturnNot0", Integer.valueOf(commandResult)),

							// i18n[popupeditableIoPanel.importWarning=Import Warning]
							s_stringMgr.getString("popupeditableIoPanel.importWarning"), JOptionPane.YES_NO_OPTION);
					if (option != JOptionPane.YES_OPTION) {
						return;
					}
				}

				//import the data back from the same file
				importData(file);

				// If the file was a temp file, delete it now.
				// We assume that Export-only operations want to leave the
				// file in place, but Execute operations just want a temp
				// space to work with and do not want it lying around afterwards.
				file.delete();
			}
		} // end of combined export and execute operations
	}

	/**
	 * Function to import data from a file.
	 * This is a separate function because it is called from two places.
	 * One is when the user asks to do an import, and the other is when they
	 * ask to run an external command, which involves importing from the file
	 * after the command is completed.
	 */
	private void importData(File file) {
		// create the imput stream
		// (so that DataType objects don't have to)
		FileInputStream inStream;
		String canonicalFilePathName = fileNameField.getText();
		try {
			inStream = new FileInputStream(file);

			// it is handy to have the cannonical path name
			// to show user in error messages.  Since getting
			// that name might involve an IOException, we need
			// to put it inside a try statement.  However,
			// since the file does exist there is no good reason
			// for getting an IOException at this point, but
			// if we get one there is something seriously wrong
			// and we want to abort.  Therefore it make sense
			// to get that name here and save it for later use.
			canonicalFilePathName = file.getCanonicalPath();
		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};

			JOptionPane.showMessageDialog(this,
				// i18n[popupeditableIoPanel.fileOpenError=There was an error opening file {0}.\nThe error was:\n{1}]
				s_stringMgr.getString("popupeditableIoPanel.fileOpenError", args),
				// i18n[popupeditableIoPanel.fileOpenErrorHeader=File Open Error]
				s_stringMgr.getString("popupeditableIoPanel.fileOpenErrorHeader"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		// hand file input stream to DataType object for import
		// Also, handle File IO errors here so that DataType objects
		// do not have to.
		try {

			// The DataObject returns a string to put into the
			// popup which can later be converted to the appropriate
			// object type.
			String replacementText =
				CellComponentFactory.importObject(_colDef, inStream);

			// since the above did not throw an exception,
			// we now have a good new data object, so
			// change the text area to reflect that new object.
			//

			// If the user has selected a non-cannonical Binary format, we need
			// to convert the text appropriately
			if (radixList != null &&
				! (radixList.getSelectedItem().equals("Hex") &&
					showAscii.isSelected() == false) ) {
				// we need to convert to a different format
				int base = 16;	// default to hex
				if (radixList.getSelectedItem().equals("Decimal")) base = 10;
				else if (radixList.getSelectedItem().equals("Octal")) base = 8;
				else if (radixList.getSelectedItem().equals("Binary")) base = 2;

				Byte[] bytes = BinaryDisplayConverter.convertToBytes(replacementText,
					16, false);

				// return the expected format for this data
				replacementText = BinaryDisplayConverter.convertToString(bytes,
					base, showAscii.isSelected());
			}

			((RestorableJTextArea)_ta).updateText(replacementText);
		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};
			JOptionPane.showMessageDialog(this,
				// i18n[popupeditableIoPanel.errorReadingFile=There was an error while reading file {0}.\nThe error was:\n{1}]
				s_stringMgr.getString("popupeditableIoPanel.errorReadingFile", args),
				// i18n[popupeditableIoPanel.importError2=Import Error]
				s_stringMgr.getString("popupeditableIoPanel.importError2"),JOptionPane.ERROR_MESSAGE);
			return;
		}

		// cleanup resources used
		try {
			inStream.close();
		}
		catch (Exception ex) {
			// I cannot think of any reason for doing anything
			// at all here
		}

		/*
				 * Issue: After importing the data, we could tell the user that
				 * it has been successfully imported.  This would give good
				 * positive feedback.  However, it would mean that they need
				 * to click on yet another button to dismiss that message.
				 * On the other hand, since the operation is synchronous,
				 * the user cannot proceed to do anything until it is done.
				 * If we assume that import usually works correctly unless
				 * they are shown an error message, then we do not need to
				 * display anything here.
				 */
	}


	/**
	 * Function to export data to a file.
	 * This is a separate function because it is called from two places.
	 * One is when the user asks to export, and the other is when they
	 * as to run an external command, which involves exporting to file first.
	 */
	private boolean exportData(FileOutputStream outStream,
	                           String canonicalFilePathName){

		// hand file output stream to DataType object for export
		// Also, handle File IO errors here so that DataType objects
		// do not have to.
		try {

			// Hand the current text to the DataType object.
			// DataType object is responsible for validating
			// that the text makes sense for this type of object
			// and converting it to the proper form for output.
			// All errors are handled as IOExceptions
			CellComponentFactory.exportObject(_colDef, outStream,
				getTextAreaCannonicalForm());

		}
		catch (Exception ex) {

			Object[] args = new Object[]{canonicalFilePathName, ex.getMessage()};

			JOptionPane.showMessageDialog(this,
				// i18n[popupeditableIoPanel.errorWritingFile=There was an error while writing file {0}.\nThe error was:\n{1}]
				s_stringMgr.getString("popupeditableIoPanel.errorWritingFile", args),
				// i18n[popupeditableIoPanel.exportError100=Export Error]
				s_stringMgr.getString("popupeditableIoPanel.exportError100"),JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * catch and handle mouse events to put up a menu
	 */
	public void addNotify()
	{
		super.addNotify();
		if (_lis == null)
		{
			_lis = new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					if (evt.isPopupTrigger())
					{
						_popupMenu.show(evt);
					}
				}
				public void mouseReleased(MouseEvent evt)
				{
						if (evt.isPopupTrigger())
					{
						_popupMenu.show(evt);
					}
				}
			};
			_ta.addMouseListener(_lis);
		}
	}

	public void removeNotify()
	{
		super.removeNotify();
		if (_lis != null)
		{
			_ta.removeMouseListener(_lis);
			_lis = null;
		}
	}

	@SuppressWarnings("serial")
	private class LineWrapAction extends BaseAction
	{
		LineWrapAction()
		{
			// i18n[popupEditableIoPanel.wrapLines=Wrap Lines on/off]
			super(s_stringMgr.getString("popupEditableIoPanel.wrapLines"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setLineWrap(!_ta.getLineWrap());
			}
		}
	}

	private class WordWrapAction extends BaseAction
	{
        private static final long serialVersionUID = 1L;

        WordWrapAction()
		{
			// i18n[popupEditableIoPanel.wrapWord=Wrap on Word on/off]
			super(s_stringMgr.getString("popupEditableIoPanel.wrapWord"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setWrapStyleWord(!_ta.getWrapStyleWord());
			}
		}
	}

	private class XMLReformatAction extends BaseAction
	{
        private static final long serialVersionUID = 1L;

        XMLReformatAction()
		{
			// i18n[popupEditableIoPanel.reformatXml=Reformat XML]
			super(s_stringMgr.getString("popupEditableIoPanel.reformatXml"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_ta != null)
			{
				_ta.setText(XmlRefomatter.reformatXml(_ta.getText()));
			}
		}
	}

	/**
	 * Helper function that ensures that the data is acceptable to the DataType
	 * object. The issue addressed here is that Binary data can be represented
	 * in multiple formats (Hex, Octal, etc), and to keep the DataTypes simple
	 * we assume that they get only Hex data with ASCII chars shown as their
	 * numeric value. This makes sense from the point of view that the different
	 * formats are temporary views handled by this class rather than permanent
	 * settings applied to either the column or the DataType. Therefore, when we
	 * pass the data from the TextArea into the DataType, we may need to do a
	 * conversion on the way.
	 */
	private String getTextAreaCannonicalForm() {
		// handle null
		if (_ta.getText() == null ||
			_ta.getText().equals("<null>") ||
			_ta.getText().length() == 0)
			return _ta.getText();

		// if the data is not binary, then there is no need for conversion.
		// if the data is Hex with ASCII not shown as chars, then no conversion needed.
		if (radixList == null ||
			(radixList.getSelectedItem().equals("Hex") && ! showAscii.isSelected()) ) {
			// no need for conversion
			return _ta.getText();
		}

		// The field is binary and not in the format expected by the DataType
		int base = 16;	// default to hex
		if (radixList.getSelectedItem().equals("Decimal")) base = 10;
		else if (radixList.getSelectedItem().equals("Octal")) base = 8;
		else if (radixList.getSelectedItem().equals("Binary")) base = 2;

		// the following can cause and exception if the text is not formatted correctly
		Byte[] bytes = BinaryDisplayConverter.convertToBytes(_ta.getText(),
			base, showAscii.isSelected());

		// return the expected format for this data
		return BinaryDisplayConverter.convertToString(bytes, 16, false);
	}
}
