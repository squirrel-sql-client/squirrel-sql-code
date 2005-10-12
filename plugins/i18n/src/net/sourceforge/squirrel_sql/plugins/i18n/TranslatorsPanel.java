package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import java.awt.*;

public class TranslatorsPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(TranslatorsPanel.class);


   JComboBox cboLocales = new JComboBox();
   JButton btnLoad = new JButton(s_stringMgr.getString("I18n.loadTabs")); // i18n[I18n.loadTabs=Load tabs]

   JTextField txtWorkingDir = new JTextField();
   JButton btnChooseWorkDir;

   JTextField txtEditorCommand = new JTextField();
   JButton btnChooseEditorCommand;
   JTable tblBundels = new JTable();


   public TranslatorsPanel(PluginResources resources)
   {
      GridBagConstraints gbc;

      setLayout(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.locales")), gbc); // i18n[I18n.locales=Locales]

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(cboLocales, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnLoad, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.WorkingDir")), gbc); // i18n[I18n.WorkingDir=Working Directory]

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(txtWorkingDir, gbc);

      btnChooseWorkDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseWorkDir, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.EditorCommand")), gbc); // i18n[I18n.WorkingDir=Working Directory]

      gbc = new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(txtEditorCommand, gbc);

      btnChooseEditorCommand = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseEditorCommand, gbc);

      gbc = new GridBagConstraints(0,3,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.bundles")), gbc); // i18n[I18n.bundles=Bundles]

      gbc = new GridBagConstraints(0,4,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      add(new JScrollPane(tblBundels), gbc);
   }


}
