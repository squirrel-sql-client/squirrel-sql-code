package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

import javax.swing.*;
import java.awt.*;

public class I18nPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanel.class);


   JComboBox cboLocales = new JComboBox();
   JButton btnLoad = new JButton(s_stringMgr.getString("I18n.loadTabs")); // i18n[I18n.loadTabs=Load tabs]

   JTextField txtWorkingDir = new JTextField();
   JButton btnChooseWorkDir;

   JTextField txtEditorCommand = new JTextField();
   JButton btnChooseEditorCommand;

   JTextField txtSourceDir = new JTextField();
   JButton btnChooseSourceDir;
   JCheckBox chkAppendChanged;
   JButton btnAppendI18nInCode;



   JTabbedPane tabTranslations = new JTabbedPane();


   JTable tblBundels = new JTable();
   JTextField txtNote;


   public I18nPanel(PluginResources resources)
   {
      JPanel pnlTranslators = createTranslatorsTab(resources);

      JPanel pnlDevelopers = createDevelopersTab(resources);


      GridBagConstraints gbc;

      tabTranslations.add(s_stringMgr.getString("I18n.translators"), pnlTranslators); // i18n[I18n.translators=Translators]
      tabTranslations.add(s_stringMgr.getString("I18n.developers"), pnlDevelopers); // i18n[I18n.developers=Developers]

      setLayout(new GridBagLayout());
      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      add(tabTranslations, gbc);


      txtNote = new JTextField();
      txtNote.setText(s_stringMgr.getString("I18n.changeApplicationDefaultLocaleNote"));
      txtNote.setEditable(false);
      // i18n[I18n.changeApplicationDefaultLocaleNote=Example VM Parameters to change locale used by SQuirreL: -Duser.language=en -Duser.country=US]
      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0);
      add(txtNote, gbc);


   }

   private JPanel createDevelopersTab(PluginResources resources)
   {
      GridBagConstraints gbc;

      JPanel ret = new JPanel(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("I18n.SourceDir")), gbc); // i18n[I18n.SourceDir=Source directory]

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      ret.add(txtSourceDir, gbc);

      btnChooseSourceDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      ret.add(btnChooseSourceDir, gbc);

      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      chkAppendChanged = new JCheckBox(s_stringMgr.getString("I18n.appendChangedProperties"));
      ret.add(chkAppendChanged, gbc);
      // i18n[I18n.appendChangedProperties=Append properties that are different in code and properties file]

      gbc = new GridBagConstraints(0,2,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      MultipleLineLabel lblDescription = new MultipleLineLabel(s_stringMgr.getString("I18n.appendCodeDescription"));
      // i18n[I18n.appendCodeDescription=
      // The Create/Append ... button will parse the Java files in the source directory
      // for comments of the form // i18n[myKey=My text] and generate a myKey=My text property in the
      // I18nStrings.propertiesfile in the same directory as the Java file.
      // If the file doesn't exist it will be created.
      // Such a comment may stretch over serveral subsequent lines. Note that
      // /* ... */ comments are not supported.]
      ret.add(lblDescription, gbc);



      gbc = new GridBagConstraints(0,3,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      btnAppendI18nInCode = new JButton(s_stringMgr.getString("I18n.appendI18nStringsProps"));
      ret.add(btnAppendI18nInCode, gbc);
      // i18n[I18n.appendI18nStringsProps=Create/Append I18nString.properties files]

      JPanel pnlDist = new JPanel();
      gbc = new GridBagConstraints(0,4,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      ret.add(pnlDist, gbc);

      return ret;
   }

   private JPanel createTranslatorsTab(PluginResources resources)
   {
      GridBagConstraints gbc;

      JPanel ret = new JPanel(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("I18n.locales")), gbc); // i18n[I18n.locales=Locales]

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      ret.add(cboLocales, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      ret.add(btnLoad, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("I18n.WorkingDir")), gbc); // i18n[I18n.WorkingDir=Working Directory]

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      ret.add(txtWorkingDir, gbc);

      btnChooseWorkDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      ret.add(btnChooseWorkDir, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("I18n.EditorCommand")), gbc); // i18n[I18n.WorkingDir=Working Directory]

      gbc = new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      ret.add(txtEditorCommand, gbc);

      btnChooseEditorCommand = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      ret.add(btnChooseEditorCommand, gbc);

      gbc = new GridBagConstraints(0,3,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("I18n.bundles")), gbc); // i18n[I18n.bundles=Bundles]

      gbc = new GridBagConstraints(0,4,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      ret.add(new JScrollPane(tblBundels), gbc);
      return ret;
   }
}
