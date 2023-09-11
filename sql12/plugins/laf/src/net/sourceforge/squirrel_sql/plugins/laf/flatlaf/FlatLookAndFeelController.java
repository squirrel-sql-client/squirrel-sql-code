package net.sourceforge.squirrel_sql.plugins.laf.flatlaf;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.SquirrelLookAndFeelHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
import net.sourceforge.squirrel_sql.plugins.laf.BaseLAFPreferencesPanelComponent;
import net.sourceforge.squirrel_sql.plugins.laf.DefaultLookAndFeelController;
import net.sourceforge.squirrel_sql.plugins.laf.LAFPlugin;
import net.sourceforge.squirrel_sql.plugins.laf.LAFRegister;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FlatLookAndFeelController extends DefaultLookAndFeelController
{
   private final static ILogger s_log = LoggerController.createLogger(FlatLookAndFeelController.class);

   public static final String FLAT_LAF_PLACEHOLDER_CLASS_NAME = new FlatLafPlaceholder().getClass().getName();

   private FlatThemePreference selectedTheme;

   private Map<String, Object> availableThemes;

   private FileWrapper userExtraLAFFolder;

   private FlatLafProxy flatProxy;
   private ThemePrefsPanel _themePrefsPanel;

   public FlatLookAndFeelController(LAFPlugin plugin, LAFRegister register)
   {
      flatProxy = new FlatLafProxy(register.getLookAndFeelClassLoader());
      userExtraLAFFolder = plugin.getUsersExtraLAFFolder();

      XMLObjectCache cache = plugin.getSettingsCache();
      Iterator<?> it = cache.getAllForClass(FlatThemePreference.class);
      if (it.hasNext())
      {
         selectedTheme = (FlatThemePreference) it.next();
      }
      else
      {
         selectedTheme = new FlatThemePreference();
         try
         {
            cache.add(selectedTheme);
         }
         catch (DuplicateObjectException e)
         {
            s_log.warn("FlatTheme object already in XMLObjectCache", e);
         }
      }
   }

   Map<String, Object> getAvailableThemes()
   {
      if (availableThemes == null)
      {
         Map<String, Object> themes = new LinkedHashMap<>();
         themes.putAll(flatProxy.getStandardThemes());
         themes.putAll(loadUserThemes());
         availableThemes = themes;
      }
      return availableThemes;
   }

   private Map<String, ?> loadUserThemes()
   {
      Map<String, Object> themes = new TreeMap<>();
      // IntelliJ .theme.json
      for (FileWrapper json : userExtraLAFFolder.listFiles((dir, name) -> name.endsWith(".theme.json")))
      {
         try (InputStream in = json.getFileInputStream())
         {
            Object theme = flatProxy.createIntelliJTheme(in);
            themes.put(flatProxy.getIntelliJThemeName(theme), theme);
         }
         catch (IOException e)
         {
            s_log.error("Could not read: " + json.getAbsolutePath(), e);
         }
      }
      // FlatLaf .properties
      for (FileWrapper props : userExtraLAFFolder.listFiles((dir, name) -> name.endsWith(".properties")))
      {
         if (props.getName().equals("extralafs.properties"))
         {
            s_log.debug("FlatLaf theme: extralafs.properties skipped");
            continue;
         }

         Properties theme = new Properties();
         try (InputStream in = props.getFileInputStream())
         {
            theme.load(in);
            if (theme.getProperty("@baseTheme") == null) {
               s_log.info("Properties file doesn't appear to be a FlatLaf theme: " + props);
               continue;
            }
            themes.put(props.getName().replaceFirst(".properties$", ""), theme);
         }
         catch (IOException e)
         {
            s_log.error("Could not read: " + props.getAbsolutePath(), e);
         }
      }
      return themes;
   }

   @Override
   public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
   {
      _hasBeenInstalled();
   }

   private void _hasBeenInstalled()
   {
      Object theme = getAvailableThemes().get(selectedTheme.getName());

      try
      {
         if(null == theme && 0 < getAvailableThemes().size())
         {
            s_log.error("Flat-Laf theme \"" + selectedTheme.getName() +"\" is not in available themes." );
            theme = getAvailableThemes().values().iterator().next();
         }

         LookAndFeel lnf;
         if(theme instanceof Class)
         {
            lnf = ((Class<LookAndFeel>) theme).getConstructor().newInstance();
         }
         else if(theme instanceof Properties)
         {
            lnf = flatProxy.createPropsLaf(selectedTheme.getName(), (Properties) theme);
         }
         else
         {
            lnf = flatProxy.createLaf(theme);
         }
         UIManager.put("ClassLoader", lnf.getClass().getClassLoader());
         SquirrelLookAndFeelHandler.setLookAndFeel(lnf);
      }
      catch (Exception e)
      {
         s_log.error("Problem setting look and feel: " + theme, e);
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
         availableThemes = null; // Reload user themes.
         _themePrefsPanel = new ThemePrefsPanel(this);
      }
      return _themePrefsPanel;
   }

   public void applyTheme(String flatLafThemeName)
   {
      for (Map.Entry<String, Class<? extends LookAndFeel>> entry : flatProxy.getStandardThemes().entrySet())
      {
         if(entry.getKey().equals(flatLafThemeName))
         {
            selectedTheme.setName(entry.getKey());
            getThemePrefsPanel().loadPreferencesPanel();
            _hasBeenInstalled();
            break;
         }
      }
   }


   private static class ThemePrefsPanel extends BaseLAFPreferencesPanelComponent
   {
      private FlatLookAndFeelController ctrl;

      private JComboBox<String> themeList;

      ThemePrefsPanel(FlatLookAndFeelController ctrl)
      {
         this.ctrl = ctrl;
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
         DefaultComboBoxModel<String> items = new DefaultComboBoxModel<>();
         for (String name : ctrl.getAvailableThemes().keySet())
         {
            items.addElement(name);
         }
         items.setSelectedItem(ctrl.selectedTheme.getName());
         themeList.setModel(items);
      }

      @Override
      public boolean applyChanges()
      {
         if(   null != themeList.getSelectedItem()
            && false == StringUtilities.isEmpty(themeList.getSelectedItem().toString(), true))
         {
            ctrl.selectedTheme.setName(themeList.getSelectedItem().toString());
         }
         return true;
      }
   }


}
