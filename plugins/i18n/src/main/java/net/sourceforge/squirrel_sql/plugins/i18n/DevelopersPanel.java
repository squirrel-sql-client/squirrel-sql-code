package net.sourceforge.squirrel_sql.plugins.i18n;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DevelopersPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DevelopersPanel.class);


   JTextField txtSourceDir = new JTextField();
   JButton btnChooseSourceDir;
   JButton btnAppendI18nInCode;


   public DevelopersPanel(PluginResources resources)
   {
      GridBagConstraints gbc;

      setLayout(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[I18n.SourceDir=Source directory]
		add(new JLabel(s_stringMgr.getString("I18n.SourceDir")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      add(txtSourceDir, gbc);

      btnChooseSourceDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseSourceDir, gbc);

      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
		// i18n[I18n.appendCodeDescription=
		// The Create/Append ... button will parse the Java files in the source directory
		// for comments of the form // i18n[myKey=My text]] and generate a myKey=My text property in the
		// I18nStrings.properties file in the same directory as the Java file.
		// If the file doesn't exist it will be created.
		// Such a comment may stretch over serveral subsequent lines.
		// To have a ]] in a property use ]]]] in the comment. /* ... */ comments are not supported.\n\n
		//The parser is also able to replace a string in the source code by the usual s_stringMgr.getString("key") if:\n
		// - the string fits in none line\n
		// - the i18n comment is placed in the line above the string\n
		// - the string doesn't contain parameters]
		MultipleLineLabel lblDescription = new MultipleLineLabel(s_stringMgr.getString("I18n.appendCodeDescription"));
      add(lblDescription, gbc);

      gbc = new GridBagConstraints(0,2,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
		// i18n[I18n.appendI18nStringsProps=Create/Append I18nString.properties files]
      btnAppendI18nInCode = new JButton(s_stringMgr.getString("I18n.appendI18nStringsProps"));
      add(btnAppendI18nInCode, gbc);      
      
      JPanel pnlDist = new JPanel();
      gbc = new GridBagConstraints(0,3,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      add(pnlDist, gbc);

   }


}
