package net.sourceforge.squirrel_sql.plugins.laf.flatlaf;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
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
import javax.swing.UIManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class FlatLookAndFeelController extends DefaultLookAndFeelController
{
   public static final String FLAT_LAF_PLACEHOLDER_CLASS_NAME = new FlatLafPlaceholder().getClass().getName();

   private static ILogger log = LoggerController.createLogger(FlatLookAndFeelController.class);

   private FlatThemePreference selectedTheme;

   private Map<String, Object> availableThemes;

   private FileWrapper userExtraLAFFolder;

   private FlatLafProxy flatProxy;

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
            log.warn("FlatTheme object already in XMLObjectCache", e);
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
            if (log.isDebugEnabled())
               log.warn("Could not read: " + json.getAbsolutePath(), e);
            else
               log.warn("Could not read: " + json.getAbsolutePath() + "\n\t" + e);
         }
      }
      // FlatLaf .properties
      for (FileWrapper props : userExtraLAFFolder.listFiles((dir, name) -> name.endsWith(".properties")))
      {
         Properties theme = new Properties();
         try (InputStream in = props.getFileInputStream())
         {
            theme.load(in);
            if (theme.getProperty("@baseTheme") == null) {
               log.info("Properties file doesn't appear to be a FlatLaf theme: " + props);
               continue;
            }
            themes.put(props.getName().replaceFirst(".properties$", ""), theme);
         }
         catch (IOException e)
         {
            if (log.isDebugEnabled())
               log.warn("Could not read: " + props.getAbsolutePath(), e);
            else
               log.warn("Could not read: " + props.getAbsolutePath() + "\n\t" + e);
         }
      }
      return themes;
   }

   @Override
   public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
   {
      Object theme = getAvailableThemes().get(selectedTheme.getName());
      try
      {
         LookAndFeel lnf;
         if (theme instanceof Class) {
            lnf = ((Class<LookAndFeel>) theme).getConstructor().newInstance();
         } else if (theme instanceof Properties) {
            lnf = flatProxy.createPropsLaf(selectedTheme.getName(), (Properties) theme);
         } else {
            lnf = flatProxy.createLaf(theme);
         }
         UIManager.put("ClassLoader", lnf.getClass().getClassLoader());
         UIManager.setLookAndFeel(lnf);
      }
      catch (Exception e)
      {
         log.error("Problem setting look and feel: " + theme, e);
      }
   }

   @Override
   public BaseLAFPreferencesPanelComponent getPreferencesComponent()
   {
      availableThemes = null; // Reload user themes.
      return new PrefsPanel(this);
   }


   @SuppressWarnings("serial")
   private static class PrefsPanel extends BaseLAFPreferencesPanelComponent
   {
      private FlatLookAndFeelController ctrl;

      private JComboBox<String> themeList;

      PrefsPanel(FlatLookAndFeelController ctrl)
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
         ctrl.selectedTheme.setName(themeList.getSelectedItem().toString());
         return true;
      }
   }


}
