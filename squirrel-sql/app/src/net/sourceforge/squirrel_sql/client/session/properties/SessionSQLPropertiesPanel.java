package net.sourceforge.squirrel_sql.client.session.properties;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.gui.CharField;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionSQLPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	private IApplication _app;
	private SessionProperties _props;

	private SQLPropertiesPanel _myPanel;

	public SessionSQLPropertiesPanel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new SQLPropertiesPanel(app);
	}

	public void initialize(IApplication app)
	{
		_props = app.getSquirrelPreferences().getSessionProperties();
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
		return SQLPropertiesPanel.SQLPropertiesPanelI18n.SQL;
	}

	public String getHint()
	{
		return SQLPropertiesPanel.SQLPropertiesPanelI18n.SQL;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class SQLPropertiesPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface SQLPropertiesPanelI18n
		{
			String ALL_OTHER = "All Other Data Types";
			String AUTO_COMMIT = "Auto Commit SQL";
			String BINARY = "Binary";
			String BLOB = "Blob";
			String CLOB = "Clob";
			String COMMIT_ON_CLOSE = "Commit On Closing Session";
			String NBR_BYTES = "Number of bytes to read:";
			String NBR_CHARS = "Number of chars to read:";
			String NBR_ROWS_CONTENTS = "Number of rows:";
			String NBR_ROWS_SQL = "Number of rows:";
			String LIMIT_ROWS_CONTENTS = "Contents - Limit rows";
			String LIMIT_ROWS_SQL = "SQL - Limit rows";
			String LONGVARBINARY = "LongVarBinary";
			String SQL_OTHER = "SQL Other";
			String SHOW_ROW_COUNT = "Show Row Count for Tables (can slow application)";
			String SOL_COMENT = "Start of Line Comment";
			String TABLE = "Table";
			String TEXT = "Text";
			String STATEMENT_SEPARATOR = "Statement Separator:";
			String SQL = "SQL";
			String VARBINARY = "VarBinary";
		}

		private JCheckBox _autoCommitChk = new JCheckBox(SQLPropertiesPanelI18n.AUTO_COMMIT);
		private JCheckBox _commitOnClose = new JCheckBox(SQLPropertiesPanelI18n.COMMIT_ON_CLOSE);
		private IntegerField _contentsNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _contentsLimitRowsChk = new JCheckBox(SQLPropertiesPanelI18n.LIMIT_ROWS_CONTENTS);
		private JCheckBox _showRowCountChk = new JCheckBox(SQLPropertiesPanelI18n.SHOW_ROW_COUNT);
		private IntegerField _sqlNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _sqlLimitRowsChk = new JCheckBox(SQLPropertiesPanelI18n.LIMIT_ROWS_SQL);
		private CharField _stmtSepCharField = new CharField(' ');
		private JTextField _solCommentField = new JTextField(2);

		private JCheckBox _showBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.BINARY);
		private JCheckBox _showVarBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.VARBINARY);
		private JCheckBox _showLongVarBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.LONGVARBINARY);

		private JCheckBox _showBlobChk = new JCheckBox(SQLPropertiesPanelI18n.BLOB);
		private JCheckBox _showClobChk = new JCheckBox(SQLPropertiesPanelI18n.CLOB);

		private final ReadTypeCombo _blobTypeDrop = new ReadTypeCombo();
		private final ReadTypeCombo _clobTypeDrop = new ReadTypeCombo();

		private IntegerField _showBlobSizeField = new IntegerField(5);
		private IntegerField _showClobSizeField = new IntegerField(5);

		private JCheckBox _showSQLOtherChk = new JCheckBox(SQLPropertiesPanelI18n.SQL_OTHER);

		private JCheckBox _showAllOtherChk = new JCheckBox(SQLPropertiesPanelI18n.ALL_OTHER);

		/**
		 * This object will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final ControlMediator _controlMediator = new ControlMediator();

		SQLPropertiesPanel(IApplication app)
		{
			super();
			createGUI(app);
		}

		void loadData(SessionProperties props)
		{
			_autoCommitChk.setSelected(props.getAutoCommit());
			_commitOnClose.setSelected(props.getCommitOnClosingConnection());
			_contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
			_contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
			_sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());
			_sqlLimitRowsChk.setSelected(props.getSQLLimitRows());
			_showRowCountChk.setSelected(props.getShowRowCount());
			_stmtSepCharField.setChar(props.getSQLStatementSeparatorChar());
			_solCommentField.setText(props.getStartOfLineComment());

			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();
			_showBinaryChk.setSelected(largeObjInfo.getReadBinary());
			_showVarBinaryChk.setSelected(largeObjInfo.getReadVarBinary());
			_showLongVarBinaryChk.setSelected(largeObjInfo.getReadLongVarBinary());

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
			props.setAutoCommit(_autoCommitChk.isSelected());
			props.setCommitOnClosingConnection(_commitOnClose.isSelected());
			props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
			props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());
			props.setSQLNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
			props.setSQLLimitRows(_sqlLimitRowsChk.isSelected());
			props.setShowRowCount(_showRowCountChk.isSelected());
			props.setSQLStatementSeparatorChar(_stmtSepCharField.getChar());
			props.setStartOfLineComment(_solCommentField.getText());

			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();
			largeObjInfo.setReadBinary(_showBinaryChk.isSelected());
			largeObjInfo.setReadVarBinary(_showVarBinaryChk.isSelected());
			largeObjInfo.setReadLongVarBinary(_showLongVarBinaryChk.isSelected());

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
			_commitOnClose.setEnabled(!_autoCommitChk.isSelected());

			_contentsNbrRowsToShowField.setEnabled(_contentsLimitRowsChk.isSelected());
			_sqlNbrRowsToShowField.setEnabled(_sqlLimitRowsChk.isSelected());

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
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createSQLPanel(app), gbc);

			++gbc.gridy;
			add(createDataTypesPanel(), gbc);
		}

		private JPanel createSQLPanel(IApplication app)
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(0, 4, 0, 4);
			gbc.anchor = gbc.WEST;

			_autoCommitChk.addChangeListener(_controlMediator);
			_contentsLimitRowsChk.addChangeListener(_controlMediator);
			_sqlLimitRowsChk.addChangeListener(_controlMediator);

			_contentsNbrRowsToShowField.setColumns(5);
			_sqlNbrRowsToShowField.setColumns(5);
			_stmtSepCharField.setColumns(1);

			// First column.
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_autoCommitChk, gbc);
			++gbc.gridy;
			gbc.gridwidth = 3;
			pnl.add(_showRowCountChk, gbc);
			gbc.gridwidth = 1;
			++gbc.gridy;
			pnl.add(_contentsLimitRowsChk, gbc);
			++gbc.gridy;
			pnl.add(_sqlLimitRowsChk, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.gridwidth = 3;
			pnl.add(_commitOnClose, gbc);
			gbc.gridy+=2;
			gbc.gridwidth = 1;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.NBR_ROWS_CONTENTS), gbc);
			++gbc.gridy;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.NBR_ROWS_SQL), gbc);
			++gbc.gridy;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.STATEMENT_SEPARATOR), gbc);
			++gbc.gridy;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.SOL_COMENT), gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			++gbc.gridy;
			++gbc.gridy;
			gbc.gridwidth = 3;
			pnl.add(_contentsNbrRowsToShowField, gbc);
			++gbc.gridy;
			pnl.add(_sqlNbrRowsToShowField, gbc);
			++gbc.gridy;
			pnl.add(_stmtSepCharField, gbc);
			++gbc.gridy;
			pnl.add(_solCommentField, gbc);

			return pnl;
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
			pnl.setBorder(BorderFactory.createTitledBorder("Show String Data for Columns of these Data Types"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(0, 4, 0, 4);
			gbc.anchor = gbc.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnl.add(_showBinaryChk, gbc);

			++gbc.gridy;
			gbc.gridwidth = 2;
			pnl.add(_showLongVarBinaryChk, gbc);

			gbc.gridwidth = 1;
			++gbc.gridy;
			pnl.add(_showBlobChk, gbc);

			++gbc.gridy;
			pnl.add(_showClobChk, gbc);

			++gbc.gridy;
			pnl.add(_showAllOtherChk, gbc);

			gbc.gridy = 2;
			++gbc.gridx;
			pnl.add(new RightLabel("Read"), gbc);

			++gbc.gridy;
			pnl.add(new RightLabel("Read"), gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			pnl.add(_showVarBinaryChk, gbc);

			++gbc.gridy;
			pnl.add(_showSQLOtherChk, gbc);

			++gbc.gridy;
			pnl.add(_blobTypeDrop, gbc);

			++gbc.gridy;
			pnl.add(_clobTypeDrop, gbc);

			++gbc.gridx;
			gbc.gridy = 2;
			pnl.add(_showBlobSizeField, gbc);

			++gbc.gridy;
			pnl.add(_showClobSizeField, gbc);

			++gbc.gridx;
			gbc.gridy = 2;
			pnl.add(new JLabel("Bytes"), gbc);

			++gbc.gridy;
			pnl.add(new JLabel("Chars"), gbc);

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