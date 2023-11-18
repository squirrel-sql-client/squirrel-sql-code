package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CompletionCaseSpelling;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class CodeCompletionPreferencesPanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CodeCompletionPreferencesPanel.class);


	JRadioButton optSPWithParams;
	JRadioButton optSPWithoutParams;
	JRadioButton optUDFWithParams;
	JRadioButton optUDFWithoutParams;

	JTable tblPrefixes;

	JButton btnNewRow;
	JButton btnDeleteRows;

	JTextField txtMaxLastSelectedCompletionNames;
   JCheckBox chkShowTableNameOfColumnsInCompletion;
   JCheckBox chkCompleteColumnsQualified;

   JCheckBox chkShowRemarksInColumnCompletion;
   JCheckBox chkMatchCamelCase;
   JCheckBox chkMatchContains;
   JCheckBox chkIncludeUDTs;
   JCheckBox chkSortColumnsAlphabetically;

   JComboBox<CompletionCaseSpelling> _cboColumnUpperLower;
   JComboBox<CompletionCaseSpelling> _cboTableViewUpperLower;
   JComboBox<CompletionCaseSpelling> _cboSchemaUpperLower;
   JComboBox<CompletionCaseSpelling> _cboCatalogUpperLower;


   public CodeCompletionPreferencesPanel()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0 );
		// i18n[codeCompletion.prefsExplain=When completing functions SQuirreL doesn't know
		// if a function is a stored procedure or a user defined function.
		// To make code completion of these two kinds of functions convenient SQuirreL offers to
		// configure which way completion should work.]
		add(new MultipleLineLabel(s_stringMgr.getString("codeCompletion.prefsExplain")), gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
		// i18n[codeCompletion.globalFunctCompltion=If there is no matching prefix configuration functions should complete like:]
		add(new JLabel(s_stringMgr.getString("codeCompletion.globalFunctCompltion")),gbc);

		ButtonGroup grp = new ButtonGroup();

		// i18n[codeCompletion.spWithParams=stored procedure with parameter info: {call mySP(<IN INTEGER tid>)}]
		optSPWithParams = new JRadioButton(s_stringMgr.getString("codeCompletion.spWithParams"));
		gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optSPWithParams,gbc);
		grp.add(optSPWithParams);

		// i18n[codeCompletion.spWithoutParams=stored procedure without parameter info: {call mySP()}]
		optSPWithoutParams = new JRadioButton(s_stringMgr.getString("codeCompletion.spWithoutParams"));
		gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optSPWithoutParams,gbc);
		grp.add(optSPWithoutParams);

		// i18n[codeCompletion.UDFWithParams=user defined function with parameter info: myFunct(<IN INTEGER tid>)]
		optUDFWithParams = new JRadioButton(s_stringMgr.getString("codeCompletion.UDFWithParams"));
		gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optUDFWithParams,gbc);
		grp.add(optUDFWithParams);

		// i18n[codeCompletion.UDFWithoutParams=user defined function without parameter info: myFunct()]
		optUDFWithoutParams = new JRadioButton(s_stringMgr.getString("codeCompletion.UDFWithoutParams"));
		gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optUDFWithoutParams,gbc);
		grp.add(optUDFWithoutParams);


		gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5),0,0 );
		// i18n[codeCompletion.prefixConfig=Configure function completion for function name prefixes:]
		add(new JLabel(s_stringMgr.getString("codeCompletion.prefixConfig")), gbc);


		tblPrefixes = new JTable();
		gbc = new GridBagConstraints(0,7,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0 );
		add(GUIUtils.setPreferredHeight(new JScrollPane(tblPrefixes), 200), gbc);


		gbc = new GridBagConstraints(0,8,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add( createButtonsPanel(), gbc);


      gbc = new GridBagConstraints(0,9,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      add(createMaxLastSelectedCompletionNamesPanel(),gbc);

      gbc = new GridBagConstraints(0,10,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      add(createShowRemarksInColumnCompletionPanel(),gbc);


      gbc = new GridBagConstraints(0,11,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,0,5),0,0 );
      chkMatchCamelCase = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.matchCamelCase"));
      add(chkMatchCamelCase,gbc);

      gbc = new GridBagConstraints(0,12,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0 );
      chkMatchContains = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.matchContains"));
      add(chkMatchContains,gbc);


      gbc = new GridBagConstraints(0,13,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      chkIncludeUDTs = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.includeUDTs"));
      add(chkIncludeUDTs, gbc);

      gbc = new GridBagConstraints(0,14,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0 );
      chkSortColumnsAlphabetically = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.SortColumnsAlphabetically"));
      add(chkSortColumnsAlphabetically, gbc);

      gbc = new GridBagConstraints(0,15,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0 );
      add(createCaseSpellingPanel(), gbc);

      GUIUtils.setPreferredWidth(this, 550);
   }

   private JPanel createCaseSpellingPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.upper.lower.column.completion")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0 );
      _cboColumnUpperLower = new JComboBox<>(CompletionCaseSpelling.values());
      ret.add(_cboColumnUpperLower, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0 );
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.upper.lower.table.completion")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0 );
      _cboTableViewUpperLower = new JComboBox<>(CompletionCaseSpelling.values());
      ret.add(_cboTableViewUpperLower, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0 );
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.upper.lower.schema.completion")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0 );
      _cboSchemaUpperLower = new JComboBox<>(CompletionCaseSpelling.values());
      ret.add(_cboSchemaUpperLower, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0 );
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.upper.lower.catalog.completion")), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,0),0,0 );
      _cboCatalogUpperLower = new JComboBox<>(CompletionCaseSpelling.values());
      ret.add(_cboCatalogUpperLower, gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("CodeCompletionPreferencesPanel.UpperCase.LowerCase")));


      return ret;
   }


   private JPanel createMaxLastSelectedCompletionNamesPanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames=If you call code completion without being in the scope of a table,
      //for which number of tables the parser last found would you like to see colums on top of the completion list?]
      MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames"));
      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      ret.add(lbl, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      NumberFormat format = NumberFormat.getIntegerInstance();
      txtMaxLastSelectedCompletionNames = new JFormattedTextField(format);
      txtMaxLastSelectedCompletionNames.setPreferredSize(new Dimension(30, txtMaxLastSelectedCompletionNames.getPreferredSize().height));

      ret.add(txtMaxLastSelectedCompletionNames, gbc);

      // i18n[CodeCompletionPreferencesPanel.numberOfTables=number of tables]
      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.numberOfTables")), gbc);

      MultipleLineLabel lblNote = new MultipleLineLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.note.duplicate.column.names"));
      gbc = new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      ret.add(lblNote, gbc);

      chkShowTableNameOfColumnsInCompletion = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.show.tableNames.of.columns.in.completion.popup"));
      gbc = new GridBagConstraints(0, 3, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 3, 5), 0, 0);
      ret.add(chkShowTableNameOfColumnsInCompletion, gbc);

      chkCompleteColumnsQualified = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.complete.columns.qualified.by.tableName"));
      gbc = new GridBagConstraints(0, 4, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 25, 5, 5), 0, 0);
      ret.add(chkCompleteColumnsQualified, gbc);


      ret.setBorder(BorderFactory.createEtchedBorder());

      return ret;
   }

   private JPanel createShowRemarksInColumnCompletionPanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[CodeCompletionPreferencesPanel.showRemarksInColumnCompletionNote=
      // Choose this option to see column remarks in code completion lists.\n
      // Note: If you change this option on a running Session you need to do a Session cache refresh (F5)]
      MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.showRemarksInColumnCompletionNote"));
      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      ret.add(lbl, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      // i18n[CodeCompletionPreferencesPanel.showRemarksInColumnCompletionChk=Show remarks in column completion]
      chkShowRemarksInColumnCompletion = new JCheckBox(s_stringMgr.getString("CodeCompletionPreferencesPanel.showRemarksInColumnCompletionChk"));
      ret.add(chkShowRemarksInColumnCompletion, gbc);


      ret.setBorder(BorderFactory.createEtchedBorder());

      return ret;
   }


   private JPanel createButtonsPanel()
   {
      GridBagConstraints gbc;
      JPanel pnlButtons = new JPanel(new GridBagLayout());

      // i18n[codeCompletion.prefixConfig.newRow=Add new row]
      btnNewRow = new JButton(s_stringMgr.getString("codeCompletion.prefixConfig.newRow"));
      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,5,5),0,0);
      pnlButtons.add(btnNewRow, gbc);

      // i18n[codeCompletion.prefixConfig.deleteSelRows=Delete selected rows]
      btnDeleteRows = new JButton(s_stringMgr.getString("codeCompletion.prefixConfig.deleteSelRows"));
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      pnlButtons.add(btnDeleteRows, gbc);
      return pnlButtons;
   }

}
