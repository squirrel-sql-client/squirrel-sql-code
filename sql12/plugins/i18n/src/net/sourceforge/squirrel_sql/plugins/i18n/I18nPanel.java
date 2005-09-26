package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

import javax.swing.*;
import java.awt.*;

public class I18nPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanel.class);


   JComboBox cboLocales = new JComboBox();
   JButton btnLoad = new JButton(s_stringMgr.getString("I18n.loadTabs")); // i18n{I18n.loadTabs=Load tabs}

   JTextField txtWorkingDir = new JTextField();
   JButton btnChooseDir;

   JTextField txtEditorCommand = new JTextField();
   JButton btnChooseCommand;



   //JCheckBox chkShowSelectedPackages = new JCheckBox(s_stringMgr.getString("I18n.showSelectedPackages"));
   // i18n{I18n.showSelectedPackages=Show translations for selected packages only}

   //JCheckBox chkShowUntranslated = new JCheckBox(s_stringMgr.getString("I18n.showUntranslated"));
   // i18n{I18n.showUntranslated=Show untranslated resources only}

   JTabbedPane tabTranslations = new JTabbedPane();


   JTable tblBundels = new JTable();
   JTextField txtNote;


   public I18nPanel(PluginResources resources)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.locales")), gbc); // i18n{I18n.locales=Locales}

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(cboLocales, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnLoad, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.WorkingDir")), gbc); // i18n{I18n.WorkingDir=Working Directory}

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(txtWorkingDir, gbc);

      btnChooseDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseDir, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.EditorCommand")), gbc); // i18n{I18n.WorkingDir=Working Directory}

      gbc = new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(txtEditorCommand, gbc);

      btnChooseCommand = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseCommand, gbc);


//      JPanel pnlChk = new JPanel(new GridBagLayout());

//      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
//      pnlChk.add(chkShowSelectedPackages, gbc);
//
//      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
//      pnlChk.add(chkShowUntranslated, gbc);

//      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0);
//      add(pnlChk, gbc);


      gbc = new GridBagConstraints(0,3,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0);
      add(tabTranslations, gbc);

      tabTranslations.add(s_stringMgr.getString("I18n.packages"), new JScrollPane(tblBundels)); // i18n{I18n.packages=Packages}

      txtNote = new JTextField();
      txtNote.setText(s_stringMgr.getString("I18n.changeApplicationDefaultLocaleNote"));
      txtNote.setEditable(false);
      // i18n{I18n.changeApplicationDefaultLocaleNote=Example VM Parameters to change used locale: -Duser.language=en -Duser.country=US}
      gbc = new GridBagConstraints(0,4,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0);
      add(txtNote, gbc);


   }
}
