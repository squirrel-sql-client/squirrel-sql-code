package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerEditableTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GeneralSessionPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GeneralSessionPropertiesPanel.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	interface GeneralSessionPropertiesPanelI18n
	{
		// i18n[generalSessionPropertiesPanel.generalSettings=General settings for the current session]
		String HINT = s_stringMgr.getString("generalSessionPropertiesPanel.generalSettings");
		// i18n[generalSessionPropertiesPanel.mainTabs=Main Tabs:]
		String MAIN_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.mainTabs");
		// i18n[generalSessionPropertiesPanel.metaData=Meta Data:]
		String META_DATA = s_stringMgr.getString("generalSessionPropertiesPanel.metaData");
		// i18n[generalSessionPropertiesPanel.showToolbar=Show toolbar]
		String SHOW_TOOLBAR = s_stringMgr.getString("generalSessionPropertiesPanel.showToolbar");
		// i18n[generalSessionPropertiesPanel.objectTabs=Object Tabs:]
		String OBJECT_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.objectTabs");
		// i18n[generalSessionPropertiesPanel.sqlExecTabs=SQL Execution Tabs:]
		String SQL_EXECUTION_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.sqlExecTabs");
		// i18n[generalSessionPropertiesPanel.sqlResults=SQL Results:]
		String SQL_RESULTS = s_stringMgr.getString("generalSessionPropertiesPanel.sqlResults");
		// i18n[generalSessionPropertiesPanel.sqlResultTabs=SQL Results Tabs:]
		String SQL_RESULTS_TAB_PLACEMENT= s_stringMgr.getString("generalSessionPropertiesPanel.sqlResultTabs");
		// i18n[generalSessionPropertiesPanel.general=General]
		String TITLE = s_stringMgr.getString("generalSessionPropertiesPanel.general");
		// i18n[generalSessionPropertiesPanel.tableContents=Table Contents:]
		String TABLE_CONTENTS = s_stringMgr.getString("generalSessionPropertiesPanel.tableContents");

		// i18n[generalSessionPropertiesPanel.table=Table]
		String TABLE = s_stringMgr.getString("generalSessionPropertiesPanel.table");
		// i18n[generalSessionPropertiesPanel.editableTable=Editable Table]
		String EDITABLE_TABLE = s_stringMgr.getString("generalSessionPropertiesPanel.editableTable");
      // i18n[generalSessionPropertiesPanel.chkKeepTableLayoutOnRerun=Keep table layout on rerun SQL]
      String KEEP_TABLE_LAYOUT_ON_RERUN= s_stringMgr.getString("generalSessionPropertiesPanel.chkKeepTableLayoutOnRerun");
      // i18n[generalSessionPropertiesPanel.text=Text]
		String TEXT = s_stringMgr.getString("generalSessionPropertiesPanel.text");

		// i18n[generalSessionPropertiesPanel.dataTYpe1=Properties for the individual Data Types may be set in the]
		String DATA_TYPE1 = s_stringMgr.getString("generalSessionPropertiesPanel.dataTYpe1");
		// i18n[generalSessionPropertiesPanel.dataTYpe2='General Preferences' window under the 'Data Type Controls' tab.]
		String DATA_TYPE2 = s_stringMgr.getString("generalSessionPropertiesPanel.dataTYpe2");
		
		String SQL_PANEL_ORIENTATION = s_stringMgr.getString("generalSessionPropertiesPanel.sqlPanelOrientation");
	}

	private SessionProperties _props;

	private MyPanel _myPanel = new MyPanel();
	private JScrollPane _scrolledMyPanel = new JScrollPane(_myPanel);

	public GeneralSessionPropertiesPanel()
	{
		super();
	}

	public void initialize(IApplication app) throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_props = app.getSquirrelPreferences().getSessionProperties();

		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_props = session.getProperties();

		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _scrolledMyPanel;
	}

	public String getTitle()
	{
		return GeneralSessionPropertiesPanelI18n.TITLE;
	}

	public String getHint()
	{
		return GeneralSessionPropertiesPanelI18n.HINT;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class MyPanel extends JPanel
	{
		private JCheckBox _showToolBar = new JCheckBox(GeneralSessionPropertiesPanelI18n.SHOW_TOOLBAR);
		private TabPlacementCombo _mainTabPlacementCmb = new TabPlacementCombo();
		private TabPlacementCombo _objectTabPlacementCmb = new TabPlacementCombo();
		private TabPlacementCombo _sqlExecutionTabPlacementCmb = new TabPlacementCombo();
		private TabPlacementCombo _sqlResultsTabPlacementCmb = new TabPlacementCombo();
		private OutputTypeCombo _metaDataCmb = new OutputTypeCombo(false);
		private OutputTypeCombo _sqlResultsCmb = new OutputTypeCombo(true);
		private JCheckBox _chkKeepTableLayoutOnRerun = new JCheckBox();
		private OutputTypeCombo _tableContentsCmb = new OutputTypeCombo(true);
		private SplitPaneOrientationCombo _splitPaneOrientationCmb = new SplitPaneOrientationCombo();

		MyPanel()
		{
			super(new GridBagLayout());
			createGUI();
		}

		void loadData(SessionProperties props)
		{
			_showToolBar.setSelected(props.getShowToolBar());

			int mainTabPlacement = props.getMainTabPlacement();
			for (int i = 0, limit = _mainTabPlacementCmb.getModel().getSize(); i < limit; ++i)
			{
				TabPlacement tp = (TabPlacement)_mainTabPlacementCmb.getItemAt(i);
				if (tp.getValue() == mainTabPlacement)
				{
					_mainTabPlacementCmb.setSelectedIndex(i);
					break;
				}
			}
			if (_mainTabPlacementCmb.getSelectedIndex() == -1)
			{
				_mainTabPlacementCmb.setSelectedIndex(0);
			}

			int objectTabPlacement = props.getObjectTabPlacement();
			for (int i = 0, limit = _objectTabPlacementCmb.getModel().getSize(); i < limit; ++i)
			{
				TabPlacement tp = (TabPlacement)_objectTabPlacementCmb.getItemAt(i);
				if (tp.getValue() == objectTabPlacement)
				{
					_objectTabPlacementCmb.setSelectedIndex(i);
					break;
				}
			}
			if (_objectTabPlacementCmb.getSelectedIndex() == -1)
			{
				_objectTabPlacementCmb.setSelectedIndex(0);
			}

			int sqlExecutionTabPlacement = props.getSQLExecutionTabPlacement();
			for (int i = 0, limit = _sqlExecutionTabPlacementCmb.getModel().getSize(); i < limit; ++i)
			{
				TabPlacement tp = (TabPlacement)_sqlExecutionTabPlacementCmb.getItemAt(i);
				if (tp.getValue() == sqlExecutionTabPlacement)
				{
					_sqlExecutionTabPlacementCmb.setSelectedIndex(i);
					break;
				}
			}
			if (_sqlExecutionTabPlacementCmb.getSelectedIndex() == -1)
			{
				_sqlExecutionTabPlacementCmb.setSelectedIndex(0);
			}

			int sqlResultsTabPlacement = props.getSQLResultsTabPlacement();
			for (int i = 0, limit = _sqlResultsTabPlacementCmb.getModel().getSize(); i < limit; ++i)
			{
				TabPlacement tp = (TabPlacement)_sqlResultsTabPlacementCmb.getItemAt(i);
				if (tp.getValue() == sqlResultsTabPlacement)
				{
					_sqlResultsTabPlacementCmb.setSelectedIndex(i);
					break;
				}
			}
			if (_sqlResultsTabPlacementCmb.getSelectedIndex() == -1)
			{
				_sqlResultsTabPlacementCmb.setSelectedIndex(0);
			}

			int splitPaneOrientation = props.getSqlPanelOrientation();
			
			
			for (int i = 0, limit = _splitPaneOrientationCmb.getModel().getSize(); i < limit; ++i)
			{
				SplitPaneOrientation spo = (SplitPaneOrientation)_splitPaneOrientationCmb.getItemAt(i);
				if (spo.getValue() == splitPaneOrientation)
				{
					_splitPaneOrientationCmb.setSelectedIndex(i);
					break;
				}
			}
			if (_splitPaneOrientationCmb.getSelectedIndex() == -1)
			{
				_splitPaneOrientationCmb.setSelectedIndex(0);
			}
			
			
			_metaDataCmb.selectClassName(props.getMetaDataOutputClassName());
			_sqlResultsCmb.selectClassName(props.getSQLResultsOutputClassName());
			_chkKeepTableLayoutOnRerun.setSelected(props.getKeepTableLayoutOnRerun());
			_tableContentsCmb.selectClassName(props.getTableContentsOutputClassName());
		}

		void applyChanges(SessionProperties props)
		{
			props.setShowToolBar(_showToolBar.isSelected());
			props.setMetaDataOutputClassName(_metaDataCmb.getSelectedClassName());
			props.setSQLResultsOutputClassName(_sqlResultsCmb.getSelectedClassName());
			props.setKeepTableLayoutOnRerun(_chkKeepTableLayoutOnRerun.isSelected());
			props.setTableContentsOutputClassName(_tableContentsCmb.getSelectedClassName());

			TabPlacement tp = (TabPlacement)_mainTabPlacementCmb.getSelectedItem();
			props.setMainTabPlacement(tp.getValue());

			tp = (TabPlacement)_objectTabPlacementCmb.getSelectedItem();
			props.setObjectTabPlacement(tp.getValue());

			tp = (TabPlacement)_sqlExecutionTabPlacementCmb.getSelectedItem();
			props.setSQLExecutionTabPlacement(tp.getValue());

			tp = (TabPlacement)_sqlResultsTabPlacementCmb.getSelectedItem();
			props.setSQLResultsTabPlacement(tp.getValue());
			
			SplitPaneOrientation spOrientation = (SplitPaneOrientation) _splitPaneOrientationCmb.getSelectedItem();
			props.setSqlPanelOrientation(spOrientation.getValue());
		}

		private void createGUI()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createAppearancePanel(), gbc);

			++gbc.gridy;
			add(createOutputPanel(), gbc);

			++gbc.gridy;
			add(new JLabel(""), gbc);
			++gbc.gridy;
			add(new JLabel(GeneralSessionPropertiesPanelI18n.DATA_TYPE1), gbc);
			++gbc.gridy;
			add(new JLabel(GeneralSessionPropertiesPanelI18n.DATA_TYPE2), gbc);
		}

		private JPanel createAppearancePanel()
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			// i18n[generalSessionPropertiesPanel.appearance=Appearance]
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("generalSessionPropertiesPanel.appearance")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_showToolBar, gbc);

			++gbc.gridy;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.MAIN_TAB_PLACEMENT, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 0.5;
			pnl.add(_mainTabPlacementCmb, gbc);

			++gbc.gridx;
			gbc.weightx = 0.0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.OBJECT_TAB_PLACEMENT, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 0.5;
			pnl.add(_objectTabPlacementCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.SQL_EXECUTION_TAB_PLACEMENT, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 0.5;
			pnl.add(_sqlExecutionTabPlacementCmb, gbc);

			++gbc.gridx;
			gbc.weightx = 0.0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.SQL_RESULTS_TAB_PLACEMENT, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 0.5;
			pnl.add(_sqlResultsTabPlacementCmb, gbc);
			
			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.SQL_PANEL_ORIENTATION, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 0.5;
			gbc.gridwidth=3;
			pnl.add(_splitPaneOrientationCmb, gbc);


			return pnl;
		}

		private JPanel createOutputPanel()
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			// i18n[editWherColsSheet.output=Output]
			pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("editWherColsSheet.output")));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.META_DATA, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_metaDataCmb, gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.TABLE_CONTENTS, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_tableContentsCmb, gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.SQL_RESULTS, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_sqlResultsCmb, gbc);


         ++gbc.gridy;
         gbc.gridx = 0;
         gbc.gridwidth = 2;
         _chkKeepTableLayoutOnRerun.setText(GeneralSessionPropertiesPanelI18n.KEEP_TABLE_LAYOUT_ON_RERUN);
         pnl.add(_chkKeepTableLayoutOnRerun, gbc);

			return pnl;
		}
	}

	private final static class OutputType
	{
		static final OutputType TEXT =
				new OutputType(GeneralSessionPropertiesPanelI18n.TEXT,
								DataSetViewerTextPanel.class.getName());
		static final OutputType TABLE =
				new OutputType(GeneralSessionPropertiesPanelI18n.TABLE,
								DataSetViewerTablePanel.class.getName());
		static final OutputType EDITABLE_TABLE =
				new OutputType(GeneralSessionPropertiesPanelI18n.EDITABLE_TABLE,
								DataSetViewerEditableTablePanel.class.getName());
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

	private static final class OutputTypeCombo extends JComboBox
	{
		OutputTypeCombo(boolean possiblyEditable)
		{
			super();
			addItem(OutputType.TABLE);
			addItem(OutputType.TEXT);
			if (possiblyEditable)
			{
				addItem(OutputType.EDITABLE_TABLE);
			}
		}

		void selectClassName(String className)
		{
			if (className.equals(DataSetViewerTablePanel.class.getName()))
			{
				setSelectedItem(OutputType.TABLE);
			}
			else if (className.equals(DataSetViewerTextPanel.class.getName()))
			{
				setSelectedItem(OutputType.TEXT);
			}
			else if (className.equals(DataSetViewerEditableTablePanel.class.getName()))
			{
				setSelectedItem(OutputType.EDITABLE_TABLE);
			}
		}

		String getSelectedClassName()
		{
			return ((OutputType) getSelectedItem()).getPanelClassName();
		}
	}

	private static final class TabPlacement
	{
		// i18n[generalPropertiesPanel.top=Top]
		static final TabPlacement TOP = new TabPlacement(s_stringMgr.getString("generalPropertiesPanel.top"), SwingConstants.TOP);
		// i18n[generalPropertiesPanel.left=Left]
		static final TabPlacement LEFT = new TabPlacement(s_stringMgr.getString("generalPropertiesPanel.left"), SwingConstants.LEFT);
		// i18n[generalPropertiesPanel.bottom=Bottom]
		static final TabPlacement BOTTOM = new TabPlacement(s_stringMgr.getString("generalPropertiesPanel.bottom"), SwingConstants.BOTTOM);
		// i18n[generalPropertiesPanel.right=Right]
		static final TabPlacement RIGHT = new TabPlacement(s_stringMgr.getString("generalPropertiesPanel.right"), SwingConstants.RIGHT);

		private final String _name;
		private final int _value;

		TabPlacement(String name, int value)
		{
			super();
			_name = name;
			_value = value;
		}

		public String toString()
		{
			return _name;
		}

		int getValue()
		{
			return _value;
		}
	}

	private static final class TabPlacementCombo extends JComboBox
	{
		TabPlacementCombo()
		{
			super();
			addItem(TabPlacement.TOP);
			addItem(TabPlacement.LEFT);
			addItem(TabPlacement.BOTTOM);
			addItem(TabPlacement.RIGHT);
		}

		void selectClassName(String className)
		{
			if (className.equals(DataSetViewerTablePanel.class.getName()))
			{
				setSelectedItem(OutputType.TABLE);
			}
			else if (className.equals(DataSetViewerTextPanel.class.getName()))
			{
				setSelectedItem(OutputType.TEXT);
			}
			else if (className.equals(DataSetViewerEditableTablePanel.class.getName()))
			{
				setSelectedItem(OutputType.EDITABLE_TABLE);
			}
		}

		String getSelectedClassName()
		{
			return ((OutputType) getSelectedItem()).getPanelClassName();
		}
	}
	
	private static final class SplitPaneOrientation
	{
		static final SplitPaneOrientation HORIZONTAL = new SplitPaneOrientation(s_stringMgr.getString("generalPropertiesPanel.horizontal"), JSplitPane.HORIZONTAL_SPLIT);
		static final SplitPaneOrientation VERTICAL = new SplitPaneOrientation(s_stringMgr.getString("generalPropertiesPanel.vertical"), JSplitPane.VERTICAL_SPLIT);

		private final String _name;
		private final int _value;

		SplitPaneOrientation(String name, int value)
		{
			super();
			_name = name;
			_value = value;
		}

		public String toString()
		{
			return _name;
		}

		int getValue()
		{
			return _value;
		}
	}

	private static final class SplitPaneOrientationCombo extends JComboBox
	{
		SplitPaneOrientationCombo()
		{
			super();
			addItem(SplitPaneOrientation.VERTICAL);
			addItem(SplitPaneOrientation.HORIZONTAL);
		}
	}
}
