package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class CodeCompletionPreferencesPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CodeCompletionPreferencesPanel.class);


	JRadioButton optSPWithParams;
	JRadioButton optSPWithoutParams;
	JRadioButton optUDFWithParams;
	JRadioButton optUDFWithoutParams;

	JTable tblPrefixes;

	JButton btnNewRow;
	JButton btnDeleteRows;
   JTextField txtMaxLastSelectedCompletionNames;
   JCheckBox chkShowRemarksInColumnCompletion;


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
		add(new JScrollPane(tblPrefixes), gbc);


		gbc = new GridBagConstraints(0,8,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add( createButtonsPanel(), gbc);


      gbc = new GridBagConstraints(0,9,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      add(createMaxLastSelectedCompletionNamesPanel(),gbc);

      gbc = new GridBagConstraints(0,10,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      add(createShowRemarksInColumnCompletionPanel(),gbc);
   }


   private JPanel createMaxLastSelectedCompletionNamesPanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames=If you call code completion without being in the scope of a table,
      //for which number of tables the parser last found would you like to see colums on top of the completion list?]
      MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames"));
      //JLabel lbl = new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames"));
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
