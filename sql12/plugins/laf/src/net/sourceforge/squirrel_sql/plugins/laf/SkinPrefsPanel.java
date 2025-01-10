package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

final class SkinPrefsPanel extends BaseLAFPreferencesPanelComponent
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SkinPrefsPanel.class);

   private SkinLookAndFeelController _ctrl;

   private DirectoryListComboBox _themePackCmb = new DirectoryListComboBox();

   SkinPrefsPanel(SkinLookAndFeelController ctrl)
   {
      super(new GridBagLayout());
      _ctrl = ctrl;

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      add(new JLabel(s_stringMgr.getString("laf.skinThemPack")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      add(_themePackCmb, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      add(new JLabel(s_stringMgr.getString("laf.skinThemePackDir")), gbc);

      gbc = new GridBagConstraints(1,1,1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,0), 0,0);
      final String themePackDir = _ctrl.getSkinPreferences().getThemePackDirectory();
      add(GUIUtils.styleTextFieldToCopyableLabel(new JTextField(themePackDir)), gbc);

      //setBorder(BorderFactory.createLineBorder(Color.red));
   }

   /**
    * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
    */
   public void loadPreferencesPanel()
   {
      super.loadPreferencesPanel();
      final String themePackDir = _ctrl.getSkinPreferences().getThemePackDirectory();
      // i18n[laf.jarZip=JAR/Zip files]
      final FileExtensionFilter filter = new FileExtensionFilter(s_stringMgr.getString("laf.jarZip"), new String[]{".jar", ".zip"});
      _themePackCmb.load(new File(themePackDir), filter);
      _themePackCmb.setSelectedItem(_ctrl.getSkinPreferences().getThemePackName());
      if(_themePackCmb.getSelectedIndex() == -1 && _themePackCmb.getModel().getSize() > 0)
      {
         _themePackCmb.setSelectedIndex(0);
      }
   }

   /**
    * @see BaseLAFPreferencesPanelComponent#applyChanges()
    */
   public boolean applyChanges()
   {
      super.applyChanges();
      _ctrl.getSkinPreferences().setThemePackName((String) _themePackCmb.getSelectedItem());

      // Force the LAF to be set even if Skin is the current one. This
      // allows a change in theme to take affect.
      return true;
   }
}
