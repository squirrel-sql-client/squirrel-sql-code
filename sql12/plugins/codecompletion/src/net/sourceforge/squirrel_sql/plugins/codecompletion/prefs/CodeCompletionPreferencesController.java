package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.table.TableColumn;
import javax.swing.table.TableCellEditor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CodeCompletionPreferencesController
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CodeCompletionPreferencesController.class);

	private CodeCompletionPreferences _prefs;

	private CodeCompletionPreferencesPanel _panel;

   public CodeCompletionPreferencesController(CodeCompletionPreferences prefs, boolean inNewSessionProps)
	{
      _panel = new CodeCompletionPreferencesPanel();
		_prefs = prefs;

		switch(_prefs.getGeneralCompletionConfig())
		{
			case CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS:
				_panel.optSPWithParams.setSelected(true);
				break;
			case CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS:
				_panel.optSPWithoutParams.setSelected(true);
				break;
			case CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS:
				_panel.optUDFWithParams.setSelected(true);
				break;
			case CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS:
				_panel.optUDFWithoutParams.setSelected(true);
				break;
		}

		PrefixesTableModel tm = new PrefixesTableModel(_prefs.getPrefixedConfigs());
		_panel.tblPrefixes.setModel(tm);


		TableColumn tcPrefix = new TableColumn();
		// i18n[codecompletion.prefs.table.col.prefix=Prefix]
		tcPrefix.setHeaderValue(s_stringMgr.getString("codecompletion.prefs.table.col.prefix"));
		tcPrefix.setModelIndex(0);
		_panel.tblPrefixes.addColumn(tcPrefix);

		TableColumn tcCompletionConfig = new TableColumn();
		// i18n[codecompletion.prefs.table.col.config=Configuration]
		tcCompletionConfig.setHeaderValue(s_stringMgr.getString("codecompletion.prefs.table.col.config"));


		JComboBox cboConfigs = new JComboBox(ConfigCboItem.items);
		cboConfigs.setSelectedIndex(0);
		tcCompletionConfig.setCellEditor(new DefaultCellEditor(cboConfigs));
		tcCompletionConfig.setModelIndex(1);
		_panel.tblPrefixes.addColumn(tcCompletionConfig);


      _panel.txtMaxLastSelectedCompletionNamesPanel.setText("" + _prefs.getMaxLastSelectedCompletionNames());

      _panel.txtMaxLastSelectedCompletionNamesPanel.setEnabled(inNewSessionProps);


      _panel.btnNewRow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onAddRow();
			}
		});

		_panel.btnDeleteRows.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onDeleteRows();
			}
		});
	}

	private void onDeleteRows()
	{
		stopEditing();
		PrefixesTableModel tm = (PrefixesTableModel) _panel.tblPrefixes.getModel();
		int[] selRows = _panel.tblPrefixes.getSelectedRows();
		tm.removeRows(selRows);
	}



	private void onAddRow()
	{
		PrefixesTableModel tm = (PrefixesTableModel) _panel.tblPrefixes.getModel();
		tm.addNewConfig();
	}


	public void initialize(IApplication app)
	{
	}

	public void initialize(IApplication app, ISession session)
	{
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return	the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent()
	{
		return _panel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{

		if(_panel.optSPWithParams.isSelected())
		{
			_prefs.setGeneralCompletionConfig(CodeCompletionPreferences.CONFIG_SP_WITH_PARARMS);
		}
		else if(_panel.optSPWithoutParams.isSelected())
		{
			_prefs.setGeneralCompletionConfig(CodeCompletionPreferences.CONFIG_SP_WITHOUT_PARARMS);
		}
		else if(_panel.optUDFWithParams.isSelected())
		{
			_prefs.setGeneralCompletionConfig(CodeCompletionPreferences.CONFIG_UDF_WITH_PARARMS);
		}
		else if(_panel.optUDFWithoutParams.isSelected())
		{
			_prefs.setGeneralCompletionConfig(CodeCompletionPreferences.CONFIG_UDF_WITHOUT_PARARMS);
		}
		else
		{
			throw new IllegalStateException("No valid config selected");
		}


		stopEditing();

		PrefixesTableModel tm = (PrefixesTableModel) _panel.tblPrefixes.getModel();

		_prefs.setPrefixedConfigs(tm.getData());


      try
      {
         _prefs.setMaxLastSelectedCompletionNames(Math.max(0, Integer.parseInt(_panel.txtMaxLastSelectedCompletionNamesPanel.getText())));
      }
      catch (NumberFormatException e)
      {

      }


   }

	private void stopEditing()
	{
		TableCellEditor cellEditor = _panel.tblPrefixes.getCellEditor();
		if(null != cellEditor)
		{
			cellEditor.stopCellEditing();
		}
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return	the title for this panel.
	 */
	public String getTitle()
	{
		// i18n[codeCompletion.PrefsTabTitle=Code Completion]
		return s_stringMgr.getString("codeCompletion.PrefsTabTitle");
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return	the hint for this panel.
	 */
	public String getHint()
	{
		// i18n[codeCompletion.PrefsTabTitleHint=Configure Code Completion]
		return s_stringMgr.getString("codeCompletion.PrefsTabTitleHint");
	}

}
