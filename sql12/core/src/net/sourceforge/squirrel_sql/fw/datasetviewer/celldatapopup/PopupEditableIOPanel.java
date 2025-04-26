package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.DBDIffService;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff.TableSelectionDiffUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteFromHistoryAttach;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BinaryDisplayConverter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.GlobalFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.textfind.TextFindCtrl;
import net.sourceforge.squirrel_sql.fw.util.DesktopUtil;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.Types;
import java.util.Objects;

/**
 * @author gwg
 *
 * Class to handle IO between user and editable text, text and object,
 * and text/object to/from file.
 */
public class PopupEditableIOPanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PopupEditableIOPanel.class);
	public final static ILogger s_log = LoggerController.createLogger(PopupEditableIOPanel.class);

	public static final String ACTION_BROWSE = "browse";
	public static final String ACTION_EXPORT = "export";
	public static final String ACTION_FIND = "find";
	public static final String ACTION_EXECUTE = "execute";
	public static final String ACTION_APPLY = "apply";
	public static final String ACTION_IMPORT = "import";

	public static final String RADIX_HEX = "Hex";
	public static final String RADIX_DECIMAL = "Decimal";
	public static final String RADIX_OCTAL = "Octal";
	public static final String RADIX_BINARY = "Binary";

	// The text area displaying the object contents
	private final RestorableRSyntaxTextArea _textArea;

	// the scroll pane that holds the text area
	private final JScrollPane _scrollPane;

	private final TextFindCtrl _textFindCtrl;


	// Description needed to handle conversion of data to/from Object
	private final ColumnDisplayDefinition _colDef;
	private final JCheckBoxMenuItem _chkMnuLineWrap;
	private final JCheckBoxMenuItem _chkMnuWordWrap;

	private MouseAdapter _lis;

	private final TextPopupMenu _popupMenu;

	// name of file to do export/import/process on
	private EditableComboBoxHandler cboFileNameHandler;


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

	private final ReformatHandler _reformatHandler;
	private boolean _dontReactToBinaryDisplayChanges;

	// text put in file name field to indicate that we should
	// create a temp file for export
	private final String TEMP_FILE_FLAG = "<temp file>";

	// Symbol used by user in Command field to indicate
	// "Put the file name here" when the command is executed.
	private final String FILE_REPLACE_FLAG = "%f";

	/**
	 * Constructor
	 */
	public PopupEditableIOPanel(ColumnDisplayDefinition colDef, Object value, boolean isEditable)
	{
		super(new BorderLayout());

		originalValue = value;	// save for possible future use

		_popupMenu = new TextPopupMenu();

		_colDef = colDef;
		_textArea = CellComponentFactory.getRestorableRSyntaxTextArea(colDef, value);

		_reformatHandler = new ReformatHandler(_textArea, CellComponentFactory.useBinaryEditingPanel(_colDef), () -> onOriginalTextWasRestored());

		if (isEditable)
		{
			_textArea.setEditable(true);
			_textArea.setBackground(SquirrelConstants.CELL_EDITABLE_COLOR);	// tell user it is editable
		}
		else
		{
			_textArea.setEditable(false);
			_reformatHandler.maybeDoInitialAutoReformat();
		}

		_textArea.setLineWrap(true);


		if(   colDef.getSqlType() == Types.VARCHAR
			|| colDef.getSqlType() == Types.CLOB
			|| colDef.getSqlType() == Types.LONGNVARCHAR
			|| colDef.getSqlType() == Types.NVARCHAR
			|| colDef.getSqlType() == Types.SQLXML)
		{
			_textArea.setWrapStyleWord(true);
		}

		// add a panel containing binary data editing options, if needed
		JPanel displayPanel = new JPanel(new BorderLayout());
		_scrollPane = new JScrollPane(_textArea);

		_textFindCtrl = new TextFindCtrl(_textArea, _scrollPane);
		displayPanel.add(_textFindCtrl.getContainerPanel(), BorderLayout.CENTER);
		if (CellComponentFactory.useBinaryEditingPanel(colDef))
		{
			// this is a binary field, so allow for multiple viewing options

			String[] radixListData = {RADIX_HEX, RADIX_DECIMAL, RADIX_OCTAL, RADIX_BINARY};
			radixList = new JComboBox(radixListData);
			radixList.addActionListener(e -> updateBinaryDisplay());
			previousRadixListItem = RADIX_HEX;

			showAscii = new JCheckBox(s_stringMgr.getString("popupeditableIoPanel.showAscii"));
			previousShowAscii = false;
			showAscii.addActionListener(e -> updateBinaryDisplay());

			JPanel displayControlsPanel = new JPanel();
			// use default sequential layout

			displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.numberBase")));
			displayControlsPanel.add(radixList);
			displayControlsPanel.add(new JLabel("    "));	// add some space
			displayControlsPanel.add(showAscii);
			//displayControlsPanel.add(new JLabel(s_stringMgr.getString("popupeditableIoPanel.showAscii")));
			displayPanel.add(displayControlsPanel, BorderLayout.SOUTH);
		}
		add(displayPanel, BorderLayout.CENTER);

		// add controls for file handling, but only if DataType
		// can do File operations
		if (CellComponentFactory.canDoFileIO(colDef))
		{
			// yes it can, so add controls
			add(createExportImportPanel(isEditable), BorderLayout.SOUTH);
		}

		_chkMnuLineWrap = new JCheckBoxMenuItem(new LineWrapAction());
		_chkMnuLineWrap.setSelected(_textArea.getLineWrap());
		_popupMenu.add(_chkMnuLineWrap);

		_chkMnuWordWrap = new JCheckBoxMenuItem(new WordWrapAction());
		_chkMnuWordWrap.setSelected(_textArea.getWrapStyleWord());
		_popupMenu.add(_chkMnuWordWrap);

		_popupMenu.add(new XMLJsonReformatAction());
		_popupMenu.add(new CompareToClipAction()).setToolTipText(s_stringMgr.getString("popupEditableIoPanel.compare.to.clip.tooltip"));
		_popupMenu.setTextComponent(_textArea);

		new PasteFromHistoryAttach(_textArea);
	}

	private void updateBinaryDisplay()
	{
		if(_dontReactToBinaryDisplayChanges)
		{
			return;
		}

		if( _reformatHandler.isReformatted() )
		{
			Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("popupEditableIoPanel.cannot.update.binary.display.while.reformatted"));
			return;
		}

		// user asked to see binary data in a different format
		int base = 16;	// default to hex
		if (previousRadixListItem.equals(RADIX_DECIMAL)) base = 10;
		else if (previousRadixListItem.equals(RADIX_OCTAL)) base = 8;
		else if (previousRadixListItem.equals(RADIX_BINARY)) base = 2;

		Byte[] bytes = BinaryDisplayConverter.convertToBytes(_textArea.getText(),
																			  base, previousShowAscii);

		// return the expected format for this data
		base = 16;	// default to hex
		if (Objects.equals(radixList.getSelectedItem(), RADIX_DECIMAL)) base = 10;
		else if (Objects.equals(radixList.getSelectedItem(), RADIX_OCTAL)) base = 8;
		else if (Objects.equals(radixList.getSelectedItem(), RADIX_BINARY)) base = 2;

		((RestorableRSyntaxTextArea) _textArea).updateText(
				BinaryDisplayConverter.convertToString(bytes, base, showAscii.isSelected()));

		previousRadixListItem = (String)radixList.getSelectedItem();
		previousShowAscii = showAscii.isSelected();
	}

	private void onOriginalTextWasRestored()
	{
		if(null == radixList || null == showAscii)
		{
			return;
		}

		try
		{
			_dontReactToBinaryDisplayChanges = true;
			previousRadixListItem = RADIX_HEX;
			radixList.setSelectedItem(RADIX_HEX);
			showAscii.setSelected(false);
		}
		finally
		{
			_dontReactToBinaryDisplayChanges = false;
		}
	}


	/**
	 * build the user interface for export/import operations
	 */
	private JPanel createExportImportPanel(boolean isEditable)
	{
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
		gbc.weightx=1;
		JComboBox<Object> cboFileName = new JComboBox<>();
		cboFileNameHandler = new EditableComboBoxHandler(cboFileName, getClass().getName() + ".cboFileName");
		cboFileNameHandler.selectFirstItemIfExists();

		Dimension preferredSize = cboFileName.getPreferredSize();
		// ensure, that the text field has a suitable size, even the panel is too small.
		cboFileName.setMinimumSize(new Dimension(preferredSize.width / 2, preferredSize.height));
		gbc.gridx++;
		
		eiPanel.add(cboFileName, gbc);
		gbc.fill = GridBagConstraints.NONE;;
		gbc.weightx=0;

		// add button for Brows
		// i18n[popupeditableIoPanel.browse=Browse]

		JButton browseButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.browse"),
													  Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DIR_GIF));
		browseButton.setToolTipText(s_stringMgr.getString("popupeditableIoPanel.browse.tooltip"));
		browseButton.setActionCommand(ACTION_BROWSE);
		browseButton.addActionListener(e -> onBrowse());


		gbc.gridx++;
		eiPanel.add(browseButton, gbc);


		// i18n[popupeditableIoPanel.export44=Export]
		JButton exportButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.export44"),
													  Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SAVE));
		exportButton.setToolTipText(s_stringMgr.getString("popupeditableIoPanel.export.tooltip"));
		exportButton.setActionCommand(ACTION_EXPORT);
		exportButton.addActionListener(e -> onActionPerformed(e));

		gbc.gridx++;
		eiPanel.add(exportButton, gbc);

		// import and external processing can only be done if
		// panel is editable
		if ( isEditable == false)
		{
			addReformatToggleButton(eiPanel, gbc, _reformatHandler.getBtnReformat());
			addFindButton(eiPanel, gbc);

			return eiPanel;
		}

		// Add import control
		// i18n[popupeditableIoPanel.import44=Import]
		JButton importButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.import44"));
		importButton.setActionCommand(ACTION_IMPORT);
		importButton.addActionListener(e -> onActionPerformed(e));

		gbc.gridx++;
		eiPanel.add(importButton, gbc);

		addReformatToggleButton(eiPanel, gbc, _reformatHandler.getBtnReformat());
		addFindButton(eiPanel, gbc);


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
		externalCommandCombo.setPreferredSize(cboFileName.getPreferredSize());
		externalCommandCombo.setMinimumSize(cboFileName.getMinimumSize());
		
		// ensure, that the text field can use the extra space if the user resize the dialog.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx=0;
		eiPanel.add(externalCommandCombo, gbc);
		gbc.fill = GridBagConstraints.NONE;;
		gbc.weightx=0;
		
		
		// add button to execute external command
		// i18n[popupeditableIoPanel.execute34=Execute]
		JButton externalCommandButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.execute34"));
		externalCommandButton.setActionCommand(ACTION_EXECUTE);
		externalCommandButton.addActionListener(e -> onActionPerformed(e));

		gbc.gridx++;
		eiPanel.add(externalCommandButton, gbc);

		// add button for applying file & cmd info without doing anything else
		// i18n[popupeditableIoPanel.applyFile=Apply File & Cmd]
		JButton applyButton = new JButton(s_stringMgr.getString("popupeditableIoPanel.applyFile"));
		applyButton.setActionCommand(ACTION_APPLY);
		applyButton.addActionListener(e -> onActionPerformed(e));

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
			cboFileNameHandler.addOrReplaceCurrentItem(info.getFileName());
			externalCommandCombo.getEditor().setItem(info.getCommand());
		}

		return eiPanel;
	}

	private JToggleButton addReformatToggleButton(JPanel eiPanel, GridBagConstraints gbc, JToggleButton btnReformat)
	{
		gbc.gridx++;
		eiPanel.add(btnReformat, gbc);
		return btnReformat;
	}

	private void addFindButton(JPanel eiPanel, GridBagConstraints gbc)
	{
		JButton findButton = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND));
		findButton.setActionCommand(ACTION_FIND);
		findButton.addActionListener(e -> onActionPerformed(e));

		gbc.gridx++;
		eiPanel.add(findButton, gbc);
	}


	/**
	 * Return the contents of the editable text area as an Object
	 * converted by the correct DataType function.  Errors in converting
	 * from the text form into the Object are reported
	 * through the messageBuffer.
	 */
	public Object getObject(StringBuffer messageBuffer)
	{
		try
		{
			String text = getTextAreaCannonicalForm();
			return CellComponentFactory.validateAndConvertInPopup(_colDef, originalValue, text, messageBuffer);
		}
		catch (Exception e)
		{
			messageBuffer.append("Failed to convert binary text; error was:\n" + e.getMessage());
			return null;
		}
	}

	/**
	 * When focus is passed to this panel, automatically pass it
	 * on to the text area.
	 */
	public void requestFocus() {
		_textArea.requestFocus();
	}

	/**
	 * Handle actions on the buttons in the file operations panel
	 */
	private void onActionPerformed(ActionEvent e)
	{

		if (e.getActionCommand().equals(ACTION_APPLY))
		{
			onActionApply();
		}
		else if (e.getActionCommand().equals(ACTION_IMPORT))
		{
			onActionImport();
		}
		else if(e.getActionCommand().equals(ACTION_FIND))
		{
			_textFindCtrl.toggleFind();
		}
		else
		{
			FileResult result = createFileResult();
			if( result == null )
			{
				return;
			}

			if (e.getActionCommand().equals(ACTION_EXPORT))
			{
				onActionExport(result);
			}
			else if(e.getActionCommand().equals(ACTION_EXECUTE))
			{
				onActionExecute(result);
			}
		} // end of combined export and execute operations
	}

	private void onActionExecute(FileResult result)
	{
		FileOutputStream outStream = getFileOutputStream(result.file, result.canonicalFilePathName);
		if( outStream == null )
		{
			return;
		}

		onActionExecute(result.file, result.canonicalFilePathName, outStream);
	}

	private void onActionExport(FileResult result)
	{
		// EXPORT OBJECT TO OSX_FILE

		FileOutputStream outStream = getFileOutputStream(result.file, result.canonicalFilePathName);
		if( outStream == null )
		{
			return;
		}

		if( exportData(outStream, result.canonicalFilePathName) == true)
		{

			// if we get here, then everything worked correctly, so
			// tell user that data was put into file.
			// This is different from the Import strategy
			// because the user may not know the name of the file
			// that was used if they selected the automatic temp file
			// operation, or they may not know what directory the file
			// was actually put into, so this tells them the full file path.
			Object[] options = {
					s_stringMgr.getString("popupeditableIoPanel.ok"),
					s_stringMgr.getString("popupeditableIoPanel.ok.open.in.file.manager")
			};

			int selectIndex = JOptionPane.showOptionDialog(this,
					s_stringMgr.getString("popupeditableIoPanel.exportedToFile", result.canonicalFilePathName),
					s_stringMgr.getString("popupeditableIoPanel.exportSuccess"),
					JOptionPane.OK_OPTION,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					options,
					options[0]
			);

			if(1 == selectIndex)
			{
				DesktopUtil.openInFileManager(result.file);
			}
		}
	}

	private FileResult createFileResult()
	{
		// GET OSX_FILE FOR EXPORT & EXTERNAL PROCESSING

		String canonicalFilePathName = cboFileNameHandler.getItem();

		// file name verification operations are the same for both
		// export and execute, so do that work here for both.
		//
		// If file name is null or empty, do not proceed
		if (cboFileNameHandler.getItem() == null ||
			 cboFileNameHandler.getItem().equals(""))
		{

			JOptionPane.showMessageDialog(this,

					// i18n[popupeditableIoPanel.noExportFile=No file name given for export.\nPlease enter a file name  or use Browse before clicking Export.]
					s_stringMgr.getString("popupeditableIoPanel.noExportFile"),
					// i18n[popupeditableIoPanel.exportError=Export Error]
					s_stringMgr.getString("popupeditableIoPanel.exportError"), JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// create the file to open
		//user must have supplied a file name.
		File file = new File(cboFileNameHandler.getItem());

		try
		{
			canonicalFilePathName = file.getCanonicalPath();
		}
		catch (Exception ex)
		{
			// an error here may mean that the file cannot be
			// reached or has moved or some such.  In any case,
			// the file cannot be used for export.

			String msg = s_stringMgr.getString("popupeditableIoPanel.cannotAccessFile", cboFileNameHandler.getItem());
			s_log.error(msg, ex);

			JOptionPane.showMessageDialog(this,
													msg,
													s_stringMgr.getString("popupeditableIoPanel.exportError3"), JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// see if file exists
		if(file.exists())
		{
			if(!file.isFile())
			{
				JOptionPane.showMessageDialog(this,
														// i18n[popupeditableIoPanel.notANormalFile=File is not a normal file.\n Cannot do export to a directory or system file.]
														s_stringMgr.getString("popupeditableIoPanel.notANormalFile"),
														// i18n[popupeditableIoPanel.exportError4=Export Error]
														s_stringMgr.getString("popupeditableIoPanel.exportError4"), JOptionPane.ERROR_MESSAGE);
				return null;
			}
			if(!file.canWrite())
			{
				JOptionPane.showMessageDialog(this,
														// i18n[popupeditableIoPanel.notWriteable=File is not writeable.\nChange file permissions or select a differnt file for export.]
														s_stringMgr.getString("popupeditableIoPanel.notWriteable"),
														// i18n[popupeditableIoPanel.exportError5=Export Error]
														s_stringMgr.getString("popupeditableIoPanel.exportError5"), JOptionPane.ERROR_MESSAGE);
				return null;
			}
			// file exists, is normal and is writable, so see if user
			// wants to overwrite contents of file
			int option = JOptionPane.showConfirmDialog(this,
																	 // i18n[popupeditableIoPanel.fileOverwrite=File {0} already exists.\n\nDo you wish to overwrite this file?]
																	 s_stringMgr.getString("popupeditableIoPanel.fileOverwrite", canonicalFilePathName),
																	 // i18n[popupeditableIoPanel.overwriteWarning=File Overwrite Warning]
																	 s_stringMgr.getString("popupeditableIoPanel.overwriteWarning"), JOptionPane.YES_NO_OPTION);
			if(option != JOptionPane.YES_OPTION)
			{
				// user does not want to overwrite the file

				// we could tell user here that export was canceled,
				// but I don't think its necessary, and that avoids
				// forcing user to do yet another annoying mouse click.
				return null;
			}
			// user is ok with overwriting file
			// We do not need to do anything special to overwrite
			// (as opposed to appending) since the OutputString
			// starts at the beginning of the file by default.
		}
		else
		{
			// file does not already exist, so try to create it
			try
			{
				if( false == createNewFileInclNeededDirs(file) )
				{
					JOptionPane.showMessageDialog(this,
															s_stringMgr.getString("popupeditableIoPanel.createFileError", canonicalFilePathName),
															s_stringMgr.getString("popupeditableIoPanel.exportError6"), JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			catch (Exception ex)
			{
				String msg = s_stringMgr.getString("popupeditableIoPanel.cannotOpenFile", canonicalFilePathName, ex.getMessage());
				s_log.error(msg, ex);

				JOptionPane.showMessageDialog(this, msg, s_stringMgr.getString("popupeditableIoPanel.exportError7"), JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}


		String extCmdComboItemStr = null;
		if (externalCommandCombo != null
				&& externalCommandCombo.getEditor() != null)
		{
			extCmdComboItemStr =
					(String) externalCommandCombo.getEditor().getItem();
		}

		// if user did anything other than default, then save
		// their options
		if ((extCmdComboItemStr != null && extCmdComboItemStr.length() > 0))
		{

			// This may be called either when the table is editable or when it is
			// read-only.  When it is read-only, there is no command to be saved,
			// but when it is editable, there may be a command.
			String commandString = extCmdComboItemStr;

			CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(), cboFileNameHandler.getItem(),
					commandString);
		}
		FileResult result = new FileResult(canonicalFilePathName, file);
		return result;
	}

	private static boolean createNewFileInclNeededDirs(File file) throws IOException
	{
		if(null != file.getParentFile())
		{
			file.getParentFile().mkdirs();
		}

		return file.createNewFile();
	}

	// at this point we have an actual file that we can output to,
	// so create the output stream
	// (so that data type objects do not have to).
	private FileOutputStream getFileOutputStream(File file, String canonicalFilePathName)
	{
		// We have done everything we can prior to this point
		// to ensure that the the file is accessible, but it is
		// still possible that an existing file was removed
		// at a bad moment.  Also, the compiler insists that we
		// put this in a try statement
		FileOutputStream outStream;
		try
		{
			outStream = new FileOutputStream(file);
		}
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("popupeditableIoPanel.cannotFindFile", canonicalFilePathName);
			s_log.error(msg, ex);

			JOptionPane.showMessageDialog(this,
													msg,
													s_stringMgr.getString("popupeditableIoPanel.exportError8"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return outStream;
	}

	private void onActionExecute(File file, String canonicalFilePathName, FileOutputStream outStream)
	{
		// EXPORT OBJECT TO OSX_FILE, EXECUTE PROGRAM ON IT, IMPORT IT BACK

		if(((String) externalCommandCombo.getEditor().getItem()) == null ||
			((String) externalCommandCombo.getEditor().getItem()).length() == 0)
		{
			// cannot execute a null command
			JOptionPane.showMessageDialog(this,
													// i18n[popupeditableIoPanel.cannotExec=Cannot execute a null command.\nPlease enter a command in the Command field before clicking on Execute.]
													s_stringMgr.getString("popupeditableIoPanel.cannotExec"),
													// i18n[popupeditableIoPanel.executeError=Execute Error]
													s_stringMgr.getString("popupeditableIoPanel.executeError"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		// replace any instance of flag in command with file name
		String command = ((String) externalCommandCombo.getEditor().getItem());

		int index;
		while ((index = command.indexOf(FILE_REPLACE_FLAG)) >= 0)
		{
			command = command.substring(0, index) +
						 canonicalFilePathName +
						 command.substring(index + FILE_REPLACE_FLAG.length());
		}

		// export data to file
		if( exportData(outStream, canonicalFilePathName) == false)
		{
			// bad export - do not proceed with command
			// The exportData() method has already put up a message
			// to the user saying the export failed.
			return;
		}

		int commandResult;
		BufferedReader err = null;
		try
		{
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
			if(errMsg != null)
			{
				throw new IOException(
						"text on error stream from command starting with:\n" + errMsg);
			}
		}
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("popupeditableIoPanel.errWhileExecutin", command, ex.getMessage());
			s_log.error(msg, ex);


			JOptionPane.showMessageDialog(this,
													msg,
													s_stringMgr.getString("popupeditableIoPanel.executeError2"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		finally
		{
			_iou.closeReader(err);
		}

		// check for possibly bad return from child
		if(commandResult != 0)
		{
			// command returned non-standard value.
			// ask user before proceeding.
			int option = JOptionPane.showConfirmDialog(this,
																	 // i18n[popupeditableIoPanel.commandReturnNot0=The convention for command returns is that 0 means success, but this command returned {0}.\nDo you wish to import the file contents anyway?]
																	 s_stringMgr.getString("popupeditableIoPanel.commandReturnNot0", Integer.valueOf(commandResult)),

																	 // i18n[popupeditableIoPanel.importWarning=Import Warning]
																	 s_stringMgr.getString("popupeditableIoPanel.importWarning"), JOptionPane.YES_NO_OPTION);
			if(option != JOptionPane.YES_OPTION)
			{
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

	private void onActionImport()
	{
		File file;
		// IMPORT OBJECT FROM OSX_FILE

		if (StringUtilities.isEmpty(cboFileNameHandler.getItem(), true))
		{
			// not allowed - must have existing file for import
			JOptionPane.showMessageDialog(this,
					// i18n[popupeditableIoPanel.selectImportDataFile=You must select an existing file to import data from.]
					s_stringMgr.getString("popupeditableIoPanel.selectImportDataFile"),
					// i18n[popupeditableIoPanel.noFile=No File Selected]
					s_stringMgr.getString("popupeditableIoPanel.noFile"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		// get name of file, which must exist
		file = new File(cboFileNameHandler.getItem());
		if (!file.exists() || !file.isFile() || !file.canRead())
		{
			// not something we can read

			// i18n[popupeditableIoPanel.fileDoesNotExist=File {0} does not exist,\nor is not a readable, normal file.\nPlease enter a valid file name or use Browse to select a file.]
			String msg = s_stringMgr.getString("popupeditableIoPanel.fileDoesNotExist", cboFileNameHandler.getItem());

			JOptionPane.showMessageDialog(this, msg,

					// i18n[popupeditableIoPanel.fileError=File Name Error]
					s_stringMgr.getString("popupeditableIoPanel.fileError"), JOptionPane.ERROR_MESSAGE);
			return;
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
				_colDef.getFullTableColumnName(), cboFileNameHandler.getItem(),
				((String) externalCommandCombo.getEditor().getItem()));
	}

	private void onActionApply()
	{
		// If file name default and cmd is null or empty,
		// make sure this entry is not being held in CellImportExportInfoSaver
		if ((cboFileNameHandler.getItem() != null) &&
			 (externalCommandCombo.getEditor().getItem() == null ||
						((String) externalCommandCombo.getEditor().getItem()).length() == 0))
		{
			// user has not entered anything or has reset to defaults,
			// so make sure there is no entry for this column in the
			// saved info
			CellImportExportInfoSaver.remove(_colDef.getFullTableColumnName());
		}
		else
		{
			// user has entered some non-default info, so save it
			CellImportExportInfoSaver.getInstance().save(
					_colDef.getFullTableColumnName(), cboFileNameHandler.getItem(),
					((String) externalCommandCombo.getEditor().getItem()));
		}
	}

	private void onBrowse()
	{
		JFileChooser chooser = new JFileChooser();
		if( false == StringUtilities.isEmpty(cboFileNameHandler.getItem(), true))
		{
			File f = new File(cboFileNameHandler.getItem());
			chooser.setCurrentDirectory(f);
		}

		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			//	System.out.println("You chose to open this file: " +
			//		chooser.getSelectedFile().getName());
			try
			{
				cboFileNameHandler.addOrReplaceCurrentItem(chooser.getSelectedFile().getCanonicalPath());
				cboFileNameHandler.saveCurrentItem();
			}
			catch (Exception ex)
			{
				// should not happen since the file that was selected was
				// just being shown in the Chooser dialog, but just to be safe...
				String msg = s_stringMgr.getString("popupeditableIoPanel.errorGettingPath");

				s_log.error(msg, ex);

				JOptionPane.showMessageDialog(this,
														msg,
														s_stringMgr.getString("popupeditableIoPanel.fileChooserError"), JOptionPane.ERROR_MESSAGE);
			}
		}
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
		String canonicalFilePathName = cboFileNameHandler.getItem();
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
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("popupeditableIoPanel.fileOpenError", canonicalFilePathName, ex.getMessage());
			s_log.error(msg, ex);

			JOptionPane.showMessageDialog(this,
													msg,
													s_stringMgr.getString("popupeditableIoPanel.fileOpenErrorHeader"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		// hand file input stream to DataType object for import
		// Also, handle File IO errors here so that DataType objects
		// do not have to.
		try
		{

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
				! (radixList.getSelectedItem().equals(RADIX_HEX) &&
					showAscii.isSelected() == false) ) {
				// we need to convert to a different format
				int base = 16;	// default to hex
				if (radixList.getSelectedItem().equals(RADIX_DECIMAL)) base = 10;
				else if (radixList.getSelectedItem().equals(RADIX_OCTAL)) base = 8;
				else if (radixList.getSelectedItem().equals(RADIX_BINARY)) base = 2;

				Byte[] bytes = BinaryDisplayConverter.convertToBytes(replacementText,
					16, false);

				// return the expected format for this data
				replacementText = BinaryDisplayConverter.convertToString(bytes,
					base, showAscii.isSelected());
			}

			((RestorableRSyntaxTextArea) _textArea).updateText(replacementText);
		}
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("popupeditableIoPanel.errorReadingFile", canonicalFilePathName, ex.getMessage());

			s_log.error(msg, ex);

			JOptionPane.showMessageDialog(this,
													msg,
													s_stringMgr.getString("popupeditableIoPanel.importError2"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		// cleanup resources used
		try
		{
			inStream.close();
		}
		catch (Exception ex)
		{
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
	private boolean exportData(FileOutputStream outStream, String canonicalFilePathName)
	{

		// hand file output stream to DataType object for export
		// Also, handle File IO errors here so that DataType objects
		// do not have to.
		try
		{

			// Hand the current text to the DataType object.
			// DataType object is responsible for validating
			// that the text makes sense for this type of object
			// and converting it to the proper form for output.
			// All errors are handled as IOExceptions
			CellComponentFactory.exportObject(_colDef, outStream, getTextAreaCannonicalForm());

			cboFileNameHandler.saveCurrentItem();

		}
		catch (Exception ex)
		{
			String msg = s_stringMgr.getString("popupeditableIoPanel.errorWritingFile", canonicalFilePathName, ex.getMessage());
			s_log.error(msg, ex);

			JOptionPane.showMessageDialog(this, msg, s_stringMgr.getString("popupeditableIoPanel.exportError100"), JOptionPane.ERROR_MESSAGE);
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
			_textArea.addMouseListener(_lis);
		}
	}

	public void removeNotify()
	{
		super.removeNotify();
		if (_lis != null)
		{
			_textArea.removeMouseListener(_lis);
			_lis = null;
		}
	}

	public GlobalFindRemoteControl getDisplayHandlerFindRemoteControlOrNull()
	{
		return _textFindCtrl.getFindRemoteControl();
	}

	private class LineWrapAction extends BaseAction
	{
		LineWrapAction()
		{
			// i18n[popupEditableIoPanel.wrapLines=Wrap Lines on/off]
			super(s_stringMgr.getString("popupEditableIoPanel.wrapLines"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_textArea != null)
			{
				lineWrapWordWrapChanged(false);
			}
		}
	}

	private class WordWrapAction extends BaseAction
	{

		WordWrapAction()
		{
			// i18n[popupEditableIoPanel.wrapWord=Wrap on Word on/off]
			super(s_stringMgr.getString("popupEditableIoPanel.wrapWord"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_textArea != null)
			{
				lineWrapWordWrapChanged(true);
			}
		}
	}

	private void lineWrapWordWrapChanged(boolean wordWrapChanged)
	{
		if(wordWrapChanged)
		{
			_textArea.setWrapStyleWord(!_textArea.getWrapStyleWord());
			if(_textArea.getWrapStyleWord())
			{
				// Word wrap requires line wrap.
				_textArea.setLineWrap(true);
			}
		}
		else
		{
			_textArea.setLineWrap(!_textArea.getLineWrap());
			if(_textArea.getWrapStyleWord())
			{
				// Word wrap requires line wrap.
				_textArea.setWrapStyleWord(false);
			}
		}

		_chkMnuLineWrap.setSelected(_textArea.getLineWrap());
		_chkMnuWordWrap.setSelected(_textArea.getWrapStyleWord());
	}


	private class XMLJsonReformatAction extends BaseAction
	{
      XMLJsonReformatAction()
		{
			// i18n[popupEditableIoPanel.reformatXml=Reformat XML]
			super(s_stringMgr.getString("popupEditableIoPanel.popup.menu.reformatXml"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			_reformatHandler.setReformated(true);
		}
	}

	private class CompareToClipAction extends BaseAction
	{
		CompareToClipAction()
		{
			// i18n[popupEditableIoPanel.reformatXml=Reformat XML]
			super(s_stringMgr.getString("popupEditableIoPanel.compare.to.clip"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			compareToClip();
		}
	}

	private void compareToClip()
	{

		String clipboardAsString = ClipboardUtil.getClipboardAsString();

		if(StringUtilities.isEmpty(clipboardAsString, true))
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("popupEditableIoPanel.clipboard.empty.warn"));
			return;
		}


		String cellText = _textArea.getSelectedText();

		if(StringUtilities.isEmpty(cellText, true))
		{
			cellText = _textArea.getText();
		}

		if(StringUtilities.isEmpty(cellText, true))
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("popupEditableIoPanel.cell.empty.warn"));
			return;
		}

		Path leftClipboardTempFile = TableSelectionDiffUtil.createLeftTempFile(clipboardAsString);

		Path rightCellTextTempFile = TableSelectionDiffUtil.createRightTempFile(cellText);

		String title = s_stringMgr.getString("popupEditableIoPanel.clipboard.vs.cell.data");
		DBDIffService.showDiff(leftClipboardTempFile, rightCellTextTempFile, title, GUIUtils.getOwningWindow(_textArea));
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
		if (_textArea.getText() == null ||
			_textArea.getText().equals(StringUtilities.NULL_AS_STRING) ||
			_textArea.getText().length() == 0)
			return _textArea.getText();

		// if the data is not binary, then there is no need for conversion.
		// if the data is Hex with ASCII not shown as chars, then no conversion needed.
		if (radixList == null ||
			(radixList.getSelectedItem().equals(RADIX_HEX) && ! showAscii.isSelected()) ) {
			// no need for conversion
			return _textArea.getText();
		}

		// The field is binary and not in the format expected by the DataType
		int base = 16;	// default to hex
		if (radixList.getSelectedItem().equals(RADIX_DECIMAL)) base = 10;
		else if (radixList.getSelectedItem().equals(RADIX_OCTAL)) base = 8;
		else if (radixList.getSelectedItem().equals(RADIX_BINARY)) base = 2;

		// the following can cause and exception if the text is not formatted correctly
		Byte[] bytes = BinaryDisplayConverter.convertToBytes(_textArea.getText(),
			base, showAscii.isSelected());

		// return the expected format for this data
		return BinaryDisplayConverter.convertToString(bytes, 16, false);
	}
}
