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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.gui.CharField;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
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
			String SHOW_ROW_COUNT = "Show Row Count for Tables (can slow application)";
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
		private JCheckBox _showRowCount = new JCheckBox(SQLPropertiesPanelI18n.SHOW_ROW_COUNT);
		private IntegerField _sqlNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _sqlLimitRows = new JCheckBox(SQLPropertiesPanelI18n.LIMIT_ROWS_SQL);
		private CharField _stmtSepChar = new CharField(' ');

		private JCheckBox _showBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.BINARY);
		private JCheckBox _showVarBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.VARBINARY);
		private JCheckBox _showLongVarBinaryChk = new JCheckBox(SQLPropertiesPanelI18n.LONGVARBINARY);

		private JCheckBox _showBlobChk = new JCheckBox(SQLPropertiesPanelI18n.BLOB);
		private JCheckBox _showClobChk = new JCheckBox(SQLPropertiesPanelI18n.CLOB);

		private IntegerField _showBlobSize = new IntegerField(5);
		private IntegerField _showClobSize = new IntegerField(5);

		/** Label displaying the selected font. */
		private JLabel _fontLbl = new JLabel();

		/** Button to select font. */
		private FontButton _fontBtn = new FontButton("Font", _fontLbl);

		SQLPropertiesPanel(IApplication app)
		{
			super();
			createUserInterface(app);
		}

		void loadData(SessionProperties props)
		{
			_autoCommitChk.setSelected(props.getAutoCommit());
			_commitOnClose.setSelected(props.getCommitOnClosingConnection());
			_contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
			_contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
			_sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());
			_sqlLimitRows.setSelected(props.getSQLLimitRows());
			_showRowCount.setSelected(props.getShowRowCount());
			_stmtSepChar.setChar(props.getSQLStatementSeparatorChar());

			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();
			_showBinaryChk.setSelected(largeObjInfo.getReadBinary());
			_showVarBinaryChk.setSelected(largeObjInfo.getReadVarBinary());
			_showLongVarBinaryChk.setSelected(largeObjInfo.getReadLongVarBinary());
			_showBlobChk.setSelected(largeObjInfo.getReadBlobs());
			_showClobChk.setSelected(largeObjInfo.getReadClobs());
			_showBlobSize.setInt(largeObjInfo.getReadBlobsSize());
			_showClobSize.setInt(largeObjInfo.getReadClobsSize());

			_showBlobSize.setEnabled(_showBlobChk.isSelected());
			_showClobSize.setEnabled(_showClobChk.isSelected());

			FontInfo fi = props.getFontInfo();
			if (fi == null)
			{
				fi = new FontInfo(UIManager.getFont("TextArea.font"));
			}
			_fontLbl.setText(fi.toString());
			_fontBtn.setSelectedFont(fi.createFont());
		}

		void applyChanges(SessionProperties props)
		{
			props.setAutoCommit(_autoCommitChk.isSelected());
			props.setCommitOnClosingConnection(_commitOnClose.isSelected());
			props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
			props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());
			props.setSQLNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
			props.setSQLLimitRows(_sqlLimitRows.isSelected());
			props.setShowRowCount(_showRowCount.isSelected());
			props.setSQLStatementSeparatorChar(_stmtSepChar.getChar());

			LargeResultSetObjectInfo largeObjInfo = props.getLargeResultSetObjectInfo();
			largeObjInfo.setReadBinary(_showBinaryChk.isSelected());
			largeObjInfo.setReadVarBinary(_showVarBinaryChk.isSelected());
			largeObjInfo.setReadLongVarBinary(_showLongVarBinaryChk.isSelected());
			largeObjInfo.setReadBlobs(_showBlobChk.isSelected());
			largeObjInfo.setReadClobs(_showClobChk.isSelected());
			largeObjInfo.setReadBlobsSize(_showBlobSize.getInt());
			largeObjInfo.setReadClobsSize(_showClobSize.getInt());

			props.setFontInfo(_fontBtn.getFontInfo());
		}

		private void createUserInterface(IApplication app)
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

			++gbc.gridy;
			add(createFontPanel(), gbc);
		}

		private JPanel createSQLPanel(IApplication app)
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(0, 4, 0, 4);
			gbc.anchor = gbc.WEST;

			_autoCommitChk.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					_commitOnClose.setEnabled(!((JCheckBox) evt.getSource()).isSelected());
				}
			});

			_contentsLimitRowsChk.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					_contentsNbrRowsToShowField.setEnabled(_contentsLimitRowsChk.isSelected());
				}
			});

			_sqlLimitRows.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					_sqlNbrRowsToShowField.setEnabled(_sqlLimitRows.isSelected());
				}
			});

			_contentsNbrRowsToShowField.setColumns(5);
			_sqlNbrRowsToShowField.setColumns(5);
			_stmtSepChar.setColumns(1);

			// First column.
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_autoCommitChk, gbc);
			++gbc.gridy;
			gbc.gridwidth = 3;
			pnl.add(_showRowCount, gbc);
			gbc.gridwidth = 1;
			++gbc.gridy;
			pnl.add(_contentsLimitRowsChk, gbc);
			++gbc.gridy;
			pnl.add(_sqlLimitRows, gbc);

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

			++gbc.gridx;
			gbc.gridy = 0;
			++gbc.gridy;
			++gbc.gridy;
			gbc.gridwidth = 3;
			pnl.add(_contentsNbrRowsToShowField, gbc);
			++gbc.gridy;
			pnl.add(_sqlNbrRowsToShowField, gbc);
			++gbc.gridy;
			pnl.add(_stmtSepChar, gbc);

			return pnl;
		}

		private JPanel createDataTypesPanel()
		{
			_showBlobSize.setColumns(5);
			_showClobSize.setColumns(5);

			_showBlobChk.addActionListener(new DataTypeCheckBoxListener(_showBlobSize));
			_showClobChk.addActionListener(new DataTypeCheckBoxListener(_showClobSize));

			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Show String Data for Columns of these Data Types"));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(0, 4, 0, 4);
			gbc.anchor = gbc.WEST;

			gbc.gridwidth = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnl.add(_showBinaryChk, gbc);

			++gbc.gridx;
			gbc.weightx = 0;
			gbc.gridwidth = 2;
			pnl.add(_showVarBinaryChk, gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			pnl.add(_showLongVarBinaryChk, gbc);

			++gbc.gridy;
			pnl.add(_showBlobChk, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.NBR_BYTES), gbc);

			++gbc.gridx;
			pnl.add(_showBlobSize, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(_showClobChk, gbc);

			++gbc.gridx;
			pnl.add(new RightLabel(SQLPropertiesPanelI18n.NBR_CHARS), gbc);

			++gbc.gridx;
			pnl.add(_showClobSize, gbc);

			return pnl;
		}

		private JPanel createFontPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL Entry Area"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(0, 4, 0, 4);

			_fontBtn.addActionListener(new FontButtonListener());

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_fontBtn, gbc);

			++gbc.gridx;
			gbc.fill = gbc.HORIZONTAL;
			gbc.weightx = 1.0;
			pnl.add(_fontLbl, gbc);

			return pnl;
		}

		private static final class RightLabel extends JLabel
		{
			RightLabel(String title)
			{
				super(title, SwingConstants.RIGHT);
			}
		}

		private static final class FontButton extends JButton
		{
			private FontInfo _fi;
			private JLabel _lbl;
			private Font _font;

			FontButton(String text, JLabel lbl)
			{
				super(text);
				_lbl = lbl;
			}

			FontInfo getFontInfo()
			{
				return _fi;
			}

			Font getSelectedFont()
			{
				return _font;
			}

			void setSelectedFont(Font font)
			{
				_font = font;
				if (_fi == null)
				{
					_fi = new FontInfo(font);
				}
				else
				{
					_fi.setFont(font);
				}
			}
		}

		private static final class DataTypeCheckBoxListener implements ActionListener
		{
			private IntegerField _field;

			DataTypeCheckBoxListener(IntegerField field)
			{
				super();
				_field = field;
			}

			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() instanceof JCheckBox)
				{
					_field.setEnabled(((JCheckBox)evt.getSource()).isSelected());
				}
			}
		}

		private static final class FontButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() instanceof FontButton)
				{
					FontButton btn = (FontButton) evt.getSource();
					FontInfo fi = btn.getFontInfo();
					Font font = null;
					if (fi != null)
					{
						font = fi.createFont();
					}
					font = new FontChooser().showDialog(font);
					if (font != null)
					{
						btn.setSelectedFont(font);
						btn._lbl.setText(new FontInfo(font).toString());
					}
				}
			}
		}

		private final static class OutputType
		{
			private final String _name;
			private final String _className;

			OutputType(String name, String className)
			{
				super();
				_name = name;
				_className = className;
			}

			public String toString()
			{
				return _name;
			}

			String getPanelClassName()
			{
				return _className;
			}
		}

	}
}