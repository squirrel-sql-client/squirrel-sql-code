package net.sourceforge.squirrel_sql.client.session.properties;
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This panel allows the user to tailor formatting settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class FormatSessionPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Application API. */
	private final IApplication _app;

	/** The actual GUI panel that allows user to do the maintenance. */
	private final FormatPropertiesPanel _myPanel;

	/** Session properties object being maintained. */
	private SessionProperties _props;

	/**
	 * ctor specifying the Application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public FormatSessionPropertiesPanel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new FormatPropertiesPanel(app);
	}


	public void initialize(IApplication app)
	{
		_props = _app.getSquirrelPreferences().getSessionProperties();
		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_props = session.getProperties();
		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _myPanel;
	}

	public String getTitle()
	{
		return FormatPropertiesPanel.i18n.TITLE;
	}

	public String getHint()
	{
		return FormatPropertiesPanel.i18n.HINT;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class FormatPropertiesPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String ALL_OTHER = "All Other Data Types";
			String BLOB = "Blob";
			String CLOB = "Clob";
			String HINT = "Specify formatting options";
			String NBR_BYTES = "Number of bytes to read:";
			String NBR_CHARS = "Number of chars to read:";
			String SQL_OTHER = "SQL Other";
			String TITLE = "Format";
			String BLOB_WARNING = "Some DBMSs implement BLOB/CLOB fields as other Data Types.\nThe following works only for Data Types 2004(BLOB) and 2005(CLOB).";
			String OTHER_TYPE_WARNING = "The following Data Types are not the same in all DBMSs and\nmay cause exceptions if interpreted as Strings.";
		}

		private JCheckBox _showBlobChk = new JCheckBox(i18n.BLOB);
		private JCheckBox _showClobChk = new JCheckBox(i18n.CLOB);

		private final ReadTypeCombo _blobTypeDrop = new ReadTypeCombo();
		private final ReadTypeCombo _clobTypeDrop = new ReadTypeCombo();

		private IntegerField _showBlobSizeField = new IntegerField(5);
		private IntegerField _showClobSizeField = new IntegerField(5);

		private JCheckBox _showSQLOtherChk = new JCheckBox(i18n.SQL_OTHER);

		private JCheckBox _showAllOtherChk = new JCheckBox(i18n.ALL_OTHER);

		/**
		 * This object will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final ControlMediator _controlMediator = new ControlMediator();

		FormatPropertiesPanel(IApplication app)
		{
			super();
			createGUI(app);
		}

		void loadData(SessionProperties props)
		{
			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();

			_showBlobChk.setSelected(largeObjInfo.getReadBlobs());
			_showBlobSizeField.setInt(largeObjInfo.getReadBlobsSize());
			_blobTypeDrop.setSelectedIndex(largeObjInfo.getReadCompleteBlobs()
												? ReadTypeCombo.READ_ALL_IDX
												: ReadTypeCombo.READ_PARTIAL_IDX);

			_showClobChk.setSelected(largeObjInfo.getReadClobs());
			_showClobSizeField.setInt(largeObjInfo.getReadClobsSize());
			_clobTypeDrop.setSelectedIndex(largeObjInfo.getReadCompleteClobs()
												? ReadTypeCombo.READ_ALL_IDX
												: ReadTypeCombo.READ_PARTIAL_IDX);

			_showSQLOtherChk.setSelected(largeObjInfo.getReadSQLOther());
			_showAllOtherChk.setSelected(largeObjInfo.getReadAllOther());

			updateControlStatus();
		}

		void applyChanges(SessionProperties props)
		{
			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();

			largeObjInfo.setReadBlobs(_showBlobChk.isSelected());
			largeObjInfo.setReadBlobsSize(_showBlobSizeField.getInt());
			largeObjInfo.setReadCompleteBlobs(
						_blobTypeDrop.getSelectedIndex() == ReadTypeCombo.READ_ALL_IDX);

			largeObjInfo.setReadClobsSize(_showClobSizeField.getInt());
			largeObjInfo.setReadClobs(_showClobChk.isSelected());
			largeObjInfo.setReadCompleteClobs(
				_clobTypeDrop.getSelectedIndex() == ReadTypeCombo.READ_ALL_IDX);

			largeObjInfo.setReadSQLOther(_showSQLOtherChk.isSelected());
			largeObjInfo.setReadAllOther(_showAllOtherChk.isSelected());
		}

		private void updateControlStatus()
		{
			final boolean showBlobs = _showBlobChk.isSelected();
			final boolean showPartialBlobs =
				_blobTypeDrop.getSelectedIndex() == ReadTypeCombo.READ_PARTIAL_IDX;
			_showBlobSizeField.setEnabled(showBlobs && showPartialBlobs);
			_blobTypeDrop.setEnabled(showBlobs);

			final boolean showClobs = _showClobChk.isSelected();
			final boolean showPartialClobs =
				_clobTypeDrop.getSelectedIndex() == ReadTypeCombo.READ_PARTIAL_IDX;
			_showClobSizeField.setEnabled(showClobs && showPartialClobs);
			_clobTypeDrop.setEnabled(showClobs);
		}

		private void createGUI(IApplication app)
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createDataTypesPanel(), gbc);
		}

		private JPanel createDataTypesPanel()
		{
			_showBlobSizeField.setColumns(5);
			_showClobSizeField.setColumns(5);

			_showBlobChk.addActionListener(_controlMediator);
			_showClobChk.addActionListener(_controlMediator);
			_blobTypeDrop.addActionListener(_controlMediator);
			_clobTypeDrop.addActionListener(_controlMediator);

			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Show Data as Strings for Columns of these Data Types"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			pnl.add(new JLabel(""), gbc);
			
			++gbc.gridy;	// leave a blank line to separate text from header
			gbc.gridwidth = GridBagConstraints.REMAINDER;

			JTextArea text1 = new JTextArea(i18n.BLOB_WARNING);
			text1.setEditable(false);
			pnl.add(text1, gbc);


			gbc.gridwidth = 1;
			++gbc.gridy;
			gbc.gridx = 0;
			pnl.add(_showBlobChk, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel("Read"), gbc);

			++gbc.gridx;
			pnl.add(_blobTypeDrop, gbc);

			++gbc.gridx;
			pnl.add(_showBlobSizeField, gbc);
			
			++gbc.gridx;
			pnl.add(new JLabel("Bytes"), gbc);
			

			++gbc.gridy;
			gbc.gridx = 0;
			pnl.add(_showClobChk, gbc);			
			
			++gbc.gridx;
			pnl.add(new RightLabel("Read"), gbc);

			++gbc.gridx;
			pnl.add(_clobTypeDrop, gbc);

			++gbc.gridx;
			pnl.add(_showClobSizeField, gbc);

			++gbc.gridx;
			pnl.add(new JLabel("Chars"), gbc);


			++gbc.gridy;	// leave blank line for visual separation
			pnl.add(new JLabel(""), gbc);
			
			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			JTextArea text2 = new JTextArea(i18n.OTHER_TYPE_WARNING);
			text2.setEditable(false);
			pnl.add(text2, gbc);
						
			++gbc.gridy;
			pnl.add(_showAllOtherChk, gbc);

			++gbc.gridy;
			pnl.add(_showSQLOtherChk, gbc);


			return pnl;
		}

		private static final class RightLabel extends JLabel
		{
			RightLabel(String title)
			{
				super(title, SwingConstants.RIGHT);
			}
		}

		private static final class ReadTypeCombo extends JComboBox
		{
			static final int READ_ALL_IDX = 1;
			static final int READ_PARTIAL_IDX = 0;

			ReadTypeCombo()
			{
				addItem("only the first");
				addItem("all");
			}
		}

		/**
		 * This class will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final class ControlMediator implements ChangeListener,
															ActionListener
		{
			public void stateChanged(ChangeEvent evt)
			{
				updateControlStatus();
			}

			public void actionPerformed(ActionEvent evt)
			{
				updateControlStatus();
			}
		}
	}
}
