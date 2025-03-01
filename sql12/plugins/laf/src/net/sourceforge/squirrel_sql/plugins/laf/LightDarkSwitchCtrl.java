package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.preferences.themes.ThemesEnum;
import net.sourceforge.squirrel_sql.client.session.action.syntax.theme.SyntaxThemeFactory;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

import javax.swing.JPanel;
import java.awt.Color;

public class LightDarkSwitchCtrl
{
   private final LightDarkSwitchPanel _panel;

   public LightDarkSwitchCtrl()
   {
      _panel = new LightDarkSwitchPanel();
   }

   public JPanel getPanel()
   {
      return _panel;
   }

   public void apply()
   {
      if(_panel.radLight.isSelected() && ThemesEnum.LIGH != ThemesEnum.getCurrentTheme())
      {
         ThemesEnum.saveCurrentTheme(ThemesEnum.LIGH);

         Main.getApplication().getSyntaxManager().getSyntaxPreferences().initSyntaxTheme(SyntaxThemeFactory.createDefaultLightTheme());
         SquirrelPreferences prefsToUpdate = Main.getApplication().getSquirrelPreferences();

         SquirrelPreferences defaultPrefs = new SquirrelPreferences();
         prefsToUpdate.getSessionProperties().setNullValueColorRGB(SessionProperties.DEFAULT_NULL_VALUE_COLOR_RGB);

         prefsToUpdate.setMessagePanelMessageForeground(defaultPrefs.getMessagePanelMessageForeground());
         prefsToUpdate.setMessagePanelMessageBackground(defaultPrefs.getMessagePanelMessageBackground());
         prefsToUpdate.setMessagePanelMessageHistoryForeground(defaultPrefs.getMessagePanelMessageHistoryForeground());
         prefsToUpdate.setMessagePanelMessageHistoryBackground(defaultPrefs.getMessagePanelMessageHistoryBackground());

         prefsToUpdate.setMessagePanelWarningForeground(defaultPrefs.getMessagePanelWarningForeground());
         prefsToUpdate.setMessagePanelWarningBackground(defaultPrefs.getMessagePanelWarningBackground());
         prefsToUpdate.setMessagePanelWarningHistoryForeground(defaultPrefs.getMessagePanelWarningHistoryForeground());
         prefsToUpdate.setMessagePanelWarningHistoryBackground(defaultPrefs.getMessagePanelWarningHistoryBackground());

         //Main.getApplication().getMainFrame().getMessagePanel().applyMessagePanelStyle(prefsToUpdate);
      }
      else if(_panel.radDark.isSelected() && ThemesEnum.DARK != ThemesEnum.getCurrentTheme())
      {
         ThemesEnum.saveCurrentTheme(ThemesEnum.DARK);
         Main.getApplication().getSyntaxManager().getSyntaxPreferences().initSyntaxTheme(SyntaxThemeFactory.createDarkTheme());

         SquirrelPreferences prefsToUpdate = Main.getApplication().getSquirrelPreferences();
         prefsToUpdate.getSessionProperties().setNullValueColorRGB(new Color(90, 100, 90).getRGB());


         prefsToUpdate.setMessagePanelMessageForeground(Color.green.getRGB());
         prefsToUpdate.setMessagePanelMessageBackground(Color.white.getRGB());
         prefsToUpdate.setMessagePanelMessageHistoryForeground(Color.black.getRGB());
         prefsToUpdate.setMessagePanelMessageHistoryBackground(Color.white.getRGB());

         prefsToUpdate.setMessagePanelWarningForeground(Color.yellow.getRGB());
         prefsToUpdate.setMessagePanelWarningBackground(Color.white.getRGB());
         prefsToUpdate.setMessagePanelWarningHistoryForeground(Color.yellow.darker().darker().getRGB());
         prefsToUpdate.setMessagePanelWarningHistoryBackground(Color.white.getRGB());

         //Main.getApplication().getMainFrame().getMessagePanel().applyMessagePanelStyle(prefsToUpdate);
      }
   }

   public void load()
   {
      switch(ThemesEnum.getCurrentTheme())
      {
         case LIGH:
            _panel.radLight.setSelected(true);
            break;
         case DARK:
            _panel.radDark.setSelected(true);
            break;
      }
   }
}
