package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;

public class ThemesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThemesController.class);

   public static final String PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS = "GlobalPreferences.ThemesController.last.applied.theme";

   private final ThemesPanel _themesPanel;

   /**
    * Is saved in Props instead of Global preferences because applying a theme persistently changes
    * LAF-Preferences and New Session Properties. Hence the applied theme is saved immediately, too.
    *
    */
   private ThemesEnum _lastAppliedTheme;

   public ThemesController()
   {
      _themesPanel = new ThemesPanel();

      _lastAppliedTheme = ThemesEnum.valueOf(Props.getString(PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS, ThemesEnum.LIGH.name()));

      _themesPanel.cboThemes.setSelectedItem(_lastAppliedTheme);


      _themesPanel.btnApply.addActionListener(e -> onApply());
   }

   private void onApply()
   {
      _lastAppliedTheme = (ThemesEnum) _themesPanel.cboThemes.getSelectedItem();


      switch (_lastAppliedTheme)
      {
         case LIGH:
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_themesPanel, s_stringMgr.getString("ThemesController.apply.light.theme")))
            {
               return;
            }

            LAFPluginAccessor.applyMetalOcean();
            break;
         case DARK:
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_themesPanel, s_stringMgr.getString("ThemesController.apply.dark.theme")))
            {
               return;
            }
            LAFPluginAccessor.applyMetalCharCoal();
            break;
      }

      Props.putString(PREF_KEY_THEMESCONTROLLER_LAST_APPLIED_THEMS, _lastAppliedTheme.name());

   }

   public ThemesPanel getPanel()
   {
      return _themesPanel;
   }

}
