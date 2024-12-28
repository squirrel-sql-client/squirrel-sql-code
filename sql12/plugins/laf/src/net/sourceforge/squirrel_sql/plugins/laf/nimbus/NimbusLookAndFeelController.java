package net.sourceforge.squirrel_sql.plugins.laf.nimbus;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.SquirrelLookAndFeelHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.laf.BaseLAFPreferencesPanelComponent;
import net.sourceforge.squirrel_sql.plugins.laf.DefaultLookAndFeelController;
import net.sourceforge.squirrel_sql.plugins.laf.LAFPlugin;
import net.sourceforge.squirrel_sql.plugins.laf.LAFRegister;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class NimbusLookAndFeelController extends DefaultLookAndFeelController
{
   public static final String NIMBUS_LAF_CLASS_NAME = NimbusLookAndFeel.class.getName();
   private static final String PREF_KEY_NIMBUS_THEME = "lafplugin.NimbusLookAndFeelController.PREF_KEY_NIMBUS_THEME";


   private final static ILogger s_log = LoggerController.createLogger(NimbusLookAndFeelController.class);

   private ThemePrefsPanel _themePrefsPanel;

   public NimbusLookAndFeelController(LAFPlugin plugin, LAFRegister register)
   {
   }

   private static NimbusThemeEnum getSelectedTheme()
   {
      String nimbusThemeName = Props.getString(PREF_KEY_NIMBUS_THEME, NimbusThemeEnum.NIMBUS_DEFAULT.name());
      return NimbusThemeEnum.valueOf(nimbusThemeName);
   }

   private static void setSelectedTheme(NimbusThemeEnum selectedTheme)
   {
      Props.putString(PREF_KEY_NIMBUS_THEME, selectedTheme.name());
   }

   @Override
   public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
   {
      _hasBeenInstalled();
   }

   private void _hasBeenInstalled()
   {
      try
      {
         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
         {
            if ("Nimbus".equals(info.getName()))
            {
               SquirrelLookAndFeelHandler.setLookAndFeel(info.getClassName());

               if(getSelectedTheme() == NimbusThemeEnum.NIMBUS_DARK)
               {
                  NimbusDarkTheme.configDarkTheme();
               }
               else
               {
                  SwingUtilities.invokeLater(() -> NimbusDarkTheme.resetDefaultTheme(Main.getApplication().getMainFrame()));
               }

               break;
            }
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to load Nimbus look and feel", e);
      }

   }

   @Override
   public BaseLAFPreferencesPanelComponent getPreferencesComponent()
   {
      return getThemePrefsPanel();
   }

   private ThemePrefsPanel getThemePrefsPanel()
   {
      if(null == _themePrefsPanel)
      {
         _themePrefsPanel = new ThemePrefsPanel();
      }
      return _themePrefsPanel;
   }


   private static class ThemePrefsPanel extends BaseLAFPreferencesPanelComponent
   {
      private JComboBox<NimbusThemeEnum> themeList;

      ThemePrefsPanel()
      {
         initUI();
      }

      private void initUI()
      {
         setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
         themeList = new JComboBox<>();
         JLabel label = new JLabel("Theme:");
         label.setLabelFor(themeList);
         add(label);
         add(Box.createHorizontalStrut(5));
         add(themeList);
      }

      @Override
      public void loadPreferencesPanel()
      {
         DefaultComboBoxModel<NimbusThemeEnum> items = new DefaultComboBoxModel<>();
         for (NimbusThemeEnum theme : NimbusThemeEnum.values())
         {
            items.addElement(theme);
         }
         items.setSelectedItem(getSelectedTheme());
         themeList.setModel(items);
      }

      @Override
      public boolean applyChanges()
      {
         if(null != themeList.getSelectedItem())
         {
            setSelectedTheme((NimbusThemeEnum) themeList.getSelectedItem());
         }
         return true;
      }
   }


}
