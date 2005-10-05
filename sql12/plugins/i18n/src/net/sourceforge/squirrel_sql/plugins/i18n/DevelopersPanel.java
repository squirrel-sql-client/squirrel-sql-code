package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

import javax.swing.*;
import java.awt.*;

public class DevelopersPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DevelopersPanel.class);


   JTextField txtSourceDir = new JTextField();
   JButton btnChooseSourceDir;
   JCheckBox chkAppendChanged;
   JButton btnAppendI18nInCode;


   public DevelopersPanel(PluginResources resources)
   {
      GridBagConstraints gbc;

      setLayout(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("I18n.SourceDir")), gbc); // i18n[I18n.SourceDir=Source directory]

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,5),0,0);
      add(txtSourceDir, gbc);

      btnChooseSourceDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseSourceDir, gbc);

      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      chkAppendChanged = new JCheckBox(s_stringMgr.getString("I18n.appendChangedProperties"));
      add(chkAppendChanged, gbc);
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
      add(lblDescription, gbc);



      gbc = new GridBagConstraints(0,3,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      btnAppendI18nInCode = new JButton(s_stringMgr.getString("I18n.appendI18nStringsProps"));
      add(btnAppendI18nInCode, gbc);
      // i18n[I18n.appendI18nStringsProps=Create/Append I18nString.properties files]

      JPanel pnlDist = new JPanel();
      gbc = new GridBagConstraints(0,4,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      add(pnlDist, gbc);

   }


}
