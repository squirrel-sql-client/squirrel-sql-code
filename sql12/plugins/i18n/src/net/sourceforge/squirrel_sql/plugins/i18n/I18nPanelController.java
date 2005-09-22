package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;

public class I18nPanelController implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanelController.class);

   I18nPanel _panel;
   private IApplication _app;

   I18nPanelController()
   {
      _panel = new I18nPanel();

      Locale[] availableLocales = Locale.getAvailableLocales();

      Arrays.sort(availableLocales, new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            return o1.toString().compareTo(o2.toString());
         }
      });


      for (int i = 0; i < availableLocales.length; i++)
      {
         _panel.cboLocales.addItem(availableLocales[i]);
      }

      _panel.cboLocales.setSelectedItem(Locale.getDefault());

      _panel.btnLoad.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLoadBundels(_app);
         }
      });


   }

   public void initialize(final IApplication app)
   {
      _app = app;

   }


   private void onLoadBundels(IApplication app)
   {
      Locale selLocale = (Locale) _panel.cboLocales.getSelectedItem();

      URL[] urLs = ((URLClassLoader) getClass().getClassLoader()).getURLs();

      String pluginDir = new ApplicationFiles().getPluginsDirectory().getPath();

      ArrayList defaultI18nProps = new ArrayList();
      ArrayList localizedI18nProps = new ArrayList();

      String i18nPropsFileName = "I18NStrings.properties";
      String localizedI18nPropsFileName = "I18NStrings_" + selLocale + ".properties";

      String workDir = _panel.txtWorkingDir.getText();
      if(null != workDir && 0 < workDir.trim().length())
      {
         File f = new File(workDir);
         if(false == f.exists())
         {
            String msg = s_stringMgr.getString("I18n.noWorkdir");
            // i18n{I18n.noWorkdir=Working directory doesn't exist.\nDo you want to create it?}
            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(app.getMainFrame(), msg))
            {
               f.mkdirs();
            }
         }
         else if(false == f.isDirectory())
         {
            String msg = s_stringMgr.getString("I18n.WorkdirIsNoDir", f.getAbsolutePath());
            // i18n{I18n.WorkdirIsNoDir=The working directory is not a directory.\nNo bundles will be loaded from {0}}
            JOptionPane.showMessageDialog(app.getMainFrame(), msg);

         }
         else
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, f, defaultI18nProps, localizedI18nProps);
         }
      }

      for (int i = 0; i < urLs.length; i++)
      {
         File file = new File(urLs[i].getFile());

         if(file.isDirectory())
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
         else if (file.getName().equalsIgnoreCase("squirrel-sql.jar") || file.getName().equalsIgnoreCase("fw.jar"))
         {
            findI18nInArchive(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
         else if(file.getPath().startsWith(pluginDir))
         {
            findI18nInArchive(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
      }

      Hashtable i18nPackagesByName = new Hashtable();

      for (int i = 0; i < defaultI18nProps.size(); i++)
      {
         I18nProps i18nProps = (I18nProps) defaultI18nProps.get(i);
         I18nPackage pack = new I18nPackage(i18nProps);
         i18nPackagesByName.put(i18nProps.getPackage(), pack);
      }

      for (int i = 0; i < localizedI18nProps.size(); i++)
      {
         I18nProps locI18nProps = (I18nProps) localizedI18nProps.get(i);
         I18nPackage pack = (I18nPackage) i18nPackagesByName.get(locI18nProps.getPackage());
         if(null != pack)
         {
            System.out.println(locI18nProps.getPackage());
            pack.setLocalizedProp(locI18nProps);
         }
      }

      _panel.listPackages.setListData(i18nPackagesByName.values().toArray(new I18nPackage[0]));
   }

   private void findI18nInArchive(String i18nPropsFileName, String localizedI18nPropsFileName, File file, ArrayList defaultI18nProps, ArrayList localizedI18nProps)
   {
   }

   private void findI18nInDir(String i18nPropsFileName, String localizedI18nPropsFileName, File dir, ArrayList defaultI18nProps, ArrayList localizedI18nProps)
   {
      File[] files = dir.listFiles();

      for (int i = 0; i < files.length; i++)
      {
         if(files[i].isDirectory())
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, files[i], defaultI18nProps, localizedI18nProps);
         }
         else if(files[i].getName().equals(i18nPropsFileName))
         {
            defaultI18nProps.add(new I18nProps(files[i]));
         }
         else if(files[i].getName().equals(localizedI18nPropsFileName))
         {
            localizedI18nProps.add(new I18nProps(files[i]));
         }
      }

   }


   public void applyChanges()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getTitle()
   {
      return s_stringMgr.getString("I18n.title");
   }

   public String getHint()
   {
      return s_stringMgr.getString("I18n.hint");
   }

   public Component getPanelComponent()
   {
      return _panel;
   }

}
